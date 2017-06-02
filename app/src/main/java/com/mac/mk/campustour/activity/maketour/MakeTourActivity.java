package com.mac.mk.campustour.activity.maketour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.SettingsActivity;
import com.mac.mk.campustour.activity.api.MapApiConst;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.firebase.FirebaseUtils;
import com.mac.mk.campustour.activity.map.MapActivity;
import com.mac.mk.campustour.activity.search.Item;
import com.mac.mk.campustour.activity.search.OnFinishSearchListener;
import com.mac.mk.campustour.activity.search.Searcher;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mk on 2017. 6. 1..
 */

public class MakeTourActivity extends AppCompatActivity implements MapView.POIItemEventListener{

    // Tag for Comment
    private final static String TAG = "MakeTourActivity";
    // For Intent
    private final static int MOVE_TO_MAP_ACTIVITY = 9001;

    // View Injection
    @Bind(R.id.name_et)
    EditText name_et;
    @Bind(R.id.title_et)
    EditText title_et;
    @Bind(R.id.school_et)
    EditText school_et;
    @Bind(R.id.capacity_et)
    EditText capacity_et;
    @Bind(R.id.specification_et)
    EditText specification_et;

    @Bind(R.id.restaurant_et)
    EditText restaurant_et;
    @Bind(R.id.map_view)
    RelativeLayout mapViewContainer;
    @Bind(R.id.restaurant_add_btn)
    Button restaurant_add_btn;
    @Bind(R.id.register_tour_btn)
    Button register_tour_btn;

    Tour tour = null;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

    MapView mapView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maketour);

        //Butter Knife
        ButterKnife.bind(this);


        // TODO :: 맵객체를 여기서 초기화 시켜줄 필요는 없을 거 같고 빈 이미지를 넣어주면 될 거 같다.
        mapView = new MapView(this);
        mapView.setFocusableInTouchMode(false);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);

        mapViewContainer = (RelativeLayout) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        tour = new Tour();
    }

    @OnClick({R.id.restaurant_add_btn, R.id.register_tour_btn})
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.restaurant_add_btn:{
                moveToMapActivity();
                break;
            }
            case R.id.register_tour_btn:{

                SettingTourInformation();

                // 데이터베이스에 저장
                FirebaseUtils.registerTourInfoToDatabase(this.tour);
                break;
            }
        }
    }

    public void SettingTourInformation(){
        this.tour.settName(name_et.getText().toString());
        this.tour.settSchoolName(title_et.getText().toString());
        this.tour.settSchoolName(school_et.getText().toString());
        this.tour.setCapacity(Integer.parseInt(capacity_et.getText().toString()));
        this.tour.settSpecification(specification_et.getText().toString());
    }

    public void moveToMapActivity(){
        mapViewContainer.removeView(mapView);
        mapViewContainer = null;
        mapView = null;
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        startActivityForResult(intent, MOVE_TO_MAP_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode == MOVE_TO_MAP_ACTIVITY)
        {
            if(resultCode == RESULT_OK){
                Double latitude = data.getDoubleExtra("latitude", 0);
                Double longitude = data.getDoubleExtra("longitude", 0);
                String title = data.getStringExtra("title");
                String address = data.getStringExtra("address");
                this.tour.setLatitute(latitude);
                this.tour.setLongitude(longitude);
                this.tour.settRestaurantName(title);
                this.tour.settAddress(address);
            }
        }
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
}
