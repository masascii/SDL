package com.example.alarmtest;


import twitter4j.AsyncTwitterFactory;
import twitter4j.Twitter;
import twitter4j.AsyncTwitter;
import twitter4j.auth.AccessToken;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
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
import android.widget.Toast;


public class AlarmActivity extends Activity {
	private static String TAG = "AlarmActivity";
	private PlaceholderFragment phf = new PlaceholderFragment();
	//private MediaPlayer mp;
	//public Ringtone rt;
	
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
		//Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        //phf.rt = RingtoneManager.getRingtone(this, uri);
        phf.rt.play();
	}
	
	
	
	public static class PlaceholderFragment extends Fragment implements OnClickListener {
		Button alarmStop;
		String name;
		private MySQLiteHelper dbHelper;
        private SQLiteDatabase db;
        int timer;
        String account, message;
        private static final String ConsumerKey = "yCdQfyICPOERmjeCYfDzqpiwn";
        private static final String ConsumerSecret = "rQ9J3exh3KtIMlqh5el02bnMLszZETsACwTmqDsb0LQJ5rLa0M";
        private static final String AccessToken = "2675803550-af8DOnau91nUHLRmeWOhF4FPhyr1QGNsC2k2Jp0";
        private static final String AccessTokenSecret = "pPrnS2loPWoETQrOO0hEcEErr80PLpQtB3sJNHMI9ZyVS";
        Ringtone rt;
        
        
        Handler handler = new Handler();
        Runnable tweet = new Runnable() {
        	@Override
        	public void run() {
        		Log.i(TAG, "twittertest");
        		/*
        		TwitterListener listener = new TwitterAdapter() {
        			@Override
        			public void updatedStatus(Status status) {
        				System.out.println("Successfully updated the status to [" +
        			                   status.getText() + "].");
        			}

        			@Override
        			public void onException(TwitterException e, int method) {
        				if (method == TwitterMethods.UPDATE_STATUS) {
        					e.printStackTrace();
        			    } else {
        			        throw new AssertionError("Should not happen");
        			    }
        			}
        		};
        		
        		AsyncTwitterFactory factory = new AsyncTwitterFactory();
        	    AsyncTwitter asyncTwitter = factory.getInstance();
        	    asyncTwitter.addListener(listener);
        	    asyncTwitter.updateStatus("@" + account + " " + message);
        		*/
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
        	             Toast.makeText(getActivity(), "ネットワークの問題です", Toast.LENGTH_LONG);
        	        }
        	    }finally {
        	    	rt.stop();
            		getActivity().finish();
        	    }
        		
        		
        		/*
        		ConfigurationBuilder cb = new ConfigurationBuilder();
        		cb.setDebugEnabled(true)
        		  .setOAuthConsumerKey(ConsumerKey)
        		  .setOAuthConsumerSecret(ConsumerSecret)
        		  .setOAuthAccessToken(AccessToken)
        		  .setOAuthAccessTokenSecret(AccessTokenSecret);
        		TwitterFactory tf = new TwitterFactory(cb.build());
        		Twitter twitter = tf.getSingleton();
        	    Status status = twitter.updateStatus(latestStatus);
        	    System.out.println("Successfully updated the status to [" + status.getText() + "].");
        	    
        	    	  Twitter twitter = new TwitterFactory().getInstance();
        	    	  AccessToken accessToken = new AccessToken(
        	    	    AccessToken, // サンプルで取得したAccess token
        	    	    AccessTokenSecret); // サンプルで取得したAccess token secret
        	    	  twitter.setOAuthConsumer(ConsumerKey, // アプリケーションのconsumer key
        	    	    ConsumerSecret); // アプリケーションのconsumer secret
        	    	  twitter.setOAuthAccessToken(accessToken);
        	    	  try {
        	    	   Status status = twitter.updateStatus(args[0]);
        	    	   System.out.println("つぶやき成功 [" + status.getText() + "].");
        	    	  } catch (TwitterException e1) {
        	    	   e1.printStackTrace();
        	    	  }
        	    	  System.exit(0);
        	    	 }
        	    	 */
        	}
        };
	    
	    private View rootView;
		
	    public PlaceholderFragment() {
		}
	        
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
	            	rootView = inflater.inflate(R.layout.fragment_alarm,
	            			container, false);
	            	
	            	
	            	alarmStop = (Button) rootView.findViewById(R.id.alarmstop);
	            	Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	                rt = RingtoneManager.getRingtone(getActivity().getApplicationContext(), uri);
	                
	                alarmStop.setOnClickListener(this);
	               handler.postDelayed(tweet, searchByName(db) * 60 * 1000);
	                
	            	return rootView;
		}
		
		@Override
		public void onAttach(Activity act) {
			super.onAttach(act);
			dbHelper = new MySQLiteHelper(act.getApplication());
			db = dbHelper.getReadableDatabase();
			timer = searchByName(db);
			account = searchByNameAccountMessage(db)[0];
			message = searchByNameAccountMessage(db)[1];
			
			
		}
		
		@Override
		public void onClick(View v) {
			rt.stop();
			getActivity().finish();
		}
		
		private int searchByName(SQLiteDatabase db){
            // Cursorを確実にcloseするために、try{}～finally{}にする
            Cursor cursor = null;
            try{
                // name_book_tableからnameとageのセットを検索する
                // ageが指定の値であるものを検索
                cursor = db.query("alarm", 
                        new String[]{"timer"}, 
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
        private int readCursor(Cursor cursor) {
            int result = 0;
 
            // まず、Cursorからnameカラムとageカラムを
            // 取り出すためのインデクス値を確認しておく
            int indexTimer = cursor.getColumnIndex("timer");
 
            // ↓のようにすると、検索結果の件数分だけ繰り返される
            while(cursor.moveToNext()){
                // 検索結果をCursorから取り出す
                int timer = cursor.getInt(indexTimer);
                result = timer;
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
                return readCursorAccountMessage(cursor);
            }
            finally{
                // Cursorを忘れずにcloseする
                if( cursor != null ){
                    cursor.close();
                }
            }
        }
 
 
        /** 検索結果の読み込み */
        private String[] readCursorAccountMessage(Cursor cursor) {
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
