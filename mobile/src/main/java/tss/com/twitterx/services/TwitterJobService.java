package tss.com.twitterx.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import javax.inject.Inject;

import tss.com.twitterx.R;
import tss.com.twitterx.di.ApplicationModule;
import tss.com.twitterx.di.DaggerTwitterComponent;
import tss.com.twitterx.di.TwitterComponent;
import tss.com.twitterx.managers.JobSchedulerManager;
import tss.com.twitterx.managers.TwitterSyncManager;

/**
 * Created by danbozdog on 18/07/15.
 */
public class TwitterJobService extends JobService {

    @Inject
    TwitterSyncManager mTwitterSyncManager;

    @Inject
    JobSchedulerManager mJobSchedulerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterComponent component = DaggerTwitterComponent.builder().applicationModule(new ApplicationModule(getBaseContext())).build();
        component.inject(this);
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        mTwitterSyncManager.sync(new TwitterSyncManager.TwitterSyncListener() {
            @Override
            public void onSucces(int tweetCount) {
                Log.d("TwitterX", "Job succes");
                notifyJobSucces(tweetCount);
                mJobSchedulerManager.scheduleJob();
                jobFinished(params, false);
            }

            @Override
            public void onFailure(String message) {
                Log.d("TwitterX", "Job failure");
                notifyJobFailure(message);
                jobFinished(params, true);
            }
        });
        Log.d("TwitterX", "Job started");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("TwitterX", "Job finished");
        return true;
    }

    private void notifyJobSucces(int tweetCount) {
        notifyStatus(String.valueOf(tweetCount) + " " + getString(R.string.new_tweets_available));
    }

    private void notifyJobFailure(String message) {
        notifyJobFailure(getString(R.string.error_sync) + message);
    }

    private void notifyStatus(String text) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(getBaseContext());

        Intent i = new Intent(this, TwitterPlayService.class);
        i.setAction(TwitterPlayService.ACTION_START);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1000, i, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action startAction = new NotificationCompat.Action.Builder(R.drawable.ic_play_dark, "Start", pendingIntent).build();

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_play_dark)
                .setContentTitle("TwitterX")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setContentInfo(getString(R.string.play))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);


        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());
    }
}
