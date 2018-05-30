package com.project.zhi.tigerapp.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;

@EBean
public class ActivityService {
    @Pref
    UserPrefs_ userPrefs;

    DataSourceServices service = new DataSourceServices();

    public boolean validVoiceActivity(Context context){
        String voiceFilePath = null;
        File voiceFile = null;
        if(userPrefs == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            voiceFilePath = prefs.getString("voiceFolder",null);
            if (voiceFilePath == null){
                return false;
            }
            voiceFile = new File(voiceFilePath);
        }else{
            voiceFile = new File(userPrefs.voiceFolder().get());
        }
        if (voiceFile != null && voiceFile.exists() && voiceFile.listFiles().length!=0){
            return true;
        }else {
            return false;
        }
    }

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

//    boolean isDataBaseLoaded(){
//
//        service.getEntityById(getActivity(),id)
//    }

}
