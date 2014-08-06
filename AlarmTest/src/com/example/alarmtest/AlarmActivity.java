package com.example.alarmtest;


import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;


public class AlarmActivity extends Activity {
	private static String TAG = "AlarmActivity";
	private PlaceholderFragment phf = new PlaceholderFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		
		setContentView(R.layout.activity_alarm);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, phf).commit();
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		phf.readDBData();
		/*
		phf.handler.postDelayed(phf.tweet, phf.timer * 60 * 1000);
		*/
        phf.rt.play();
	}
	
	public static class PlaceholderFragment extends Fragment implements OnClickListener {
		private Button alarmStop;
		private MySQLiteHelper dbHelper;
        private SQLiteDatabase db;
        private int timer;
        private String account, message;
        private static final String ConsumerKey = "yCdQfyICPOERmjeCYfDzqpiwn";
        private static final String ConsumerSecret = "rQ9J3exh3KtIMlqh5el02bnMLszZETsACwTmqDsb0LQJ5rLa0M";
        private static final String AccessToken = "2675803550-af8DOnau91nUHLRmeWOhF4FPhyr1QGNsC2k2Jp0";
        private static final String AccessTokenSecret = "pPrnS2loPWoETQrOO0hEcEErr80PLpQtB3sJNHMI9ZyVS";
        private Ringtone rt = null;
        private String btnName;
        /*
        Handler handler = new Handler();
        Runnable tweet = new Runnable() {
        	@Override
        	public void run() {
        		Log.i(TAG, "twittertest");
        		
        		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());		
        	    Twitter twitter = TwitterFactory.getSingleton();
        	    twitter.setOAuthConsumer(ConsumerKey, ConsumerSecret);
        	    AccessToken at = new twitter4j.auth.AccessToken(AccessToken, AccessTokenSecret);
        	    twitter.setOAuthAccessToken(at);
        	    
        	    try {
        	        twitter.updateStatus("@" + account + " " + message);
        	    } catch (TwitterException e) {
        	        e.printStackTrace();
        	        if(e.isCausedByNetworkIssue()){
        	        }
        	    }finally {
        	    	if(rt != null) {
        	    		rt.stop();
        	    	}
        	    	getActivity().finish();
        	    }
        	}
        };
        */
	    
	    public PlaceholderFragment() {
		}
	        
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
	            	View rootView = inflater.inflate(R.layout.fragment_alarm,
	            			container, false);
	            	
	            	Intent intent = getActivity().getIntent();
	            	btnName = intent.getStringExtra("keyword");
	            	
	            	alarmStop = (Button) rootView.findViewById(R.id.alarmstop);
	            	Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	                rt = RingtoneManager.getRingtone(getActivity().getApplicationContext(), uri); 
	                alarmStop.setOnClickListener(this);
	                
	            	return rootView;
		}
		
		@Override
		public void onAttach(Activity act) {
			super.onAttach(act);
			dbHelper = new MySQLiteHelper(act.getApplicationContext());
			db = dbHelper.getReadableDatabase();
		}
		
		@Override
		public void onClick(View v) {
			rt.stop();
			getActivity().finish();
		}
		
		private void readDBData() {
			Cursor cursor = null;
			
			try {
                cursor = db.query("alarm", 
                        new String[]{"timer", "account", "message"}, 
                        "name = ?", new String[]{"" + btnName}, 
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
            int indexTimer = cursor.getColumnIndex("timer");
            int indexAccount = cursor.getColumnIndex("account");
            int indexMessage = cursor.getColumnIndex("message"); 
            
            while(cursor.moveToNext()){
                timer = cursor.getInt(indexTimer);
                account = cursor.getString(indexAccount);
                message = cursor.getString(indexMessage);
            }
        }
	}
}
