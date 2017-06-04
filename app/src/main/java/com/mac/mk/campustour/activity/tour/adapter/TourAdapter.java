package com.mac.mk.campustour.activity.tour.adapter;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.tourdetail.TourDetailActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mk on 2017. 6. 1..
 */

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourItemViewHolder>{

    // Tag for comments
    private static final String TAG = "TourAdapter";

    // Objects
    private ArrayList<Tour> tourList;
    private Context mContext;
    final private ListItemClickListener mOnClickListener;

    // define Interface to click listener
    public interface ListItemClickListener{
        void onListItemClick(Tour tour);
    }

    public TourAdapter(ArrayList tourList, Context context, ListItemClickListener listener){
        this.tourList = tourList;
        this.mContext = context;
        this.mOnClickListener = listener;
    }

    @Override
    public TourAdapter.TourItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.tour_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new TourItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TourAdapter.TourItemViewHolder holder, int position) {

        Log.d(TAG, "adapterTest : " + position + tourList.get(position).gettName() + " , " + tourList.get(position).gettWriter());
        holder.tl_title_tv.setText(tourList.get(position).gettName());
        holder.tl_writerName_tv.setText(tourList.get(position).gettWriter());
        holder.tl_capacity_tv.setText(String.valueOf(tourList.get(position).getCapacity()));
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

        public TourItemViewHolder(View itemView) {
            super(itemView);
            tl_logo_iv = (ImageView) itemView.findViewById(R.id.tl_logo_iv);
            tl_title_tv = (TextView) itemView.findViewById(R.id.tl_title_tv);
            tl_writerName_tv = (TextView) itemView.findViewById(R.id.tl_writerName_tv);
            tl_date_tv = (TextView) itemView.findViewById(R.id.tl_date_tv);
            tl_capacity_tv = (TextView) itemView.findViewById(R.id.tl_capacity_tv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Tour tour = tourList.get(position);
            mOnClickListener.onListItemClick(tour);
        }
    }

    public void setTourItemList(ArrayList<Tour> tour){
        this.tourList = tour;
    }

}
