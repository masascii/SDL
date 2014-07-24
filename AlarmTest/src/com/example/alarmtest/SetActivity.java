package com.example.alarmtest;

import java.util.Calendar;

import com.example.alarmtest.MainActivity.PlaceholderFragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

public class SetActivity extends Activity {
	private static String TAG = "SetActivity";
	private PlaceholderFragment phf = new PlaceholderFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.activity_setting);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, phf).commit();
        }
        
        
	}

	@Override
	protected void onResume() {
		super.onResume();
		phf.timePicker = (TimePicker) this.findViewById(R.id.time_picker);
		phf.time = phf.searchByName(phf.db);
		phf.timePicker.setCurrentHour(phf.time[0]);
		phf.timePicker.setCurrentMinute(phf.time[1]);
		
		phf.numberPicker.setMaxValue(5);
		phf.numberPicker.setMinValue(1);
		phf.numberPicker.setValue(1);
		
		phf.account.setText(phf.searchByNameAccountMessage(phf.db)[0]);
		phf.message.setText(phf.searchByNameAccountMessage(phf.db)[1]);
	}
	
	public static class PlaceholderFragment extends Fragment implements OnClickListener{
        
		private MySQLiteHelper dbHelper;
        private SQLiteDatabase db;
        String name;
        TimePicker timePicker;
        NumberPicker numberPicker;
        int[] time = {0, 0};
        Intent intent;
        EditText account, message;
		
		
		public PlaceholderFragment() {
		}
	        
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
	            	View rootView = inflater.inflate(R.layout.fragment_setting,
	            			container, false);
	            	intent = getActivity().getIntent();
	            	name = intent.getStringExtra("keyword");
	            	account = (EditText) rootView.findViewById(R.id.twitteraccount);
	            	message = (EditText) rootView.findViewById(R.id.twittermessage);
	            	Button okBtn = (Button) rootView.findViewById(R.id.alarm_set);
	            	Button cancelBtn = (Button) rootView.findViewById(R.id.cancel_set);
	            	numberPicker = (NumberPicker) rootView.findViewById(R.id.number_picker);
	            	
	                okBtn.setOnClickListener(this);
	                cancelBtn.setOnClickListener(this);
	          
	            	return rootView;
		}
		
		@Override
		public void onClick(View v) {
			Log.i(TAG,"onClick");
			switch(v.getId()) {
	        case R.id.alarm_set:
	        	doSetting();
	            break;
	        case R.id.cancel_set:
	        	doCancel();
	            break;
	        }
		}
		
		
		@Override
		public void onAttach(Activity act) {
			super.onAttach(act);
			dbHelper = new MySQLiteHelper(act.getApplication());
			db = dbHelper.getWritableDatabase();
		}
		
		public void doSetting() {
			Log.i(TAG, "doSetting in SetActivity");
			
			int hour = timePicker.getCurrentHour();
			int min = timePicker.getCurrentMinute();
			numberPicker = (NumberPicker) getActivity().findViewById(R.id.number_picker);
			numberPicker.setMaxValue(5);
			numberPicker.setMinValue(1);
			numberPicker.setValue(1);
			int timer = numberPicker.getValue(); 
			account.selectAll();
			message.selectAll();
			updateEntry(db, name, hour , min, timer, account.getText().toString(), message.getText().toString());
			
			Intent data = new Intent();
			
			data.putExtra("keyword",name);
			getActivity().setResult(1002, data);
			getActivity().finish();
		}
		
		public void doCancel() {
			Log.i(TAG, "doCancel");
			getActivity().finish();
		}
		
		private void updateEntry(SQLiteDatabase db, String targetName, int newHour, int newMinutes, int newTimer, String account, String message) {
            
            ContentValues val = new ContentValues();
            val.put("hour", newHour);
            val.put("minutes" , newMinutes);
            val.put("timer", newTimer);
            val.put("account", account);
            val.put("message", message);
 
            db.update("alarm", 
                    val,
                    "name = ?",
                    new String[]{"" + targetName});
        }
		
		private int[] searchByName(SQLiteDatabase db){
            // Cursorを確実にcloseするために、try{}～finally{}にする
            Cursor cursor = null;
            try{
                // name_book_tableからnameとageのセットを検索する
                // ageが指定の値であるものを検索
                cursor = db.query("alarm", 
                        new String[]{"hour", "minutes"}, 
                        "name = ?", new String[]{"" + "alarm_set"}, 
                        null, null, null );
 
                // 検索結果をcursorから読み込んで返す
                return readCursor(cursor);
            }
            finally{
                // Cursorを忘れずにcloseする
                if( cursor != null ){
                    cursor.close();
                }
            }
        }
 
 
        /** 検索結果の読み込み */
        private int[] readCursor(Cursor cursor) {
            int[] result = {0, 0};
 
            // まず、Cursorからnameカラムとageカラムを
            // 取り出すためのインデクス値を確認しておく
            int indexHour = cursor.getColumnIndex("hour");
            int indexMinutes  = cursor.getColumnIndex("minutes");
 
            // ↓のようにすると、検索結果の件数分だけ繰り返される
            while(cursor.moveToNext()){
                // 検索結果をCursorから取り出す
            	int hour = cursor.getInt(indexHour);
                int minutes  = cursor.getInt(indexMinutes);
                result[0] = hour;
                result[1] = minutes;
            }
            return result;
        }
        
        private String[] searchByNameAccountMessage(SQLiteDatabase db){
            // Cursorを確実にcloseするために、try{}～finally{}にする
            Cursor cursor = null;
            try{
                // name_book_tableからnameとageのセットを検索する
                // ageが指定の値であるものを検索
                cursor = db.query("alarm", 
                        new String[]{"account", "message"}, 
                        "name = ?", new String[]{"" + "alarm_set"}, 
                        null, null, null );

                // 検索結果をcursorから読み込んで返す
                return readAccountMessageCursor(cursor);
            }
            finally{
                // Cursorを忘れずにcloseする
                if( cursor != null ){
                    cursor.close();
                }
            }
        }


        /** 検索結果の読み込み */
        private String[] readAccountMessageCursor(Cursor cursor) {
            String[] result = {"", ""};

            // まず、Cursorからnameカラムとageカラムを
            // 取り出すためのインデクス値を確認しておく
            int indexAccount = cursor.getColumnIndex("account");
            int indexMessage = cursor.getColumnIndex("message");

            // ↓のようにすると、検索結果の件数分だけ繰り返される
            while(cursor.moveToNext()){
                // 検索結果をCursorから取り出す
            	String account = cursor.getString(indexAccount);
            	String message = cursor.getString(indexMessage);
                
                result[0] = account;
                result[1] = message;
            }
            return result;
        }
        
        
	}
	
	
	
}
