package tss.com.twitterx.managers;

import android.content.Context;
import android.text.format.DateUtils;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.fabric.sdk.android.Fabric;

/**
 * Created by danbozdog on 16/07/15.
 */
@Singleton
public class TwitterService {

    TwitterServiceManager mServiceManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);

    @Inject
    public TwitterService(Context context, TwitterServiceManager serviceManager) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig("51QOUfeGh0tg4t6RCMZNhVyVE", "rMF6OtjHCza8sBEAPCJ5roMYhcaEtKEs0SojBavAFDB84970tJ");
        Fabric.with(context, new Twitter(authConfig));
        mServiceManager = serviceManager;
    }


    public boolean isUserLogged() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        return session != null;
    }

    public void getLastTweet(final TweetListener listener) {
        getLastTweets(1, listener);
    }

    public void getLastTweets(int count, final TweetListener listener) {
        if (listener == null) {
            throw new IllegalStateException("The tweet listener can't be null");
        }

        TwitterApiClient apiClient = Twitter.getApiClient();
        apiClient.getStatusesService().homeTimeline(count, null, null, null, null, null, true, new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                listener.onTweets(result.data);
            }

            @Override
            public void failure(TwitterException e) {
                listener.onFailure(e.getMessage());
            }
        });
    }

    public void logout() {
        mServiceManager.setSinceId(0);
        Twitter.logOut();
    }

    public void getLatestTweets(final int hours, final TweetListener listener) {
        if (listener == null) {
            throw new IllegalStateException("The tweet listener can't be null");
        }

        TwitterApiClient apiClient = Twitter.getApiClient();
        long sinceId = mServiceManager.getSinceId();
        if(sinceId==0){
            getLastTweets(100, new TweetListener() {
                @Override
                public void onTweets(List<Tweet> tweets) {
                    if(tweets.size()>0){
                        mServiceManager.setSinceId(tweets.get(0).id);
                    }
                    listener.onTweets(tweets);
                }

                @Override
                public void onFailure(String failureMessage) {
                    listener.onFailure(failureMessage);
                }
            });
        }else {
            apiClient.getStatusesService().homeTimeline(null, sinceId, null, null, null, null, true, new Callback<List<Tweet>>() {
                @Override
                public void success(Result<List<Tweet>> result) {
                    List<Tweet> results = result.data;
                    if (results.size() > 0) {
                        mServiceManager.setSinceId(results.get(0).id);
                    }
                    listener.onTweets(results);
                }

                @Override
                public void failure(TwitterException e) {
                    listener.onFailure(e.getMessage());
                }
            });
        }
    }

    public interface TweetListener {
        public void onTweets(List<Tweet> tweets);

        public void onFailure(String failureMessage);
    }
}
