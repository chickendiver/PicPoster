package ca.ualberta.cs.picposter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import ca.ualberta.cs.picposter.controller.PicPosterController;
import ca.ualberta.cs.picposter.model.PicPostModel;
import ca.ualberta.cs.picposter.model.PicPosterModelList;
import ca.ualberta.cs.picposter.view.PicPostModelAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PicPosterActivity extends Activity {


	public static final int OBTAIN_PIC_REQUEST_CODE = 117;

	private HttpClient httpclient = new DefaultHttpClient();
	
	private Gson gson = new Gson();

	EditText searchPostsEditText;
	ImageView addPicImageView;
	EditText addPicEditText;
	ListView picPostList;

	private Bitmap currentPicture;
	PicPosterModelList model;
	PicPosterController controller;
	PicPostModelAdapter adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		this.searchPostsEditText = (EditText)this.findViewById(R.id.search_posts_edit_text);
		this.addPicImageView = (ImageView)this.findViewById(R.id.add_pic_image_view);
		this.addPicEditText = (EditText)this.findViewById(R.id.add_pic_edit_text);
		this.picPostList = (ListView)this.findViewById(R.id.pic_post_list);

		this.model = new PicPosterModelList();
		this.controller = new PicPosterController(this.model, this);
		this.adapter = new PicPostModelAdapter(this, R.layout.pic_post, model.getList());

		this.picPostList.setAdapter(this.adapter);
		this.model.setAdapter(this.adapter);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OBTAIN_PIC_REQUEST_CODE && resultCode == RESULT_OK) {
			this.currentPicture = (Bitmap)data.getExtras().get("data");
			this.addPicImageView.setImageBitmap(this.currentPicture);
		}
	}


	public void obtainPicture(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, OBTAIN_PIC_REQUEST_CODE);
	}


	public void pushPicture(View view) {
		this.controller.addPicPost(this.currentPicture, this.addPicEditText.getText().toString());
		this.addPicEditText.setText(null);
		this.addPicEditText.setHint(R.string.add_pic_edit_text_hint);
		this.addPicImageView.setImageResource(R.drawable.camera);
		this.currentPicture = null;
	}


	public void searchPosts(View view) {
		final String searchTerm = this.searchPostsEditText.getText().toString();

		//TODO : perform search, update model, etc
		Thread thread = new Thread(){
			@Override
			public void run(){
				try{
					HttpPost searchRequest = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/bpoulett/_search?pretty=1&q=" + searchTerm);

					HttpResponse response = httpclient.execute(searchRequest);

					String status = response.getStatusLine().toString();
					Log.e("Query Response", status);
					
					String json = getEntityContent(response);
					
					Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<PicPostModel>>(){}.getType();
					ElasticSearchSearchResponse<PicPostModel> esResponse = gson.fromJson(json, elasticSearchSearchResponseType);
					Log.e("Query Reponse",esResponse.toString());
					
					for (ElasticSearchResponse<PicPostModel> r : esResponse.getHits()) {
						final PicPostModel ppm = r.getSource();
						this.runOnUIThread(new Runnable(){
							public void run(){
								model.addPicPostSearch(ppm);
							}
						});
						Log.e("PicPostModel",ppm.toString());
						}
						
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		thread.start();
		

		this.searchPostsEditText.setText(null);
		this.searchPostsEditText.setHint(R.string.search_posts_edit_text_hint);
	}
	
	public String getEntityContent(HttpResponse response) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader((response.getEntity().getContent())));
		String output;
		String json = "";
		while ((output = br.readLine()) != null) {
			Log.e("OUTPUT", output);
			json += output;
		}
		Log.e("JSON", json);
		return json;
	}
}