package tss.com.twitterx.di;

import javax.inject.Singleton;

import dagger.Component;
import tss.com.twitterx.DatabaseTest;
import tss.com.twitterx.ParserTest;
import tss.com.twitterx.PhotoTest;
import tss.com.twitterx.SyncTest;
import tss.com.twitterx.TwitterTest;

/**
 * Created by danbozdog on 17/07/15.
 */

@Singleton
@Component(modules = {ApplicationModule.class, TwitterModule.class})
public interface TwitterTestComponent {

    void inject(TwitterTest test);
    void inject(ParserTest test);
    void inject(DatabaseTest test);
    void inject(SyncTest test);
    void inject(PhotoTest test);

}
