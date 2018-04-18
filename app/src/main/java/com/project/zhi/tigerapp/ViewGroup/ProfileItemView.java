package com.project.zhi.tigerapp.ViewGroup;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Utils.Utils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.item_profile)
public class ProfileItemView extends RelativeLayout {
    @ViewById(R.id.tvProfileSubTitle)
    TextView tvProfileSubTitle;
    @ViewById(R.id.tvProfileContent)
    TextView tvProfileContent;
    @Bean
    DataFilteringService dataFilteringService;

    public ProfileItemView(Context context) {
        super(context);
    }
    public void bind(Attributes attributes) {
        tvProfileSubTitle.setText(Utils.displayKeyAsTitle(attributes.getAttributeKey()));
        if(attributes.getType() == "TEXT") {
            tvProfileContent.setText(Utils.displayKeyAsTitle(attributes.getStringValue()));
        }
        else{
            tvProfileContent.setText(attributes.getDoubleValue().toString());
        }
    }
}
