package com.mac.mk.campustour.activity.tour;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.GoogleClient;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.data.User;
import com.mac.mk.campustour.activity.maketour.MakeTourActivity;
import com.mac.mk.campustour.activity.mytour.MyTourActivity;
import com.mac.mk.campustour.activity.tour.adapter.TourAdapter;
import com.mac.mk.campustour.activity.tour.fragment.HighSchoolFragment;
import com.mac.mk.campustour.activity.tour.fragment.UniversityFragment;
import com.mac.mk.campustour.activity.tourdetail.TourDetailActivity;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by mk on 2017. 5. 31..
 */

public class TourActivity extends AppCompatActivity implements TourAdapter.ListItemClickListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "TourActivity";

    private static final int PICK_FROM_ALBUM = 1000;
    private static final int REQ_CODE_CAMERA = 2000;

    // View Injection
    @Bind(R.id.fragment_container)
    FrameLayout fragment_container;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.mytour_btn)
    Button mytour_btn;
    @Bind(R.id.setting_btn)
    Button setting_btn;

    // Objects
    String imagePath;

    ImageView profileImage;
    //for profile
    Bitmap bmp;
    Integer studentType;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    int type = 0;

    // Firebase
    FirebaseStorage storage;
    StorageReference storageRef;

    String userId;

    // fragment
    FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        ButterKnife.bind(this);
        //Toolbar
        setSupportActionBar(toolbar);

        // Navigation Test
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get user type data from intent
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");
        type = user.getType();

        // preference
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();
        userId = setting.getString("key", null);


        // firebase
        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://firebase-campustour.appspot.com");

        Log.d(TAG, "타입테스트 in TourActivity " + type);
        fm = getFragmentManager();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_mytour) {
            Intent intent = new Intent(this, MyTourActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_logout) {
            // log out
            GoogleClient.getInstance().connect();
            GoogleClient.getInstance().registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {

                    FirebaseAuth.getInstance().signOut();
                    if(GoogleClient.getInstance().isConnected()) {
                        Auth.GoogleSignInApi.signOut(GoogleClient.getInstance()).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "Log Out" , Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            });

        }else if(id == R.id.nav_profile){
            profileDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void profileDialog(){

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        LayoutInflater inflater=getLayoutInflater();
        final View dialogView= inflater.inflate(R.layout.dialog_profile_update, null);
        AlertDialog.Builder builder= new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
        builder.setView(dialogView);

        Button cancel = (Button)dialogView.findViewById(R.id.cancel);
        Button setbtn = (Button)dialogView.findViewById(R.id.setbtn);
        Button chProfileBtn =(Button)dialogView.findViewById(R.id.chProfileBtn);
        profileImage = (ImageView)dialogView.findViewById(R.id.profileImage);

        // profile Image를 위한 layout 미리 설정
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int size = Math.round(150 * dm.density);

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(size, size);
        profileImage.setLayoutParams(ll);

        // imageView 보여주기..!
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference urlRef = database.getReference("user").child(userId).child("url");
        urlRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = (String) dataSnapshot.getValue();
                if(url == null){
                    Glide.with(getApplicationContext()).load(R.drawable.ic_no_profile_img).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(profileImage);
                }else{
                    Glide.with(getApplicationContext()).load(url).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(profileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final RadioGroup group = (RadioGroup)dialogView.findViewById(R.id.studentsGroup);
        RadioButton highschool = (RadioButton)dialogView.findViewById(R.id.highschool);
        RadioButton univ = (RadioButton)dialogView.findViewById(R.id.university);

        if(type == 0){
            highschool.setChecked(true);
        }else{
            univ.setChecked(true);
        }


        highschool.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: 고등학생일때 처리해야할 작업 적으면돼
                Toast.makeText(getApplicationContext(), "고등학생", Toast.LENGTH_SHORT).show();
                studentType = 0;
            }
        });
        univ.setOnClickListener(new RadioButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: 대학생일때 처리해야할 작업 적으면돼
                Toast.makeText(getApplicationContext(), "대학생", Toast.LENGTH_SHORT).show();
                studentType = 1;
            }
        });

        final AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정
        dialog.show();

        setbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set 누르면 파이어베이스 스토리지에 저장하기
                updateProfileImage();

                int sType = 0;

                // 라디오 그룹 정보 받아와서 파이어베이스에 업데이트
                int checkedradioBtn = group.getCheckedRadioButtonId();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference typeRef = database.getReference("user").child(userId).child("type");
                switch (checkedradioBtn){
                    case R.id.highschool:{
                        // 프리퍼런스
                        type = 0;
                        editor.putInt("type", 0);
                        editor.commit();
                        // 파이어베이스
                        typeRef.setValue(0);
                        break;
                    }
                    case R.id.university:{
                        // 프리퍼런스
                        type = 1;
                        editor.putInt("type", 1);
                        editor.commit();
                        // 데이터 베이스
                        typeRef.setValue(1);
                        sType = 1;
                        break;
                    }
                }

                android.app.Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                if(sType == 0){
                    HighSchoolFragment newHighSchoolFragment = new HighSchoolFragment();
                    fragmentTransaction.remove(fragment).replace(R.id.fragment_container, newHighSchoolFragment);
                }else{
                    UniversityFragment newUniversityFragment = new UniversityFragment();
                    fragmentTransaction.remove(fragment).replace(R.id.fragment_container, newUniversityFragment);
                }

                fragmentTransaction.commit();
                dialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : 취소버튼눌렀을때
                dialog.cancel();
            }
        });


        chProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }


    public void selectImage(){

        final CharSequence[] select = {"사진촬영", "앨범선택"};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setIcon(R.drawable.ic_photo);
        alt_bld.setTitle("사진선택");
        alt_bld.setSingleChoiceItems(select, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 0){
                    doTakePhotoAction();
                    Toast.makeText(getApplicationContext(), "사진촬영", Toast.LENGTH_SHORT).show();
                    dialog.cancel();

                }
                else{
                    doTakeAlbumAction();
                    Toast.makeText(getApplicationContext(), "앨범선택", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            }
        });

        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    public void doTakePhotoAction(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQ_CODE_CAMERA);

    }
    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PICK_FROM_ALBUM :{
                    if(data != null){
                        Uri imageUri = data.getData();
                        // uri로 filepath가져오기
                        Cursor cursor = this.getBaseContext().getContentResolver().query(imageUri, null, null, null, null);
                        cursor.moveToNext();
                        String path = cursor.getString(cursor.getColumnIndex("_data"));
                        cursor.close();
                        Log.d(TAG, "image path from album " + path);
                        imagePath = path;
                        Glide.with(this.getBaseContext()).load(imagePath).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(profileImage);

                    }
                    break;
                }
                case REQ_CODE_CAMERA:{

                    Log.d(TAG, "이미지경로테스트");
                    if(data != null){
                        if(data.getExtras() != null){
                            Log.d(TAG, "이미지경로테스트  " + (Bitmap)data.getExtras().get("data"));
                        }else{
                            Log.d(TAG, "이미지경로테스트 data.getExtras is null");
                        }
                        Uri imageUri = data.getData();

                        Cursor cursor = this.getBaseContext().getContentResolver().query(imageUri, null, null, null, null);
                        cursor.moveToNext();
                        String path = cursor.getString(cursor.getColumnIndex("_data"));
                        cursor.close();
                        Log.d(TAG, "이미지경로테스트 from album in camera" + path);
                        imagePath = path;
                        Glide.with(this.getBaseContext()).load(imagePath).bitmapTransform(new CropCircleTransformation(getApplicationContext())).into(profileImage);
                    }
                    break;
                }

            }
        }
    }

    public void updateProfileImage(){

        StorageReference profileRef = storageRef.child(userId);

        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        Bitmap bitmap = profileImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data2 = baos.toByteArray();

        UploadTask uploadTask = profileRef.putBytes(data2);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String user_profile_url = downloadUrl.toString();
                // user_profile_url을 user에다가 입력해야하는데 여기다 쓰면 쓰레드에 쓰레드? 하다가 만약 이 엑티비티를 나간다면.?
                // 우선 파이어베이스 user의 profile_url에 이 데이터를 업데이트 해보자
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference occupiedRef = database.getReference("user").child(userId).child("url");
                occupiedRef.setValue(user_profile_url);
            }
        });
    }

}
