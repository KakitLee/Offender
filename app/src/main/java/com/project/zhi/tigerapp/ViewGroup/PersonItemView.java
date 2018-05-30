package com.project.zhi.tigerapp.ViewGroup;

import android.content.Context;
import android.graphics.Bitmap;
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

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
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
    @ViewById(R.id.tvFacialScore)
    TextView tvFacialScore;
    @ViewById(R.id.tvFacialScoreTitle)
    TextView tvFacialScoreTitle;
    @ViewById(R.id.tvVoiceScore)
    TextView tvVoiceScore;
    @ViewById(R.id.tvVoiceScoreTitle)
    TextView tvVoiceScoreTitle;
    @ViewById(R.id.tvOverallScore)
    TextView tvOverallScore;
    @ViewById(R.id.tvOverallScoreTitle)
    TextView tvOverallScoreTitle;

    @Bean
    DataFilteringService dataFilteringService;
    @Pref
    UserPrefs_ userPrefs;
    Bitmap image;

    public PersonItemView(Context context) {
        super(context);
    }

    public void bind(Person person, Float score) {
        if(person == null){
            tvPersonName.setText("");
            return;
        }
        Entities entities = person.getEntity();

        tvPersonName.setText(dataFilteringService.getPersonName(entities));
        if(score == null){
            scoreText.setVisibility(View.INVISIBLE);
        }
        else{
            scoreText.setText(String.valueOf(score*100)+"%");
        }

        loadImage(entities);
        showScore(person);
    }

    void loadImage(Entities entities){
        Bitmap image = null;
        if(userPrefs.isUrl().get()){
            image =  Utils.getImageExternal(entities,userPrefs.urlImagePath().get());
        }
        else if(userPrefs.isFolder().get() && userPrefs.folder().get() != null && !userPrefs.folder().get().isEmpty() && Utils.getImageExternal(entities,userPrefs.folder().get()) != null){
            image = Utils.getImageExternal(entities,userPrefs.folder().get());
        }
        setImage(image, entities);
    }
    @UiThread
    void showScore(Person person){
        if(person.getFacialSimilarity() != null){
            tvFacialScoreTitle.setVisibility(VISIBLE);
            tvFacialScore.setText(person.getFacialSimilarity().toString());
            tvFacialScore.setVisibility(VISIBLE);

        }
        else{
            tvFacialScoreTitle.setVisibility(GONE);
            tvFacialScore.setVisibility(GONE);
        }
        if(person.getVoiceSimilarity() != null){
            tvVoiceScore.setText(person.getVoiceSimilarity().toString());
            tvVoiceScoreTitle.setVisibility(VISIBLE);
            tvVoiceScore.setVisibility(VISIBLE);

        }
        else{
            tvVoiceScore.setVisibility(GONE);
            tvVoiceScoreTitle.setVisibility(GONE);
        }
        if(person.getOverallSimilarity() != null){
            tvOverallScore.setText(person.getOverallSimilarity().toString());
            tvOverallScoreTitle.setVisibility(VISIBLE);
            tvOverallScore.setVisibility(VISIBLE);
        }
        else{
            tvOverallScoreTitle.setVisibility(GONE);
            tvOverallScore.setVisibility(GONE);
        }
    }

    void setImage( Bitmap image, Entities entities){
        if(image != null) {
            imgPersonAvatar.setImageBitmap(image);
        }
        else{
            imgPersonAvatar.setImageResource(Utils.getImageId(entities, getContext()));
        }
    }
}