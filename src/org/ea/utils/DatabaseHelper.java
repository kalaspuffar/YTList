package org.ea.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_VIDEOS = "videos";
	
	private static final String DATABASE_NAME = "ytsane.db";
	private static final int DATABASE_VERSION = 1;
	
	private static DatabaseHelper databaseHelper = null;
		
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static DatabaseHelper get(Context context) {
		if(databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context.getApplicationContext());
		}
		return databaseHelper;
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		StringBuilder sb = new StringBuilder();
		sb.append("create table if not exists ");
		sb.append(TABLE_VIDEOS);
		sb.append(" (videoId varchar(20), title varchar(255), published int, image blob, watched int);");		
		database.execSQL(sb.toString());
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
		onCreate(db);
	}
		
	public SQLiteDatabase open() throws SQLException {
		return this.getWritableDatabase();
	}
}
