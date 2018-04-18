package com.project.zhi.tigerapp.Utils;

import android.content.Context;

import com.google.common.base.CaseFormat;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.R;

import org.apache.commons.text.WordUtils;

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
}
