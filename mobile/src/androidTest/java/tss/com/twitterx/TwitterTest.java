package tss.com.twitterx;

import android.test.AndroidTestCase;
import android.text.format.DateUtils;

import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import javax.inject.Inject;

import tss.com.twitterx.di.ApplicationModule;
import tss.com.twitterx.di.DaggerTwitterTestComponent;
import tss.com.twitterx.di.TwitterTestComponent;
import tss.com.twitterx.managers.TwitterService;

/**
 * Created by danbozdog on 15/07/15.
 */
public class TwitterTest extends AndroidTestCase {

    @Inject
    TwitterService mTwitterService;
    private Semaphore s = new Semaphore(0);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TwitterTestComponent component = DaggerTwitterTestComponent.builder().applicationModule(new ApplicationModule(getContext())).build();
        component.inject(this);
    }

    public void testIfUserIsLogged() {
        assertEquals(true, mTwitterService.isUserLogged());
    }

    public void testRetrivingLastTweet() {
        mTwitterService.getLastTweet(mTweetListener);
        try {
            s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(mTweets);
        assertEquals(1, mTweets.size());
    }

    public void testRetrivingLastNTweets(){
        mTwitterService.getLastTweets(10, mTweetListener);
        try {
            s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(10,mTweets.size());
    }

    public void testRetrivingLast24HoursTweets(){
        mTwitterService.getLatestTweets(24, mTweetListener);
        try {
            s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotSame(0,mTweets.size());

        boolean inocent = true;

        for(Tweet t:mTweets){
            try {
                Date createdDate = dateFormat.parse(t.createdAt);
                if(System.currentTimeMillis() - createdDate.getTime()< DateUtils.DAY_IN_MILLIS){
                    inocent = false;
                }
            } catch (ParseException e) {
                inocent = false;
                e.printStackTrace();
            }
        }

        assertEquals(true,inocent);

    }

    private List<Tweet> mTweets;
    private TwitterService.TweetListener mTweetListener = new TwitterService.TweetListener() {
        @Override
        public void onTweets(List<Tweet> tweets) {
            mTweets = tweets;
            s.release();
        }

        @Override
        public void onFailure(String failureMessage) {
            s.release();
        }
    };

}
