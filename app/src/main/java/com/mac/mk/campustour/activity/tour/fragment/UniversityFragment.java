package com.mac.mk.campustour.activity.tour.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.maketour.MakeTourActivity;
import com.mac.mk.campustour.activity.tour.adapter.TourAdapter;
import com.mac.mk.campustour.activity.tourdetail.TourDetailActivity;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017. 6. 5..
 */

public class UniversityFragment extends Fragment implements TourAdapter.ListItemClickListener{

    // TAG for comment
    private final static String TAG = "UniversityFragment";

    // View Injection
    @Bind(R.id.tour_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.floating_action_btn)
    FloatingActionButton mFloatingBtn;


    // Objects
    private TourAdapter tourAdapter;
    private ArrayList<Tour> tourItemList;
    private HashMap<String, Tour> hm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_university, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tourItemList = new ArrayList<>();
        hm = new HashMap<>();

        // recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // get tour data from firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("tour");
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Tour tour = dataSnapshot.getValue(Tour.class);
                hm.put(dataSnapshot.getKey(), tour);
                tourItemList.add(tour);
                tourAdapter.setTourItemList(tourItemList);
                tourAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "바보바보바보! " + dataSnapshot.getKey());
                Tour tour = hm.get(dataSnapshot.getKey());
                int index = tourItemList.indexOf(tour);
                tourItemList.get(index).setOccupied((boolean)dataSnapshot.child("occupied").getValue());
                tourAdapter.setTourItemList(tourItemList);
                tourAdapter.notifyDataSetChanged();
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

        tourAdapter = new TourAdapter(tourItemList, getActivity().getApplicationContext(), this);
        mRecyclerView.setAdapter(tourAdapter);

        mFloatingBtn.attachToRecyclerView(mRecyclerView);

        // 이벤트 적용
        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MakeTourActivity.class);
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    public void onListItemClick(Tour tour) {
        Log.d(TAG, "프레그먼트테스트 onListItemClick in UniversityFragment");
        Intent intent = new Intent(getActivity().getApplicationContext(), TourDetailActivity.class);
        intent.putExtra("tour", tour);
        startActivity(intent);
    }


}
