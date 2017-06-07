package com.mac.mk.campustour.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.User;
import com.mac.mk.campustour.activity.tour.TourActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017. 5. 31..
 */

public class SettingsActivity extends AppCompatActivity{

    // View Injection
    @Bind(R.id.userTypeSpinner)
    Spinner userTypeSpinner;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private static final int HiGHSCHOOL = 0;
    private static final int UNIVERSITY = 1;
    private static final String TAG = "SettingsActivity";

    private SharedPreferences setting;
    private SharedPreferences.Editor editor;

    private String key;
    private String email;
    private String name;

    private int flag = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        // set toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CampusTour");
        
        // Preference 에서 데이터 불러오자
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();
        key = setting.getString("key", null);
        email = setting.getString("email", null);
        name = setting.getString("name", null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference table = database.getReference("user");
        Query query = table.orderByKey().equalTo(key);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do something with the individual "issues"
                        User user = issue.getValue(User.class);
                        // user의 type에 따라서 다른 값을 보여준다..!
                        Log.d(TAG, "타입테스트 : " + user.getType() + user.getEmail());
                        Intent intent = new Intent(getApplicationContext(), TourActivity.class);
                        intent.putExtra("user", user);
                        startActivityForResult(intent, 0);
                    }
                }else{
                    userTypeSpinner.performClick();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        init();
    }

    public void init(){
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(flag == 1){
                    if(position == 0) {
                        Toast.makeText(getApplicationContext(), "고등학생", Toast.LENGTH_SHORT).show();
                        editor.putInt("type", HiGHSCHOOL);
                        editor.commit();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "대학생", Toast.LENGTH_SHORT).show();
                        editor.putInt("type", UNIVERSITY);
                        editor.commit();
                    }


                    int type = setting.getInt("type", 0);
                    Log.d(TAG, "타입테스트 : " + type);

                    // TODO :: 우선 데이터베이스 있는지 없는지 확인 하고 진행해야한다. Query문으로 해당 id값을 검색해보는걸로 될 듯 하다..!
                    // 값 정상적으로 들어오니까 파이어베이스에 추가
                    DatabaseReference mDatabase;
                    mDatabase = FirebaseDatabase.getInstance().getReference("user/" + key);
//                    mDatabase.child("Email").setValue(email);
//                    mDatabase.child("Name").setValue(name);
//                    mDatabase.child("type").setValue(type);
                    User user = new User(email, name, type);
                    mDatabase.setValue(user);

                    Intent intent = new Intent(getApplicationContext(), TourActivity.class);
                    intent.putExtra("user", user);
                    startActivityForResult(intent, 0);
                }

                if(flag == 0){
                    flag = 1;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

    }
}
