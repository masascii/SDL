package com.example.alarmtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	public MySQLiteHelper(Context context){
        super(context, "alarm.db", null, 1);
    }
	
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL(
                 "create table alarm("
                 + "name text not null, "
                 + "hour integer not null, "
                 + "minutes integer not null, " 
                 + "timer integer not null, "
                 + "switchstate integer not null, "
                 + "account text not null, "
                 + "message text not null)");
		 db.execSQL("insert into alarm(name, hour, minutes, timer, switchstate, account, message) values('alarm_set1', 0, 0, 1, 0, 'alarmtest', '電話して起こして')");
		 db.execSQL("insert into alarm(name, hour, minutes, timer, switchstate, account, message) values('alarm_set2', 0, 0, 1, 0, 'alarmtest', '電話して起こして')");
		 db.execSQL("insert into alarm(name, hour, minutes, timer, switchstate, account, message) values('alarm_set3', 0, 0, 1, 0, 'alarmtest', '電話して起こして')");
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + "alarm");
	    onCreate(db);
	}
	
}
