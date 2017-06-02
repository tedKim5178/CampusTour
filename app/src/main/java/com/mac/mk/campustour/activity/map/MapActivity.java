package com.mac.mk.campustour.activity.map;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.api.MapApiConst;
import com.mac.mk.campustour.activity.maketour.MakeTourActivity;
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
 * Created by mk on 2017. 6. 2..
 */

public class MapActivity extends AppCompatActivity implements MapView.POIItemEventListener{

    // Tag for commnet
    private static final String TAG = "MapActivity";

    // View Injection
    @Bind(R.id.restaurant_query_et)
    EditText restaurant_query_et;
    @Bind(R.id.restaurant_query_btn)
    Button restaurant_query_btn;
    @Bind(R.id.map_view)
    RelativeLayout map_view;
    @Bind(R.id.restaurant_title_tv)
    TextView restaurant_title_tv;
    @Bind(R.id.restaurant_address_tv)
    TextView restaurant_address_tv;
    @Bind(R.id.select_map_btn)
    Button select_map_btn;

    // Objects
    private MapView mapView;
    private RelativeLayout mapViewContainer;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

    // 위도 경도 음식점이름 주소
    private double latitude;
    private double longitude;
    private String title;
    private String address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);
        mapReady();
    }

    public void mapReady(){
        mapView = new MapView(this);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
        mapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mapView.setPOIItemEventListener(this);
        mapViewContainer = (RelativeLayout) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
    }

    @OnClick({R.id.restaurant_query_btn, R.id.select_map_btn})
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.restaurant_query_btn :{
                // query 이용해서 데이터 가져 오는 거
                Log.d(TAG, "clickTest : ");
                showMaps();
                break;
            }
            case R.id.select_map_btn :{
                // 종료
                goBackToMakeTourActivity();
                break;
            }
        }
    }

    public void showMaps(){
        String query = restaurant_query_et.getText().toString();
        if (query == null || query.length() == 0) {
            Toast.makeText(getApplicationContext(), "검색어를 입력하세요. ", Toast.LENGTH_SHORT);
            return;
        }

        hideSoftwareKeyboard();

        MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
        double latitude = geoCoordinate.latitude; // 위도
        double longitude = geoCoordinate.longitude; // 경도

        int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
        int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
        String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;

        Searcher searcher = new Searcher();
        searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
            @Override
            public void onSuccess(List<Item> itemList) {
                mapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                showResult(itemList); // 검색 결과 보여줌
            }

            @Override
            public void onFail() {
            }
        });
    }

    public void goBackToMakeTourActivity(){
        // Intent to go back to MakeTourActivity
        Intent intent = new Intent();
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("title", title);
        intent.putExtra("address", address);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void hideSoftwareKeyboard(){

    }

    public void showResult(List<Item> itemList){
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.ic_marker);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.ic_marker_selected);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
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
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        latitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        longitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;
        int tag = mapPOIItem.getTag();
        Item itemFromTag = mTagItemMap.get(tag);

        //Setting textView information
        title = itemFromTag.title;
        address = itemFromTag.address;

        // set textViews
        restaurant_title_tv.setText(title);
        restaurant_address_tv.setText(address);
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
