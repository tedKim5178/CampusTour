package com.mac.mk.campustour.activity.tour.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.tour.adapter.TourAdapter;
import com.mac.mk.campustour.activity.tourdetail.TourDetailActivity;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mk on 2017. 6. 5..
 */

public class HighSchoolFragment extends Fragment implements TourAdapter.ListItemClickListener{

    // TAG for comment
    private final static String TAG = "HighSchoolFragment";

    // View Injection
    @Bind(R.id.tour_recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.search_tourlist_btn)
    Button search_tourlist_btn;
    @Bind(R.id.search_tourlist_et)
    EditText search_tourlist_et;

    // Objects
    private TourAdapter tourAdapter;
    private ArrayList<Tour> tourItemList = new ArrayList<>();
    private LinkedHashMap<String, Tour> lhm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_highschool, container, false);

        ButterKnife.bind(this, view);

        return view;
    }
    @Override
    public void onResume() {

        tourItemList.clear();
        // get tour data from firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("tour");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                HashMap hm = (HashMap) dataSnapshot.getValue();
//                LinkedHashMap newMap = new LinkedHashMap<>(hm);
//
//                Set<Map.Entry<String, LinkedHashMap>> set = newMap.entrySet();
//
//                Iterator<Map.Entry<String, LinkedHashMap>> iterator = set.iterator();
//                while(iterator.hasNext()){
//                    Map.Entry entry = (Map.Entry) iterator.next();
//                    String tempKey = (String) entry.getKey();
//                    HashMap tempValue = (HashMap) entry.getValue();
//                    Gson gson = new Gson();
//                    String json = gson.toJson(tempValue);
//                    Tour tempTour = gson.fromJson(json, Tour.class);
//
//                    lhm.put(tempKey, tempTour);
//                }

                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Log.d(TAG, "파이어베이스테스트 : " + child.getValue());
                    Gson gson = new Gson();
                    String json = gson.toJson(child.getValue());
                    Tour tempTour = gson.fromJson(json, Tour.class);
                    lhm.put(child.getKey(), tempTour);
                    tourItemList.add(tempTour);
                }
                if(tourAdapter != null){
                    tourAdapter.notifyDataSetChanged();
                }

//                for(DataSnapshot child : dataSnapshot.getChildren()){
//                    Log.d(TAG, "파이어베이스 테스트 신 포문 : " + child.getKey());
//                    if(child.getKey().equals("restaurants")){
//                        Restaurant restaurant = (Restaurant) child.getValue();
//                    }else{
//
//                    }
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lhm = new LinkedHashMap<>();

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

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "바보바보바보! " + dataSnapshot.getKey());
                if(lhm.get(dataSnapshot.getKey()) != null){
                    Tour tour = lhm.get(dataSnapshot.getKey());
                    tour.setOccupied((boolean)dataSnapshot.child("occupied").getValue());
                }
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

    }

    @OnClick({R.id.search_tourlist_btn, R.id.refresh_btn})
    public void onClick(View view){
        int viewId = view.getId();
        switch (viewId){
            case R.id.search_tourlist_btn : {
                // 클릭하면
                String query = search_tourlist_et.getText().toString();
                if(query.length() == 0){
                    Toast.makeText(getActivity().getApplicationContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }else{
                    // 검색어를 통해서 리스트 정렬
                    // 전체 투어 목록은..해쉬맵에 있음
                    tourItemList.clear();

                    Collection v = lhm.values();
                    Iterator iterator = v.iterator();
                    while(iterator.hasNext()){
                        Tour tempTour = (Tour)iterator.next();
                        Log.d(TAG, "test" + tempTour.gettSchoolName());
                        if(tempTour.gettSchoolName().contains(query)){

                            tourItemList.add(tempTour);

                        }
                    }

                    tourAdapter.setTourItemList(tourItemList);
                    tourAdapter.notifyDataSetChanged();
                }
                break;
            }
            case R.id.refresh_btn :{
                // refresh 즉 모든 투어 데이터 다 보여주기
                tourItemList.clear();

                Collection v = lhm.values();
                Iterator iterator = v.iterator();
                while(iterator.hasNext()){
                    Tour tempTour = (Tour)iterator.next();
                    tourItemList.add(tempTour);
                    Log.d(TAG, "투어테스트 : " + tempTour.gettSchoolName());
                }
                tourAdapter.setTourItemList(tourItemList);
                tourAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onListItemClick(Tour tour) {
        Intent intent = new Intent(getActivity().getApplicationContext(), TourDetailActivity.class);
        intent.putExtra("tour", tour);
        String key = (String) getKeyFromValue(tour);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    public  Object getKeyFromValue(Object value) {
        for (Object o : lhm.keySet()) {
            if (lhm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

}
