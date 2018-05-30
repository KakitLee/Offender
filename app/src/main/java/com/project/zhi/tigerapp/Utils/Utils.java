package com.project.zhi.tigerapp.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.project.zhi.tigerapp.Entities.Attachments;
import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Enums.AttributeType;
import com.project.zhi.tigerapp.FaceUtils.MatchedImage;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.complexmenu.MenuModel;

import org.apache.commons.text.WordUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class Utils {
    public static Gson gson = new Gson();
    public static String displayKeyValue(String key){
        return WordUtils.capitalize(key);
    }
    public static String displayKeyAsTitle(String key){
        return WordUtils.capitalize(key);
    }

    public static Comparator<MenuModel> getComparatorMenuModelAlphabetically(){
        Comparator<MenuModel> comparator = new Comparator<MenuModel>() {
            @Override
            public int compare(MenuModel o1, MenuModel o2) {
                return o1.getAttributeKey().compareTo(o2.getAttributeKey());
            }
        };
        return comparator;
    }
    public static Comparator<MatchedImage> getComparator(){
        Comparator<MatchedImage> comparator = new Comparator<MatchedImage>() {
            @Override
            public int compare(MatchedImage o1, MatchedImage o2) {
                return Float.compare(o2.getScore(),o1.getScore());
            }
        };
        return comparator;
    }


    public static String getAttributeValues(Attributes attribute){
        if(attribute.getType().equalsIgnoreCase(AttributeType.TEXT.name())){
            return attribute.getStringValue();
        }
        else if(attribute.getType().equalsIgnoreCase(AttributeType.LIST.name())){
            return attribute.getListKey();
        }
        else if(attribute.getType().equalsIgnoreCase(AttributeType.NUMERIC.name())){
            return attribute.getDoubleValue().toString();
        }
        else if(attribute.getType().equalsIgnoreCase(AttributeType.BOOLEAN.name())){
            if(attribute.getDoubleValue() == 1.0){
                return "Yes";
            }
            return "No";
        }
        return null;
    }

    public static Attachments getPrimaryAttachent(ArrayList<Attachments> attachments){
        for (Attachments attachement: attachments
                ) {
            if(attachement.getIsPrimary()){
                return attachement;
            }
        }
        return attachments.get(0);
    }
    public static Boolean hasAttachments(Entities entity){
        return entity.getAttachments() != null && entity.getAttachments().size() > 0;
    }
    public static int getImageId(Entities entities, Context context){
        if (hasAttachments(entities)) {
            return context.getResources().getIdentifier(getPrimaryAttachent(entities.getAttachments()).getFilename(), "raw", context.getPackageName());
        }
        return R.drawable.place_holder;
    }
    public static Bitmap getImageExternal(Entities entities, String imagePath){
        if (hasAttachments(entities)) {

            String fullImagePath = (imagePath + "/" + getPrimaryAttachent(entities.getAttachments()).getFilename());
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

    public static AlertDialog setProgressDialog(Context context){

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding,llPadding,llPadding,llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(110, 110);
        llParam.gravity = Gravity.CENTER;

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0,0,llPadding,0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(context);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if(window != null){
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = 756;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
        return dialog;
    }
}
