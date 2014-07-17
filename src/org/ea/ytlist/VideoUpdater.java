package org.ea.ytlist;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.ea.utils.DatabaseHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VideoUpdater extends AsyncTask<Object, List<String>, Object> {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	private DatabaseHelper dh = null;
	private ProgressDialog dialog = null;
	private MainListAdapter mla = null;
	private boolean allReadyRunning = false;
	private Activity act = null;

	public VideoUpdater(Activity activity, MainListAdapter mla) {
		this.mla = mla;
		this.act = activity;
	}

	protected void onPreExecute() {
		if(dialog == null) dialog = new ProgressDialog(this.act);
		if(dialog.isShowing()) allReadyRunning = true;
		dialog.setMessage("Retrieving videos...");
		dialog.show();
	}

	@Override
	protected String doInBackground(Object... params) {
		if(allReadyRunning) return "";
		dh = DatabaseHelper.get(mla.getContext());
		SQLiteDatabase db = dh.open();
        List<String> videoIds = new ArrayList<String>(); 
        
		long numUpdated = 0;
		long numFetched = 0;
        try {
			Cursor c = db.rawQuery("select videoId from "+DatabaseHelper.TABLE_VIDEOS+ ";", null);
			while(c.moveToNext()) {
	            videoIds.add(c.getString(c.getColumnIndex("videoId")));
			}
			
            ObjectMapper mapper = new ObjectMapper();
	        JsonNode rootNode = mapper.readValue(new URL("https://gdata.youtube.com/feeds/api/users/monsterkalaspuffen/newsubscriptionvideos?alt=json&key=AIzaSyBwwilGKJ5j-S3tRAGuaPQ2o1IJRKgFFpU"), JsonNode.class); // src can be a File, URL, InputStream etc
	        
			ContentValues values = null;
            for(JsonNode entry : rootNode.path("feed").path("entry")) {
            	values = new ContentValues();
                String videoId = entry.path("id").path("$t").asText();                
                videoId = videoId.substring(videoId.lastIndexOf("/")+1);
                
                numFetched++;
                
                if(videoIds.contains(videoId)) continue;
                
                String published = entry.path("published").path("$t").asText();
                                                               
    			values.put("videoId", videoId);
    			values.put("title", entry.path("title").path("$t").asText());
    			values.put("published", sdf.parse(published).getTime());
    			values.put("image", getBytesFromUrl(entry.path("media$group").path("media$thumbnail").path(0).path("url").asText()));
    			values.put("watched", 0l);
            
    			synchronized(dh) {
    				if(!db.isOpen()) {
    					db.close();
    					db = dh.open();
    				}
    				db.insertOrThrow(DatabaseHelper.TABLE_VIDEOS, null, values);
    				numUpdated++;
    			}
            }
		} catch (Exception e) {
			Log.e(VideoUpdater.class.getName(), "Exception", e);
		}							

		db.close();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Updated videos. New/Fetched (");
		sb.append(numUpdated);
		sb.append("/");
		sb.append(numFetched);
		sb.append(")");
		return sb.toString();	
	}	
	
	
	private byte[] getBytesFromUrl(String url) {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = new URL(url).openStream();
			byte[] byteChunk = new byte[4096]; 												
			int n;
			while ((n = is.read(byteChunk)) > 0) {
				bais.write(byteChunk, 0, n);
			}
			return bais.toByteArray();
		} catch (IOException e) {
			Log.e(VideoUpdater.class.getName(), "Exception", e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				Log.e(VideoUpdater.class.getName(), "Exception", e);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object numOfUpdated) {
		if(allReadyRunning) return;
		super.onPostExecute(numOfUpdated);
		mla.notifyDataSetChanged();
		if (dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		
		if(numOfUpdated instanceof String && !((String)numOfUpdated).isEmpty()) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
	 
			// set title
			alertDialogBuilder.setTitle("Status");
	 
				// set dialog message
			alertDialogBuilder.setMessage((String)numOfUpdated)
				.setCancelable(false)
				.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();					
					}
				  }); 
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
		
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}		
	}
}
