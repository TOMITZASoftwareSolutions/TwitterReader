package tss.com.twitterx.managers;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import tss.com.twitterx.database.TweetDataManager;
import tss.com.twitterx.model.TweetX;

/**
 * Created by danbozdog on 17/07/15.
 */
public class TwitterReadingManager {

    ReadingManager mReadingManager;
    TweetDataManager mTweetDataManager;
    PhotoManager mPhotoManager;

    List<TweetX> mToRead;

    int mCurrentIndex = 0;
    String mCurrentId;

    boolean mRunning = false;

    private final String mSilenceId = "silence";

    ReaderListener mReaderListener;

    @Inject
    public TwitterReadingManager(TweetDataManager tweetDataManager, ReadingManager readingManager,PhotoManager photoManager) {
        mReadingManager = readingManager;
        mTweetDataManager = tweetDataManager;
        mPhotoManager = photoManager;
    }

    public void setReaderListener(ReaderListener readerListener) {
        mReaderListener = readerListener;
    }

    public void init() {
        mReadingManager.setReaderProgressListener(mReaderProgressListener);
        List<TweetX> tweetXes = mTweetDataManager.getUnread();
        if (mToRead == null) {
            mToRead = new ArrayList<>();
        }
        mToRead.addAll(tweetXes);
    }

    public void start() {
        if (mToRead == null) {
            init();
        }
        Log.d("Reading", String.valueOf(mCurrentIndex));
        TweetX t = mToRead.get(mCurrentIndex);
        mReadingManager.read(composeMessage(t), t.getId());
    }

    public void previous() {
        mCurrentIndex--;
        if (mCurrentIndex >= 0) {
            start();
        } else {
            mCurrentIndex++;
        }
    }

    public void next() {
        Log.d("Reader", "Next");
        mCurrentIndex++;
        if (mCurrentIndex < mToRead.size()) {
            start();
        } else {
            mCurrentIndex--;
        }
    }

    public void stop() {
        mReadingManager.stop();
    }

    private String composeMessage(TweetX tweetX) {
        return tweetX.getUser() + ": " + tweetX.getText();
    }

    ;


    private ReadingManager.ReaderProgressListener mReaderProgressListener = new ReadingManager.ReaderProgressListener() {
        @Override
        public void onStart(String id) {
            if(!id.equals(mSilenceId)){
                TweetX current = mToRead.get(mCurrentIndex);
                if(current.getId().equals(id)){
                    Bitmap image = current.getPhoto()!=null?mPhotoManager.getPhoto(current.getId()):null;
                    mReaderListener.onStarted(current.getUser(),image);
                }
            }
        }

        @Override
        public void onDone(String id) {
            if(id.equals(mSilenceId)) {
                next();
            }else{
                mReadingManager.silence(300,mSilenceId);
            }
        }

        @Override
        public void onInterrupted(String id) {
        }

        @Override
        public void onStop() {

        }
    };


    public interface ReaderListener{
        void onStarted(String username,Bitmap image);
    }
}
