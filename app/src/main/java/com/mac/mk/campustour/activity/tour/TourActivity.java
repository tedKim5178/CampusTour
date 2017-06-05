package com.mac.mk.campustour.activity.tour;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.data.User;
import com.mac.mk.campustour.activity.maketour.MakeTourActivity;
import com.mac.mk.campustour.activity.tour.adapter.TourAdapter;
import com.mac.mk.campustour.activity.tour.fragment.HighSchoolFragment;
import com.mac.mk.campustour.activity.tour.fragment.UniversityFragment;
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
    @Bind(R.id.fragment_container)
    FrameLayout fragment_container;

    // Objects

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        ButterKnife.bind(this);

        // get user type data from intent
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        int type = user.getType();

        Log.d(TAG, "타입테스트 in TourActivity " + type);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if(type == 0){
            fragmentTransaction.add(R.id.fragment_container, new HighSchoolFragment());
        }else{
            fragmentTransaction.add(R.id.fragment_container, new UniversityFragment());
        }
        fragmentTransaction.commit();

    }

    @Override
    public void onListItemClick(Tour tour) {
        // TODO:: 옵저버 패턴 공부.!
    }
}
