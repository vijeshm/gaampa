package com.example.gaampa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GaampaHomeScreen extends Activity {
	String FILENAME = "myFile";
	String filedata = "{'vijesh1':{'tag1':'desc1', 'tag2':'desc2'}, 'vijesh2':{'tag2':'desc2', 'tag3':'desc3'}}";		
	JSONObject jObj;
	FileOutputStream fos;
	int currentContact = -1;
	int currentTag = -1;
	int nextId;
	int nextTagId;

	PopupWindow searchPopup;
    Button searchBtn;
    boolean click = true;
	
    AlertDialog.Builder addUserBuilder;
    AlertDialog.Builder editUserBuilder;
    AlertDialog.Builder deleteUserBuilder;
    
    AlertDialog.Builder addTagBuilder;
    AlertDialog.Builder editTagBuilder;
    AlertDialog.Builder deleteTagBuilder;
    
    public static final String DB_NAME = "userInfo"; 
    SharedPreferences sharedData;
    SharedPreferences.Editor editor;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaampa_home_screen);
		try {
			sharedData = getSharedPreferences(DB_NAME, 0);
			editor = sharedData.edit();
			filedata = sharedData.getString("data", "{}");
			jObj = new JSONObject(filedata);
			TableLayout tags = (TableLayout) findViewById(R.id.tagsAndDescriptions);
			tags.removeAllViews();
			TableLayout contacts = (TableLayout) findViewById(R.id.contacts);
			contacts.removeAllViews();
			TableRow row;
			TextView name;
			JSONArray keys = jObj.names();
			if (keys == null)	{
				nextId = 1000;
			}
			else	{
				for(int i=0; i<keys.length(); i++)	{
					row = new TableRow(getApplicationContext());
					row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					
					name = new TextView(getApplicationContext());
					name.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					name.setText(keys.get(i).toString());
					name.setTag(keys.get(i).toString());
					name.setId(1000 + i); //contact IDs start from 1000
					name.setTextSize(30);
					
					name.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							
							currentContact = arg0.getId();
							TableLayout tags = (TableLayout) findViewById(R.id.tagsAndDescriptions);
							tags.removeAllViews();
							
							String name = (String) arg0.getTag();
							JSONObject tagsAndDescriptions;
							try {
								tagsAndDescriptions = jObj.getJSONObject(name);
								
								TableRow row;
								TextView tag;
								TextView desc;
								JSONArray keys = tagsAndDescriptions.names();
								if (keys != null)	{
									currentTag = 11000;
									nextTagId = 11000 + keys.length();
									for(int i=0; i<keys.length(); i++)	{
										row = new TableRow(getApplicationContext());
										row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
										row.setId(11000 + i);
										
										row.setOnClickListener(new View.OnClickListener() {
											@Override
											public void onClick(View arg0) {										
												currentTag = arg0.getId();
											}
										});
										
										tag = new TextView(getApplicationContext());
										tag.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
										tag.setText(keys.get(i).toString());
										tag.setTag(keys.get(i).toString());
										tag.setTextSize(20);
										row.addView(tag);
										
										desc = new TextView(getApplicationContext());
										desc.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
										desc.setText(tagsAndDescriptions.getString(keys.get(i).toString()));
										desc.setTag(tagsAndDescriptions.getString(keys.get(i).toString()));
										desc.setTextSize(20);
										row.addView(desc);
										
										tags.addView(row);
									}
								}
								else	{
									currentTag = -1;
								}								
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
					
					row.addView(name);
					contacts.addView(row);
				}
				
				
					TextView firstContact = (TextView) findViewById(1000);
					firstContact.performClick();
					currentContact = 1000;
					currentTag = 11000;
					nextId = 1000 + keys.length();
			}
			
			addUserBuilder = new AlertDialog.Builder(this);
			editUserBuilder = new AlertDialog.Builder(this);
			deleteUserBuilder = new AlertDialog.Builder(this);
			
			Button adduser = (Button) findViewById(R.id.adduser);
			adduser.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					LayoutInflater addUserInflater = getLayoutInflater();
					final View addUserView = addUserInflater.inflate(R.layout.add_user, null);
					
					addUserBuilder.setView(addUserView)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			               @Override
			               public void onClick(DialogInterface dialog, int id) {
									TextView addName = (TextView) addUserView.findViewById(R.id.addName);
									String key = addName.getText().toString();
									try {
										if ( !key.equals("") && !jObj.has(key) )	{
											jObj.put(key, new JSONObject("{}"));
											editor.putString("data", jObj.toString());
											editor.commit();
											
											TableLayout contacts = (TableLayout) findViewById(R.id.contacts);
											TableRow row = new TableRow(getApplicationContext());
											row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
											
											TextView name = new TextView(getApplicationContext());
											name.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
											name.setText(addName.getText().toString());
											name.setTag(addName.getText().toString());
											name.setId(nextId);
											nextId += 1;
											name.setTextSize(30);
											
											name.setOnClickListener(new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													
													currentContact = arg0.getId();
													TableLayout tags = (TableLayout) findViewById(R.id.tagsAndDescriptions);
													tags.removeAllViews();
													
													String name = (String) arg0.getTag();
													JSONObject tagsAndDescriptions;
													try {
														tagsAndDescriptions = jObj.getJSONObject(name);
														TableRow row;
														TextView tag;
														TextView desc;
														JSONArray keys = tagsAndDescriptions.names();
														if (keys != null)	{
															currentTag = 11000;
															nextTagId = 11000 + keys.length();
															for(int i=0; i<keys.length(); i++)	{
																row = new TableRow(getApplicationContext());
																row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
																row.setId(11000 + i);
																
																row.setOnClickListener(new View.OnClickListener() {
																	@Override
																	public void onClick(View arg0) {										
																		currentTag = arg0.getId();
																	}
																});
																
																tag = new TextView(getApplicationContext());
																tag.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
																tag.setText(keys.get(i).toString());
																tag.setTag(keys.get(i).toString());
																tag.setTextSize(20);
																row.addView(tag);
																
																desc = new TextView(getApplicationContext());
																desc.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
																desc.setText(tagsAndDescriptions.getString(keys.get(i).toString()));
																desc.setTag(tagsAndDescriptions.getString(keys.get(i).toString()));
																desc.setTextSize(20);
																row.addView(desc);
																
																tags.addView(row);
															}
														}
														else	{
															currentTag = -1;
														}
														
													}
													catch (JSONException e) {
													e.printStackTrace();
													}
												}
											});
										
										row.addView(name);
										contacts.addView(row);
										
										name.performClick();
									} 
								}
								catch (JSONException e) {
									e.printStackTrace();
								}					
							}
			           }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			                   dialog.cancel();
			               }
			           });
					
					AlertDialog addUserDialog = addUserBuilder.create();
					addUserDialog.show();
				}
			});
			
			Button edituser = (Button) findViewById(R.id.edituser);
			edituser.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					TableLayout table = (TableLayout) findViewById(R.id.contacts);
					if(table.getChildCount() > 0)	{
						LayoutInflater editUserInflater = getLayoutInflater();
						final View editUserView = editUserInflater.inflate(R.layout.edit_user, null);
						TextView editName = (TextView) editUserView.findViewById(R.id.editName);
						editName.setText( ((TextView)findViewById(currentContact)).getText().toString() );
						editUserBuilder.setView(editUserView)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
				            	   TextView current = (TextView) findViewById(currentContact);
									
									TextView editName = (TextView) editUserView.findViewById(R.id.editName);
									String key = editName.getText().toString();
									if ( !key.equals("") )	{
										try {
											jObj.put(key, jObj.get(current.getText().toString()));
											jObj.remove(current.getText().toString());
											editor.putString("data", jObj.toString());
											editor.commit();
											current.setText(key);
											current.setTag(key);
											
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								}
				           }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {
				                   dialog.cancel();
				               }
				           });
						
						AlertDialog editUserDialog = editUserBuilder.create();
						editUserDialog.show();
					}
				}
			});
			
			Button deleteuser = (Button) findViewById(R.id.deleteuser);
			deleteuser.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					TableLayout table = (TableLayout) findViewById(R.id.contacts);
					if(table.getChildCount() > 0)	{
						TextView contact = (TextView) findViewById(currentContact);
						deleteUserBuilder.setMessage("Delete '" + contact.getText().toString() +"'?")
						.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   TableLayout table = (TableLayout) findViewById(R.id.contacts);
			                	   TextView contact = (TextView) findViewById(currentContact);
			                	   jObj.remove(contact.getText().toString());
			                	   editor.putString("data", jObj.toString());
			                	   editor.commit();
									 
									TableRow row = (TableRow) contact.getParent();
									int index = table.indexOfChild(row);
									table.removeViewAt(index);

									TableLayout descTable = (TableLayout) findViewById(R.id.tagsAndDescriptions);
									descTable.removeAllViews();
									
									if (table.getChildCount() > 0)	{
										if( index == table.getChildCount() )	{
											currentContact = ((TableRow) table.getChildAt(table.getChildCount() - 1)).getChildAt(0).getId();
										}
										else {
											currentContact = ((TableRow) table.getChildAt(index)).getChildAt(0).getId();
										}
										currentTag = 11000;
										
										TextView prevContact = (TextView) findViewById(currentContact);
										Log.w("current1", currentContact + prevContact.getText().toString());
										prevContact.performClick();
									}
									else if( table.getChildCount() == 0 )	{
										currentContact = -1;
									}
			                   }
			               }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   //Do nothing
			                   }
			               });
						AlertDialog deleteUserDialog = deleteUserBuilder.create();
						deleteUserDialog.show();					
					}
				}
			});

			addTagBuilder = new AlertDialog.Builder(this);
			editTagBuilder = new AlertDialog.Builder(this);
			deleteTagBuilder = new AlertDialog.Builder(this);			
			
			Button addTag = (Button) findViewById(R.id.addTag);
			addTag.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (currentContact != -1)	{
						LayoutInflater addTagInflater = getLayoutInflater();
						final View addTagView = addTagInflater.inflate(R.layout.add_tag, null);
						
						addTagBuilder.setView(addTagView)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
								try {
									String user = ((TextView) findViewById(currentContact)).getText().toString();
					            	JSONObject userInfo;
									userInfo = jObj.getJSONObject(user);
									String tag = ((EditText) addTagView.findViewById(R.id.tagName)).getText().toString();
					            	   String desc = ((EditText) addTagView.findViewById(R.id.tagDesc) ).getText().toString();
					            	   if ( !tag.equals("") && !userInfo.has(tag) )	{
					            		   userInfo.put(tag, desc);
					            		   editor.putString("data", jObj.toString());
					            		   editor.commit();
					            		   
					            		   TableLayout tagsAndDescriptions = (TableLayout) findViewById(R.id.tagsAndDescriptions);
					            		   TableRow row = new TableRow(getApplicationContext());
					            		   row.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
											
					            		   TextView tagEntry = new TextView(getApplicationContext());
					            		   tagEntry.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					            		   tagEntry.setText(tag);
					            		   tagEntry.setTextSize(20);
											
					            		   TextView descEntry = new TextView(getApplicationContext());
					            		   descEntry.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					            		   descEntry.setText(desc);
					            		   descEntry.setTextSize(20);
											
					            		   row.setId(nextTagId);
					            		   currentTag = nextTagId;
					            		   nextTagId += 1;
											
					            		   row.addView(tagEntry);
					            		   row.addView(descEntry);

					            		   row.setOnClickListener(new View.OnClickListener() {
					            			   @Override
					            			   public void onClick(View arg0) {
					            				   currentTag = arg0.getId();
					            			   }
											});
											
											tagsAndDescriptions.addView(row);
					            	   }
								} catch (JSONException e) {
									e.printStackTrace();
								}   
				               }
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {
				                   dialog.cancel();
				               }
						});
						
						AlertDialog addTagDialog = addTagBuilder.create();
						addTagDialog.show();
					}
				}
			});
			
			Button editTag = (Button) findViewById(R.id.editTag);
			editTag.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if ( ((TableLayout)findViewById(R.id.tagsAndDescriptions)).getChildCount() > 0 )	{
						
						LayoutInflater editTagInflater = getLayoutInflater();
						final View editTagView = editTagInflater.inflate(R.layout.edit_tag, null);
						
						TableRow current = (TableRow) findViewById(currentTag);
						((EditText)editTagView.findViewById(R.id.tagName)).setText( ((TextView) current.getChildAt(0)).getText() );
						((EditText)editTagView.findViewById(R.id.tagDesc)).setText( ((TextView) current.getChildAt(1)).getText() );

						editTagBuilder.setView(editTagView)
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				               @Override
				               public void onClick(DialogInterface dialog, int id) {
				            	   try {
										String user = ((TextView) findViewById(currentContact)).getText().toString();
						            	JSONObject userInfo;
										userInfo = jObj.getJSONObject(user);
										
										TableRow current = (TableRow) findViewById(currentTag);
										
										EditText tagName = (EditText) editTagView.findViewById(R.id.tagName);
										String tag = tagName.getText().toString();
										
										EditText descName = (EditText) editTagView.findViewById(R.id.tagDesc);
										String desc = descName.getText().toString();
										
										if ( !tag.equals("") )	{
												userInfo.remove( ((TextView)current.getChildAt(0)).getText().toString());
												userInfo.put(tag, desc);
												editor.putString("data", jObj.toString());
												editor.commit();
												
												((TextView) current.getChildAt(0)).setText(tag);
												((TextView) current.getChildAt(1)).setText(desc);
										}
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
				               }
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {
				                   dialog.cancel();
				               }
						});
						
						AlertDialog editTagDialog = editTagBuilder.create();
						editTagDialog.show();
					}
				}
			});
			
			Button deleteTag = (Button) findViewById(R.id.deleteTag);
			deleteTag.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					TableLayout table = (TableLayout) findViewById(R.id.tagsAndDescriptions);
					if (table.getChildCount() > 0)	{
						
						TableRow tagRow = (TableRow) findViewById(currentTag);
						String tag = ((TextView)tagRow.getChildAt(0)).getText().toString();
						
						deleteTagBuilder.setMessage("Delete '" + tag +"'?")
						.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					           public void onClick(DialogInterface dialog, int id) {
					        	   try {
					        		   String userName = ((TextView)findViewById(currentContact)).getText().toString();
					        		   JSONObject userInfo;
					        		   userInfo = (JSONObject) jObj.get(userName);
					        		   
					        		   TableLayout table = (TableLayout) findViewById(R.id.tagsAndDescriptions);
					        		   TableRow tagRow = (TableRow) findViewById(currentTag);
					        		   String tag = ((TextView)tagRow.getChildAt(0)).getText().toString();
					        		   
					            	   userInfo.remove(tag);
					            	   editor.putString("data", jObj.toString());
				            		   editor.commit();
					            	   
					            	   int index = table.indexOfChild(tagRow);
					            	   table.removeViewAt(index);
					            	   
					            	   if (table.getChildCount() > 0)	{
					            		   if( index == table.getChildCount() )	{
					            			   currentTag = ((TableRow) table.getChildAt(table.getChildCount() - 1)).getId();
					            		   }
					            		   else {
					            			   currentTag = ((TableRow) table.getChildAt(index)).getId();
					            		   }
					            	   }
					        	   } catch (JSONException e) {
					        		   e.printStackTrace();
					        	   }
					           }
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog, int id) {
				                   dialog.cancel();
				               }
						});

						AlertDialog deleteTagDialog = deleteTagBuilder.create();
						deleteTagDialog.show();
					}
				}
			});
			
	        searchBtn = (Button) findViewById(R.id.search);
	        searchBtn.setOnClickListener(new View.OnClickListener() {
	        	@Override
	            public void onClick(View v) {
	        		
	        		Intent i = new Intent(getApplicationContext(), searchresults.class);
	        		i.putExtra("query", ((EditText)findViewById(R.id.searchText)).getText().toString());
	        		i.putExtra("jsonObject", jObj.toString());
	        		startActivity(i);
	            }
	        });
			
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(jObj.toString().getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_gaampa_home_screen, menu);
		return true;
	}
}