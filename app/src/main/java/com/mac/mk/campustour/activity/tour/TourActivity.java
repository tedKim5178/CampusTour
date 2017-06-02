package com.mac.mk.campustour.activity.tour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.maketour.MakeTourActivity;
import com.mac.mk.campustour.activity.tour.adapter.TourAdapter;
import com.melnykov.fab.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017. 5. 31..
 */

public class TourActivity extends AppCompatActivity{

    private static final String TAG = "TourActivity";

    @Bind(R.id.tour_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.floating_action_btn)
    FloatingActionButton mFloatingBtn;

    private TourAdapter tourAdapter;
    private ArrayList tourItemList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        ButterKnife.bind(this);

        // TODO :: make recyclerview and adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        tourItemList = new ArrayList();

        // TODO:: 뿌려줄 리스트는 파이어베이스에서 플랜정보이다.
        tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");
        tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");
        tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");tourItemList.add("1");


        tourAdapter = new TourAdapter(tourItemList);
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
}
