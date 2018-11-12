package com.project.zhi.tigerapp.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Entity;
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
import com.project.zhi.tigerapp.Entities.Person;
import com.project.zhi.tigerapp.Enums.AttributeType;
import com.project.zhi.tigerapp.FaceUtils.MatchedImage;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.complexmenu.MenuModel;

import org.apache.commons.text.WordUtils;
import org.simpleframework.xml.Attribute;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class Utils {
    public static DecimalFormat getSimilarityFormat(){
        return new DecimalFormat("####0.00");
    }

    public static Gson gson = new Gson();
    public static String displayKeyValue(String key){
        return WordUtils.capitalize(key);
    }
    public static String displayKeyAsTitle(String key){
        return WordUtils.capitalize(key);
    }

    public static String getDisplayAttributeLabel(Attributes attribute){
        if(attribute.getLabel() != null && !attribute.getLabel().isEmpty()){
            return attribute.getLabel();
        }
        return attribute.getAttributeKey();
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
    public static Comparator<MatchedImage> getMatchedImageComparator(){
        Comparator<MatchedImage> comparator = new Comparator<MatchedImage>() {
            @Override
            public int compare(MatchedImage o1, MatchedImage o2) {
                return Float.compare(o2.getScore(),o1.getScore());
            }
        };
        return comparator;
    }

    public static Comparator<Person> getPersonComparator(){
        Comparator<Person> comparator = new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return Double.compare(o2.getOverallSimilarity(),o1.getOverallSimilarity());
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
        return attribute.getStringValue();
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

    public static ArrayList<File> getAllAttachmentsPath(Entities entity, String imagePath){
        ArrayList<File> files = new ArrayList<File>();
        if(hasAttachments(entity)){
            for (Attachments attachment: entity.getAttachments()
                 ) {
                String fullImagePath = (imagePath + "/" + attachment.getFilename());
                File newFile = new File(fullImagePath);
                if(newFile.exists()){
                    files.add(newFile);
                }
            }
        }
        return files;
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
        //dialog.show();
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

    public static boolean isExpiredFile(File file){
        if(file.exists()){
            Calendar time = Calendar.getInstance();
            time.add(Calendar.DAY_OF_YEAR,- 28);

            Date lastModified = new Date(file.lastModified());
            if(lastModified.before(time.getTime())) {
                return true;
            }
        }
        return false;
    }

    public static void secureDelete(File file) throws IOException {
        if (file.exists()) {
            long length = file.length();
            SecureRandom random = new SecureRandom();
            RandomAccessFile raf = new RandomAccessFile(file, "rws");
            raf.seek(0);
            raf.getFilePointer();
            byte[] data = new byte[64];
            int pos = 0;
            while (pos < length) {
                random.nextBytes(data);
                raf.write(data);
                pos += data.length;
            }
            raf.close();
            file.delete();
        }
    }
    public static ArrayList<File> getAllFilesInDir(File dir) {
        if (dir == null)
            return null;

        ArrayList<File> files = new ArrayList<File>();

        Stack<File> dirlist = new Stack<File>();
        dirlist.clear();
        dirlist.push(dir);

        while (!dirlist.isEmpty()) {
            File dirCurrent = dirlist.pop();

            File[] fileList = dirCurrent.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory())
                    dirlist.push(aFileList);
                else
                    files.add(aFileList);
            }
        }

        return files;
    }

}
