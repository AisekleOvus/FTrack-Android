package com.ftrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class FtrackServiceTimer extends Service {

    private AlarmManager am;
    private Intent alIntent;
    private PendingIntent pIntent;
//  private  FtrackLocationListener ftrackLocationListener;

    public void FtrackServiceTimer() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
//      ftrackLocationListener = FtrackLocationListener.getInstance(this);
        alIntent = new Intent(this, FtrackServiceReciever.class);
        pIntent = PendingIntent.getBroadcast(this, 0, alIntent, 0);
//      am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 30000, pIntent);
        am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 3000,
                30000, pIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        am.cancel(pIntent);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
