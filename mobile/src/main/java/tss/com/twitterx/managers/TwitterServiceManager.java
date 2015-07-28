package tss.com.twitterx.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

/**
 * Created by danbozdog on 24/07/15.
 */
public class TwitterServiceManager {

    private static final String SINCEID = "SinceId";
    SharedPreferences mPreferences;

    @Inject
    public TwitterServiceManager(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSinceId(long sinceId) {
        mPreferences.edit().putLong(SINCEID, sinceId).commit();
    }

    public long getSinceId() {
        return mPreferences.getLong(SINCEID, 0);
    }
}
