package com.project.zhi.tigerapp.Adapter;


import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.ViewGroup.PersonItemView;
import com.project.zhi.tigerapp.ViewGroup.PersonItemView_;
import com.project.zhi.tigerapp.complexmenu.MenuModel;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhi on 30/09/2015.
 */
@EBean
public class PeopleAdapter extends BaseAdapter {
    @RootContext
    Context context;
    @Bean
    DataSourceServices dataSourceServices;

    private ArrayList<Entities> entities;
    int screenHeight;

    @AfterInject
    void initAdapter() {
        entities = dataSourceServices.getPeopleSource(context).getEntitiesList();
        screenHeight = ((Activity) context).getWindowManager()
                .getDefaultDisplay().getHeight();
    }
    public void setDataList(ArrayList<Entities> list){
        entities = list;
    }

    @Override
    public int getCount() {
        return entities.size();
    }

    @Override
    public Entities getItem(int position) {
        return entities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PersonItemView personItemView;
        if (convertView == null) {
            personItemView = PersonItemView_.build(context);
        } else {
            personItemView = (PersonItemView) convertView;
        }
        personItemView.bind(getItem(position));
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            personItemView.setMinimumHeight((screenHeight - TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics())) / 3);
        } else {
            personItemView.setMinimumHeight(screenHeight / 3);
        }
        return personItemView;
    }
}