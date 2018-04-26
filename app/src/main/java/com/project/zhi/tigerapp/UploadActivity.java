package com.project.zhi.tigerapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.ProgressDialog;

import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.Services.NavigationService;
import com.project.zhi.tigerapp.Services.UserPrefs_;
import com.project.zhi.tigerapp.Utils.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import lib.folderpicker.FolderPicker;

@EActivity(R.layout.activity_upload)
public class UploadActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bean
    NavigationService navigationService;

    private static final int SDCARD_PERMISSION_FOLDER = 12,
            SDCARD_PERMISSION_FILE = 123,
            FOLDER_PICKER_CODE = 78,
            FILE_PICKER_CODE = 786;

    @ViewById(R.id.tv_folder)
    TextView tv_folderPath;

    @ViewById(R.id.tv_file)
    TextView tv_filePath;

    @ViewById(R.id.btn_confirm)
    Button btn_comfirm;

    @Pref
    UserPrefs_ userPrefs;

    @Bean
    DataSourceServices dataSourceServices;

    ProgressBar progressBar;

    AlertDialog dialog;

    @AfterViews
    void bindAdapter() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(userPrefs.isFolder().get()) {
            tv_folderPath.setText(userPrefs.folder().get());
        }
        if(userPrefs.isFile().get()) {
            tv_filePath.setText(userPrefs.file().get());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        startActivity(navigationService.getActivity(this, item));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void pickFolder(View v) {
        pickFolderOrFile(true);
    }

    public void pickFile(View v) {
        pickFolderOrFile(false);
    }

    void pickFolderOrFile(boolean folder) {

        if (Build.VERSION.SDK_INT < 23) {

            if (folder)
                pickFolder();
            else
                pickFile();

        } else {

            if (storagePermissionAvailable()) {

                if (folder)
                    pickFolder();
                else
                    pickFile();

            } else {
                if (folder) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            SDCARD_PERMISSION_FOLDER);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            SDCARD_PERMISSION_FILE);
                }
            }

        }

    }

    boolean storagePermissionAvailable() {
        // For api Level 23 and above.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SDCARD_PERMISSION_FOLDER:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pickFolder();

                }
                break;

            case SDCARD_PERMISSION_FILE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pickFile();

                }
                break;
        }
    }


    void pickFolder() {

        Intent intent = new Intent(this, FolderPicker.class);
        startActivityForResult(intent, FOLDER_PICKER_CODE);
    }

    void pickFile() {
        Intent intent = new Intent(this, FolderPicker.class);

        //Optional

        intent.putExtra("title", "Select file to upload");
        intent.putExtra("location", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        intent.putExtra("pickFiles", true);

        //Optional

        startActivityForResult(intent, FILE_PICKER_CODE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FOLDER_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            String folderLocation = intent.getExtras().getString("data");
            tv_folderPath.setText(folderLocation);
            userPrefs.isFolder().put(true);
            userPrefs.folder().put(folderLocation);
        } else if (requestCode == FILE_PICKER_CODE && resultCode == Activity.RESULT_OK) {
            String folderLocation = intent.getExtras().getString("data");
            setProgressDialog();
            if(dataSourceServices.isValidDataSource(folderLocation)){
                tv_filePath.setText(folderLocation);
                userPrefs.isFile().put(true);
                userPrefs.file().put(folderLocation);
                dialog.dismiss();
                onValid();
            }
            else{
                tv_filePath.setText(R.string.file_not_selected);
                dialog.dismiss();
                onInValid();
            }

        }
    }

    protected void onComfirm(View v){
        userPrefs.isFile().put(true);
        userPrefs.file().put(tv_filePath.getText().toString());
        startActivity(new Intent(this, MainActivity_.class));
    }

    @UiThread
    protected void onValid(){
        Utils.setAlertDialog("Valid data source", "Valid data source", this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn_comfirm.setEnabled(true);
                dialog.dismiss();
            }
        }).show();
    }

    @UiThread
    protected void onInValid(){
        Utils.setAlertDialog("Warning", "Invalid data source", this).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn_comfirm.setEnabled(false);
                dialog.dismiss();
            }
        }).show();
    }


    public void setProgressDialog(){

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding,llPadding,llPadding,llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(110, 110);
        llParam.gravity = Gravity.CENTER;

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0,0,llPadding,0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if(window != null){
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = 756;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }
}
