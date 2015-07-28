package tss.com.twitterx.di;

import javax.inject.Singleton;

import dagger.Component;
import tss.com.twitterx.MainActivity;
import tss.com.twitterx.services.TwitterJobService;
import tss.com.twitterx.services.TwitterPlayService;

/**
 * Created by danbozdog on 16/07/15.
 */

@Singleton
@Component(modules = {ApplicationModule.class, TwitterModule.class})
public interface TwitterComponent {

    void inject(MainActivity activity);
    void inject(TwitterJobService service);
    void inject(TwitterPlayService service);

}
