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
import com.project.zhi.tigerapp.Entities.Name;
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
    @ViewById(R.id.tvPersonFirstName)
    TextView tvPersonFirstName;
    @ViewById(R.id.tvPersonLastName)
    TextView tvPersonLastName;
    @ViewById(R.id.imgPerson)
    ImageView imgPersonAvatar;
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

    public void bind(Person person) {
        if(person == null){
            tvPersonFirstName.setText("");
            tvPersonLastName.setText("");
            return;
        }
        Entities entities = person.getEntity();
        Name name = dataFilteringService.getPersonName(entities);
        tvPersonFirstName.setText(name.getFirstName());
        tvPersonLastName.setText(name.getLastName());

        loadImage(entities);
        if(person.getFacialSimilarity() != null || person.getVoiceSimilarity() != null || person.getOverallSimilarity() != null)
        {
            showScore(person);
        }
        else{
            tvFacialScoreTitle.setVisibility(GONE);
            tvFacialScore.setVisibility(GONE);
            tvVoiceScoreTitle.setVisibility(GONE);
            tvVoiceScore.setVisibility(GONE);
            tvOverallScoreTitle.setVisibility(GONE);
            tvOverallScore.setVisibility(GONE);

        }
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
            tvFacialScore.setText(Utils.getSimilarityFormat().format(person.getFacialSimilarity() * 100) + " %");
            tvFacialScore.setVisibility(VISIBLE);

        }
        else{
            tvFacialScoreTitle.setVisibility(VISIBLE);
            tvFacialScore.setText("N/A");
            tvFacialScore.setVisibility(VISIBLE);
        }
        if(person.getVoiceSimilarity() != null){
            tvVoiceScore.setText(Utils.getSimilarityFormat().format(person.getVoiceSimilarity() * 100) + " %");
            tvVoiceScoreTitle.setVisibility(VISIBLE);
            tvVoiceScore.setVisibility(VISIBLE);

        }
        else{
            tvVoiceScore.setVisibility(VISIBLE);
            tvVoiceScore.setText("N/A");
            tvVoiceScoreTitle.setVisibility(VISIBLE);
        }
        if(person.getOverallSimilarity() != null){
            tvOverallScore.setText(Utils.getSimilarityFormat().format(person.getOverallSimilarity() * 100) + " %");
            tvOverallScoreTitle.setVisibility(VISIBLE);
            tvOverallScore.setVisibility(VISIBLE);
        }
        else{
            tvOverallScoreTitle.setVisibility(VISIBLE);
            tvOverallScore.setText("N/A");
            tvOverallScore.setVisibility(VISIBLE);
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