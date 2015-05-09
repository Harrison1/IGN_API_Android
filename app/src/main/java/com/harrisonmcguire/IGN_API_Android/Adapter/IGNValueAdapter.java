package com.harrisonmcguire.IGN_API_Android.Adapter;

/**
 * Created by Harrison on 4/18/2015.
 */

import com.harrisonmcguire.IGN_API_Android.Classes.IGNClass;
import com.harrisonmcguire.IGN_API_Android.R;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class IGNValueAdapter extends BaseAdapter {

    //declare variables for adapter
    private Activity activity;
    private LayoutInflater inflater;
    private List<IGNClass> ignValues;

    public IGNValueAdapter(Activity activity, List<IGNClass> ignValues) {
        this.activity = activity;
        this.ignValues = ignValues;
    }

    //get size of array class
    @Override
    public int getCount() {
        return ignValues.size();
    }

    //get the values in the class
    @Override
    public Object getItem(int location) {
        return ignValues.get(location);
    }

    //get the item ids in the class
    @Override
    public long getItemId(int position) {
        return position;
    }

    //function to clear adapter when updating the list view
    public void clearAdapter()
    {
        ignValues.clear();
        //selected.clear();
        notifyDataSetChanged();
    }

    //function populate rows in the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //declare inflater
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //declare convertView
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row, null);
        }

        //setup row items
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);
        TextView urlLink = (TextView) convertView.findViewById(R.id.urlLink);
        TextView cellCount = (TextView) convertView.findViewById(R.id.cellCount);

        // getting ign values for the row
        IGNClass m = ignValues.get(position);

        // set title to text box
        title.setText(m.getTitle());

        // set the description
        description.setText(m.getDescription());

        //set duration text box
        duration.setText(String.valueOf(m.getDuration()));

        // set the url
        urlLink.setText(String.valueOf(m.getUrlLink()));

        //set the cell count
        cellCount.setText(String.valueOf(m.getCellCount()));

        return convertView;
    }
}
