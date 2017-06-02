package com.mac.mk.campustour.activity.tour.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by mk on 2017. 6. 1..
 */

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourItemViewHolder>{

    private ArrayList<Tour> tourList;

    public TourAdapter(ArrayList tourList){
        this.tourList = tourList;
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
        holder.tName_tv.setText(tourList.get(position).gettName());
        holder.tSchool_tv.setText(tourList.get(position).gettSchoolName());
        holder.tAddress_tv.setText(tourList.get(position).gettAddress());
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    //View holder
    public final static class TourItemViewHolder
            extends RecyclerView.ViewHolder {

        TextView tName_tv;
        TextView tSchool_tv;
        TextView tAddress_tv;
        public TourItemViewHolder(View itemView) {
            super(itemView);
            tName_tv = (TextView)itemView.findViewById(R.id.tName_tv);
            tSchool_tv = (TextView)itemView.findViewById(R.id.tSchool_tv);
            tAddress_tv = (TextView) itemView.findViewById(R.id.tAddress_tv);
        }
        // ViewHolder
    }

    public void setTourItemList(ArrayList<Tour> tour){
        this.tourList = tour;
    }
}
