package com.project.zhi.tigerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;


@EActivity (R.layout.activity_photo)
public class PhotoActivity extends AppCompatActivity {

    @AfterViews
    void init(){}
}
