package tss.com.twitterx;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

import tss.com.twitterx.di.ApplicationModule;
import tss.com.twitterx.di.DaggerTwitterComponent;
import tss.com.twitterx.di.TwitterComponent;
import tss.com.twitterx.managers.JobSchedulerManager;
import tss.com.twitterx.managers.TwitterReadingManager;
import tss.com.twitterx.managers.TwitterService;
import tss.com.twitterx.managers.TwitterSyncManager;
import tss.com.twitterx.services.TwitterJobService;
import tss.com.twitterx.services.TwitterPlayService;


@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {


    @Inject
    TwitterService mTwitterService;

    @Inject
    JobSchedulerManager mJobSchedulerManager;

    @ViewById(R.id.login_button)
    TwitterLoginButton mLoginButton;

    @ViewById(R.id.container_buttons)
    View mContainerButtons;


    @Click(R.id.btn_start)
    public void onBtnStart() {
        Intent i = new Intent(this, TwitterPlayService.class);
        i.setAction(TwitterPlayService.ACTION_START);
        startService(i);
    }

    @Click(R.id.btn_stop)
    public void onBtnStop() {
        Intent i = new Intent(this, TwitterPlayService.class);
        stopService(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterComponent component = DaggerTwitterComponent.builder().applicationModule(new ApplicationModule(this)).build();
        component.inject(this);
    }

    @AfterViews
    public void init() {
        if (!mTwitterService.isUserLogged()) {
            mLoginButton.setVisibility(View.VISIBLE);
            mLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with result, which provides a TwitterSession for making API calls
                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                }
            });
        } else {
            mJobSchedulerManager.scheduleJob();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatemen
        if (id == R.id.action_sync) {
            mJobSchedulerManager.scheduleJobNow();
            return true;
        }

        if (id == R.id.action_logout){
            mTwitterService.logout();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    TwitterSyncManager.TwitterSyncListener mTwitterSyncListener = new TwitterSyncManager.TwitterSyncListener() {
        @Override
        public void onSucces(int count) {
            mContainerButtons.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFailure(String message) {
            setTitle(message);
        }
    };
}
