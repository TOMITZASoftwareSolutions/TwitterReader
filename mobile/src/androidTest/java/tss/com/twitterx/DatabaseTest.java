package tss.com.twitterx;

import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import tss.com.twitterx.database.TweetDataManager;
import tss.com.twitterx.di.ApplicationModule;
import tss.com.twitterx.di.DaggerTwitterTestComponent;
import tss.com.twitterx.di.TwitterTestComponent;
import tss.com.twitterx.model.TweetX;

/**
 * Created by danbozdog on 15/07/15.
 */
public class DatabaseTest extends AndroidTestCase {

    @Inject
    TweetDataManager mTweetDataManager;

    List<TweetX> tweets = Arrays.asList(new TweetX("1", 1, new Date(10000), "This is a test", null, null, "Danutz"),
            new TweetX("2", 2, new Date(5000), "This is another test", null, null, "Danutz2"),
            new TweetX("3", 3, new Date(30000), "This is another test 3", null, null, "Danutz3"));

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TwitterTestComponent component = DaggerTwitterTestComponent.builder().applicationModule(new ApplicationModule(getContext())).build();
        component.inject(this);
    }

    public void testDatabase() {
        mTweetDataManager.deleteAll();
        List<TweetX> tweetx = mTweetDataManager.getAll();
        assertEquals(0, tweetx.size());

        mTweetDataManager.save(tweets);
        tweetx = mTweetDataManager.getAll();

        assertEquals(3, tweetx.size());
        assertEquals(tweets.get(0).getId(), tweetx.get(0).getId());

        tweetx = mTweetDataManager.getUnread();
        assertEquals(3, tweetx.size());
        assertEquals("2", tweetx.get(0).getId());
        assertEquals("1", tweetx.get(1).getId());
        assertEquals("3", tweetx.get(2).getId());

        TweetX t = tweets.get(0);
        t.setRead(true);
        mTweetDataManager.save(Arrays.asList(t));

        tweetx = mTweetDataManager.getUnread();
        assertEquals(2, tweetx.size());
        assertEquals("2", tweetx.get(0).getId());
        assertEquals(false, tweetx.get(0).isRead());
    }


    @Override
    protected void tearDown() throws Exception {
        mTweetDataManager.deleteAll();
        super.tearDown();
    }
}
