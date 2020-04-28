package com.example.readyrescuecus.utils;

import com.google.protobuf.StringValue;

import java.util.ArrayList;

public class CalculateRatings {
    private static String rating;

    public static String getRatings(ArrayList<String> ratings){
        int numberOfRatings = ratings.size();
        double total = 0;
        double avg;

        for (int i=0; i<numberOfRatings; i++) {
            total = Double.parseDouble(ratings.get(i))+total;
        }
        avg = total/numberOfRatings;
        double avgRounded = Math.round(avg * 100D) / 100D;
        rating = String.valueOf(avgRounded);

        return rating;
    }
}
