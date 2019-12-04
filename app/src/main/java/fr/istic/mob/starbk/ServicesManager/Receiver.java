package fr.istic.mob.starbk.ServicesManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    private static final String TAG = Receiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
            runService(context);

        Log.d(TAG, "received");
    }

    private void runService(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyServices.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long startTime = System.currentTimeMillis();
        long intervalTime = 60 * 1000;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, pendingIntent);
    }
}
