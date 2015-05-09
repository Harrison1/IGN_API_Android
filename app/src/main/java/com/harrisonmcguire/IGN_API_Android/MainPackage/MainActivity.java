package com.harrisonmcguire.IGN_API_Android.MainPackage;

/**
 * Created by Harrison on 4/18/2015.
 *
 IIIII       GGGGGGGGGGGGGGGG             NNNNNNNNNN         NNNNNN
 IIIII       GGGGGGGGGGGGGGGG             NNNNNN NNNN        NNNNNN
 IIIII       GGGGGGGGGGGGGGGG             NNNNNN  NNNN       NNNNNN
 IIIII       GGGGGGG                      NNNNNN   NNNN      NNNNNN
 IIIII       GGGGGGG                      NNNNNN    NNNN     NNNNNN
 IIIII       GGGGGGG     GGGGGGGGG        NNNNNN     NNNN    NNNNNN
 IIIII       GGGGGGG     GGGGGGGGG        NNNNNN      NNNN   NNNNNN
 IIIII       GGGGGGG       GGGGGGG        NNNNNN       NNNN  NNNNNN
 IIIII       GGGGGGGGGGGGGGGGGGGGG        NNNNNN        NNNN NNNNNN
 IIIII       GGGGGGGGGGGGGGGGGGGGG        NNNNNN         NNNNNNNNNN
 */

import com.harrisonmcguire.IGN_API_Android.Classes.IGNClass;
import com.harrisonmcguire.IGN_API_Android.VolleyPackage.VolleySingletonClass;
import com.harrisonmcguire.IGN_API_Android.Adapter.IGNValueAdapter;
import com.harrisonmcguire.IGN_API_Android.R;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

public class MainActivity extends Activity {

    //json urls
    private static final String ignAPI = "http://ign-apis.herokuapp.com/";
    private static final String videos = "videos";
    private static final String articles = "articles";
    private String startIndex = "/?startIndex=";
    private String ignArticlesLink = "http://www.ign.com/articles/";
    private String ignHomePage = "http://www.ign.com/";

    private String durationTime = "";

    private int counter = 0;
    private int cellCounter = 0;
    private int seconds = 0;
    private int index = 0;
    private int top = 0;

    private String count;
    private String jsonText;
    private String slug;
    private String date;
    private String year;
    private String month;
    private String day;

    private ListView listView;

    private List<IGNClass> ignList = new ArrayList<IGNClass>();
    private IGNValueAdapter adapter;

    //Loading box pop up
    private ProgressDialog pDialog;

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();


    //Create main activity and start app using the activity_main layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // declare list view in activity-main for clickable rows
        ListView lv = (ListView) findViewById(R.id.list);

        //I could have added the toggle button as a header to the list, but instead I made it separate so it didn't scroll away.
        //View header = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.header, null, false);
        //lv.addHeaderView(header);

        //put Load More button at the bottom of the list view
        View footerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_layout, null, false);
        lv.addFooterView(footerView);

        // enable each row to be clickable and launch the intent
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String postUrl = ((TextView) view.findViewById(R.id.urlLink)).getText().toString();
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("url", postUrl);
                Log.d("the url", postUrl); // for debugging
                startActivity(intent);
            }
        });

        //Once the app launches, by default update list row with http://ign-apis.herokuapp.com/videos data
        ignList(videos);

        //add the load more button at the bottom of the list
        Button btnLoadMore = (Button) findViewById(R.id.load_more_button);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadMore(jsonText);
            }
        });
    }

    public void onToggleClicked(View v) {
        //TextView Title
        TextView toggleTitle = (TextView)findViewById(R.id.toggleTitle);
        Button loadText = (Button) findViewById(R.id.load_more_button);

        //Is the toggle on?
        boolean on = ((ToggleButton) v).isChecked();

        if (on) {
            ignList(articles);
            toggleTitle.setText("ARTICLES");
            loadText.setText("Load More Articles");

        } else {
            ignList(videos);
            toggleTitle.setText("VIDEOS");
            loadText.setText("Load More Videos");
        }
    }

    //convert seconds to minutes. I purposely left off hours.
    private static String convertTime(int totalSeconds) {

        final int sixtySeconds = 60;

        int seconds = totalSeconds % sixtySeconds;
        int totalMinutes = totalSeconds / sixtySeconds;

        if(seconds < 10){
            return totalMinutes + ":" + "0" + seconds;
        } else {
            return totalMinutes + ":" + seconds;
        }
    }

    //updateList function using Volley to parse json data from the provided url
    public void ignList(String value){

        final String dataText = value;

        //reset index counter
        counter = 0;

        //cell counter
        cellCounter = 0;

        //seconds
        seconds = 0;

        //durationTime
        durationTime = "";

        //get index
        listView = (ListView) findViewById(R.id.list);

        // set url for parsing
        value = ignAPI + value;
        Log.d("ignurl", value);


        //declare new adapter to handle the values
        adapter = new IGNValueAdapter(this, ignList);

        //set the list view to the adapter
        listView.setAdapter(adapter);

        //use the clear adapter function to clear all current data so new data
        //can overtake the old data
        adapter.clearAdapter();

        //declare and show the loading box
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        //Parse JSON function
        //Establish the request cache to catch the json data using volley.jar
        JsonObjectRequest ignReq = new JsonObjectRequest(Request.Method.GET, value, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //print to logcat the entire json string for debugging
                Log.d(TAG, response.toString());
                hidePDialog();

                //try catch block to parse json
                try {

                    JSONArray data = response.getJSONArray("data");

                    // Parsing json. The for loop will loop through array getting the data specified
                    // and catch the exceptions
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject obj = data.getJSONObject(i).getJSONObject("metadata");
                        IGNClass post = new IGNClass();
                        cellCounter = i + 1;

                        if(cellCounter < 10) {
                            post.setCellCount("0" + Integer.toString(cellCounter));
                        } else {
                            post.setCellCount(Integer.toString(cellCounter));
                        }

                        if(dataText.equals("videos")) {
                            if (obj.has("title")) {
                                post.setTitle(obj.getString("title"));
                            } else {
                                post.setTitle("");
                            }

                            if(obj.has("description")) {
                                post.setDescription(obj.getString("description"));
                            } else {
                                post.setDescription("description");
                            }

                            if(obj.has("url")) {
                                post.setUrlLink(obj.getString("url"));
                            } else {
                                post.setUrlLink(ignHomePage);
                            }

                            jsonText = "videos";
                        }

                        if(dataText.equals("articles")) {
                            if (obj.has("headline")) {
                                post.setTitle(obj.getString("headline"));
                            } else {
                                post.setTitle("");
                            }

                            if(obj.has("subHeadline")) {
                                post.setDescription(obj.getString("subHeadline"));
                            } else {
                                post.setDescription("");
                            }

                            if(obj.has("slug") && obj.has("publishDate")) {
                                slug = obj.getString("slug");
                                date = obj.getString("publishDate");
                                year = date.substring(0,4);
                                month = date.substring(5, 7);
                                day = date.substring(8,10);
                                post.setUrlLink(ignArticlesLink + year +"/" + month + "/" + day + "/" + slug);
                            } else {
                                post.setUrlLink(ignHomePage);
                            }

                            jsonText = "articles";
                        }

                        if(obj.has("duration")) {
                            //seconds = Integer.parseInt(obj.getString("duration"));
                            seconds = obj.getInt("duration");
                            convertTime(seconds);
                            post.setDuration(convertTime(seconds));
                        } else {
                            post.setDuration("");
                        }

                        // adding posts to list array
                        ignList.add(post);

                    }} catch (JSONException e) {
                    e.printStackTrace();
                }

                // update list by notifying the adapter of changes
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        // maintain scroll position
        listView.setSelectionFromTop(index, top);

        //Volley singleton
        VolleySingletonClass.getInstance().addToRequestQueue(ignReq);
    }

    //function for loadMore clickable button at the footer of the list
    public void loadMore(String value){

        // variables for the next page of ign content
        counter = counter + 10;
        count = String.valueOf(counter);
        value = jsonText;

        final String dataText = value;

        seconds = 0;
        durationTime = "";

        // url for the nex page of ign content
        value = ignAPI + value + startIndex + count;
        Log.d("url", value); //for debugging

        // maintain position in the scroll view
        int index = listView.getFirstVisiblePosition();
        View v = listView.getChildAt(counter);
        int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

        //find the list view in activity_main with the id of list
        listView = (ListView) findViewById(R.id.list);

        //set the list view to the adapter
        listView.setAdapter(adapter);

        //declare and show the loading box
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        //Parse JSON function
        //Establish the request cache to catch the json data using volley.jar
        JsonObjectRequest ignReq = new JsonObjectRequest(Request.Method.GET, value, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //print to logcat the entire json string for debugging
                Log.d(TAG, response.toString());
                hidePDialog();

                //try catch block to parse json
                try {

                    // after_id = data.getString("after");
                    // Log.d("after id", after_id);
                    JSONArray data = response.getJSONArray("data");

                    // Parsing json. The for loop will loop through array getting the data specified
                    // and catch the exceptions
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject obj = data.getJSONObject(i).getJSONObject("metadata");
                        IGNClass post = new IGNClass();
                        cellCounter = i + 1 + counter;

                        if(cellCounter < 10) {
                            post.setCellCount("0" + Integer.toString(cellCounter));
                        } else {
                            post.setCellCount(Integer.toString(cellCounter));
                        }

                        if(dataText.equals("videos")) {
                            if (obj.has("title")) {
                                post.setTitle(obj.getString("title"));
                            } else {
                                post.setTitle("");
                            }

                            if(obj.has("description")) {
                                post.setDescription(obj.getString("description"));
                            } else {
                                post.setDescription("description");
                            }

                            if(obj.has("url")) {
                                post.setUrlLink(obj.getString("url"));
                            } else {
                                post.setUrlLink(ignHomePage);
                            }
                        }

                        if(dataText.equals("articles")) {
                            if (obj.has("headline")) {
                                post.setTitle(obj.getString("headline"));
                            } else {
                                post.setTitle("");
                            }

                            if(obj.has("subHeadline")) {
                                post.setDescription(obj.getString("subHeadline"));
                            } else {
                                post.setDescription("");
                            }

                            if(obj.has("slug") && obj.has("publishDate")) {
                                slug = obj.getString("slug");
                                date = obj.getString("publishDate");
                                year = date.substring(0,4);
                                month = date.substring(5, 7);
                                day = date.substring(8,10);
                                post.setUrlLink(ignArticlesLink + year +"/" + month + "/" + day + "/" + slug);
                            } else {
                                post.setUrlLink(ignHomePage);
                            }
                        }

                        if(obj.has("duration")) {
                            seconds = Integer.parseInt(obj.getString("duration"));
                            convertTime(seconds);
                            post.setDuration(convertTime(seconds));
                        } else {
                            post.setDuration("");
                        }

                        // adding posts to array
                        ignList.add(post);

                    }} catch (JSONException e) {
                    e.printStackTrace();
                }

                // update list by notifying the adapter of changes
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        // maintain scroll position
        listView.setSelectionFromTop(index, top);

        //Volley singleton
        VolleySingletonClass.getInstance().addToRequestQueue(ignReq);
    }

    //menu option if applicable
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // function to hide the loading dialog box
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    // Stop app from running
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

}