package com.project.zhi.tigerapp;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.project.zhi.tigerapp.Services.NavigationService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.apache.commons.collections4.Get;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    @Bean
    NavigationService navigationService;

    @AfterViews
    void setup(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void getToken(View v){
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Toast.makeText(LoginActivity.this,username.getText().toString()+password.getText().toString(),Toast.LENGTH_SHORT).show();

//        String s = "Hello world.";
        String url = "http://10.0.2.2:8080/oauth/token";
        MyThread t = new MyThread(url,"my-trusted-client","secret","password",username.getText().toString(),password.getText().toString());
        t.start();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        startActivity(navigationService.getActivity(this, item));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;    }

    @Background
    void login(){

    }

    public class MyThread extends Thread {
        private String url;
        private String authorizationUsername;
        private String authorizationPassword;
        private String grant_type;
        private String username;
        private String password;
        public MyThread(String url,String authorizationUsername,String authorizationPassword,String grant_type,String username,String password) {
            this.url = url;
            this.authorizationUsername = authorizationUsername;
            this.authorizationPassword = authorizationPassword;
            this.grant_type = grant_type;
            this.username = username;
            this.password = password;
        }
        public void run() {
            GetToken example = new GetToken();
            String json = example.bowlingJson(grant_type,username,password);
            try{
                String response = example.post(url, authorizationUsername,authorizationPassword,json);
                System.out.println(response);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public class GetToken{
//        public final MediaType JSON
//                = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        String post(String url,String username,String password, String json) throws IOException {
            MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");

            RequestBody requestBody = new FormBody.Builder()
                    .add("grant_type", "password")
                    .add("username", "test")
                    .add("password", "test")
                    .build();
            String credential = Credentials.basic(username, password);

            final Request request = new Request.Builder()
                    .header("Authorization", credential)
                    .url(url)
                    .post(requestBody)
                    .build();


            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }

        String bowlingJson(String grant_type,String username,String password) {
            return "{"
                    +"'grant_type':"+grant_type+","
                    + "'username':"+username+","
                    + "'password':"+password
                    +"}";
        }
    }





//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            GetToken example = new GetToken();
//
//            try{
//                String response = example.run("http://10.0.2.2:8080/upload");
//                System.out.println(response);
//            }catch (Exception e){
////                Toast.makeText(LoginActivity.this,"Exception in get response!!!!!!!!",Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//        }
//    };
//
//    public class GetToken{
//        OkHttpClient client = new OkHttpClient();
//
//        String run(String url) throws IOException {
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//
//            try (Response response = client.newCall(request).execute()) {
//                return response.body().string();
//            }
//        }
//    }
}
