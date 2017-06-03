package com.mac.mk.campustour.activity.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mac.mk.campustour.activity.data.Restaurant;
import com.mac.mk.campustour.activity.data.Tour;

import java.util.ArrayList;

/**
 * Created by mk on 2017. 6. 2..
 */

public class FirebaseUtils {

    public static void registerTourInfoToDatabase(Tour tour){
        Tour t = tour;
        // firebase에 데이터 넘기자..!
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("tour");

        ref.push().setValue(tour);
    }

}
