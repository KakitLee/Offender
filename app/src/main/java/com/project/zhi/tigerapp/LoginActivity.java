package com.project.zhi.tigerapp;

import org.androidannotations.annotations.UiThread;
import org.json.JSONObject;

import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.project.zhi.tigerapp.Services.NavigationService;
import com.project.zhi.tigerapp.Services.UserPrefs_;
import com.project.zhi.tigerapp.Utils.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.commons.collections4.Get;
import org.json.JSONObject;

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

    @Pref
    UserPrefs_ userPrefs;

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



//        TextView state = (TextView) findViewById(R.id.state);
        String username = userPrefs.username().get();
        if (username!=null){
//            state.setText("Logged as "+username);
//            R.string.LoginActivity = "Logged as "+username;
        }

    }

    public void login(View v){
        EditText host_text = (EditText) findViewById(R.id.url);
        EditText username_text = (EditText) findViewById(R.id.username);
        EditText password_text = (EditText) findViewById(R.id.password);

//        userPrefs.urlAddres().put(host_text.getText().toString());
        userPrefs.urlAddres().put("http://10.12.220.140:8080");
        userPrefs.username().put(username_text.getText().toString());
        String password = password_text.getText().toString();
        String url = userPrefs.urlAddres().get()+"/oauth/token";
        System.out.println(url);

        getToken(url,"my-trusted-client","secret","password",userPrefs.username().get(),password);
        System.out.println(userPrefs.token().get());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        startActivity(navigationService.getActivity(this, item));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;    }

    @Background
    void getToken(String url,String authorizationUsername,String authorizationPassword,String grant_type,String username,String password){
        postRequest request = new postRequest();

        try{
            String response = request.post(url, authorizationUsername,authorizationPassword,grant_type,username,password);
            JSONObject response_json = new JSONObject(response);
            userPrefs.token().put(response_json.get("access_token").toString());
            onValid(username);
//            TextView state = (TextView) findViewById(R.id.state);
//            state.setText("Logged as "+username);

        }catch (Exception e){
            onInValid();
            e.printStackTrace();
        }
    }

    public class postRequest{

        OkHttpClient client = new OkHttpClient();

        String post(String url,String authorizationUsername,String authorizationPassword, String grant_type,String username, String password) throws IOException {
            MediaType CONTENT_TYPE = MediaType.parse("application/x-www-form-urlencoded");

            RequestBody requestBody = new FormBody.Builder()
                    .add("grant_type", grant_type)
                    .add("username", username)
                    .add("password", password)
                    .build();
            String credential = Credentials.basic(authorizationUsername, authorizationPassword);

            final Request request = new Request.Builder()
                    .header("Authorization", credential)
                    .url(url)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
//                System.out.println(response.body().string());
                return response.body().string();
            }
        }
    }

    @UiThread
    protected void onInValid(){
        Utils.setAlertDialog("Warning", "Login Failed", this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                btn_comfirm.setEnabled(false);
                dialog.dismiss();
            }
        }).show();
    }

    @UiThread
    protected void onValid(String username){
        Utils.setAlertDialog("Login Success", "You have logged as "+username, this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
