package tss.com.twitterx.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import tss.com.twitterx.R;
import tss.com.twitterx.di.ApplicationModule;
import tss.com.twitterx.di.DaggerTwitterComponent;
import tss.com.twitterx.di.TwitterComponent;
import tss.com.twitterx.managers.TwitterReadingManager;

/**
 * Created by danbozdog on 19/07/15.
 */
public class TwitterPlayService extends Service {


    @Inject
    TwitterReadingManager mTwitterReadingManager;

    public static final String ACTION_START = "start";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PREVIOUS = "previous";
    public static final String ACTION_QUIT = "quit";
    public static final String ACTION_STOP = "stop";

    GoogleApiClient mGoogleApiClient;

    private static final String PATH = "/READER";
    private static final String KEY_USER = "USER";
    private static final String KEY_IMAGE = "IMAGE";

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterComponent component = DaggerTwitterComponent.builder().applicationModule(new ApplicationModule(this)).build();
        component.inject(this);

        mTwitterReadingManager.setReaderListener(mReaderListener);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServiceInForeground();
        if (intent.getAction() == ACTION_START) {
            mTwitterReadingManager.start();
        } else if (intent.getAction() == ACTION_STOP) {
            mTwitterReadingManager.stop();
        } else if (intent.getAction() == ACTION_NEXT) {
            mTwitterReadingManager.next();
        } else if (intent.getAction() == ACTION_PREVIOUS) {
            mTwitterReadingManager.previous();
        } else if (intent.getAction() == ACTION_QUIT) {
            mTwitterReadingManager.stop();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mTwitterReadingManager.stop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    private void startServiceInForeground() {
        NotificationCompat.Builder b = new NotificationCompat.Builder(getBaseContext());

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_play_dark)
                .setContentTitle("TwitterX")
                .setContentText("Play on dj")
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setOngoing(false)
        ;

        startForeground(100, b.build());
    }


    private TwitterReadingManager.ReaderListener mReaderListener = new TwitterReadingManager.ReaderListener() {
        @Override
        public void onStarted(String username,Bitmap media) {
            if(!mGoogleApiClient.isConnected()){
                ConnectionResult result = mGoogleApiClient.blockingConnect(15, TimeUnit.SECONDS);
                if(!result.isSuccess()){
                    return;
                }
            }

            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH);

            putDataMapRequest.getDataMap().putString(KEY_USER,
                    username);

            if(media!=null) {
                Asset asset = createAssetFromBitmap(media);
                putDataMapRequest.getDataMap().putAsset(KEY_IMAGE, asset);
            }

            PutDataRequest request = putDataMapRequest.asPutDataRequest();

            Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {

                        }
                    });
        }
    };

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }


}
