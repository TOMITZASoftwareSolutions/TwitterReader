package tss.com.twitterx.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import tss.com.twitterx.managers.TwitterService;
import tss.com.twitterx.managers.TwitterServiceManager;

/**
 * Created by danbozdog on 16/07/15.
 */

@Module
public class TwitterModule {

    @Provides
    public TwitterService provideTwitterService(Context context, TwitterServiceManager serviceManager) {
        return new TwitterService(context, serviceManager);
    }
}
