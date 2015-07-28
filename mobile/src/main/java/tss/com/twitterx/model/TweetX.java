package tss.com.twitterx.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by danbozdog on 17/07/15.
 */

@DatabaseTable(tableName = TweetX.TABLE_TWEETS)
public class TweetX {

    public final static String TABLE_TWEETS = "tweets";
    public final static String COLUMN_ID = "id";
    public final static String COLUMN_LONGID="longid";
    public final static String COLUMN_DATE_CREATED = "datecreated";
    public final static String COLUMN_TEXT = "text";
    public final static String COLUMN_URL = "url";
    public final static String COLUMN_USER = "user";
    public final static String COLUMN_READ = "read";
    public final static String COLUMN_PHOTO = "photo";

    @DatabaseField(columnName = COLUMN_ID, id = true)
    private String mId;
    @DatabaseField(columnName = COLUMN_LONGID)
    private long mLongId;
    @DatabaseField(columnName = COLUMN_DATE_CREATED)
    private Date mCreatedDate;
    @DatabaseField(columnName = COLUMN_TEXT)
    private String mText;
    @DatabaseField(columnName = COLUMN_URL)
    private String mUrl;
    @DatabaseField(columnName = COLUMN_USER)
    private String mUser;
    @DatabaseField(columnName = COLUMN_READ)
    private boolean mRead;
    @DatabaseField(columnName = COLUMN_PHOTO)
    private String mPhoto;

    public TweetX() {
    }

    public TweetX(String id,long longId, Date createdDate, String text, String url,String photo, String user) {
        mId = id;
        mLongId = longId;
        mCreatedDate = createdDate;
        mText = text;
        mUrl = url;
        mPhoto = photo;
        mUser = user;
        mRead = false;
    }

    public void setRead(boolean read) {
        this.mRead = read;
    }


    public String getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getUser() {
        return mUser;
    }

    public boolean isRead() {
        return mRead;
    }

    public Date getCreatedDate() {
        return mCreatedDate;
    }

    public long getLongId() {
        return mLongId;
    }

    public String getPhoto() {
        return mPhoto;
    }
}
