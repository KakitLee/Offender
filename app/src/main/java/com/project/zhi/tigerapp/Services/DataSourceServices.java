package com.project.zhi.tigerapp.Services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.project.zhi.tigerapp.Entities.Attachments;
import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Data;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Entities.Person;
import com.project.zhi.tigerapp.FaceUtils.Application;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;
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
import java.util.Timer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.experimental.var;

interface  IDataSourceServices{
    Data getPeopleSource(Context context);
}

@EBean
public class DataSourceServices implements IDataSourceServices {

    @Pref
    UserPrefs_ userPrefs;

    @Bean
    MenuService menuService;

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

    public ArrayList<Entities> getEntityById(Context context, String id){
        ArrayList<Entities> matchedEntity = new ArrayList<Entities>();
        for (Entities entity: getPeopleSource(context).getEntitiesList()
             ) {
            if(entity.getId().equalsIgnoreCase(id)){
                matchedEntity.add(entity);
                break;
            }
        }
        return matchedEntity;
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

    public ArrayList<Attributes> getUniqueKeyAttributes(Data data){
        ArrayList<Attributes> attributesList = new ArrayList<Attributes>();
        ArrayList<String> uniqueKeys = new ArrayList<String>();
        for (Entities entity: data.getEntitiesList()
                ) {
            for (Attributes attributes: entity.getList()
                    ) {
                if(!uniqueKeys.contains(attributes.getAttributeKey())){
                    uniqueKeys.add(attributes.getAttributeKey());
                    attributesList.add(attributes);
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
    public Entities getEntityByImageName(String imageName, Context context){
        Data data = this.getPeopleSource(context);
        for (Entities entity: data.getEntitiesList()
             ) {
            if (entity.getAttachments() != null){
                for(Attachments attachment:entity.getAttachments()) {
                    if (attachment.getFilename().equalsIgnoreCase(imageName)) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }
    public Entities getEntityByImageName( ArrayList<Entities> entitiesList, String imageName, Context context){
        for (Entities entity: entitiesList
                ) {
            if (entity.getAttachments() != null){
                for(Attachments attachment:entity.getAttachments()) {
                    if (attachment.getFilename().equalsIgnoreCase(imageName)) {
                        return entity;
                    }
                }
            }
        }
        return null;
    }

    public Entities getEntityById(String id, Context context){
        Data data = this.getPeopleSource(context);
        for (Entities entity: data.getEntitiesList()
                ) {
            if (entity.getId() != null){

                if (entity.getId().equalsIgnoreCase(id)) {
                    return entity;
                }

            }
        }
        return null;
    }

    public String getSourceFolder(){
        String path = "";
        if(userPrefs.isUrl().get()){
            path = userPrefs.urlImagePath().get();;
        }
        else{
            path = userPrefs.folder().get();
        }

        return path;
    }

    public ArrayList<Person> getPeopleFromEntities(ArrayList<Entities> entities){
        Timer timer = new Timer();
        long startTime = System.nanoTime();
        ArrayList<Person> people = new ArrayList<Person>();
        if(entities == null || entities.size() == 0) return people;
        for (Entities entity: entities
             ) {
            Person person = new Person();
            person.setEntity(entity);
            people.add(person);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000 ;
        System.out.println("PEOPLE TO ENTITIES TOOK " + duration + " MILLISECONDS");
        return people;
    }
    public void dataSourceChange(Context context){
        Data data = this.getPeopleSource(context);
        if(data == null) {
            return;
        }
        List<Entities> entities = data.getEntitiesList();
        ArrayList<Attributes> keys = getUniqueKeyAttributes(data);
        if(menuService == null){
            menuService = new MenuService();
        }
        ArrayList<MenuModel> mainMenu = menuService.getMainMenus();
        ArrayList<ArrayList<MenuModel>> allMenus = new ArrayList<ArrayList<MenuModel>>();
        allMenus.add(mainMenu);
        ArrayList<ArrayList<MenuModel>> allMenusRight = menuService.getAllMenus(keys);
        allMenus.add(allMenusRight.get(0));
        allMenus.add(allMenusRight.get(1));
        allMenus.add(allMenusRight.get(2));

        if(userPrefs != null) {
            userPrefs.allMenu().put(Utils.gson.toJson(allMenus));
        }
        else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("allMenu", (Utils.gson.toJson(allMenus)));
            editor.commit();
        }
    }
}

