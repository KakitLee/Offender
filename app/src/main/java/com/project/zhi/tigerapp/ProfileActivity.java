package com.project.zhi.tigerapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.project.zhi.tigerapp.Adapter.PeopleAdapter;
import com.project.zhi.tigerapp.Adapter.ProfileAdapter;
import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Services.DataSortService;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.Services.UserPrefs_;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;
import com.project.zhi.tigerapp.complexmenu.SelectMenuView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import lombok.experimental.var;


@EActivity(R.layout.activity_profile)
public class ProfileActivity extends AppCompatActivity {
    @ViewById(R.id.toolbar)
    Toolbar Toolbar;

    @ViewById(R.id.attributeList)
    ListView listView;

    @ViewById(R.id.imgProfilePic)
    ImageView imageView;

    @Bean
    DataFilteringService dataFilteringService;

    @Bean
    DataSourceServices dataSourceServices;

    @Bean
    DataSortService dataSortService;

    ProfileAdapter adapter;

    Entities entity;

    @Pref
    UserPrefs_ userPrefs;

    @AfterViews
    void bindAdapter() {
        Intent i = getIntent();
        String target = getIntent().getStringExtra("Profile");
        Gson gS = new Gson();
        Entities entity = gS.fromJson(target, Entities.class);
        ArrayList<Attributes> attributes = entity.getList();
        attributes = dataSortService.sortAttributesGeneral(attributes);
        if(userPrefs.isFolder().get() & userPrefs.folder().get() != null && !userPrefs.folder().get().isEmpty()){
            imageView.setImageBitmap(Utils.getImageExternal(entity, userPrefs.folder().get()));
        }
        else {
            imageView.setImageResource(Utils.getImageId(entity, this));
        }
        adapter = new ProfileAdapter(this, attributes);
        listView.setAdapter(adapter);
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
        setTitle(dataFilteringService.getPersonName(entity));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }


}
