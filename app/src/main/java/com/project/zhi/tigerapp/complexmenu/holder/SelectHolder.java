package com.project.zhi.tigerapp.complexmenu.holder;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.project.zhi.tigerapp.R;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

/**
 *
 * 筛选
 * Created by vonchenchen on 2016/4/5 0005.
 */
public class SelectHolder extends BaseWidgetHolder<List<String>> {

    private View mNoRuleView;
    private View mTeacherGenderView;
    private View mCourseTypeView;
    private TextView mSureBtn;

    private View mGenderView;
    private View mTypeView;

    private RadioItemView mGenderNoRuleRIView;
    private RadioItemView mGenderMaleRIView;
    private RadioItemView mGenderFemaleRIView;
    private RadioItemView mTypeNoRuleRIView;
    private RadioItemView mTypeTeacherToHomeRIView;
    private RadioItemView mTypeStudentToSchoolRIView;

    private RadioItemView mGenderRecorder = null;
    private RadioItemView mTypeRecorder = null;

    private TextView mTeacherGenderText;
    private TextView mTypeText;

    private boolean mIsFirstExtendGender = true;
    private boolean mIsFirstExtendType = true;

    private OnSelectedInfoListener mOnSelectedInfoListener = null;

    private String mRetGender = "";
    private String mRetType = "";
    private ImageView mTeacherGenderArrorImage;
    private ImageView mTypeArrorImage;
    private EditText mLongitudeView;
    private EditText mLatitudeView;
    private EditText mRadiusView;

    private OnLocationSearchBtnListener onLocationSearchBtnListener;

    public SelectHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.layout_holder_select, null);
        mLongitudeView = view.findViewById(R.id.tv_longitude);
        mLatitudeView = view.findViewById(R.id.tv_latitude);
        mRadiusView = view.findViewById(R.id.tv_radius);

        mSureBtn = (TextView) view.findViewById(R.id.btn_location_Search);

        mSureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double longitude = NumberUtils.toDouble(mLongitudeView.getText().toString());
                Double latitude = NumberUtils.toDouble(mLatitudeView.getText().toString());
                Double radius = NumberUtils.toDouble(mRadiusView.getText().toString());
                onLocationSearchBtnListener.OnLocationSearchBtnListener(longitude, latitude, radius);
            }
        });

        initViewListners();
        initGenderListener();
        initTypeListener();

        //默认不限
//        mGenderNoRuleRIView.setSelected(true);
//        mTypeNoRuleRIView.setSelected(true);

        return view;
    }

    private void initViewListners(){
    }

    public String getRetGender(){
        return mRetGender;
    }

    public String getRetClassType(){
        return mRetType;
    }

    private void initGenderListener(){
    }

    private void clearGenderInfo(RadioItemView radioItemView, String text){

        if(mIsFirstExtendGender){
            mIsFirstExtendGender = false;
            mGenderRecorder = mGenderNoRuleRIView;
        }

        if(radioItemView != mGenderRecorder && mGenderRecorder != null){
            mGenderRecorder.setSelected(false);
        }
        mGenderRecorder = radioItemView;
        mGenderView.setVisibility(View.GONE);
        mTeacherGenderText.setText(text);

        mTeacherGenderText.setTextColor(mContext.getResources().getColor(R.color.text_color_gey));
        mTeacherGenderArrorImage.setImageResource(R.mipmap.ic_down);
    }

    private void initTypeListener(){
    }

    private void clearTypeInfo(RadioItemView radioItemView, String text){

        if(mIsFirstExtendType){
            mIsFirstExtendType = false;
            mTypeRecorder = mTypeNoRuleRIView;
        }

        if(radioItemView != mTypeRecorder && mTypeRecorder != null){
            mTypeRecorder.setSelected(false);
        }
        mTypeRecorder = radioItemView;
        mTypeView.setVisibility(View.GONE);
        mTypeText.setText(text);

        mTypeText.setTextColor(mContext.getResources().getColor(R.color.text_color_gey));
        mTypeArrorImage.setImageResource(R.mipmap.ic_down);
    }

    @Override
    public void refreshView(List<String> data) {
        clearTypeInfo(mTypeNoRuleRIView, "no rule");
        mRetType = "";
        clearGenderInfo(mGenderNoRuleRIView, "no rule");
        mRetGender = "";
    }

    public void setOnSelectedInfoListener(OnSelectedInfoListener onSelectedInfoListener){
        this.mOnSelectedInfoListener = onSelectedInfoListener;
    }

    public interface OnSelectedInfoListener{
        void OnselectedInfo(String gender, String type);
    }
    public interface OnLocationSearchBtnListener{
        void OnLocationSearchBtnListener(Double longitude, Double latitude, Double radius);
    }
    public void setOnLocationSearchBtnListner(OnLocationSearchBtnListener onSearchBtnListener){
        this.onLocationSearchBtnListener = onSearchBtnListener;
    }
}
