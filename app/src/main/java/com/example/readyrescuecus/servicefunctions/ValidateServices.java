package com.example.readyrescuecus.servicefunctions;

import com.example.readyrescuecus.CustomerMapsActivity;
import com.example.readyrescuecus.CustomerServiceActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ValidateServices {

    public static Boolean bTireChange, bBatteryJump, bLockSmith, bFuelDelivery, bEngineIssues, bTowing;
    public static Boolean bCTireChange, bCBatteryJump, bCLockSmith, bCFuelDelivery, bCEngineIssues, bCTowing;
    public static String userId;

    public static Boolean validateMechanicService(final String key) {

        userId = CustomerMapsActivity.userId;

        CustomerMapsActivity.services = CustomerMapsActivity.serviceDetails.dataSnapshot;
        Boolean serviceState = false;

        String tirechange = CustomerMapsActivity.services.child("Mechanics").child(key).child("Services").child("TireChange").getValue().toString();
        if (Boolean.parseBoolean(tirechange))
            bTireChange = true;
        else
            bTireChange = false;

        String batteryJumpStart = CustomerMapsActivity.services.child("Mechanics").child(key).child("Services").child("BatteryJumpStart").getValue().toString();
        if (batteryJumpStart.equalsIgnoreCase("true"))
            bBatteryJump = true;
        else
            bBatteryJump = false;

        String lockSmithService = CustomerMapsActivity.services.child("Mechanics").child(key).child("Services").child("LockSmithService").getValue().toString();
        if (lockSmithService.equalsIgnoreCase("true"))
            bLockSmith = true;
        else
            bLockSmith = false;

        String fuelDelivery = CustomerMapsActivity.services.child("Mechanics").child(key).child("Services").child("FuelDelivery").getValue().toString();
        if (fuelDelivery.equalsIgnoreCase("true"))
            bFuelDelivery = true;
        else
            bFuelDelivery = false;

        String engineIssues = CustomerMapsActivity.services.child("Mechanics").child(key).child("Services").child("EngineIssues").getValue().toString();
        if (engineIssues.equalsIgnoreCase("true"))
            bEngineIssues = true;
        else
            bEngineIssues = false;

        String towingServices = CustomerMapsActivity.services.child("Mechanics").child(key).child("Services").child("TowingServices").getValue().toString();
        if (towingServices.equalsIgnoreCase("true"))
            bTowing = true;
        else
            bTowing =false;

//        Customer Services
        String cTirechange = CustomerMapsActivity.services.child("Customers").child(userId).child("Services").child("TireChange").getValue().toString();
        if (cTirechange.equalsIgnoreCase("true"))
            bCTireChange = true;
        else
            bCTireChange = false;

        String cBatteryJumpStart = CustomerMapsActivity.services.child("Customers").child(userId).child("Services").child("BatteryJumpStart").getValue().toString();
        if (cBatteryJumpStart.equalsIgnoreCase("true"))
            bCBatteryJump = true;
        else
            bCBatteryJump = false;

        String cLockSmithService = CustomerMapsActivity.services.child("Customers").child(userId).child("Services").child("LockSmithService").getValue().toString();
        if (cLockSmithService.equalsIgnoreCase("true"))
            bCLockSmith = true;
        else
            bCLockSmith = false;

        String cFuelDelivery = CustomerMapsActivity.services.child("Customers").child(userId).child("Services").child("FuelDelivery").getValue().toString();
        if (cFuelDelivery.equalsIgnoreCase("true"))
            bCFuelDelivery = true;
        else
            bCFuelDelivery = false;

        String cEngineIssues = CustomerMapsActivity.services.child("Customers").child(userId).child("Services").child("EngineIssues").getValue().toString();
        if (cEngineIssues.equalsIgnoreCase("true"))
            bCEngineIssues = true;
        else
            bCEngineIssues = false;

        String cTowingServices = CustomerMapsActivity.services.child("Customers").child(userId).child("Services").child("TowingServices").getValue().toString();
        if (cTowingServices.equalsIgnoreCase("true"))
            bCTowing = true;
        else
            bCTowing = false;

//        Verify if the customer selected services are subscribed by the mechanic

        if (bCTireChange) {
            if (bCTireChange && bCTireChange)
                serviceState = true;
            else
                serviceState = false;
        }if (bCBatteryJump) {
            if (bBatteryJump && bCBatteryJump)
                serviceState = true;
            else
                serviceState = false;
        }if (bCLockSmith){
            if (bLockSmith&&bCLockSmith)
                serviceState = true;
            else
                serviceState = false;
        }if (bCFuelDelivery){
            if (bFuelDelivery&&bCFuelDelivery)
                serviceState = true;
            else
                serviceState = false;
        }if (bCEngineIssues){
            if (bEngineIssues&&bCEngineIssues)
                serviceState = true;
            else
                serviceState = false;
        }if (bCTowing){
            if (bTowing&&bCTowing)
                serviceState = true;
            else
                serviceState = false;
        }

        return serviceState;

    }

    public static Boolean mechanicWorking(String key){
        CustomerMapsActivity.services = CustomerMapsActivity.serviceDetails.dataSnapshot;

        String workingState = CustomerMapsActivity.services.child("Mechanics").child(key).child("WorkingState").getValue().toString();
        String customerAcceptedState = CustomerMapsActivity.services.child("Mechanics").child(key).child("CustomerAccepted").getValue().toString();
        try {
            CustomerMapsActivity.customerPresent =  CustomerMapsActivity.services.child("Mechanics").child(key).child("CustomerID").getValue().toString();
        }catch (Exception e){
        }


        if ((workingState.equalsIgnoreCase("true")&&customerAcceptedState.equalsIgnoreCase("false"))|| CustomerMapsActivity.requestSent/*&&key.equalsIgnoreCase(mechanicFoundID)*/
                ||( CustomerMapsActivity.customerPresent==null))
            return true;
        else
            return false;

    }

    public static Boolean favMechanicWorking(String key){
        CustomerMapsActivity.services = CustomerMapsActivity.serviceDetails.dataSnapshot;

        String workingState = CustomerMapsActivity.services.child("Mechanics").child(key).child("WorkingState").getValue().toString();
        String customerAcceptedState = CustomerMapsActivity.services.child("Mechanics").child(key).child("CustomerAccepted").getValue().toString();


        if ((workingState.equalsIgnoreCase("true")&&customerAcceptedState.equalsIgnoreCase("false")))
            return true;
        else
            return false;
    }
}
