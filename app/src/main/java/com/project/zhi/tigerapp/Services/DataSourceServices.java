package com.project.zhi.tigerapp.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.project.zhi.tigerapp.Entities.Attachments;
import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Data;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Utils.Utils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import lombok.experimental.var;

interface  IDataSourceServices{
    Data getPeopleSource(Context context);
}

@EBean
public class DataSourceServices implements IDataSourceServices {

    public boolean isValidDataSource(String filePath){
        if(filePath == null || filePath.isEmpty()){
            return false;
        }
        boolean isValid = false;
        Serializer serializer = new Persister();
        Data data = null;
        File source = new File(filePath);
        try {
            data = serializer.read(Data.class, source);
            if(data!=null){
                isValid = true;
            }
        } catch (Exception e) {
            isValid = false;
        }
        finally {
            return isValid;
        }
    }

    @Override
    public Data getPeopleSource(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Serializer serializer = new Persister();
        Data data = null;

        try {

            String filePath = prefs.getString("file", "");
            if(prefs.getBoolean("isUrl",false)){
                filePath = context.getFilesDir() + "/source.xml";
            }
            if(filePath != null && !filePath.isEmpty()){
                File source = new File(filePath);
                data = serializer.read(Data.class, source);
            }
            else {
                InputStream input = context.getResources().openRawResource(R.raw.entities);
                data = serializer.read(Data.class, input);
                data = setImagePath(data);
            }
        } catch (Exception e) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("file", "");
            editor.putBoolean("isFile", false);
            editor.commit();
            //Likely to the issue with the data parser.

        } finally {
            return data;
        }
    }{}

    public ArrayList<String> getUniqueKey(Data data){
        ArrayList<String> attributesList = new ArrayList<String>();
        for (Entities entity: data.getEntitiesList()
             ) {
            for (Attributes attributes: entity.getList()
                 ) {
                if(!attributesList.contains(attributes.getAttributeKey())){
                    attributesList.add(attributes.getAttributeKey());
                }
            }
        }
        return attributesList;
    }
    public Data setImagePath(Data data){
        for (Entities entity: data.getEntitiesList()
             ) {
            if(Utils.hasAttachments(entity)){
                Attachments attachment = Utils.getPrimaryAttachent(entity.getAttachments());
                attachment.setFilename(setFileName(attachment.getFilename()));
            }
        }
        return data;
    }

    public String setFileName(String fileName){
        fileName = fileName.toLowerCase();
        fileName = fileName.replace(" ", "_");
        fileName = fileName.replaceAll("^\\d+","");
        fileName = fileName.replaceAll("[^a-z0-9\\\\_\\\\.]","_");
        fileName = fileName.replaceAll("^\\_+","");
        fileName = FilenameUtils.removeExtension(fileName);
        return  fileName;
    }
}

