package com.project.zhi.tigerapp.ViewGroup;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Entities.Person;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Services.UserPrefs_;
import com.project.zhi.tigerapp.Utils.Utils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

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
    @ViewById(R.id.score)
    TextView scoreText;
    @Bean
    DataFilteringService dataFilteringService;
    @Pref
    UserPrefs_ userPrefs;

    public PersonItemView(Context context) {
        super(context);
    }

    public void bind(Person person, Float score) {
        tvPersonName.setText(dataFilteringService.getPersonName(person.getEntity()));
        if(score == null){
            scoreText.setVisibility(View.INVISIBLE);
        }
        else{
            scoreText.setText(String.valueOf(score*100)+"%");
        }

        if(userPrefs.isUrl().get()){
            imgPersonAvatar.setImageBitmap(Utils.getImageExternal(person.getEntity(),userPrefs.urlImagePath().get()));
        }
        else if(userPrefs.isFolder().get() && userPrefs.folder().get() != null && !userPrefs.folder().get().isEmpty() && Utils.getImageExternal(person.getEntity(),userPrefs.folder().get()) != null){
            imgPersonAvatar.setImageBitmap(Utils.getImageExternal(person.getEntity(),userPrefs.folder().get()));
        }
        else {
            imgPersonAvatar.setImageResource(Utils.getImageId(person.getEntity(), getContext()));
        }
    }
}