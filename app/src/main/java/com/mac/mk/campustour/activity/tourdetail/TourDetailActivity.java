package com.mac.mk.campustour.activity.tourdetail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.api.MapApiConst;
import com.mac.mk.campustour.activity.api.OpenApiConst;
import com.mac.mk.campustour.activity.data.School;
import com.mac.mk.campustour.activity.data.SchoolNameEngKor;
import com.mac.mk.campustour.activity.data.Tour;
import com.squareup.picasso.Picasso;


import net.daum.android.map.coord.MapCoord;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017. 6. 4..
 */

public class TourDetailActivity extends AppCompatActivity implements MapView.POIItemEventListener
    , MapView.MapViewEventListener{

    // Tag for commnet
    private static final String TAG = "TourDetailActivity";

    // View Injection
    @Bind(R.id.sLogo_iv)
    ImageView sLogo_iv;
    @Bind(R.id.show_sName_tv)
    TextView show_sName_tv;
    @Bind(R.id.show_sAddress_tv)
    TextView show_sAddress_tv;
    @Bind(R.id.show_sZipcode_tv)
    TextView show_sZipcode_tv;
    @Bind(R.id.show_sTelephone_tv)
    TextView show_sTelephone_tv;
    @Bind(R.id.show_sWebsite_tv)
    TextView show_sWebsite_tv;
    @Bind(R.id.show_tour_detail_tv)
    TextView show_tour_detail_tv;
    @Bind(R.id.show_writer_name_tv)
    TextView show_writer_name_tv;
    @Bind(R.id.show_writer_contact_tv)
    TextView show_writer_contact_tv;
    @Bind(R.id.show_writer_email_tv)
    TextView show_writer_email_tv;
    @Bind(R.id.tour_detail_layout)
    LinearLayout tour_detail_layout;

    // Objects
    Tour tour;
    School[] schools = null;
    MapView mapView = null;
    private RelativeLayout mapViewContainer;
    private SharedPreferences setting = null;
    private String tourKey = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        // View Injection
        ButterKnife.bind(this);

        init();
        mapReady();
    }


    public void init(){

        // Get Tour data through intent
        Intent intent = getIntent();
        tourKey = intent.getStringExtra("key");
        Log.d(TAG, "투어키 " + tourKey);
        tour = (Tour) intent.getSerializableExtra("tour");

        // Extract key data to get school information from the Internet using JSON
        final String key = tour.gettKey();
        Ion.with(this).load(OpenApiConst.BASE_URL + OpenApiConst.OPEN_API_KEY + OpenApiConst.TYPE
                + OpenApiConst.SERVICE + OpenApiConst.MIN + OpenApiConst.MIN + "/" + key).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                Log.d(TAG, "gsonTest Ion Completed");
                parseJSON(result);
            }
        });

        // get UserId
        setting = getSharedPreferences("setting", 0);
        String userId = setting.getString("key", null);

        // 만약 내가 생성한 투어라면 현재 레이아웃에 버튼을 하나 더 생성해준다
        if(tour.gettWriterId().equals(userId) && !tour.isOccupied()){
            Button button = new Button(this);
            LinearLayout.LayoutParams ll= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setText("투어 모집 마감");
            button.setTextColor(Color.argb(255,255,255,255));
            button.setTextSize(20);
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            button.setLayoutParams(ll);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "??", Toast.LENGTH_SHORT).show();
                    // 파이어베이스 접근해서 tour정보를 마감으로 바꿔줘야함..!
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference occupiedRef = database.getReference("tour").child(tourKey).child("occupied");
                    occupiedRef.setValue(true);
                }
            });
            tour_detail_layout.addView(button);
        }
    }
    public void showTourDataView(){
        show_sName_tv.setText(schools[0].getNAME_KOR());
        show_sAddress_tv.setText(schools[0].getADD_KOR());
        show_sZipcode_tv.setText(schools[0].getPOSTCODE());
        show_sTelephone_tv.setText(schools[0].getTEL());
        show_sWebsite_tv.setText(schools[0].getHP());

        // tour detail
        show_tour_detail_tv.setText(tour.gettSpecification());

        // writer detail
        show_writer_name_tv.setText(tour.gettWriter());
        show_writer_contact_tv.setText(tour.gettContact());
        show_writer_email_tv.setText(tour.gettWriterEmail());

        // universito logo
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://firebase-campustour.appspot.com");
        String path = SchoolNameEngKor.schoolNameEngKor.get(tour.gettSchoolName()) + ".png";
        StorageReference imageRef = storageRef.child(path);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "바보바보 성공 " + uri);
                Picasso.with(getApplicationContext()).load(uri).into(sLogo_iv);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "바보바보 !  " + e.getMessage());
            }
        });

    }

    public void parseJSON(String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject jsonObject1 = jsonObject.getJSONObject("SebcCollegeInfoKor");
            JSONArray jsonArray = jsonObject1.getJSONArray("row");
            // jsonArray를 GSON 객체 이용해서 변환
            Gson gson = new Gson();
            schools = gson.fromJson(jsonArray.toString(), School[].class);

            // show schools data to user
            showTourDataView();

        }catch (JSONException jsonException){
            Log.d(TAG, jsonException.getMessage());
        }

    }

    public void mapReady(){
        mapView = new MapView(this);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);
        mapViewContainer = (RelativeLayout) findViewById(R.id.show_map_view);
        mapViewContainer.addView(mapView);

        Log.d(TAG, "마커테스트 : ");

    }

    public void mapMarker(){

        Log.d(TAG, "마커테스트 : " + tour.getRestaurants().size());
        MapPointBounds mapPointBounds = new MapPointBounds();
        for(int i=0; i< tour.getRestaurants().size(); i++){

            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(tour.getRestaurants().get(i).getLatitude(), tour.getRestaurants().get(i).getLongitude());
            Log.d(TAG, "마커테스트 : " + tour.getRestaurants().get(i).getLatitude());
            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(tour.getRestaurants().get(i).getName());
            marker.setTag(i);
            marker.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker.setCustomImageResourceId(R.drawable.ic_marker);
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            marker.setCustomSelectedImageResourceId(R.drawable.ic_marker_selected);
            marker.setCustomImageAutoscale(false);
            marker.setCustomImageAnchor(0.5f, 1.0f);


            mapView.addPOIItem(marker);
        }
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            for(int i=0; i<poiItems.length; i++){
                mapView.selectPOIItem(poiItems[i], false);
            }
        }
        mapView.fitMapViewAreaToShowAllPOIItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        mapMarker();
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
