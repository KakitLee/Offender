package com.project.zhi.tigerapp.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.common.base.CaseFormat;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.R;

import org.apache.commons.text.WordUtils;

import java.io.File;

public class Utils {
    public static String displayKeyValue(String key){
        return WordUtils.capitalize(key);
    }
    public static String displayKeyAsTitle(String key){
        return WordUtils.capitalize(key);
    }
    public static int getImageId(Entities entities, Context context){
        if (entities.getAttachments() != null && entities.getAttachments().getFilename() != null && !entities.getAttachments().getFilename().isEmpty()) {
            return context.getResources().getIdentifier(entities.getAttachments().getFilename(), "raw", context.getPackageName());
        }
        return R.drawable.place_holder;
    }
    public static Bitmap getImageExternal(Entities entities, String imagePath){
        if (entities.getAttachments() != null && entities.getAttachments().getFilename() != null && !entities.getAttachments().getFilename().isEmpty()) {
            String fullImagePath = (imagePath + "/" + entities.getAttachments().getFilename());
            if(new File(fullImagePath).exists()){
                return BitmapFactory.decodeFile(fullImagePath);
            }
        }
        return null;
    }
    public static AlertDialog.Builder setAlertDialog(String title, String message, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false);
        return alertDialogBuilder;
    }
}
