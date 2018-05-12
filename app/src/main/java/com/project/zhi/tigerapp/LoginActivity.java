package com.project.zhi.tigerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;


@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    @AfterViews
    void setup(){

    }

    public void getToken(View v){
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Toast.makeText(LoginActivity.this,username.getText().toString()+password.getText().toString(),Toast.LENGTH_SHORT).show();
    }
}
