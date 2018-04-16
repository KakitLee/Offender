package com.project.zhi.tigerapp.complexmenu.holder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.complexmenu.MenuModel;

import java.util.List;

/**
 * 科目
 * Created by vonchenchen on 2016/4/5 0005.
 */
public class SubjectHolder extends BaseWidgetHolder<List<List<MenuModel>>> {

    private List<List<MenuModel>> mDataList;

    private ListView mLeftListView;
    private ListView mRightListView;

    private LeftAdapter mLeftAdapter;
    private RightAdapter mRightAdapter;

    private int mLeftSelectedIndex = 0;
    private int mRightSelectedIndex = 0;
    private int mLeftSelectedIndexRecord = mLeftSelectedIndex;
    private int mRightSelectedIndexRecord = mRightSelectedIndex;

    /** 记录左侧条目背景位置 */
    private View mLeftRecordView = null;
    /** 记录右侧条目对勾位置 */
    private ImageView mRightRecordImageView = null;

    //用于首次测量时赋值标志
    private boolean mIsFirstMeasureLeft = true;
    private boolean mIsFirstMeasureRight = true;

    private OnRightListViewItemSelectedListener mOnRightListViewItemSelectedListener;

    public SubjectHolder(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_holder_subject, null);
        mLeftListView = (ListView) view.findViewById(R.id.listView1);
        mRightListView = (ListView) view.findViewById(R.id.listView2);

        mLeftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mLeftSelectedIndex = position;
                if(mLeftRecordView != null){
                    mLeftRecordView.setBackgroundResource(R.color.bg);
                }
                view.setBackgroundResource(R.color.white);
                mLeftRecordView = view;

                mRightAdapter.setDataList(mDataList.get(position + 1), mRightSelectedIndex);
                mRightAdapter.notifyDataSetChanged();
            }
        });

        mRightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mRightSelectedIndex = position;
                mLeftSelectedIndexRecord = mLeftSelectedIndex;
//                ImageView imageView = (ImageView) view.findViewById(R.id.list2_right);
                TextView textView = (TextView) view.findViewById(R.id.child_textDisplayView);

                if(mRightRecordImageView != null) {
                    mRightRecordImageView.setVisibility(View.INVISIBLE);
                }

//                imageView.setVisibility(View.VISIBLE);

//                mRightRecordImageView = imageView;

                if(mOnRightListViewItemSelectedListener != null){

                    List<MenuModel> dataList = mDataList.get(mLeftSelectedIndex+1);
                    String text = dataList.get(mRightSelectedIndex).getAttributeDisplayText();

                    mOnRightListViewItemSelectedListener.OnRightListViewItemSelected(mLeftSelectedIndex, mRightSelectedIndex, text);
                }
                List<MenuModel> dataList2 = mDataList.get(mLeftSelectedIndex+1);
                MenuModel menuModel =  dataList2.get(mRightSelectedIndex);
                dialog(textView, menuModel);
            }
        });

        return view;
    }

    private void dialog(final TextView displayView, MenuModel menuModel){
        final String[] m_Text = {""};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Title");

// Set up the input
        final EditText input = new EditText(mContext);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                menuModel.setValue(input.getText().toString());
                displayView.setText(input.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void refreshView(List<List<MenuModel>> data) {

    }

    public void refreshData(List<List<MenuModel>> data, int leftSelectedIndex, int rightSelectedIndex){

        this.mDataList = data;

        mLeftSelectedIndex = leftSelectedIndex;
        mRightSelectedIndex = rightSelectedIndex;

        mLeftSelectedIndexRecord = mLeftSelectedIndex;
        mRightSelectedIndexRecord = mRightSelectedIndex;

        mLeftAdapter = new LeftAdapter(data.get(0), mLeftSelectedIndex);
        mRightAdapter = new RightAdapter(data.get(1), mRightSelectedIndex);

        mLeftListView.setAdapter(mLeftAdapter);
        mRightListView.setAdapter(mRightAdapter);
    }

    private class LeftAdapter extends BaseAdapter{

        private List<MenuModel> mLeftDataList;

        public LeftAdapter(List<MenuModel> list, int leftIndex){
            this.mLeftDataList = list;
            mLeftSelectedIndex = leftIndex;
        }

        @Override
        public int getCount() {
            return mLeftDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mLeftDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LeftViewHolder holder;
            if(convertView == null){
                holder = new LeftViewHolder();
                convertView = View.inflate(mContext, R.layout.layout_normal_menu_item, null);
                holder.leftText = (TextView) convertView.findViewById(R.id.group_textView);
                holder.backgroundView = convertView.findViewById(R.id.ll_main);
                convertView.setTag(holder);
            }else{
                holder = (LeftViewHolder) convertView.getTag();
            }

            holder.leftText.setText(mLeftDataList.get(position).getAttributeDisplayText());
            if(mLeftSelectedIndex == position){
                holder.backgroundView.setBackgroundResource(R.color.white);  //选中项背景
                if(position == 0 && mIsFirstMeasureLeft){
                    mIsFirstMeasureLeft = false;
                    mLeftRecordView = convertView;
                }
            }else{
                holder.backgroundView.setBackgroundResource(R.color.bg);  //其他项背景
            }

            return convertView;
        }
    }

    public void clearSelectedInfo(){

    }

    private class RightAdapter extends BaseAdapter{

        private List<MenuModel> mRightDataList;

        public RightAdapter(List<MenuModel> list, int rightSelectedIndex){
            this.mRightDataList = list;
            mRightSelectedIndex = rightSelectedIndex;
        }

        public void setDataList(List<MenuModel> list, int rightSelectedIndex){
            this.mRightDataList = list;
            mRightSelectedIndex = rightSelectedIndex;
        }

        @Override
        public int getCount() {
            return mRightDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRightDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RightViewHolder holder;
            if(convertView == null){
                holder = new RightViewHolder();
                convertView = View.inflate(mContext, R.layout.layout_child_menu_item, null);
                holder.rightText = (TextView) convertView.findViewById(R.id.child_textView);
//                holder.selectedImage = (ImageView)convertView.findViewById(R.id.list2_right);
                holder.rightDisplayText = (TextView) convertView.findViewById(R.id.child_textDisplayView);
                convertView.setTag(holder);
            }else{
                holder = (RightViewHolder) convertView.getTag();
            }

            holder.rightText.setText(mRightDataList.get(position).getAttributeDisplayText());
            holder.rightDisplayText.setText(mRightDataList.get(position).getValue());
            if(mRightSelectedIndex == position && mLeftSelectedIndex == mLeftSelectedIndexRecord){
//                holder.selectedImage.setVisibility(View.VISIBLE);
//                mRightRecordImageView = holder.selectedImage;
            }else{
//                holder.selectedImage.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    private static class LeftViewHolder {
        TextView leftText;
        View backgroundView;
    }

    private static class RightViewHolder{
        TextView rightText;
        TextView rightDisplayText;
//        ImageView selectedImage;
    }


    public void setOnRightListViewItemSelectedListener(OnRightListViewItemSelectedListener onRightListViewItemSelectedListener){
        this.mOnRightListViewItemSelectedListener = onRightListViewItemSelectedListener;
    }

    public interface OnRightListViewItemSelectedListener{
        void OnRightListViewItemSelected(int leftIndex, int rightIndex, String text);
    }
}
