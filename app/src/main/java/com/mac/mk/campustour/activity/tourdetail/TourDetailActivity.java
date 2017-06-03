package com.mac.mk.campustour.activity.tourdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;

/**
 * Created by mk on 2017. 6. 4..
 */

public class TourDetailActivity extends AppCompatActivity{

    // Tag for commnet
    private static final String TAG = "TourDetailActivity";
    // Objects
    Tour tour;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        init();

    }
    public void init(){
        Intent intent = getIntent();
        tour = (Tour) intent.getSerializableExtra("tour");
        String sName = tour.gettSchoolName();
        Log.d(TAG, "디테일 : " + sName);
    }
}
