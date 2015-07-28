package tss.com.twitterx;

import android.test.AndroidTestCase;

import java.io.File;
import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import tss.com.twitterx.di.ApplicationModule;
import tss.com.twitterx.di.DaggerTwitterTestComponent;
import tss.com.twitterx.di.TwitterTestComponent;
import tss.com.twitterx.managers.PhotoManager;
import tss.com.twitterx.managers.TwitterSyncManager;

/**
 * Created by danbozdog on 18/07/15.
 */
public class SyncTest extends AndroidTestCase {

    @Inject
    TwitterSyncManager mTwitterSyncManager;

    @Inject
    PhotoManager mPhotoManager;

    Semaphore mSemaphore = new Semaphore(0);

    int mTweetSyncedCount = 0;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TwitterTestComponent component = DaggerTwitterTestComponent.builder().applicationModule(new ApplicationModule(getContext())).build();
        component.inject(this);
    }

    public void testSync() {
        mTwitterSyncManager.sync(listener);
        try {
            mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotSame(0, mTweetSyncedCount);

    }

    TwitterSyncManager.TwitterSyncListener listener = new TwitterSyncManager.TwitterSyncListener() {
        @Override
        public void onSucces(int tweetCount) {
            mTweetSyncedCount = tweetCount;
            mSemaphore.release();
        }

        @Override
        public void onFailure(String message) {
            mTweetSyncedCount = 0;
            mSemaphore.release();
        }
    };


}
