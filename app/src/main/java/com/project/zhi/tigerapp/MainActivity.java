package com.project.zhi.tigerapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import com.project.zhi.tigerapp.Adapter.PeopleAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.gridview)
    GridView gridview;

    @ViewById(R.id.toolbar)
    Toolbar Toolbar;

    @Bean
    PeopleAdapter adapter;

    @AfterViews
    void bindAdapter(){
//        setTheme(R.style.AppDarkTheme);
        setSupportActionBar(Toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark_material_dark));
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff000000")));
//            Toolbar.setTitleTextColor(Color.WHITE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        gridview.setAdapter(adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
