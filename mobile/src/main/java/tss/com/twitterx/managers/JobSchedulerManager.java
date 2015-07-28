package tss.com.twitterx.managers;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import org.joda.time.DateTime;

import javax.inject.Inject;

import tss.com.twitterx.services.TwitterJobService;

/**
 * Created by danbozdog on 19/07/15.
 */
public class JobSchedulerManager {


    private final int JOB_ID = 100;

    Context mContext;

    @Inject
    public JobSchedulerManager(Context context) {
        mContext = context;
    }

    public void scheduleJob() {
        JobScheduler scheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(mContext.getPackageName(), TwitterJobService.class.getName()));
        //builder.setPersisted(true);
        long latency = calculateMinimulLatency();
        long deadline = latency + DateUtils.HOUR_IN_MILLIS;
        builder.setMinimumLatency(latency);
        builder.setOverrideDeadline(deadline);
        JobInfo jobInfo = builder.build();
        int job = scheduler.schedule(jobInfo);
        if (job <= 0) {
            Log.d("TwitterX", "Job scheduled error");
        } else {
            Log.d("TwitterX", "Job scheduled succesfully");
        }
    }

    private long calculateMinimulLatency() {
        DateTime now = new DateTime();
        DateTime tomorrow = now.plusDays(1);
        tomorrow = tomorrow.withTimeAtStartOfDay();
        tomorrow = tomorrow.withHourOfDay(6);
        tomorrow = tomorrow.withMinuteOfHour(30);
        return tomorrow.getMillis() - now.getMillis();
    }

    public void scheduleJobNow() {
        JobScheduler scheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(mContext.getPackageName(), TwitterJobService.class.getName()));
        //builder.setPersisted(true);
        builder.setOverrideDeadline(0);
        JobInfo jobInfo = builder.build();
        int job = scheduler.schedule(jobInfo);
        if (job <= 0) {
            Log.d("TwitterX", "Job scheduled error");
        } else {
            Log.d("TwitterX", "Job scheduled succesfully");
        }
    }
}
