package com.example.alarmtest;

import java.util.Calendar;
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

public class MainActivity extends Activity {
	
	private static String TAG = "MainActivity";
	private PlaceholderFragment phf = new PlaceholderFragment();
	private static final int SETACTIVITY = 1001;
	private static final int MAINACTIVITY = 1002;
	private static final int CANCELED = 1003;
	
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
		Log.i(TAG, "onActivityResult-MainActivity");
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
        phf.dbHelper = new MySQLiteHelper(getApplication());
        phf.db = phf.dbHelper.getWritableDatabase();
        Log.i(TAG, "onResume");
        phf.readDBData();
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
		private Button alarmSetBtn1, alarmSetBtn2, alarmSetBtn3;
	    private Switch switch1, switch2, switch3;
        private boolean isStartAlarm = true;
        private boolean isDBChange = true;
        private MySQLiteHelper dbHelper;
        private SQLiteDatabase db;
        private String btnName;
        private AlarmManager alarmManager;
        
	    public PlaceholderFragment() {
		}
	    
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
	            	View rootView = inflater.inflate(R.layout.fragment_main,
	            			container, false);
	            	Log.i(TAG, "onCreateView-Fragment");
	            	
	            	alarmSetBtn1 = (Button) rootView.findViewById(R.id.alarm_set1);
	            	alarmSetBtn2 = (Button) rootView.findViewById(R.id.alarm_set2);
	            	alarmSetBtn3 = (Button) rootView.findViewById(R.id.alarm_set3);
	                switch1 = (Switch) rootView.findViewById(R.id.switch1);
	                switch2 = (Switch) rootView.findViewById(R.id.switch2);
	                switch3 = (Switch) rootView.findViewById(R.id.switch3);
	                
	                alarmSetBtn1.setOnClickListener(this);
	                alarmSetBtn2.setOnClickListener(this);
	                alarmSetBtn3.setOnClickListener(this);
	                switch1.setOnCheckedChangeListener(this);
	                switch2.setOnCheckedChangeListener(this);
	                switch3.setOnCheckedChangeListener(this);
	                
	            	return rootView;
		}
		
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			Log.i(TAG, "onActivityResult-Fragment");
			
	    	if(requestCode == SETACTIVITY) {
				if(resultCode == MAINACTIVITY) {
					btnName = data.getStringExtra("keyword");
					if(readDBSwitchState(btnName)) {
						stopAlarm(btnName);
						startAlarm(btnName);
					}else {
						updateSwitchState(btnName, 1);
						startAlarm(btnName);
					}
				}else if(resultCode == CANCELED) {
					Log.i(TAG, data.getStringExtra("keyword"));
				}
			}
	    }
		
		@Override
		public void onAttach(Activity act) {
			super.onAttach(act);
			Log.i(TAG, "onAttach-Fragment");
		}
		
		@Override
		public void onClick(View v) {
			Log.i(TAG,"onClick");
			doSetting(v.getId());
		}
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Log.i(TAG, "onCheckedChanged");
			
			if(buttonView.getId() == R.id.switch1) {
				btnName = "alarm_set1";
			}else if(buttonView.getId() == R.id.switch2) {
				btnName = "alarm_set2";
			}else if(buttonView.getId() == R.id.switch3){
				btnName = "alarm_set3";
			}else {
				Log.i(TAG, "SwitchError");
			}
			
			if(isChecked) {
				if(isDBChange) {
					updateSwitchState(btnName, 1);
				}
				startAlarm(btnName);
			}else {
				updateSwitchState(btnName, 0);
				stopAlarm(btnName);
			}
		}
		
		public void readDBData() {
			Log.i(TAG, "readDBData");
			readTime();
			readSwitch();
		}
		
		public void readTime() {
			Log.i(TAG, "readTime");
			int[] time1 = readDBTime("alarm_set1");
			int[] time2 = readDBTime("alarm_set2");
			int[] time3 = readDBTime("alarm_set3");
			
			alarmSetBtn1.setText(time1[0] + "時" + time1[1] + "分");
	        alarmSetBtn2.setText(time2[0] + "時" + time2[1] + "分");
	        alarmSetBtn3.setText(time3[0] + "時" + time3[1] + "分");
		}
		
		public int[] readDBTime(String btnName) {
			Log.i(TAG, "readDBTime");
            Cursor cursor = null;
            try {
                cursor = db.query("alarm", 
                        new String[]{"hour", "minutes", "timer"}, 
                        "name = ?", new String[]{"" + btnName}, 
                        null, null, null);
                cursor.moveToFirst();
 
                return readDBTimeCursor(cursor);
            }finally {
                if(cursor != null){
                    cursor.close();
                }
            }
		}
		
		public int[] readDBTimeCursor(Cursor cursor) {
			Log.i(TAG, "readDBTimeCursor");
            int[] result = {0, 0, 0};
            
            int indexHour = cursor.getColumnIndex("hour");
            int indexMinutes  = cursor.getColumnIndex("minutes");
            int indexTimer = cursor.getColumnIndex("timer");
            	
            result[0] = cursor.getInt(indexHour);
            result[1] = cursor.getInt(indexMinutes);
            result[2] = cursor.getInt(indexTimer);
            
            return result;
        }
		
		public void readSwitch() {
			Log.i(TAG, "readSwitch");
			isStartAlarm = false;
			isDBChange = false;
			
			if(readDBSwitchState("alarm_set1")) {
				switch1.setChecked(true);
			}
			if(readDBSwitchState("alarm_set2")) {
				switch2.setChecked(true);
			}
			if(readDBSwitchState("alarm_set3")) {
				switch3.setChecked(true);
			}
			isStartAlarm = true;
			isDBChange = true;			
		}
		
		public boolean readDBSwitchState(String btnName) {
			Log.i(TAG, "readDBSwitchState");
			Cursor cursor = null;
            try {
            	cursor = db.query("alarm", 
                        new String[]{"switchstate"}, 
                        "name = ?", new String[]{"" + btnName}, 
                        null, null, null);
                cursor.moveToFirst();
                return readDBSwitchStateCursor(cursor);
            }finally {
                if(cursor != null){
                    cursor.close();
                }
            }
		}
		
		public boolean readDBSwitchStateCursor(Cursor cursor) {
			Log.i(TAG, "readDBSwitchStateCursor");
            int indexSwitchState = cursor.getColumnIndex("switchstate");
            
            int switchState = cursor.getInt(indexSwitchState);
            
            return switchState > 0 ? true : false;
        }
		
		public void doSetting(int id) {
		     Log.i(TAG, "doSetting");
		     
		     if(id == R.id.alarm_set1) {
				btnName = "alarm_set1";
		     }else if(id == R.id.alarm_set2) {
				btnName = "alarm_set2";
		     }else {
				btnName = "alarm_set3";
		     }
		     
			 Intent intent = new Intent(getActivity(), SetActivity.class);
			 intent.putExtra("keyword",btnName);
		     startActivityForResult(intent, SETACTIVITY);
		 }
		
		public void updateSwitchState(String btnName, int switchstate) {
			Log.i(TAG, "updateSwitchState");
			ContentValues val = new ContentValues();
            val.put("switchstate", switchstate);
            
            db.update("alarm", 
                    val,
                    "name = ?",
                    new String[]{"" + btnName});
		}
		
		public void startAlarm(String btnName) {
			if(isStartAlarm == true) {
				Log.i(TAG, "startAlarm");
				int[] time;
				alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
				Calendar calendarAlarm = Calendar.getInstance();
				calendarAlarm.setTimeInMillis(System.currentTimeMillis());
				time = readDBTime(btnName);
				calendarAlarm.set(Calendar.HOUR_OF_DAY, time[0]);
			    calendarAlarm.set(Calendar.MINUTE, time[1]);
			    calendarAlarm.set(Calendar.SECOND, 0);
			    calendarAlarm.set(Calendar.MILLISECOND, 0);
			    alarmManager.set(AlarmManager.RTC_WAKEUP,
			    		calendarAlarm.getTimeInMillis(),
		                getPendingIntent(btnName));
			}
		}
		
		public void stopAlarm(String btnName) {
			Log.i(TAG, "stopAlarm");
			alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(getPendingIntent(btnName));
		}
		
		private PendingIntent getPendingIntent(String btnName) {
			Intent intent = 
	        		new Intent(getActivity().getApplicationContext(), AlarmActivity.class);
			intent.putExtra("keyword", btnName);
			if(btnName.equals("alarm_set1")) {
		        PendingIntent pendingIntent1 = 
		        		PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, 0);
			    return pendingIntent1;
			}else if(btnName.equals("alarm_set2")) {
		        PendingIntent pendingIntent2 = 
		        		PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, 0);
			    return pendingIntent2;
			}else {
		        PendingIntent pendingIntent3 = 
		        		PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, 0);
			    return pendingIntent3;
			}
	    }		
	}
}
