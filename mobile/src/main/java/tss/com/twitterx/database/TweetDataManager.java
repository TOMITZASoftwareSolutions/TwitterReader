package tss.com.twitterx.database;

import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import tss.com.twitterx.model.TweetX;

/**
 * Created by danbozdog on 18/07/15.
 */
public class TweetDataManager {

    TwitterDatabaseHelper mDatabaseHelper;

    @Inject
    public TweetDataManager(TwitterDatabaseHelper helper) {
        mDatabaseHelper = helper;
    }


    public void save(List<TweetX> tweets) {
        for (TweetX t : tweets) {
           save(t);
        }
    }

    public void save(TweetX t){
        try {
            mDatabaseHelper.getDao(TweetX.class).createOrUpdate(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TweetX> getAll() {
        List<TweetX> tweets = null;
        try {
            tweets = mDatabaseHelper.getDao(TweetX.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public void deleteAll() {
        try {
            mDatabaseHelper.getDao(TweetX.class).delete(getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<TweetX> getUnread() {
        List<TweetX> tweets = null;
        try {
            tweets = mDatabaseHelper.getDao(TweetX.class).queryBuilder().orderBy(TweetX.COLUMN_DATE_CREATED,true).where().eq(TweetX.COLUMN_READ,false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }
}
