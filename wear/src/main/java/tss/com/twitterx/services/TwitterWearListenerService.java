package tss.com.twitterx.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import tss.com.twitterx.R;
import tss.com.twitterx.TwitterXDisplayActivity;

/**
 * Created by danbozdog on 21/07/15.
 */
public class TwitterWearListenerService extends WearableListenerService {

    private static final String TAG = "TwitterWearService";
    
    private static final String PATH = "/READER";
    private static final String KEY_USER = "USER";
    private static final int NOTIFICATION_ID = 1001;
    private static final String KEY_IMAGE = "IMAGE";

    private GoogleApiClient mGoogleApiClient;
    
    
    

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();

        if (!mGoogleApiClient.isConnected()) {
            ConnectionResult connectionResult = mGoogleApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                Log.e(TAG, "Service failed to connect to GoogleApiClient.");
                return;
            }
        }

        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (PATH.equals(path)) {
                    // Get the data out of the event
                    DataMapItem dataMapItem =
                            DataMapItem.fromDataItem(event.getDataItem());
                    final String username = dataMapItem.getDataMap().getString(KEY_USER);

                    Log.d(TAG,username);

                    // Build the intent to display our custom notification
                    Intent notificationIntent =
                            new Intent(this, TwitterXDisplayActivity.class);
                    notificationIntent.putExtra(
                            TwitterXDisplayActivity.EXTRA_USER, username);

                    if(dataMapItem.getDataMap().containsKey(KEY_IMAGE)) {
                        Asset asset = dataMapItem.getDataMap().getAsset(KEY_IMAGE);
                        notificationIntent.putExtra(
                                TwitterXDisplayActivity.EXTRA_IMAGE, asset);
                    }

                    PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                            this,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    // Create the ongoing notification
                    Notification.Builder notificationBuilder =
                            new Notification.Builder(this)
                                    .setAutoCancel(true)
                                    .setSmallIcon(android.R.drawable.ic_media_play)
                                    .setContentText(username)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setLargeIcon(BitmapFactory.decodeResource(
                            getResources(), R.drawable.ic_full_sad))
                            .extend(new Notification.WearableExtender()
                                    .setDisplayIntent(notificationPendingIntent));

                    // Build the notification and show it
                    NotificationManager notificationManager =
                            (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(
                            NOTIFICATION_ID, notificationBuilder.build());
                } else {
                    Log.d(TAG, "Unrecognized path: " + path);
                }
            }
        }
    }


}
