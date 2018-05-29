package com.project.zhi.tigerapp.Services;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;

@EBean
public class ActivityService {
    @Pref
    UserPrefs_ userPrefs;

    public boolean validActivity(Context context){
        if(userPrefs.isUrl().get()){
            if(!isInternetSourceExist(context)){
                return false;
            }
            else {
                return true;
            }
        } else {
            if (isLocalSourceExist()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean validInitMenu(Context context){
        String userData = userPrefs.allMenu().get();
        if(userData == null || userData.isEmpty()){
            return false;
        }
        return true;
    }
    public boolean isInternetSourceExist(Context context){
        File targetFile = new File(context.getFilesDir() + "/", "source.xml");
        return targetFile.exists();
    }

    boolean isLocalSourceExist(){
        if(userPrefs.file().get() == null || userPrefs.file().get().isEmpty()){
            return false;
        }
        File targetFile = new File(userPrefs.file().get());
        return targetFile.exists();
    }

}
