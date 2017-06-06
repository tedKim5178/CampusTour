package com.mac.mk.campustour.activity.mytour.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mac.mk.campustour.R;
import com.mac.mk.campustour.activity.data.Tour;
import com.mac.mk.campustour.activity.tour.adapter.TourAdapter;

/**
 * Created by mk on 2017. 6. 6..
 */

//public class MyTourAdapter extends RecyclerView.Adapter<MyTourAdapter.MyTourItemViewHolder>{
//
//    @Override
//    public MyTourItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).
//                inflate(R.layout.tour_row, parent, false);
//        return new MyTourAdapter.MyTourItemViewHolder(itemView);
//
//    }
//
//    @Override
//    public void onBindViewHolder(MyTourItemViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }
//
//    //View holder
//    public class MyTourItemViewHolder
//            extends RecyclerView.ViewHolder implements View.OnClickListener{
//
//        ImageView tl_logo_iv;
//        TextView tl_title_tv;
//        TextView tl_writerName_tv;
//        TextView tl_date_tv;
//        TextView tl_capacity_tv;
//
//        public MyTourItemViewHolder(View itemView) {
//            super(itemView);
//            tl_logo_iv = (ImageView) itemView.findViewById(R.id.tl_logo_iv);
//            tl_title_tv = (TextView) itemView.findViewById(R.id.tl_title_tv);
//            tl_writerName_tv = (TextView) itemView.findViewById(R.id.tl_writerName_tv);
//            tl_date_tv = (TextView) itemView.findViewById(R.id.tl_date_tv);
//            tl_capacity_tv = (TextView) itemView.findViewById(R.id.tl_capacity_tv);
//
//            itemView.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            Tour tour = tourList.get(position);
//            mOnClickListener.onListItemClick(tour);
//        }
//    }
//}
