package com.project.zhi.tigerapp.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.Preference;
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
import org.androidannotations.annotations.PreferenceClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@PreferenceScreen(R.xml.pref_general)
@EFragment
public class NotificationFragment extends PreferenceFragment {
    AlertDialog dialog;

    @Pref
    UserPrefs_ userPrefs;

//    @PreferenceByKey(R.string.pref_url_details)
//    EditTextPreference urlAddress;

    @PreferenceByKey(R.string.pref_url_switch)
    SwitchPreference urlSwitch;

//    @PreferenceByKey(R.string.synButton)
//    SwitchPreference synButton;

    @Bean
    DataSourceServices dataSourceServices;

    OkHttpClient client = new OkHttpClient();

    @AfterPreferences
    void initPrefs() {
        urlSwitch.setChecked(userPrefs.isUrl().get());
//        urlAddress.setSummary(userPrefs.urlAddres().get());
    }

    @PreferenceChange(R.string.pref_url_switch)
    void urlChange(Boolean isUrl){

        //is URL
        userPrefs.isUrl().put(isUrl);
        //is Using URL
        userPrefs.isUsingUrl().put(isUrl);

    }

    @PreferenceClick(R.string.synButton)
    void buttonClick(){
        String url = userPrefs.urlAddres().get();

        if(!URLUtil.isHttpUrl(url) && !URLUtil.isHttpsUrl(url)){
            onError();
        }

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
        Utils.setAlertDialog("Warning", "'Using Internet URL' need to be switch on before Internet synchronize", this.getActivity()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).show();
    }

    @UiThread
    protected void onValid(String error){
        Utils.setAlertDialog("Valid data source", "Valid data source in "+error, this.getActivity()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @UiThread
    protected void onInValid(String error){
        Utils.setAlertDialog("Warning", "Invalid data source in "+error, this.getActivity()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        }).show();
    }

    @Background
    void retrieveResource(){
        Boolean isGetFile = getFile();
        Boolean isGetPhoto = getPhoto();
        Boolean isUnzip = unzipPhoto();
        if (isGetFile && isGetPhoto && isUnzip){
            onValid("file updating");
        }else {
            if (!isGetFile){
                onInValid("file updating");
            }
            if (!isGetFile){
                onInValid("photo updating");
            }
            if (!isUnzip){
                onInValid("photo unzip");
            }
        }


    }

    Boolean getFile(){
        try {
            onLoading();
            String url = userPrefs.urlAddres().get()+"/upload";
            System.out.println(url);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer"+ userPrefs.token().get())
                    .build();
            Response response = null;
            response = client.newCall(request).execute();
            System.out.println(response);
            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();
                // save the file at here!!!!!!!!!!!!!!!!!!!
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
                    return true;
                }
                else
                {
                    onFinishLoading();
                    return false;
                }
            }
            else{
                onFinishLoading();
                throw new IOException("Unexpected code " + response);
            }
        }
        catch(Exception e){
            onFinishLoading();
            return false;
        }
    }

    Boolean getPhoto(){
        try {
            onLoading();
            String url = userPrefs.urlAddres().get()+"/images/"+userPrefs.username().get();
            System.out.println(url);
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(1000, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer"+ userPrefs.token().get())
                    .build();
            Response response = null;
            response = client.newCall(request).execute();
            System.out.println(response);
            if (response.isSuccessful()) {
                InputStream inputStream = response.body().byteStream();
                // save the file at here!!!!!!!!!!!!!!!!!!!
                String imagePath = this.getActivity().getFilesDir() + "/images/";
                File imageFile = new File(this.getActivity().getFilesDir(),"images");
                deleteFile(imageFile);
                File targetFile = new File(imagePath, "images.zip");
                userPrefs.urlImagePath().put(imagePath);
                OutputStream outStream = new FileOutputStream(targetFile);

                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(outStream);
                onFinishLoading();
                return true;
            }
            else{
                onFinishLoading();
                throw new IOException("Unexpected code " + response);
            }
        }
        catch(Exception e){
            onFinishLoading();
            return false;
        }
    }

    Boolean unzipPhoto(){
        try{
            onLoading();
            unzip("images.zip",this.getActivity().getFilesDir() + "/images/");
            onFinishLoading();
            return true;
        }catch (IOException e){
            onFinishLoading();
            return false;
        }
    }

    public static void unzip(String zipFile, String location) throws IOException {
        int size;
        int BUFFER_SIZE = 8192;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if ( !location.endsWith(File.separator) ) {
                location += File.separator;
            }
            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(location + zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if(!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if ( null != parentDir ) {
                            if ( !parentDir.isDirectory() ) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        catch (Exception e) {
            System.out.println("ZIP ERROR");
            e.printStackTrace();
        }
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                f.delete();
            }
        }
    }

//    void listFile(File file){
//        System.out.println("======================");
//        if (file.isDirectory()){
//            File[] files = file.listFiles();
//            for (int i = 0; i < files.length; i++) {
//                File f = files[i];
//                System.out.println(f.getName());
//            }
//        }
//        System.out.println("======================");
//    }

}
