package com.project.zhi.tigerapp.Services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.project.zhi.tigerapp.LoginActivity_;
import com.project.zhi.tigerapp.MainActivity_;
import com.project.zhi.tigerapp.PhotoActivity_;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.SoundActivity_;
import com.project.zhi.tigerapp.UploadActivity_;
import com.project.zhi.tigerapp.SettingsActivity_;
import com.project.zhi.tigerapp.LoginActivity_;

import org.androidannotations.annotations.EBean;

import soundrecorder.activities.MainActivityVoice;

@EBean
public class NavigationService {
    public Intent getActivity(Context activity, MenuItem item){
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            return new Intent(activity, MainActivity_.class);
        } else if (id == R.id.nav_gallery) {
            return new Intent(activity, UploadActivity_.class);
        }else if(id == R.id.nav_sound){
            return new Intent(activity, MainActivityVoice.class);
        }else if(id == R.id.nav_photo){
            return new Intent(activity, PhotoActivity_.class);
        }else if (id == R.id.nav_syn){
            return new Intent(activity, SettingsActivity_.class);
        }else if (id == R.id.nav_login){
            return new Intent(activity, LoginActivity_.class);
        }
        return new Intent(activity, activity.getClass());
    }

}
