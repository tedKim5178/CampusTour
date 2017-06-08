package com.mac.mk.campustour.activity.maketour;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.SettingsActivity;
import com.mac.mk.campustour.activity.api.MapApiConst;
import com.mac.mk.campustour.activity.api.OpenApiConst;
import com.mac.mk.campustour.activity.data.Restaurant;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.firebase.FirebaseUtils;
import com.mac.mk.campustour.activity.map.MapActivity;
import com.mac.mk.campustour.activity.search.Item;
import com.mac.mk.campustour.activity.search.OnFinishSearchListener;
import com.mac.mk.campustour.activity.search.Searcher;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mk on 2017. 6. 1..
 */

public class MakeTourActivity extends AppCompatActivity implements MapView.POIItemEventListener,
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    // Tag for Comment
    private final static String TAG = "MakeTourActivity";
    // For Intent
    private final static int MOVE_TO_MAP_ACTIVITY = 9001;

    // View Injection
    @Bind(R.id.writer_et)
    EditText writer_et;
    @Bind(R.id.name_et)
    EditText name_et;
    @Bind(R.id.sName_auto_et)
    AutoCompleteTextView sName_auto_et;
    @Bind(R.id.capacity_et)
    EditText capacity_et;
    @Bind(R.id.specification_et)
    EditText specification_et;
    @Bind(R.id.contact_et)
    EditText contact_et;

    @Bind(R.id.restaurant_et)
    TextView restaurant_et;
    @Bind(R.id.restaurant_add_btn)
    Button restaurant_add_btn;
    @Bind(R.id.register_tour_btn)
    Button register_tour_btn;
    @Bind(R.id.restaurant_add_layout)
    LinearLayout restaurant_add_layout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.select_date_btn)
    Button select_date_btn;
    @Bind(R.id.add_date_ll)
    LinearLayout add_date_ll;

    // Objects
    Tour tour = null;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
    private ArrayList<Restaurant> restaurantArrayList = null;

    private SharedPreferences setting = null;
    private HashMap<String, String> hm = null;
    private ArrayList<String> schools = null;
    private String writer = null;
    private String writerId = null;
    private String writerEmail = null;
    private String date = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maketour);

        //Butter Knife
        ButterKnife.bind(this);

        // Initialize Objects
        init();

        // Get School Information from the Internet
        Ion.with(this).load(OpenApiConst.BASE_URL + OpenApiConst.OPEN_API_KEY + OpenApiConst.TYPE
                + OpenApiConst.SERVICE + OpenApiConst.MIN + OpenApiConst.MAX).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                parseJSON(result);
            }
        });


    }

    public void parseJSON(String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject jsonObject1 = jsonObject.getJSONObject("SebcCollegeInfoKor");
            JSONArray jsonArray = jsonObject1.getJSONArray("row");

            // HashMap에 데이터 넣기
            for(int i=0; i<jsonArray.length(); i++){
                String sMainKey = jsonArray.getJSONObject(i).getString("MAIN_KEY");
                String sName = jsonArray.getJSONObject(i).getString("NAME_KOR");
                hm.put(sMainKey, sName);
                schools.add(sName);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, schools);
            sName_auto_et.setAdapter(adapter);

        }catch (JSONException jsonException){
            Log.d(TAG, "JSONTESTERROR : " + jsonException.getMessage());
        }
    }

    public void init(){
        tour = new Tour();
        restaurantArrayList = new ArrayList<>();
        hm = new HashMap<>();
        schools = new ArrayList<>();

        // - (하이픈) 자동으로
        contact_et.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        contact_et.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        setting = getSharedPreferences("setting", 0);
        writer = setting.getString("name", null);
        writerId = setting.getString("key", null);
        writerEmail = setting.getString("email", null);
        // set User Name
        writer_et.setText(writer);

        // set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CampusTour");

    }

    @OnClick({R.id.restaurant_add_btn, R.id.register_tour_btn, R.id.select_date_btn})
    public void onClick(View view){
        int id = view.getId();
        switch (id){
            case R.id.restaurant_add_btn:{
                moveToMapActivity();
                break;
            }
            case R.id.register_tour_btn:{
                // Setting tour information
                SettingTourInformation();
                // 데이터베이스에 저장
                FirebaseUtils.registerTourInfoToDatabase(this.tour);
                finish();
                break;
            }
            case R.id.select_date_btn:{
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(getResources().getColor(R.color.mdtp_accent_color));
                dpd.setCancelColor(getResources().getColor(R.color.mdtp_accent_color));
                dpd.setOkColor(getResources().getColor(R.color.mdtp_accent_color));
                dpd.show(getFragmentManager(), "Datepickerdialog");

                break;
            }
        }
    }

    public void SettingTourInformation(){

        this.tour.settName(name_et.getText().toString());
        this.tour.settSchoolName(sName_auto_et.getText().toString());
        this.tour.setCapacity(Integer.parseInt(capacity_et.getText().toString()));
        this.tour.settSpecification(specification_et.getText().toString());
        this.tour.setRestaurants(restaurantArrayList);
        this.tour.settContact(contact_et.getText().toString());
        this.tour.settWriterId(writerId);
        this.tour.settWriterEmail(writerEmail);
        this.tour.settWriter(writer);
        this.tour.settDate(date);
        // name 이용해서 tour의 key값 넣기
        String key = (String) getKeyFromValue(sName_auto_et.getText().toString());
        this.tour.settKey(key);
    }

    public void moveToMapActivity(){
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
                // Restaurant Information
                String title = data.getStringExtra("title");
                String address = data.getStringExtra("address");
                Double latitude = data.getDoubleExtra("latitude", 0);
                Double longitude = data.getDoubleExtra("longitude", 0);

                Restaurant restaurant = new Restaurant(title, address, latitude, longitude);
                restaurantArrayList.add(restaurant);

                // Button 생성
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView textView = new TextView(this);
                final Button button = new Button(this);
                LinearLayout.LayoutParams textViewLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);

                DisplayMetrics dm = getResources().getDisplayMetrics();
                int removeBtnSize = Math.round(35 * dm.density);
                int tvPaddingSize = Math.round(6 * dm.density);

                textViewLP.weight = 1;
                textView.setTextSize(16);
                textView.setTextColor(Color.BLACK);
                LinearLayout.LayoutParams buttonLP = new LinearLayout.LayoutParams(removeBtnSize, removeBtnSize);
                textView.setLayoutParams(textViewLP);
                textView.setPadding(tvPaddingSize, 0,0,0);
                button.setLayoutParams(buttonLP);
                button.setBackgroundResource(R.drawable.ic_remove);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 뷰 삭제
                        LinearLayout parent = (LinearLayout)button.getParent();
                        restaurant_add_layout.removeView(parent);

                        // TODO:// 버튼, 텍스트뷰 관리 다시 해서 arraylist 에서 remove
                    }
                });
                textView.setId(restaurantArrayList.size());
                linearLayout.addView(textView);
                linearLayout.addView(button);
                restaurant_add_layout.addView(linearLayout);

                // Setting layout
                TextView setting_tv = (TextView) findViewById(restaurantArrayList.size());
                setting_tv.setText(title);

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

    public Object getKeyFromValue(Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = year+"년 "+(monthOfYear+1)+"월 "+dayOfMonth + "일";
        Toast.makeText(this, date + " 선택!", Toast.LENGTH_SHORT).show();

        // 동적으로 날짜 정보 추가해주기


        TextView date_tv = new TextView(this);

        LinearLayout.LayoutParams date_ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        date_tv.setText(date);
        date_tv.setTextColor(Color.BLACK);
        date_tv.setTextSize(18);
        date_tv.setGravity(Gravity.CENTER);
        date_tv.setLayoutParams(date_ll);

        add_date_ll.addView(date_tv);

        select_date_btn.setVisibility(View.GONE);

    }

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        Toast.makeText(this, time, Toast.LENGTH_SHORT).show();

    }
}
