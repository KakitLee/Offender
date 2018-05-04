package com.project.zhi.tigerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;

import com.google.gson.Gson;
import com.project.zhi.tigerapp.Adapter.PeopleAdapter;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.Services.NavigationService;
import com.project.zhi.tigerapp.Services.UserPrefs_;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;
import com.project.zhi.tigerapp.complexmenu.SelectMenuView;
import com.project.zhi.tigerapp.complexmenu.holder.SubjectHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import lombok.experimental.var;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @ViewById(R.id.gridview)
    GridView gridview;

    @ViewById(R.id.menu)
    SelectMenuView menu;

    @ViewById(R.id.toolbar)
    Toolbar Toolbar;

    @Pref
    UserPrefs_ userPrefs;

    @Bean
    PeopleAdapter adapter;
    @Bean
    DataFilteringService dataFilteringService;
    @Bean
    DataSourceServices dataSourceServices;
    @Bean
    NavigationService navigationService;

    android.app.AlertDialog dialog;


    @ViewById(R.id.menu)
    SelectMenuView selectMenuView;

    private SubjectHolder.OnSearchBtnListener onSearchBtnListener;
    Context context = this;

    @AfterViews
    void bindAdapter() {
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, Toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        gridview.setAdapter(adapter);

        selectMenuView.setOnFilteringBtnListener(new SelectMenuView.OnFilteringBtnListener() {
            @Override
            public void OnFiltering(ArrayList<MenuModel> nameMenus, ArrayList<MenuModel> mainDemoMenu, ArrayList<MenuModel> otherDemoMenu) {
                onLoading();
                var newList = dataFilteringService.update(dataSourceServices.getPeopleSource(context).getEntitiesList(), nameMenus, mainDemoMenu, otherDemoMenu);
                adapter.setDataList(newList);
                adapter.notifyDataSetChanged();
                onDismiss();
            }
        });

        selectMenuView.setOnSearchingBtnListener(new SelectMenuView.OnSearchingBtnListener() {
            @Override
            public void OnSearching(String query) {
                onLoading();
                var newList = dataFilteringService.search(dataSourceServices.getPeopleSource(context).getEntitiesList(),query);
                adapter.setDataList(newList);
                adapter.notifyDataSetChanged();
                onDismiss();
            }
        });

    }

    @UiThread
    void onLoading(){
        dialog = Utils.setProgressDialog(this);
    }
    @UiThread
    void onDismiss(){
        dialog.dismiss();
    }

    @ItemClick(R.id.gridview)
    void gridViewItemClicked(Entities entity) {
        Gson gson = new Gson();
        String objStr = gson.toJson(entity);
        Intent intent = new Intent(this, ProfileActivity_.class);
        intent.putExtra("Profile", objStr);
        startActivity(intent);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        startActivity(navigationService.getActivity(this, item));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
