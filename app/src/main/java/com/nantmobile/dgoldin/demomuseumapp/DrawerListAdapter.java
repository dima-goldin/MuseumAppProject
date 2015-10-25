package com.nantmobile.dgoldin.demomuseumapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DrawerListAdapter extends ArrayAdapter
{
    public DrawerListAdapter(Context context, ArrayList<Entity> resource)
    {
        super(context, R.layout.drawer_list_layout,resource);
        logMe("DrawerListAdapter constructor");

    }

    private void logMe(String s)
    {
        Log.d("Dima: ", s);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        logMe("getView");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rowView = inflater.inflate(R.layout.drawer_list_layout, parent, false);


        Entity entity = (Entity) getItem(position);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewPic);
        TextView textViewName = (TextView) rowView.findViewById(R.id.textViewPicName);

        imageView.setImageBitmap(entity.getPic());
        textViewName.setText(entity.getName());


        return rowView;
    }

    
}