package org.ea.ytlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ea.utils.DatabaseHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainListAdapter extends ArrayAdapter<String> {
	private List<View> views = null;

	private final Activity activity;
	private DatabaseHelper dh = null;
	private boolean showWatched = false;
	
	public MainListAdapter(Activity activity, String[] values) {
		super(activity.getApplicationContext(), R.layout.videoitem, values);
		dh = DatabaseHelper.get(activity.getApplicationContext());
		this.activity = activity;
	}

	private void createViews(ViewGroup parent) {
		views = new ArrayList<View>();
		LayoutInflater inflater = (LayoutInflater) activity
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);

		SQLiteDatabase db = dh.open();
		Cursor c = db.rawQuery("select * from " + DatabaseHelper.TABLE_VIDEOS
				+ " where watched "+getShowWatchedString()+" 0 order by published desc;", null);

		while (c.moveToNext()) {
			View rowView = inflater.inflate(R.layout.videoitem, parent, false);

			final String videoId = c.getString(c.getColumnIndex("videoId"));
			
			TextView videoNameView = (TextView) rowView.findViewById(R.id.videoName);
			String videoName = c.getString(c.getColumnIndex("title"));
			videoNameView.setText(videoName);

			ImageView videoImageView = (ImageView) rowView.findViewById(R.id.videoImage);
			byte[] imageData = c.getBlob(c.getColumnIndex("image"));			
			
			Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);			
			videoImageView.setImageBitmap(imageBitmap);			
			videoImageView.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId)); 
	            	intent.putExtra("VIDEO_ID", videoId); 
	            	activity.startActivity(intent);             }
	        });
			imageBitmap = null;

			Button watchedButton = (Button) rowView.findViewById(R.id.watchedButton);
			
			watchedButton.setText(this.isShowWatched() ? "Set as new" : "Watched");
			
			watchedButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	        		SQLiteDatabase db = dh.open();
	        		ContentValues values = new ContentValues();
	    			
	        		values.put("watched", (isShowWatched() ? 0l : System.currentTimeMillis()));            	
	            	db.update(DatabaseHelper.TABLE_VIDEOS, values, "videoId = ?", new String[] {videoId});
	            	db.close();
	            	notifyDataSetChanged();
	            }
	        });
			
			
			long posted = c.getLong(c.getColumnIndex("published"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			TextView textView2 = (TextView) rowView.findViewById(R.id.videoDate);
			textView2.setText(sdf.format(new Date(posted)));

			views.add(rowView);
		}

		db.close();
	}

	@Override
	public int getCount() {
		int size = 0;
		if (views == null) {
			SQLiteDatabase db = dh.open();
			Cursor c = db.rawQuery("select count(*) from " + DatabaseHelper.TABLE_VIDEOS + " where watched "+getShowWatchedString()+" 0;", null);
			c.moveToFirst();
			size = c.getInt(0);
			db.close();
		} else {
			size = views.size();
		}
		return size;
	}
	
	public String getShowWatchedString() {
		return isShowWatched() ? "!=" : "=";
	}
	
	public boolean isShowWatched() {
		return showWatched;
	}

	public void setShowWatched(boolean showWatched) {
		this.showWatched = showWatched;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (views == null) createViews(parent);
		return views.get(position);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		views = null;
	}
}