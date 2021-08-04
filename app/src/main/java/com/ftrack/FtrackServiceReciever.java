package com.ftrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class FtrackServiceReciever extends BroadcastReceiver {
    private  FtrackLocationListener ftrackLocationListener;



    @Override
    public void onReceive(Context context, Intent intent) {
        ftrackLocationListener = FtrackLocationListener.getInstance(context);
        ftrackLocationListener.LocationRequest();

    }

}
