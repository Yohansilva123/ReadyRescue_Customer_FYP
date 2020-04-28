package com.example.readyrescuecus.servicefunctions;

import com.google.firebase.database.DataSnapshot;

public class ServiceDetails {

    public DataSnapshot dataSnapshot;
    public String favMechanicName;

    public void getServiceDetails(DataSnapshot dataSnapshots) {
        dataSnapshot = dataSnapshots;
    }

    public void getFavMechanicName(String name){
        favMechanicName = name;
    }
}
