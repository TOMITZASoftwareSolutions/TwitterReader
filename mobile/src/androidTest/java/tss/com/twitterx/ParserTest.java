package tss.com.twitterx;

import android.test.AndroidTestCase;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UrlEntity;
import com.twitter.sdk.android.core.models.User;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import tss.com.twitterx.di.ApplicationModule;
import tss.com.twitterx.di.DaggerTwitterTestComponent;
import tss.com.twitterx.di.TwitterTestComponent;
import tss.com.twitterx.managers.ParseManager;
import tss.com.twitterx.model.TweetX;

/**
 * Created by danbozdog on 15/07/15.
 */
public class ParserTest extends AndroidTestCase {


    List<Tweet> mTweetList = Arrays.asList(
            new Tweet(null, "Sat Jul 18 01:58:37 +0000 2015", null,
                    new TweetEntities(Arrays.asList(new UrlEntity("http://t.co/kr9A0iXM58", null, null, 0, 0)), null, Arrays.asList(new MediaEntity("http://t.co/YrIzc5KYJ3",null,null,0,0,0,"0","http://pbs.twimg.com/media/CKtMkhgWIAA-jYV.jpg",null,null,0,null,"photo")), null), 1000, true,
                    null, 622223920318431232L, "622223920318431232", null, 0, null, 0, null, "en", null, false, null, 0, true, null, null, "Ludicrous speed, 70 kWh rear drive and 90 kWh battery pack http://t.co/kr9A0iXM58", false,
                    new User(false, null, false, false, null, null, null, 0, false, 0, 0, false, 0, null, false, null, 0, null, "Elon Musk", null, null, null, false, null, null, null, null,
                            null, null, null, false, false, "elonmusk", false, null, 0, null, null, 0, false, null, null), false, null, null),
            new Tweet(null, "Fri Jul 17 23:47:21 +0000 2015", null,
                    new TweetEntities(Arrays.asList(new UrlEntity("http://t.co/yDHYUrP5jY", null, null, 0, 0)), null,
                            Arrays.asList(new MediaEntity("http://pbs.twimg.com/media/CKD6JdxXAAAvHe_.jpg", null, null, 0, 0, 0, null, null, null, null, 0, null, "photo"))
                            , null), 1000, true,
                    null, 622190884906348544L, "622190884906348544", null, 0, null, 0, null, "en", null, false, null, 0, true,
                    new Tweet(null,null,null,null,0,false,null,0,null,null,0,null,9,null,null,null,false,null,0,false,null,null,null,false,null,false,null,null), null,
                    "RT @sciencemagazine: This week, we're all about the rise of the machines! Check out our AI #specialissue http://t.co/yDHYUrP5jY http://t.co…", false,
                    new User(false, null, false, false, null, null, null, 0, false, 0, 0, false, 0, null, false, null, 0, null, "Microsoft Research", null, null, null, false, null, null, null, null,
                            null, null, null, false, false, "MSFTResearch", false, null, 0, null, null, 0, false, null, null), false, null, null),
            new Tweet(null, "Fri Jul 17 22:48:29 +0000 2015", null,
                    new TweetEntities(new ArrayList<UrlEntity>(), null,
                            null
                            , null), 1000, true,
                    null, 622176072889729024L, "622176072889729024", null, 0, null, 0, null, "en", null, false, null, 0, true, null, null,
                    "Curious to know if there's any hipchat to Pandora tools out there...", false,
                    new User(false, null, false, false, null, null, null, 0, false, 0, 0, false, 0, null, false, null, 0, null, "++dave;", null, null, null, false, null, null, null, null,
                            null, null, null, false, false, "DaveShah", false, null, 0, null, null, 0, false, null, null), false, null, null));


    @Inject
    ParseManager mParseManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TwitterTestComponent component = DaggerTwitterTestComponent.builder().applicationModule(new ApplicationModule(getContext())).build();
        component.inject(this);
    }


    public void testParser() {
        List<TweetX> r = mParseManager.parse(mTweetList);

        assertEquals("Elon Musk", r.get(0).getUser());
        assertEquals("Microsoft Research", r.get(1).getUser());
        assertEquals("++dave;", r.get(2).getUser());


        SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
        sf.setTimeZone(TimeZone.getTimeZone("+0000"));
        assertEquals("Sat Jul 18 01:58:37 +00:00 2015", sf.format(r.get(0).getCreatedDate()));
        assertEquals("Fri Jul 17 23:47:21 +00:00 2015", sf.format(r.get(1).getCreatedDate()));
        assertEquals("Fri Jul 17 22:48:29 +00:00 2015", sf.format(r.get(2).getCreatedDate()));

        assertEquals("http://t.co/kr9A0iXM58", r.get(0).getUrl());
        assertEquals("http://t.co/yDHYUrP5jY", r.get(1).getUrl());
        assertEquals(null, r.get(2).getUrl());

        assertEquals("Ludicrous speed, 70 kWh rear drive and 90 kWh battery pack", r.get(0).getText());
        assertEquals("This week, we're all about the rise of the machines! Check out our AI #specialissue", r.get(1).getText());
        assertEquals("Curious to know if there's any hipchat to Pandora tools out there...", r.get(2).getText());

        assertEquals("http://pbs.twimg.com/media/CKtMkhgWIAA-jYV.jpg",r.get(0).getPhoto());

    }
}
