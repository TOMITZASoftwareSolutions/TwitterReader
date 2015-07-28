package tss.com.twitterx.managers;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import javax.inject.Inject;

import tss.com.twitterx.database.TweetDataManager;
import tss.com.twitterx.model.TweetX;

/**
 * Created by danbozdog on 18/07/15.
 */
public class TwitterSyncManager {

    TwitterService mTwitterService;
    ParseManager mParseManager;
    TweetDataManager mTweetDataManager;
    PhotoManager mPhotoManager;

    @Inject
    public TwitterSyncManager(TwitterService twitterService, ParseManager parseManager, TweetDataManager tweetDataManager, PhotoManager photoManager) {
        mTwitterService = twitterService;
        mParseManager = parseManager;
        mTweetDataManager = tweetDataManager;
        mPhotoManager = photoManager;
    }

    public void sync(final TwitterSyncListener listener) {
        final TwitterService.TweetListener mTweetListener = new TwitterService.TweetListener() {
            @Override
            public void onTweets(final List<Tweet> tweets) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<TweetX> tweetXes = mParseManager.parse(tweets);
                        mTweetDataManager.save(tweetXes);
                        for (TweetX t : tweetXes) {
                            if (t.getPhoto() != null) {
                                mPhotoManager.getAndSavePhoto(t.getPhoto(), t.getId());
                            }
                        }
                        listener.onSucces(tweets.size());
                    }
                });
                t.start();
            }

            @Override
            public void onFailure(String failureMessage) {
                listener.onFailure(failureMessage);
            }
        };
        mTweetDataManager.deleteAll();
        mPhotoManager.deletePhotos();
        mTwitterService.getLatestTweets(24, mTweetListener);
    }

    public interface TwitterSyncListener {
        public void onSucces(int tweetCount);

        public void onFailure(String message);
    }
}
