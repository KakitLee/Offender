package com.project.zhi.tigerapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.guo.android_extend.image.ImageConverter;
import com.project.zhi.tigerapp.Entities.Data;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.FaceUtils.Application;
import com.project.zhi.tigerapp.FaceUtils.FaceDB;
import com.project.zhi.tigerapp.FaceUtils.MatchedImage;
import com.project.zhi.tigerapp.Services.DataSourceServices;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import com.project.zhi.tigerapp.Services.NavigationService;
import com.project.zhi.tigerapp.Services.UserPrefs;
import com.project.zhi.tigerapp.Services.UserPrefs_;
import com.project.zhi.tigerapp.Utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@EActivity (R.layout.activity_photo)
public class PhotoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Pref
    UserPrefs_ userPrefs;

    private static final int REQUEST_CODE_IMAGE_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 10;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 11;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 12;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    String path;
    private Uri mPath;
    AFR_FSDKEngine engine1;
    AFR_FSDKError error1;


    @Bean
    DataSourceServices dataSourceServices;

    @Bean
    NavigationService navigationService;

    AlertDialog dialog;

    @AfterViews
    void init() {
        final List<String> permissionsList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.CAMERA);
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.CAMERA},
//                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
            if(!permissionsList.isEmpty()){
                requestPermissions( permissionsList.toArray( new String[permissionsList.size()] ), MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }

        Button b1 = this.findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent getImageByCamera = new Intent(
                        "android.media.action.IMAGE_CAPTURE");
                ContentValues values = new ContentValues(1);

                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                mPath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, mPath);

                startActivityForResult(getImageByCamera, REQUEST_CODE_IMAGE_CAMERA);
            }
        });

    }


    public static Bitmap getImageFromResult(Context context, int resultCode,
                                            Intent imageReturnedIntent) {
        Log.d("photo", "getImageFromResult, resultCode: " + resultCode);
        Bitmap bm = null;
        File imageFile = new File(context.getExternalCacheDir(), "tempImage");
        imageFile.getParentFile().mkdirs();
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage;
            boolean isCamera = (imageReturnedIntent == null ||
                    imageReturnedIntent.getData() == null ||
                    imageReturnedIntent.getData().equals(Uri.fromFile(imageFile)));
            if (isCamera) {     /** CAMERA **/
                selectedImage = Uri.fromFile(imageFile);
            } else {            /** ALBUM **/
                selectedImage = imageReturnedIntent.getData();
            }
            Log.d("photo", "selectedImage: " + selectedImage);


        }
        return bm;
    }

    private String getPath(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(this, uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    return null;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    return null;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        }
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor actualimagecursor = managedQuery(uri, proj,null,null,null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        String end = img_path.substring(img_path.length() - 4);
        if (0 != end.compareToIgnoreCase(".jpg") && 0 != end.compareToIgnoreCase(".png")) {
            return null;
        }
        return img_path;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Background
    protected void faceRecognition(int requestCode, int resultCode ){
        Bitmap b2 = null;
        AFR_FSDKFace face2 = new AFR_FSDKFace();
        if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == RESULT_OK) {
            String file = getPath(mPath);
            b2 = Application.decodeImage(file);
            face2 = convertToFace(b2);
        }
        if(face2==null){
            Toast.makeText(this, "No face detected.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PhotoActivity_.class);
            startActivity(intent);
            return;
        }


        ArrayList<String> pass = new ArrayList<String>();

        ArrayList<MatchedImage> scores = new ArrayList<MatchedImage>();

        ArrayList<String> images = new ArrayList<String>();
        //path = userPrefs.folder().get();



        engine1 = new AFR_FSDKEngine();
        error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);

        /*
         * Image resource form sever synchronization
         * */
        path = userPrefs.urlImagePath().get();
        File[] files = new File(path).listFiles();
        if(files.length>0) {
            for (File file : files) {
                String imageName = file.getName();

                String end = imageName.substring(imageName.length() - 4);
                if (end.compareToIgnoreCase(".jpg") != 0){
                    continue;
                }


                imageName = imageName.replace(".jpg", "");

                //
                //                Bitmap mBitmap = Application.decodeImage(file.getPath());
                //                byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
                //                ImageConverter convert = new ImageConverter();
                //                convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
                //                if (convert.convert(mBitmap, data)) {
                //                    Log.d("photo", "convert ok!");
                //                }
                //                convert.destroy();
                //
                //                AFD_FSDKEngine engine = new AFD_FSDKEngine();
                //
                //                List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
                //                AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
                //                Log.d("photo", "AFD_FSDK_InitialFaceEngine = " + err.getCode());

                //                err  = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
                //                Log.d("photo", "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
                //                for (AFD_FSDKFace face : result) {
                //                    Log.d("com.arcsoft", "Face:" + face.toString());
                //                }
                //                if(result.size()>0){
                //
                //                }
                //                err = engine.AFD_FSDK_UninitialFaceEngine();
                //                Log.d("com.arcsoft", "AFD_FSDK_UninitialFaceEngine =" + err.getCode());


                //String path1 = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/IMG_20180314_125422.jpg";
                //String path2 = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/IMG_20180224_180355.jpg";

                Log.d("pHOTO","this is aaaaaaaaa" + file.getPath());

                Bitmap b1 = Application.decodeImage(file.getPath());
                float score = 0;
                //score = compare(b1, b2);
                score = compare(b1, face2);
                if (score == -2) {
                    Log.d("Photo", "No face detected in the taken photo");
                }
                if (score == -1) {
                    Log.d("Photo", "No face detected in the photo file");
                }
                if (score > 0.35) {
                    Log.d("Photo 2 ", String.valueOf(score));
                    scores.add(new MatchedImage(score, imageName));
                    //pass.add(entity.getId());
                    //images.add(imageName);
                }


            }

            Log.d("Photo 001",String.valueOf(scores.size()));
            Data data1 = dataSourceServices.getPeopleSource(this);


            Collections.sort(scores,Utils.getComparator());

            //scores = descendingOrder(scores);

            for (MatchedImage currImage : scores) {
                String imageName = currImage.getImage();
                Entities entity = dataSourceServices.getEntityByImageName(imageName, this, data1);
                if (entity != null && !pass.contains(entity.getId())) {
                    Log.d("photo 001", entity.getId()+" "+currImage.getScore());
                    pass.add(entity.getId());
                }
            }
        }

        Log.d("aaaaaaaaaaaa11", scores.toString());
        error1 = engine1.AFR_FSDK_UninitialEngine();
//        Collections.sort(scores);
//        TextView text = this.findViewById(R.id.textView2);
//        text.setText(scores.toString() );

        onDismiss();
        Intent result = new Intent(this,MainActivity_.class);
        result.putStringArrayListExtra("pass",pass);
        startActivity(result);
    }


    protected ArrayList<MatchedImage> descendingOrder(ArrayList<MatchedImage> result){
        ArrayList<MatchedImage> tem = new ArrayList<MatchedImage>();
        ArrayList<MatchedImage> ordered = new ArrayList<MatchedImage>();
        tem = result;
        int loop = 0;
        if(ordered.size()>=10){
            loop = 10;
        }
        else{
            loop = ordered.size();
        }
        for(int i = 0; i < loop; i++) {

            float maxScore = 0;
            String maxImage = "";
            int index = 0;
            for (MatchedImage face : tem) {
                if (face.getScore() > maxScore) {
                    tem.remove(index);
                    maxScore = face.getScore();
                    maxImage = face.getImage();
                }
                index++;
            }
            ordered.add(new MatchedImage(maxScore,maxImage));
        }
        return ordered;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onLoading();
        faceRecognition(requestCode,resultCode);



}

    public boolean inArrayList(String id, ArrayList<MatchedImage> list){
        for(MatchedImage image : list){
            if(image.getImage().equalsIgnoreCase(id)){
                return true;
            }

        }
        return false;
    }

    public float compare(AFR_FSDKFace face1, AFR_FSDKFace face2) {

        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fd_key);
        Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error.getCode());
        AFR_FSDKMatching score = new AFR_FSDKMatching();
        error = engine.AFR_FSDK_FacePairMatching(face1, face2, score);
        Log.d("com.arcsoft", "AFR_FSDK_FacePairMatching=" + error.getCode());
        Log.d("com.arcsoft", "Score:" + score.getScore());
        error = engine.AFR_FSDK_UninitialEngine();
        Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error.getCode());
        return score.getScore();
    }


    public AFR_FSDKFace convertToFace(Bitmap mBitmap){
        if (isOdd(mBitmap.getWidth())) {
            mBitmap = scaleBitmap(mBitmap, mBitmap.getWidth() + 1, mBitmap.getHeight());
        }
        if (isOdd(mBitmap.getHeight())) {
            mBitmap = scaleBitmap(mBitmap, mBitmap.getWidth(), mBitmap.getHeight() + 1);
        }
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace face = new AFR_FSDKFace();
        byte[] faceData = Bitmap2Byte(mBitmap);
        AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        if(getFace(faceData, mBitmap)==null){
            return null;
        }
        error = engine.AFR_FSDK_ExtractFRFeature(faceData, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, getFace(faceData, mBitmap).getRect(), AFR_FSDKEngine.AFR_FOC_0, face);
        error = engine.AFR_FSDK_UninitialEngine();
        Log.d("photo ",face.toString());
        return face;
    }

    public float compare(Bitmap aBitmap, AFR_FSDKFace face2) {

        if (isOdd(aBitmap.getWidth())) {
            aBitmap = scaleBitmap(aBitmap, aBitmap.getWidth() + 1, aBitmap.getHeight());
        }
        if (isOdd(aBitmap.getHeight())) {
            aBitmap = scaleBitmap(aBitmap, aBitmap.getWidth(), aBitmap.getHeight() + 1);
        }


        AFR_FSDKFace face1 = new AFR_FSDKFace();

        byte[] faceData1 = Bitmap2Byte(aBitmap);

        if (getFace(faceData1, aBitmap) == null) {
            return -1;

        } else {
            error1 = engine1.AFR_FSDK_ExtractFRFeature(faceData1, aBitmap.getWidth(), aBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, getFace(faceData1, aBitmap).getRect(), AFR_FSDKEngine.AFR_FOC_0, face1);
            Log.d("com.arcsoft", "1Face=" + face1.getFeatureData()[0] + "," + face1.getFeatureData()[1] + "," + face1.getFeatureData()[2] + "," + error1.getCode());
            AFR_FSDKMatching score = new AFR_FSDKMatching();
            error1 = engine1.AFR_FSDK_FacePairMatching(face1, face2, score);
            Log.d("com.arcsoft", "AFR_FSDK_FacePairMatching=" + error1.getCode());
            Log.d("com.arcsoft", "Score:" + score.getScore());

            Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
            return score.getScore();
        }
    }

    public float compare(Bitmap aBitmap, Bitmap bBitmap) {

        if (isOdd(aBitmap.getWidth())) {
            aBitmap = scaleBitmap(aBitmap, aBitmap.getWidth() + 1, aBitmap.getHeight());
        }
        if (isOdd(aBitmap.getHeight())) {
            aBitmap = scaleBitmap(aBitmap, aBitmap.getWidth(), aBitmap.getHeight() + 1);
        }
        if (isOdd(bBitmap.getWidth())) {
            bBitmap = scaleBitmap(bBitmap, bBitmap.getWidth() + 1, bBitmap.getHeight());
        }
        if (isOdd(bBitmap.getHeight())) {
            bBitmap = scaleBitmap(bBitmap, bBitmap.getWidth(), bBitmap.getHeight() + 1);
        }

        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace face1 = new AFR_FSDKFace();
        AFR_FSDKFace face2 = new AFR_FSDKFace();
        AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
        Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error.getCode());

        byte[] faceData1 = Bitmap2Byte(aBitmap);
        byte[] faceData2 = Bitmap2Byte(bBitmap);

        if (getFace(faceData1, aBitmap) == null) {
            return -1;
        } else if (getFace(faceData2, bBitmap) == null) {
            return -2;
        } else {
            error = engine.AFR_FSDK_ExtractFRFeature(faceData1, aBitmap.getWidth(), aBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, getFace(faceData1, aBitmap).getRect(), AFR_FSDKEngine.AFR_FOC_0, face1);
            Log.d("com.arcsoft", "1Face=" + face1.getFeatureData()[0] + "," + face1.getFeatureData()[1] + "," + face1.getFeatureData()[2] + "," + error.getCode());
            error = engine.AFR_FSDK_ExtractFRFeature(faceData2, bBitmap.getWidth(), bBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, getFace(faceData2, bBitmap).getRect(), AFR_FSDKEngine.AFR_FOC_0, face2);
            Log.d("com.arcsoft", "2Face=" + face2.getFeatureData()[0] + "," + face2.getFeatureData()[1] + "," + face2.getFeatureData()[2] + "," + error.getCode());
            AFR_FSDKMatching score = new AFR_FSDKMatching();
            error = engine.AFR_FSDK_FacePairMatching(face1, face2, score);
            Log.d("com.arcsoft", "AFR_FSDK_FacePairMatching=" + error.getCode());
            Log.d("com.arcsoft", "Score:" + score.getScore());
            error = engine.AFR_FSDK_UninitialEngine();
            Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error.getCode());
            return score.getScore();
        }
    }
    private static byte[] Bitmap2Byte(Bitmap mBitmap) {
        Rect src = new Rect();
        src.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
        ImageConverter convert = new ImageConverter();
        convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
        if (convert.convert(mBitmap, data)) {
            //convert ok
        }
        convert.destroy();
        return data;
    }
    private AFD_FSDKFace getFace(byte[] data, Bitmap bitmap) {

        AFD_FSDKEngine engine = new AFD_FSDKEngine();
        List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
        AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
        Log.d("com.arcsoft", "AFD_FSDK_InitialFaceEngine = " + err.getCode());
        err = engine.AFD_FSDK_StillImageFaceDetection(data, bitmap.getWidth(), bitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
        Log.d("com.arcsoft", "AFD_FSDK_StillImageFaceDetection =" + err.getCode());
        Log.d("com.arcsoft", "Face=" + result.size());
        AFD_FSDKFace mFace = null;
        for (AFD_FSDKFace face : result) {
            Log.d("com.arcsoft", "Face:" + face.toString());
            mFace = face;
        }
        err = engine.AFD_FSDK_UninitialFaceEngine();
        Log.d("com.arcsoft", "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
        return mFace;
    }
    private static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }
    private static boolean isOdd(int val) {
        return (val & 0x01) != 0;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        startActivity(navigationService.getActivity(this, item));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @UiThread
    void onLoading(){
        dialog = Utils.setProgressDialog(this);
    }
    @UiThread
    void onDismiss(){
        dialog.dismiss();
    }
}


