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
