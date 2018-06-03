package com.project.zhi.tigerapp.Adapter;


import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.project.zhi.tigerapp.Entities.Person;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.ViewGroup.PersonItemView;
import com.project.zhi.tigerapp.ViewGroup.PersonItemView_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends ArrayAdapter<Person> {
    private Context context;

    private ArrayList<Float> scores;
    private ArrayList<Person> people;
    int screenHeight;


    public PeopleAdapter(Context context,ArrayList<Person> people) {
        super(context, R.layout.item_person ,people);
        this.context = context;
        this.people = people;
            screenHeight = ((Activity) context).getWindowManager()
                    .getDefaultDisplay().getHeight();
    }
    public void updatePeople(ArrayList<Person> newPeople){
        people = newPeople;
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }
    public void setDataList(ArrayList<Person> list, ArrayList<Float> scoreList){
        people = list;
        if(scoreList!=null){
            scores = scoreList;
        }

    }

    @Override
    public int getCount() {
        return people.size();
    }

    @Override
    public Person getItem(int position) {
        return people.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public Float getScore(int position) {
        if(scores.isEmpty()){
            return null;
        }
        return scores.get(position);
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