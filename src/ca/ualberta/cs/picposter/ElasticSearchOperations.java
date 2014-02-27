package ca.ualberta.cs.picposter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import ca.ualberta.cs.picposter.model.PicPostModel;

import com.google.gson.Gson;

public class ElasticSearchOperations {

	public static void pushPickPostModel(final PicPostModel model){
		Thread thread = new Thread(){ // With a thread, there is no guarantee on how long this will survive. A service would be better if you wanted it to happen after the activity dies
			@Override
			public void run(){
				Gson gson = new Gson();
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/bpoulett/"); // "testing" is the index and "bpoulett" is the type. The ID is 1.
							//Put : Go to this type and put the information in the ID
							//Post: Go to the type and put the information in some ID.
				//http://cmput301.softwareprocess.es:8080/testing/bpoulett/_search?q="SEARCH TEXT" <-- THIS IS HOW YOU SEARCH THE INDEX.
				try{
					request.setEntity(new StringEntity(gson.toJson(model)));
					HttpResponse response = client.execute(request);
					Log.e("ElasticSearch", response.getStatusLine().toString());
					
					HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					
					String output = reader.readLine();
					while(output != null){
						Log.e("ElasticSearch", output);
						output = reader.readLine();
					}
					
				}
				catch(Exception e){ // BAD PROGRAMMING STYLE.
					e.printStackTrace();
				}
			
			}
		};
		thread.start();
	}
	
}
