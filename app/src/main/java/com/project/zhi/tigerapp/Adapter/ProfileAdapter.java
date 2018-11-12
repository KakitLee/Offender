package com.project.zhi.tigerapp.Adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.ViewGroup.ProfileItemView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;


public class ProfileAdapter extends ArrayAdapter<Attributes> {
    private Context mContext;

    private ArrayList<Attributes> attributes;
    int screenHeight;

    public ProfileAdapter(Context context, ArrayList<Attributes> listAccountInfo ){
        super(context, R.layout.item_profile ,listAccountInfo);
        this.mContext = context;
        this.attributes = listAccountInfo;
    }
    @Override
    public int getCount() {
        return attributes.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfileItemView profileItemView;
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_profile, parent,false);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvProfileSubTitle);
        TextView tvValue = (TextView) convertView.findViewById(R.id.tvProfileContent);

        Attributes item = attributes.get(position);
        tvTitle.setText(Utils.getDisplayAttributeLabel(item));
        String value = Utils.getAttributeValues(item);

        tvValue.setText(value);

        return convertView;
    }
}
