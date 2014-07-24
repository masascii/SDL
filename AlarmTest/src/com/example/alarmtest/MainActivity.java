package com.example.alarmtest;

import java.util.Calendar;

import com.example.alarmtest.R.layout;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static String TAG = "MainActivity";
	private PlaceholderFragment phf = new PlaceholderFragment();
	private static final int SETACTIVITY = 1001;
	private String text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		
		
		setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, phf).commit();
        }
        
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "aaaaaaaaaaaa");
		
		if(requestCode == SETACTIVITY) {
			if(resultCode == 1002) {
				text = data.getStringExtra("keyword");
				if(text.equals("alarm_set")) {
					phf.time = phf.searchByName(phf.db);
					if(phf.searchByNameSwitch(phf.db) == true) {
						phf.stopAlarm(phf.isStopAlarm);
						phf.startAlarm(phf.isStartAlarm);
					}else {
						phf.startAlarm(phf.isStartAlarm);
						phf.isStartAlarm = false;
						(phf.toggleSwitch).setChecked(true);
						phf.isStartAlarm = true;
					}
					
				}
			}
		}
		
	}
	
	    
	
	@Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        // TextView resultView = (TextView)findViewById(R.id.first_value_view);
        // resultView.setText(result);;
        if(phf.searchByNameSwitch(phf.db) == true) {
        	phf.isStartAlarm = false;
        	phf.toggleSwitch.setChecked(true);
        	phf.isStartAlarm = true;
        }else if(phf.searchByNameSwitch(phf.db) == false){
        	phf.isStopAlarm = false;
        	phf.toggleSwitch.setChecked(false);
        	phf.isStopAlarm = true;
        }
        phf.alarmSetBtn.setText(phf.time[0] + "時" + phf.time[1] + "分");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static class PlaceholderFragment extends Fragment implements OnClickListener, CompoundButton.OnCheckedChangeListener {
		Button alarmSetBtn;
	    Switch toggleSwitch;
	    private View rootView;
	    private AlarmManager alarmManager;
	    Calendar calendarAlarm;
        boolean isStartAlarm = true;
        boolean isStopAlarm = true;
        int[] time = {0, 0, 0};
        private MySQLiteHelper dbHelper;
        private SQLiteDatabase db;
        PendingIntent pi;
		
	    public PlaceholderFragment() {
		}
	        
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	Log.v(TAG, "ccc");
	    	String text;
	    	if(requestCode == SETACTIVITY) {
				if(resultCode == 1002) {
					text = data.getStringExtra("keyword");
					if(text.equals("alarm_set")) {
						time = searchByName(db);
						if(searchByNameSwitch(db) == true) {
							stopAlarm(isStopAlarm);
							startAlarm(isStartAlarm);
						}else {
							startAlarm(isStartAlarm);
							isStartAlarm = false;
							toggleSwitch.setChecked(true);
							isStartAlarm = true;
						}
						
					}
				}
			}
	    }
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
	            	rootView = inflater.inflate(R.layout.fragment_main,
	            			container, false);
	            	
	            	
	            	alarmSetBtn = (Button) rootView.findViewById(R.id.alarm_set);
	                toggleSwitch = (Switch) rootView.findViewById(R.id.toggleswitch);
	                
	                alarmSetBtn.setOnClickListener(this);
	                toggleSwitch.setOnCheckedChangeListener(this);
	                
	            	return rootView;
		}
		@Override
		public void onAttach(Activity act) {
			super.onAttach(act);
			dbHelper = new MySQLiteHelper(act.getApplicationContext());
			db = dbHelper.getWritableDatabase();
			time = searchByName(db);
		}
		

		
		@Override
		public void onClick(View v) {
			Log.i(TAG,"onClick");
			if(v.getId() == R.id.alarm_set) {
				doSetting();
			}
		}
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Log.i(TAG, "onCheckedChanged");
			
			if(isChecked == true) {
				updateEntry(db, "on");
				startAlarm(isStartAlarm);
				isStartAlarm = true;
			}else if(isChecked == false) {
				updateEntry(db, "off");
				stopAlarm(isStopAlarm);
			}
			
		}
		
		public void doSetting() {
		     Log.i(TAG,"doSetting");
			 Intent intent = new Intent(getActivity(), SetActivity.class);
			 intent.putExtra("keyword","alarm_set");
		     startActivityForResult(intent, SETACTIVITY);
		     
		 }
		
		public void startAlarm(boolean isStartAlarm) {
			if(isStartAlarm == true) {
				alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
				calendarAlarm = Calendar.getInstance();
				calendarAlarm.setTimeInMillis(System.currentTimeMillis());
				
				calendarAlarm.set(Calendar.HOUR_OF_DAY, time[0]);
			    calendarAlarm.set(Calendar.MINUTE, time[1]);
			    calendarAlarm.set(Calendar.SECOND, 0);
			    calendarAlarm.set(Calendar.MILLISECOND, 0);
			    alarmManager.set(AlarmManager.RTC_WAKEUP,
			    		calendarAlarm.getTimeInMillis(),
		                getPendingIntent());
			}
		}
		
		public void stopAlarm(boolean isStopAlarm) {
			if(isStopAlarm == true) {
				alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
				alarmManager.cancel(getPendingIntent());
			}
		}
		
		private PendingIntent getPendingIntent() {
	        Intent intent = 
	        		new Intent(getActivity().getApplicationContext(), AlarmActivity.class);
	        PendingIntent pendingIntent = 
	        		PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, 0);
		    return pendingIntent;
	    }
		
		private int[] searchByName(SQLiteDatabase db){
            // Cursorを確実にcloseするために、try{}～finally{}にする
            Cursor cursor = null;
            try{
                // name_book_tableからnameとageのセットを検索する
                // ageが指定の値であるものを検索
                cursor = db.query("alarm", 
                        new String[]{"hour", "minutes", "timer"}, 
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
            int[] result = {0, 0, 0};
 
            // まず、Cursorからnameカラムとageカラムを
            // 取り出すためのインデクス値を確認しておく
            int indexHour = cursor.getColumnIndex("hour");
            int indexMinutes  = cursor.getColumnIndex("minutes");
            int indexTimer = cursor.getColumnIndex("timer");
 
            // ↓のようにすると、検索結果の件数分だけ繰り返される
            while(cursor.moveToNext()){
                // 検索結果をCursorから取り出す
            	int hour = cursor.getInt(indexHour);
                int minutes  = cursor.getInt(indexMinutes);
                int timer = cursor.getInt(indexTimer);
                result[0] = hour;
                result[1] = minutes;
                result[2] = timer;
            }
            return result;
        }
        
        private boolean searchByNameSwitch(SQLiteDatabase db){
            // Cursorを確実にcloseするために、try{}～finally{}にする
            Cursor cursor = null;
            try{
                // name_book_tableからnameとageのセットを検索する
                // ageが指定の値であるものを検索
                cursor = db.query("alarm", 
                        new String[]{"onoff"}, 
                        "name = ?", new String[]{"" + "alarm_set"}, 
                        null, null, null );
 
                // 検索結果をcursorから読み込んで返す
                return readCursorSwitch(cursor);
            }
            finally{
                // Cursorを忘れずにcloseする
                if( cursor != null ){
                    cursor.close();
                }
            }
        }
 
 
        /** 検索結果の読み込み */
        private boolean readCursorSwitch(Cursor cursor) {
            boolean result = false;
 
            // まず、Cursorからnameカラムとageカラムを
            // 取り出すためのインデクス値を確認しておく
            int indexOnOff = cursor.getColumnIndex("onoff");
            
 
            // ↓のようにすると、検索結果の件数分だけ繰り返される
            while(cursor.moveToNext()){
                // 検索結果をCursorから取り出す
            	String onoff = cursor.getString(indexOnOff);
                if(onoff.equals("on")) {
                	result = true;
                }else {
                	result = false;
                }
            }
            return result;
        }
        
        private void updateEntryTime(SQLiteDatabase db) {
        	ContentValues val = new ContentValues();
        }
        
        private void updateEntry(SQLiteDatabase db, String onoff) {
			ContentValues val = new ContentValues();
            val.put("onoff", onoff);
 
            
            db.update("alarm", 
                    val,
                    "name = ?",
                    new String[]{"" + "alarm_set"});
		}
	}
}
