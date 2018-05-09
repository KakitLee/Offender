package com.project.zhi.tigerapp.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.webkit.URLUtil;

import com.google.android.gms.common.util.IOUtils;
import com.google.common.io.Files;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.Services.UserPrefs_;
import com.project.zhi.tigerapp.Utils.Utils;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@PreferenceScreen(R.xml.pref_general)
@EFragment
public class NotificationFragment extends PreferenceFragment {
    AlertDialog dialog;

    @Pref
    UserPrefs_ userPrefs;

    @PreferenceByKey(R.string.pref_url_details)
    EditTextPreference urlAddress;

    @PreferenceByKey(R.string.pref_url_switch)
    SwitchPreference urlSwitch;

    @Bean
    DataSourceServices dataSourceServices;

    OkHttpClient client = new OkHttpClient();

    @AfterPreferences
    void initPrefs() {
        urlSwitch.setChecked(userPrefs.isUrl().get());
        urlAddress.setSummary(userPrefs.urlAddres().get());
    }

    @PreferenceChange(R.string.pref_url_details)
    void urlChange(String newUrl){
        if(!URLUtil.isHttpUrl(newUrl) && !URLUtil.isHttpsUrl(newUrl)){
            onError();
        }
        userPrefs.urlAddres().put(newUrl);
        urlAddress.setSummary(userPrefs.urlAddres().get());
        retrieveResource();
    }

    @UiThread
    void onLoading(){
        dialog = Utils.setProgressDialog(this.getActivity());
    }

    @UiThread
    void onFinishLoading(){
        dialog.dismiss();
    }

    @UiThread
    void onError(){
        Utils.setAlertDialog("Error", "Error!", this.getActivity()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).show();
    }

    @UiThread
    protected void onValid(){
        Utils.setAlertDialog("Valid data source", "Valid data source", this.getActivity()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @UiThread
    protected void onInValid(){
        Utils.setAlertDialog("Warning", "Invalid data source", this.getActivity()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).show();
    }

    @PreferenceChange(R.string.pref_url_switch)
    void switchChange(boolean newSwitch){
        userPrefs.isUrl().put(newSwitch);
    }

    @Background
    void retrieveResource(){
        try {
            onLoading();
            Request request = new Request.Builder().url(userPrefs.urlAddres().get()).build();
            Response response = null;
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();
                File targetFile = new File(this.getActivity().getFilesDir() + "/", "source.xml");
                OutputStream outStream = new FileOutputStream(targetFile);

                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outStream);
                if(dataSourceServices.isValidDataSource(targetFile.getAbsolutePath())) {
                    onFinishLoading();
                    onValid();
                }
                else
                {
                    onFinishLoading();
                    onInValid();
                }
            }
            else{
                onFinishLoading();
                throw new IOException("Unexpected code " + response);
            }
        }
        catch(Exception e){
            onFinishLoading();
        }
    }
}
