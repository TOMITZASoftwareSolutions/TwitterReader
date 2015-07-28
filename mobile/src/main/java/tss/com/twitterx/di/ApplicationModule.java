package tss.com.twitterx.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import tss.com.twitterx.managers.TwitterService;

/**
 * Created by danbozdog on 16/07/15.
 */
@Module
public class ApplicationModule {

    Context mContext;

    public ApplicationModule(Context context) {
        mContext = context;
    }

    @Provides
    public Context provideContext(){
        return mContext;
    }


}
