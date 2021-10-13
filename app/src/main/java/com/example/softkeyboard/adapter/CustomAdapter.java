package com.example.softkeyboard.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.softkeyboard.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int logos[];
    LayoutInflater inflter;
    public CustomAdapter(Context applicationContext, int[] logos) {
        this.context = applicationContext;
        this.logos = logos;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return logos.length;
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.theme_list_item, null); // inflate the layout
        RoundedImageView icon = view.findViewById(R.id.icon); // get the reference of ImageView
        // get the reference of ImageView
        icon.setImageResource(logos[i]); // set logo images
        // set logo images
        return view;
    }
}