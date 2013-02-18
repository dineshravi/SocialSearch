package com.example.list_tweets;

import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ListTweetsActivity extends Activity {

	String tag = "LookHere ";
	public String searchText = " ";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        searchText = intent.getStringExtra(Tweet_Main_Activity.EXTRA_MESSAGE);
        
        setContentView(R.layout.list_tweets);   
        new TwitterSearch().execute();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_tweets, menu);
        return true;
    }
    
    public class TwitterSearch extends AsyncTask<Void,Void,ArrayList<Tweet>> {
    	
    	protected ArrayList<Tweet> doInBackground(Void...params) {
			ArrayList<Tweet> tweets =  getTweets(searchText,1);
//			Log.d("LISTTWEETS", tweets.size() + "");
			return tweets;
		}
		
		protected void onPostExecute(ArrayList<Tweet> tweets){
			ListView listview = (ListView)findViewById(R.id.ListViewId);
			Log.d("LISTTWEETS", tweets.size() + "");
			listview.setAdapter(new TweetItemAdapter(getApplicationContext(), R.layout.list_tweets, tweets));
		}
			
    }
    
    /* Tweet Class */
    
    public class Tweet{
    	String username;
    	String message;
    	String img_url;
    	
    	public Tweet(String username, String message, String img_url){
    		this.username = username;
    		this.message = message;
    		this.img_url = img_url;
    	}
    }
    
    /* Parsing JSON response and constructing Tweet objetcs */
    
    public ArrayList<Tweet> getTweets(String search, int page){
		String search_url = "http://search.twitter.com/search.json?q=@" 
		        + search + "&rpp=100&page=" + page;
		
		ArrayList<Tweet>Tweets = new ArrayList<Tweet>();
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(search_url);
		
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		
		String responseBody = null;
		try{
			responseBody = client.execute(get,responseHandler);
		}catch(Exception ex){
			Log.i("Exception","Error in responseHandler");
			ex.printStackTrace();
		}
    	
		JSONObject jsonObject = null;
		JSONParser parser = new JSONParser();
		
		try{
			Object obj = parser.parse(responseBody);
			jsonObject = (JSONObject)obj;
		}catch(Exception e) {
			Log.i("Exception", "Error in building JSONObject");
		}
		
		JSONArray arr = null;
		
		try{
			Object j = jsonObject.get("results");
			arr = (JSONArray)j;
			Log.d("LISTTWEETS", arr.size() + "");
		} catch(Exception e) {
			Log.i("Exception","Error in retrieving result from JSONObject");
		}
		
		for(Object t : arr) {
			Tweet tweet = new Tweet(
		      ((JSONObject)t).get("from_user").toString(),
		      ((JSONObject)t).get("text").toString(),
		      ((JSONObject)t).get("profile_image_url").toString()
		    );
		    Tweets.add(tweet);
		}
		
    	return Tweets;
    	
    }
    
    /* Makes Bitmaps of the avatar inages */
    
    public Bitmap getBitmap(String bitmapUrl){
		try{
			URL url =  new URL(bitmapUrl);
			return BitmapFactory.decodeStream(url.openConnection().getInputStream());
		}catch(Exception ex){ return null;}
		  	
    }
        
    /* binds the tweet objects attributes to associated view elements */
    
    public class TweetItemAdapter extends ArrayAdapter<Tweet>{
    	
    	public ArrayList<Tweet>tweets;
    	
    	public TweetItemAdapter (Context context, int textViewResourceId, ArrayList<Tweet> tweets){
    		super(context, textViewResourceId, tweets);
    	    this.tweets = tweets;
    	}
    	
    	@Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
    	    View v = convertView;
    	    if (v == null) {
    	      LayoutInflater vi = 
    	         (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	      v = vi.inflate(R.layout.activity_list_tweets, null);
    	    }
    	    
    	    Tweet tweet = tweets.get(position);
    	    if (tweet != null) {
    	        
    	    	TextView username = (TextView) v.findViewById(R.id.username);
    	        TextView message = (TextView) v.findViewById(R.id.message);
    	        ImageView image = (ImageView) v.findViewById(R.id.avatar);

    	        if (username != null) {
    	          username.setText(tweet.username);
    	        }

    	        if(message != null) {
    	          message.setText(tweet.message);
    	        }
    	          
    	        if(image != null) {
    	          image.setImageBitmap(getBitmap(tweet.img_url));
    	        }
    	      }

    	    
    	    return v;
    	}
    }
}

