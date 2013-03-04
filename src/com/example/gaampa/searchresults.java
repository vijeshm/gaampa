package com.example.gaampa;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class searchresults extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchresults);
		
		try {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				String query = extras.getString("query");
				JSONObject jObj = new JSONObject(extras.getString("jsonObject"));
				TextView header = (TextView) findViewById(R.id.searchHeader);
				header.setText("Matches for \"" + query + "\"");
				
				ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
				
				JSONArray users = jObj.names();
				if (users != null)	{
					Log.w("number of users", "" + users.length());
					for (int i=0; i<users.length(); i++)	{
						JSONArray tags = ( (JSONObject) jObj.get(users.getString(i)) ).names();
						if(tags != null) 	{
							Log.w("number of tags for " + users.getString(i), "" + tags.length());
							for (int j=0; j<tags.length(); j++)	{
								if (tags.getString(j).toLowerCase().equals(query.toLowerCase()))	{
									Log.w("match", users.getString(i) + " " + tags.getString(j) + " " + ( (JSONObject) jObj.get(users.getString(i)) ).getString(tags.getString(j)));
									ArrayList<String> matched = new ArrayList<String>();
									matched.add(users.getString(i));
									matched.add(tags.getString(j));
									matched.add( ( (JSONObject) jObj.get(users.getString(i)) ).getString(tags.getString(j)) );
									
									results.add(matched);
								}
							}
						}
					}					
					
					Log.w("foo-1", "bar-1");
					TableLayout resultTable = (TableLayout) findViewById(R.id.results);
					Log.w("foo1", "bar1");
					Log.w("results_size", results.size() + "");
					Log.w("foo2", "bar2");
					for (int i=0; i<results.size(); i++)	{
						TableRow row = new TableRow(getApplicationContext());
						row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						
						TextView name = new TextView(getApplicationContext());
						name.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						name.setText(results.get(i).get(0));
						row.addView(name);

						TextView tag = new TextView(getApplicationContext());
						tag.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						tag.setText(results.get(i).get(1));
						row.addView(tag);

						TextView desc = new TextView(getApplicationContext());
						desc.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						desc.setText(results.get(i).get(2));
						row.addView(desc);
						
						resultTable.addView(row);
					}
				}
				
								
				Button backBtn = (Button) findViewById(R.id.back);
		        backBtn.setOnClickListener(new View.OnClickListener() {
		        	@Override
		            public void onClick(View v) {
		        		
		        		Intent i = new Intent(getApplicationContext(), GaampaHomeScreen.class);
		        		startActivity(i);
		            }
		        });
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
