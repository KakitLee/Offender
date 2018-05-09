package com.project.zhi.tigerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_syn)
public class SynActivity extends AppCompatActivity {

    @AfterViews
    void init(){}

    public void getFile(View v) {
        EditText url_str = (EditText) findViewById(R.id.url_syn);
        Toast.makeText(SynActivity.this,url_str.getText().toString(),Toast.LENGTH_SHORT).show();
        URL url = null;//请求的URL地址
        try{
            url = new URL(url_str.getText().toString());
        }catch (Exception e){
            Toast.makeText(SynActivity.this,"Exception in create url",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
