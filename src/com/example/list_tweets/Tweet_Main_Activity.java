package com.example.list_tweets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class Tweet_Main_Activity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.list_tweets.MESSAGE";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet__main_);
        
        final EditText edit = (EditText)findViewById(R.id.edit_message);
        
        edit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				edit.setText("");
				
			}
		});
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tweet__main_, menu);
        return true;
    }
    
    public void sendMessage(View view){
    	Intent intent = new Intent(this,ListTweetsActivity.class);
    	EditText edit = (EditText)findViewById(R.id.edit_message);
    	String message = edit.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
}
