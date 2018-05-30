package com.qingyangli.offender.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.project.zhi.tigerapp.MainActivity;
import com.project.zhi.tigerapp.MainActivity_;
import com.project.zhi.tigerapp.R;

import com.project.zhi.tigerapp.Services.ActivityService;
import com.project.zhi.tigerapp.SettingsActivity_;
import com.project.zhi.tigerapp.UploadActivity_;
import com.project.zhi.tigerapp.Utils.Utils;
import com.qingyangli.offender.fragments.FileViewerFragment;
import com.qingyangli.offender.fragments.RecordFragment;


import java.util.ArrayList;
import java.util.List;


public class MainActivityVoice extends AppCompatActivity {

    private static final String LOG_TAG = MainActivityVoice.class.getSimpleName();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    ActivityService activityService = new ActivityService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isValid =  checkValidActivity();
        super.onCreate(savedInstanceState);
        checkPermissions();
        // checkRecordPermission();
        setContentView(R.layout.activity_main_voice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity_.class);
                startActivity(intent);
            }
        });

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyAdapter(getSupportFragmentManager(), this));
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

    }

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                } else {
                    String permissionsList = "";
                    for (String per : permissions) {
                        permissionsList += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }


    private void checkRecordPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        123);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        123);
            }
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] titles = { getString(R.string.tab_title_record),
                getString(R.string.tab_title_saved_recordings)};
        private Context context;

        public MyAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:{
                    return RecordFragment.newInstance(position,this.context);
                }
                case 1:{
                    return FileViewerFragment.newInstance(position);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public MainActivityVoice() {
    }

    boolean checkValidActivity(){
        if(!activityService.validVoiceActivity(this)){
                this.onInvalidInternetActivity();
        }
        return true;
    }


    void onInvalidInternetActivity(){
        Utils.setAlertDialog("Initialize", "Cannot found voice model. Please upload voice model from local storage.", this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ////TODO SYnc activity
                Intent intent = new Intent(MainActivityVoice.this, UploadActivity_.class);
                startActivity(intent);
                dialog.dismiss();
            }
        }).show();
    }
}
