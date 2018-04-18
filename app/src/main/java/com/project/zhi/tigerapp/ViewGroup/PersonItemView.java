package com.project.zhi.tigerapp.ViewGroup;

import android.content.Context;
import android.os.Build;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Utils.Utils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by zhi on 30/09/2015.
 */
@EViewGroup(R.layout.item_person)
public class PersonItemView extends LinearLayout {
    @ViewById(R.id.tvPersonName)
    TextView tvPersonName;
    @ViewById(R.id.imgPerson)
    ImageView imgPersonAvatar;
    @Bean
    DataFilteringService dataFilteringService;

    public PersonItemView(Context context) {
        super(context);
    }

    public void bind(Entities entities) {
        tvPersonName.setText(dataFilteringService.getPersonName(entities));
        imgPersonAvatar.setImageResource(Utils.getImageId(entities,getContext()));
    }
}