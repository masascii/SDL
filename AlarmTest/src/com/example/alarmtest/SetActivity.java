package com.example.alarmtest;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class SetActivity extends Activity {
	private static String TAG = "SetActivity";
	private PlaceholderFragment phf = new PlaceholderFragment();
	private static final int MAINACTIVITY = 1002;
	private static final int CANCELED = 1003;
	
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
		phf.dbHelper = new MySQLiteHelper(getApplication());
        phf.db = phf.dbHelper.getWritableDatabase();
		
		phf.readDBData();
		
		phf.timePicker.setCurrentHour(phf.hour);
		phf.timePicker.setCurrentMinute(phf.minutes);
		
		phf.numberPicker.setMaxValue(5);
		phf.numberPicker.setMinValue(1);
		phf.numberPicker.setValue(phf.timer);
		
		phf.account.setText(phf.accountText);
		phf.message.setText(phf.messageText);
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        //phf.db.close();
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
	
	public static class PlaceholderFragment extends Fragment implements OnClickListener {
		private MySQLiteHelper dbHelper;
        private SQLiteDatabase db;
        private String btnName;
        private TimePicker timePicker;
        private NumberPicker numberPicker;
        private int hour, minutes, timer;
        private String accountText, messageText;
        private EditText account, message;
		
		
		public PlaceholderFragment() {
		}
	        
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
	            	View rootView = inflater.inflate(R.layout.fragment_setting,
	            			container, false);
	            	
	            	Intent intent = getActivity().getIntent();
	            	btnName = intent.getStringExtra("keyword");
	            	
	            	timePicker = (TimePicker) rootView.findViewById(R.id.time_picker);
	            	numberPicker = (NumberPicker) rootView.findViewById(R.id.number_picker);
	            	account = (EditText) rootView.findViewById(R.id.twitteraccount);
	            	message = (EditText) rootView.findViewById(R.id.twittermessage);
	            	Button okBtn = (Button) rootView.findViewById(R.id.alarm_set);
	            	Button cancelBtn = (Button) rootView.findViewById(R.id.cancel_set);
	            	
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
			Log.i(TAG, "onAttach");
		}
		
		public void doSetting() {
			Log.i(TAG, "doSetting");
			
			hour = timePicker.getCurrentHour();
			minutes = timePicker.getCurrentMinute();
			timer = numberPicker.getValue(); 
			account.selectAll();
			message.selectAll();
			accountText = account.getText().toString();
			messageText = message.getText().toString();
			updateEntry();
			
			Intent data = new Intent();
			data.putExtra("keyword",btnName);
			getActivity().setResult(MAINACTIVITY, data);
			getActivity().finish();
		}
		
		public void doCancel() {
			Log.i(TAG, "doCancel");
			Intent data = new Intent();
			data.putExtra("keyword","Canceled");
			getActivity().setResult(CANCELED, data);
			getActivity().finish();
		}
		
		private void updateEntry() {
			Log.i(TAG, "updateEntry");
			ContentValues val = new ContentValues();
		
			try {
				db.beginTransaction();
				val.put("hour", hour);
				val.put("minutes" , minutes);
				val.put("timer", timer);
				val.put("account", accountText);
				val.put("message", messageText);
 
				db.update("alarm", 
						val,
						"name = ?",
						new String[]{"" + btnName});
            
				db.setTransactionSuccessful();
			}finally {
				readDBData();
				
				db.endTransaction();
			}
        }
        
        private void readDBData() {
        	Log.i(TAG,"readDBData");
            Cursor cursor = null;
            try {
                cursor = db.query("alarm", 
                        new String[] {"hour", "minutes", "timer", "account", "message"}, 
                        "name = ?", new String[] {"" + btnName}, 
                        null, null, null);
                cursor.moveToFirst();
                
                readDBDataCursor(cursor);
            }finally {
                if(cursor != null) {
                    cursor.close();
                }
            }
        }
        
        private void readDBDataCursor(Cursor cursor) {
        	Log.i(TAG, "readDBDataCursor");
            int indexHour = cursor.getColumnIndex("hour");
            int indexMinutes = cursor.getColumnIndex("minutes");
            int indexTimer = cursor.getColumnIndex("timer");
            int indexAccount = cursor.getColumnIndex("account");
            int indexMessage = cursor.getColumnIndex("message");

            hour = cursor.getInt(indexHour);
            minutes = cursor.getInt(indexMinutes);
            timer = cursor.getInt(indexTimer);
            accountText = cursor.getString(indexAccount);
            messageText = cursor.getString(indexMessage);
        }
	}	
}
