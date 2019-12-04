package fr.istic.mob.starbk.ServicesManager;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import fr.istic.mob.starbk.MainActivity;
import fr.istic.mob.starbk.R;
import fr.istic.mob.starbk.MyApplication;

public class MyServices extends IntentService {
    private static final String TAG = MyServices.class.getSimpleName();

    private static final String NOTIFICATION_ID = "star_app";

    private static final String DATASET_BASE_URL = "https://data.explore.star.fr/api/records/1.0/search/?dataset=tco-busmetro-horaires-gtfs-versions-td&sort=-debutvalidite";

    private AsyncHttpResponseHandler mResponseHandler = new JsonHttpResponseHandler() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                handlingResponse(response);
            } catch (JSONException e) {
                Log.e(TAG, JSONException.class.getSimpleName(), e);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handlingResponse(JSONObject response) throws JSONException {
        Log.d(TAG, response.toString());
        JSONObject records = (JSONObject) response.getJSONArray("records").get(0);
        JSONObject fields = (JSONObject) records.get("fields");
        JSONObject file = (JSONObject) fields.get("fichier");
        String lastSync = file.getString("last_synchronized");
        String currentVersion = app.getDataSore().getCurrentDataVersion();
        if (!lastSync.equals(currentVersion)) {
            String filename = file.getString("filename");
            filename = filename.replace(".zip", "");
            String mimetype = file.getString("mimetype");
            String url = fields.getString("url");
            Bundle bundle = new Bundle();
            bundle.putString("version", lastSync);
            bundle.putString("filename", filename);
            bundle.putString("mimetype", mimetype);
            bundle.putString("url", url);

            createNotification(app, bundle);
        }
    }

    private MyApplication app;

    public MyServices() {
        super("UpdaterService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = (MyApplication) getApplication();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpRequest.get(DATASET_BASE_URL, mResponseHandler);
    }

    public String createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = NOTIFICATION_ID;
            CharSequence channelName = "Star Application";
            String channelDescription = "New Update Notification";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            int channelLockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC;

            NotificationChannel notificationChannel =
                    new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(false);
            notificationChannel.setLockscreenVisibility(channelLockscreenVisibility);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            return null;
        }
    }

    private void createNotification(Context context, Bundle bundle) {
        String channelId = createNotificationChannel(context);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.notification_text))
                .setBigContentTitle(getString(R.string.notification_title))
                .setSummaryText(getString(R.string.notification_summary));

        Intent notifyIntent = new Intent(context, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notifyIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notificationCompatBuilder =
                new NotificationCompat.Builder(context, channelId);

        Notification notification = notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.ic_directions_transit_black_24dp)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(888, notification);
    }
}
