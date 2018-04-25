package com.project.zhi.tigerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;

import com.google.gson.Gson;
import com.project.zhi.tigerapp.Adapter.PeopleAdapter;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.complexmenu.MenuModel;
import com.project.zhi.tigerapp.complexmenu.SelectMenuView;
import com.project.zhi.tigerapp.complexmenu.holder.SubjectHolder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

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

    @Bean
    PeopleAdapter adapter;
    @Bean
    DataFilteringService dataFilteringService;
    @Bean
    DataSourceServices dataSourceServices;

    @ViewById(R.id.menu)
    SelectMenuView selectMenuView;

    private SubjectHolder.OnSearchBtnListener onSearchBtnListener;
    Context context= this;
    @AfterViews
    void bindAdapter(){
//        setTheme(R.style.AppDarkTheme);

        setSupportActionBar(Toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = this.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.primary_dark_material_dark));
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff000000")));
//            Toolbar.setTitleTextColor(Color.WHITE);
        }
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
                var newList = dataFilteringService.update(dataSourceServices.getPeopleSource(context).getEntitiesList(),nameMenus,mainDemoMenu,otherDemoMenu);
                adapter.setDataList(newList);
                adapter.notifyDataSetChanged();
            }
        });

    }
    @ItemClick(R.id.gridview)
    void gridViewItemClicked(Entities entity){
        Gson gson = new Gson();
        String objStr = gson.toJson(entity);
        Intent intent = new Intent(this, ProfileActivity_.class);
        intent.putExtra("Profile",objStr);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, UploadActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;    }
}
