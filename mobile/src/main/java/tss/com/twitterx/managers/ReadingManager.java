package tss.com.twitterx.managers;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import javax.inject.Inject;

/**
 * Created by danbozdog on 17/07/15.
 */
public class ReadingManager implements TextToSpeech.OnInitListener {


    private final TextToSpeech mSpeechEngine;
    private ReaderProgressListener mReaderProgressListener;

    private String mCurrentId = null;
    private String mCurrentTextToRead = null;

    private String mFutureId = null;
    private String mFutureText = null;

    private boolean mInitialised = false;
    private boolean mStop = false;


    @Inject
    public ReadingManager(Context context) {
        mSpeechEngine = new TextToSpeech(context, this);
        mSpeechEngine.setOnUtteranceProgressListener(mUtteranceProgressListener);
    }

    public void setReaderProgressListener(ReaderProgressListener listener){
        mReaderProgressListener = listener;
    }


    public void read(String message, String id) {
        mFutureId = id;
        mFutureText = message;
        read();
    }

    public void silence(long duration,String id){
        mSpeechEngine.playSilentUtterance(1000,TextToSpeech.QUEUE_FLUSH, id);

    }

    public void stop() {
        mStop = true;
        if(mSpeechEngine.isSpeaking()) {
            mSpeechEngine.stop();
        }else{
            mStop = false;
            mReaderProgressListener.onStop();
        }
    }


    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            mInitialised = true;
            read();
        }
    }

    private void read() {
        if(mInitialised){
            if(mFutureId!=null){
                mSpeechEngine.speak(mFutureText,TextToSpeech.QUEUE_FLUSH,null,mFutureId);
            }
        }
    }



    UtteranceProgressListener mUtteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            mCurrentTextToRead = mFutureText;
            mCurrentId = mFutureId;
            mFutureId = null;
            mFutureText = null;
            mReaderProgressListener.onStart(utteranceId);
        }

        @Override
        public void onDone(String utteranceId) {
            mCurrentTextToRead = null;
            mCurrentId = null;
            if(mFutureId==null && !mStop){
                mReaderProgressListener.onDone(utteranceId);
            }else if(mStop){
                mStop = false;
                mReaderProgressListener.onInterrupted(utteranceId);
                mReaderProgressListener.onStop();
            }else if(mFutureId!=null){
                mReaderProgressListener.onInterrupted(utteranceId);
            }
        }

        @Override
        public void onError(String utteranceId) {
            mCurrentTextToRead = null;
            mCurrentId = null;
        }
    };


    public interface ReaderProgressListener {
        void onStart(String id);

        void onDone(String id);

        void onInterrupted(String id);

        void onStop();
    }
}
