package ca.ualberta.cs.picposter.model;

import java.util.Date;

import android.graphics.Bitmap;

/**
 * Represents a single Pic Post, which includes a picture, some text, and a timestamp.
 * @author zjullion
 */
public class PicPostModel {
	
	
	//private Bitmap picture;
	private String text;
	private Date timestamp;
	
	//MUST CONVERT BITMAP TO BASE 64 FORMAT BEFORE SENDING IT TO ELASTICSEARCH: http://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string
	public PicPostModel(String text, Date timestamp) {
		//this.picture = picture;
		this.text = text;
		this.timestamp = timestamp;
	}
	
	
	//public Bitmap getPicture() {
	//	return this.picture;
	//}
	
	
	public String getText() {
		return this.text;
	}
	
	
	public Date getTimestamp() {
		return this.timestamp;
	}
}