package com.project.zhi.tigerapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.project.zhi.tigerapp.Adapter.ProfileAdapter;
import com.project.zhi.tigerapp.Adapter.TransformerAdapter;
import com.project.zhi.tigerapp.Entities.Attachments;
import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Entities.Name;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Services.DataSortService;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.Services.UserPrefs_;
import com.project.zhi.tigerapp.Utils.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.val;

@EActivity(R.layout.activity_profile)
public class ProfileActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    @ViewById(R.id.toolbar)
    Toolbar Toolbar;

    @ViewById(R.id.attributeList)
    ListView listView;

    @ViewById(R.id.slider)
    com.daimajia.slider.library.SliderLayout mDemoSlider;

    @Bean
    DataFilteringService dataFilteringService;

    @Bean
    DataSourceServices dataSourceServices;

    @Bean
    DataSortService dataSortService;

    ProfileAdapter adapter;

    Entities entity;

    @Pref
    UserPrefs_ userPrefs;

    @AfterViews
    void bindAdapter() {
        Intent i = getIntent();
        String target = getIntent().getStringExtra("Profile");
        Gson gS = new Gson();
        entity = gS.fromJson(target, Entities.class);
        ArrayList<Attributes> attributes = entity.getList();
        attributes = dataSortService.sortAttributesGeneral(attributes);
        adapter = new ProfileAdapter(this, attributes);
        listView.setAdapter(adapter);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Name profileName = dataFilteringService.getPersonName(entity);
        setTitle(profileName.getFirstName() + " " + profileName.getLastName());

        String imagePaths = "";
        if(userPrefs.isUrl().get()) {
            imageView.setImageBitmap(Utils.getImageExternal(entity,userPrefs.urlImagePath().get()));
            imageView.setImageBitmap(image);
            image.recycle();
        }
        else if(userPrefs.isFolder().get() & userPrefs.folder().get() != null && !userPrefs.folder().get().isEmpty()){
            Bitmap image = Utils.getImageExternal(entity,userPrefs.urlImagePath().get());
            imageView.setImageBitmap(image);
            image.recycle();
        }
        else {
            imageView.setImageResource(Utils.getImageId(entity, this));
        }

        ArrayList<File> files = Utils.getAllAttachmentsPath(entity,imagePaths);
        for(File file : files){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
					.image(file)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);
            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    sliderDialog(file.getPath());
                }
            });
            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.stopAutoCycle();
        mDemoSlider.addOnPageChangeListener(this);
    }
    void sliderDialog(String imagePath){
        LayoutInflater inflater = LayoutInflater.from(this);
        View imgEntryView = inflater.inflate(R.layout.dialog_userhearder, null);
        final Dialog dialog=new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen); //default fullscreen titlebar
        PhotoView img = (PhotoView) imgEntryView
                .findViewById(R.id.photo_view);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        img.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        dialog.setContentView(imgEntryView);
        dialog.show();

        imgEntryView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                dialog.cancel();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
