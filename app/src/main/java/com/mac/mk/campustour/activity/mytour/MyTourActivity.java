package com.mac.mk.campustour.activity.mytour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.tour.adapter.TourAdapter;
import com.mac.mk.campustour.activity.tourdetail.TourDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017. 6. 6..
 */

public class MyTourActivity extends AppCompatActivity implements TourAdapter.ListItemClickListener{

    // TAG for comments
    private final static String TAG = "MyTourActivity";

    // View injection
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.mytour_recyclerview)
    RecyclerView mRecyclerView;

    // Objects
    private SharedPreferences setting = null;
    private TourAdapter tourAdapter = null;
    private ArrayList<Tour> tourItemList = null;
    private String userId = null;

    // String array for gathering keys
    HashMap<String, Tour> hm = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytour);

        ButterKnife.bind(this);

        init();
    }

    public void init(){

        hm = new HashMap<>();

        // set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CampusTour");

        // recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        tourItemList = new ArrayList<>();
        tourAdapter = new TourAdapter(tourItemList, this, this);
        mRecyclerView.setAdapter(tourAdapter);

        // query문을 이용해서 파이어베이스 데이터베이스에서 데이터를 가져오자 필요한것은 현재 나의 getId
        setting = getSharedPreferences("setting", 0);
        userId = setting.getString("key", null);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tourRef = database.getReference("tour");

        tourRef.orderByChild("tWriterId").equalTo(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Tour tour = dataSnapshot.getValue(Tour.class);
                hm.put(dataSnapshot.getKey(), tour);
                tourItemList.add(tour);
                tourAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "바보바보바보1" + dataSnapshot.getKey());
//
//                for (DataSnapshot child: dataSnapshot.getChildren()) {
//
//                    // TODO:: 방법이 두가지인데 어떤게 더 효율적인지 모르겠다.. 애초에 디비에 push로 넣어주면 안되는건가..?
//                    // child의 key를 저장하자
//                    keys.add(child.getKey());
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
        });
    }

    @Override
    public void onListItemClick(Tour tour) {
        Log.d(TAG, "MytourActivity onListItemClick");
        Intent intent = new Intent(this, TourDetailActivity.class);
        // Key값도 넘기자...!
        String key = (String) getKeyFromValue(tour);
        Log.d(TAG, "바보바보 " + key);
        intent.putExtra("key", key);
        intent.putExtra("tour", tour);
        startActivity(intent);
    }

    public  Object getKeyFromValue(Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }
}
