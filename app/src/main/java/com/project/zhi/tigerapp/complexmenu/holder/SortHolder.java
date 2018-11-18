package com.project.zhi.tigerapp.complexmenu.holder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Utils.Utils;

import java.util.List;

/**
 *
 * 综合排序
 * Created by vonchenchen on 2016/4/5 0005.
 */
public class SortHolder extends BaseWidgetHolder<List<String>>{

    /** 综合排序 */
    public static final String SORT_BY_NORULE = "";
    /** 评价最高 */
    public static final String SORT_BY_EVALUATION = "2";
    /** 价格最低 */
    public static final String SORT_BY_PRICELOW = "3";
    /** 价格最高 */
    public static final String SORT_BY_PRICEHIGH = "4";
    /** 离我最近 */
    public static final String SORT_BY_DISTANCE = "5";

    /** 综合排序 */
    private View mComprehensiveView;
    /** 评价最高 */
//    private View mHighEvaluateView;
    /** 价格最低 */
    private View mLowPriceView;
    /** 价格最高 */
    private View mHighPriceView;
    /** 离我最近 */
    private View mDistanceView;

    private SearchView mSearchView;

    private Button btnGeneralSearch;
    private Button btnGeneralClear;

    private ImageView mRecordImageView;
    private ImageView mComprehensiveImage;
    private ImageView mHighEvaluateImage;
    private ImageView mLowPriceImage;
    private ImageView mHighPriceImage;
    private ImageView mDistanceImage;

    private OnSortInfoSelectedListener mOnSortInfoSelectedListener;
    private OnGeneralSearchBtnListener onGeneralSearchBtnListener;
    private OnGeneralClearBtnListener onGeneralClearBtnListener;


    public SortHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        View view = View.inflate(mContext, R.layout.layout_holder_sort, null);

        mComprehensiveView = view.findViewById(R.id.re_sort1);
        mSearchView = view.findViewById(R.id.search_view_search);
        btnGeneralSearch = view.findViewById(R.id.btn_search_Search);
        btnGeneralClear = view.findViewById(R.id.btn_search_Clear);

        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGeneralSearchBtnListener.OnGeneralSearchBtnListener(mSearchView.getQuery().toString());
            }
        });

        btnGeneralSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnGeneralSearch.getWindowToken(), 0);
                onGeneralSearchBtnListener.OnGeneralSearchBtnListener(mSearchView.getQuery().toString());
            }
        });
        btnGeneralClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setQuery("",false);
                mSearchView.clearFocus();
            }
        });

        return view;
    }

    @Override
    public void refreshView(List<String> data) {
        mComprehensiveImage.setVisibility(View.INVISIBLE);
        mHighEvaluateImage.setVisibility(View.INVISIBLE);
        mLowPriceImage.setVisibility(View.INVISIBLE);
        mHighPriceImage.setVisibility(View.INVISIBLE);
        mDistanceImage.setVisibility(View.INVISIBLE);
    }

    public void clearSeachBox(){
        if(mSearchView != null){
            mSearchView.setQuery("",false);
        }
    }

    private void retSortInfo(String info, ImageView imageView){

        if(mRecordImageView != null){
            mRecordImageView.setVisibility(View.INVISIBLE);
        }
        mRecordImageView = imageView;

        imageView.setVisibility(View.VISIBLE);

        if(mOnSortInfoSelectedListener != null){
            mOnSortInfoSelectedListener.onSortInfoSelected(info);
        }
    }

    public void setOnSortInfoSelectedListener(OnSortInfoSelectedListener onSortInfoSelectedListener){
        this.mOnSortInfoSelectedListener = onSortInfoSelectedListener;
    }

    public interface OnSortInfoSelectedListener{
        void onSortInfoSelected(String info);
    }



    private void dialogSearch(final TextView displayView){
        final String[] m_Text = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(Utils.displayKeyAsTitle("Search"));

        final EditText input = new EditText(mContext);

        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                displayView.setText(value);
                if(value != null && !value.isEmpty()) {
                    displayView.setVisibility(View.VISIBLE);
                }
                else{
                    displayView.setVisibility(View.GONE);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                displayView.setText(value);
                if(value != null && !value.isEmpty()) {
                    displayView.setVisibility(View.VISIBLE);
                }
                else{
                    displayView.setVisibility(View.GONE);
                }
                dialog.cancel();
            }
        });

        builder.show();
    }
    public void setOnGeneralSearchBtnListner(SortHolder.OnGeneralSearchBtnListener onSearchBtnListener){
        this.onGeneralSearchBtnListener = onSearchBtnListener;
    }
    public interface OnGeneralSearchBtnListener{
        void OnGeneralSearchBtnListener(String query);
    }

    public void setOnGeneralClearBtnListner(SortHolder.OnGeneralClearBtnListener onGeneralClearBtnListener){
        this.onGeneralClearBtnListener = onGeneralClearBtnListener;
    }
    public interface OnGeneralClearBtnListener{
        void OnGeneralClearBtnListener();
    }
}
