package com.mac.mk.campustour.activity.tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.maketour.MakeTourActivity;
import com.mac.mk.campustour.activity.tour.adapter.TourAdapter;
import com.mac.mk.campustour.activity.tourdetail.TourDetailActivity;
import com.melnykov.fab.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017. 5. 31..
 */

public class TourActivity extends AppCompatActivity implements TourAdapter.ListItemClickListener{

    private static final String TAG = "TourActivity";

    // View Injection
    @Bind(R.id.tour_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.floating_action_btn)
    FloatingActionButton mFloatingBtn;

    // Objects
    private TourAdapter tourAdapter;
    private ArrayList<Tour> tourItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        tourItemList = new ArrayList();

        // TODO:: 뿌려줄 리스트는 파이어베이스에서 플랜정보이다.
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("tour");
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Tour tour = dataSnapshot.getValue(Tour.class);
                tourItemList.add(tour);
                Log.d(TAG, "hello : " + tour.gettName()  + " , " + tour.gettSchoolName() + tourItemList.size());
                Log.d(TAG, "hello : " + tour.getRestaurants().get(0).getAddress());
                tourAdapter.setTourItemList(tourItemList);
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
            public void onCancelled(DatabaseError databaseError) { }
        });

        tourAdapter = new TourAdapter(tourItemList, getApplicationContext(), this);
        mRecyclerView.setAdapter(tourAdapter);

        mFloatingBtn.attachToRecyclerView(mRecyclerView);

        // 이벤트 적용
        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeTourActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onListItemClick(Tour tour) {
        Intent intent = new Intent(getApplicationContext(), TourDetailActivity.class);
        intent.putExtra("tour", tour);
        startActivity(intent);
    }
}
