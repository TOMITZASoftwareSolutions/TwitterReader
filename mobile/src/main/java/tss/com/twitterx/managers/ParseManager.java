package tss.com.twitterx.managers;

import com.twitter.sdk.android.core.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import tss.com.twitterx.model.TweetX;

/**
 * Created by danbozdog on 17/07/15.
 */
public class ParseManager {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);

    @Inject
    public ParseManager() {
    }

    public List<TweetX> parse(List<Tweet> tweets) {
        List<TweetX> tweetx = new ArrayList<>(tweets.size());

        for (Tweet t : tweets) {
            String id = t.idStr;
            long longId = t.id;
            String user = t.user.name;
            String text = parseText(t);
            String photo = parseMedia(t);
            String url = parseUrl(t);
            Date createdAt = parseCreatedDate(t);
            TweetX tx = new TweetX(id,longId, createdAt, text, url,photo, user);
            tweetx.add(tx);
        }
        return tweetx;
    }


    private String parseText(Tweet t) {
        String text = t.text;
        text = text.replaceAll("http[^ ]++", "");

        if (t.retweetedStatus != null) {
            text = text.replaceAll("RT [^ ]*+", "");
        }

        text = text.trim();
        return text;
    }

    private String parseUrl(Tweet t) {
        String url = null;
        if (t.entities != null && t.entities.urls != null && t.entities.urls.size() > 0) {
            url = t.entities.urls.get(0).url;
        }
        return url;
    }

    private String parseMedia(Tweet t) {
        String media = null;
        if (t.entities != null && t.entities.media != null && t.entities.media.size() > 0) {
            media = t.entities.media.get(0).mediaUrl;
        }
        return media;
    }

    private Date parseCreatedDate(Tweet t) {
        Date d = null;
        try {
            d = dateFormat.parse(t.createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

}
