package com.mac.mk.campustour.activity.tour.adapter;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.SchoolNameEngKor;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.tourdetail.TourDetailActivity;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017. 6. 1..
 */

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourItemViewHolder>{

    // Tag for comments
    private static final String TAG = "TourAdapter";

    // listener interface
    final private ListItemClickListener mOnClickListener;

    // Objects
    private ArrayList<Tour> tourList;
    private Context mContext;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imageRef;

    // define Interface to click listener
    public interface ListItemClickListener{
        void onListItemClick(Tour tour);
    }


    public TourAdapter(ArrayList tourList, Context context, ListItemClickListener listener){
        this.tourList = tourList;
        this.mContext = context;
        this.mOnClickListener = listener;

        init();
    }

    public void init(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://firebase-campustour.appspot.com");
    }

    @Override
    public TourAdapter.TourItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.tour_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new TourItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TourAdapter.TourItemViewHolder holder, int position) {

        Log.d(TAG, "adapterTest : " + position + tourList.get(position).gettName() + " , " + tourList.get(position).gettWriter());

        int newPosition = 0;
        newPosition = tourList.size() - position -1;
        String path = SchoolNameEngKor.schoolNameEngKor.get(tourList.get(newPosition).gettSchoolName()) + ".png";
        imageRef = storageRef.child(path);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){

            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(mContext).load(uri).into(holder.tl_logo_iv);
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        holder.tl_title_tv.setText(tourList.get(newPosition).gettName());
        holder.tl_writerName_tv.setText(tourList.get(newPosition).gettWriter());
        holder.tl_date_tv.setText(tourList.get(newPosition).gettDate());
        holder.tl_capacity_tv.setText(String.valueOf(tourList.get(newPosition).getCapacity()) + "명");
        holder.tl_uName_tv.setText(tourList.get(newPosition).gettSchoolName());
        if(tourList.get(newPosition).isOccupied()){
            // 참이면 마감
            holder.tl_occupied_tv.setText("마감");
        }else{
            holder.tl_occupied_tv.setText("모집중");
        }
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    //View holder
    public class TourItemViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView tl_logo_iv;
        TextView tl_title_tv;
        TextView tl_writerName_tv;
        TextView tl_date_tv;
        TextView tl_capacity_tv;
        TextView tl_occupied_tv;
        TextView tl_uName_tv;

        public TourItemViewHolder(View itemView) {
            super(itemView);
            tl_logo_iv = (ImageView) itemView.findViewById(R.id.tl_logo_iv);
            tl_title_tv = (TextView) itemView.findViewById(R.id.tl_title_tv);
            tl_writerName_tv = (TextView) itemView.findViewById(R.id.tl_writerName_tv);
            tl_date_tv = (TextView) itemView.findViewById(R.id.tl_date_tv);
            tl_capacity_tv = (TextView) itemView.findViewById(R.id.tl_capacity_tv);
            tl_occupied_tv = (TextView) itemView.findViewById(R.id.tl_occupied_tv);
            tl_uName_tv = (TextView) itemView.findViewById(R.id.tl_uName_tv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Tour tour = tourList.get(tourList.size() -1 - position);
            mOnClickListener.onListItemClick(tour);
        }
    }

    public void setTourItemList(ArrayList<Tour> tour){
        this.tourList = tour;
    }

}
