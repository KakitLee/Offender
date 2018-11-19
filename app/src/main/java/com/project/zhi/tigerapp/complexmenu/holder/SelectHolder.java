package com.project.zhi.tigerapp.complexmenu.holder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.project.zhi.tigerapp.Adapter.CustomInfoWindowAdapter;
import com.project.zhi.tigerapp.Adapter.PlaceAutocompleteAdapter;
import com.project.zhi.tigerapp.Entities.PlaceInfo;
import com.project.zhi.tigerapp.R;
import com.project.zhi.tigerapp.Utils.Utils;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

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

    private String mRetGender = "";
    private String mRetType = "";
    private ImageView mTeacherGenderArrorImage;
    private ImageView mTypeArrorImage;
    private EditText mLongitudeView;
    private EditText mLatitudeView;
    private EditText mRadiusView;
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mSelectLocation, mSelectCancel;

    private OnLocationSearchBtnListener onLocationSearchBtnListener;
    private GoogleMap mMap;
    public boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private PlaceInfo mPlace;
    private Button mBtnSearchBack;
    private Button mBtnSelectMap;
    private LatLng selectedLatLng;
    private LatLng currentLatLng;
    AppCompatActivity act;
    RelativeLayout mapLayout;
    LinearLayout layout_location_search;

    public SelectHolder(Context context) {
        super(context);
        if (act == null) act = (AppCompatActivity) mContext;
    }

    public SelectHolder(Context context, boolean mLocationPermissionsGranted) {
        super(context);
        this.mLocationPermissionsGranted = mLocationPermissionsGranted;
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);
            try {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                mPlace.setAddress(place.getAddress().toString());
                mPlace.setId(place.getId());
                mPlace.setLatlng(place.getLatLng());
                mPlace.setRating(place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri());
            } catch (NullPointerException e) {
            }
            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();
            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private void geoLocate() {
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(mContext);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {

        }
        if (list.size() > 0) {
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    private void initSearch(View view) {
        mSearchText = view.findViewById(R.id.input_search);
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);
        mGoogleApiClient = new GoogleApiClient
                .Builder(act)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(act, connectionResult -> {
                    Utils.setAlertDialog("Error", "Connection to Map service failed pls check your internet connection and usage quota", mContext);
                })
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(act, mGoogleApiClient, LAT_LNG_BOUNDS, null);
        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                geoLocate();
            }
            return false;
        });

        mGps = view.findViewById(R.id.ic_gps);
        mGps.setOnClickListener(view1 -> getDeviceLocation(false));

        mSelectLocation = view.findViewById(R.id.ic_btn_location_select);
        mSelectLocation.setOnClickListener(view2 -> selectCurrentLocation(true));

        mSelectCancel = view.findViewById(R.id.ic_btn_location_cancel);
        mSelectCancel.setOnClickListener(view2 -> selectCurrentLocation(false));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng position = marker.getPosition();
                moveCamera(position, DEFAULT_ZOOM, "");
            }
        });
        hideSoftKeyboard();
    }

    private void hideSoftKeyboard() {
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.layout_holder_select, null);
        if (act == null) act = (AppCompatActivity) mContext;
        initSearchBox(view);
        initSearchBtns(view);

        if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
        }

        if (mMap == null && mLocationPermissionsGranted) {
            android.support.v4.app.FragmentManager fmanager = act.getSupportFragmentManager();
            Fragment fragment = fmanager.findFragmentById(R.id.map);
            SupportMapFragment supportmapfragment = (SupportMapFragment) fragment;
            supportmapfragment.getMapAsync(googleMap -> {
                mMap = googleMap;
                getDeviceLocation(true);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                initSearch(view);
            });
        }
        return view;
    }

    private void initSearchBtns(View view) {
        mapLayout = view.findViewById(R.id.layout_map);
        layout_location_search = view.findViewById(R.id.layout_location_search);
        layout_location_search.setVisibility(View.VISIBLE);
        mapLayout.setVisibility(View.GONE);
        mBtnSearchBack = view.findViewById(R.id.btn_location_Search_Back);
        mBtnSearchBack.setOnClickListener(view1 -> {
            selectCurrentLocation(true);
        });
        mBtnSelectMap = view.findViewById(R.id.btn_location_select);
        mBtnSelectMap.setOnClickListener(view1 -> {
            layout_location_search.setVisibility(View.GONE);
            mapLayout.setVisibility(View.VISIBLE);
        });
    }

    private void selectCurrentLocation(boolean updateLatLng) {
        if (updateLatLng) refreshLatLng();
        mapLayout.setVisibility(View.GONE);
        layout_location_search.setVisibility(View.VISIBLE);
    }

    private void initSearchBox(View view) {
        mLongitudeView = view.findViewById(R.id.tv_longitude);
        mLatitudeView = view.findViewById(R.id.tv_latitude);
        mRadiusView = view.findViewById(R.id.tv_radius);

        mSureBtn = view.findViewById(R.id.btn_location_Search);

        mSureBtn.setOnClickListener(v -> {
            Double longitude = NumberUtils.toDouble(mLongitudeView.getText().toString());
            Double latitude = NumberUtils.toDouble(mLatitudeView.getText().toString());
            Double radius = NumberUtils.toDouble(mRadiusView.getText().toString());
            onLocationSearchBtnListener.OnLocationSearchBtnListener(longitude, latitude, radius);
        });
    }

    private void refreshLatLng() {
        mLongitudeView.setText(String.valueOf(selectedLatLng.longitude));
        mLatitudeView.setText(String.valueOf(selectedLatLng.latitude));
    }

    private void clearGenderInfo(RadioItemView radioItemView, String text) {

        if (mIsFirstExtendGender) {
            mIsFirstExtendGender = false;
            mGenderRecorder = mGenderNoRuleRIView;
        }

        if (radioItemView != mGenderRecorder && mGenderRecorder != null) {
            mGenderRecorder.setSelected(false);
        }
        mGenderRecorder = radioItemView;
        mGenderView.setVisibility(View.GONE);
        mTeacherGenderText.setText(text);

        mTeacherGenderText.setTextColor(mContext.getResources().getColor(R.color.text_color_gey));
        mTeacherGenderArrorImage.setImageResource(R.mipmap.ic_down);
    }

    private void clearTypeInfo(RadioItemView radioItemView, String text) {

        if (mIsFirstExtendType) {
            mIsFirstExtendType = false;
            mTypeRecorder = mTypeNoRuleRIView;
        }

        if (radioItemView != mTypeRecorder && mTypeRecorder != null) {
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

    public interface OnLocationSearchBtnListener {
        void OnLocationSearchBtnListener(Double longitude, Double latitude, Double radius);
    }

    public void setOnLocationSearchBtnListner(OnLocationSearchBtnListener onSearchBtnListener) {
        this.onLocationSearchBtnListener = onSearchBtnListener;
    }

    private void getCurrentLocation() {
        LocationManager mLocationManager = (LocationManager) act.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });

    }
    private void getDeviceLocation(boolean init){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        try{
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Location currentLocation = (Location) task.getResult();
                    moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                            DEFAULT_ZOOM,"My Location");
                }else{
                    Toast.makeText(mContext, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (SecurityException e){

        }
    }
    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext));

        if(placeInfo != null){
            try{
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Longitude: " + placeInfo.getLatlng().longitude + "\n" +
                        "Latitude: " + placeInfo.getLatlng().latitude+ "\n";
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .draggable(true)
                        .snippet(snippet);
                mMap.addMarker(options);
                selectedLatLng = latLng;
            }catch (NullPointerException e){
            }
        }else{
            mMap.addMarker(new MarkerOptions().position(latLng));
        }
        hideSoftKeyboard();
    }


    private void moveCamera(LatLng latLng, float zoom,String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext));
        String snippet = "Address: My location" + "\n" +
                "Longitude: " + latLng.longitude + "\n" +
                "Latitude: " + latLng.latitude + "\n";
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(title)
                .snippet(snippet);
        mMap.addMarker(options);
        selectedLatLng = latLng;
        hideSoftKeyboard();
    }
}
