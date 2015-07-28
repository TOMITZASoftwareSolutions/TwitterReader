package tss.com.twitterx;

import android.content.Context;
import android.os.Environment;
import android.test.AndroidTestCase;

import java.io.File;

import javax.inject.Inject;

import tss.com.twitterx.di.ApplicationModule;
import tss.com.twitterx.di.DaggerTwitterTestComponent;
import tss.com.twitterx.di.TwitterTestComponent;
import tss.com.twitterx.managers.PhotoManager;

/**
 * Created by danbozdog on 25/07/15.
 */
public class PhotoTest extends AndroidTestCase {

    @Inject
    PhotoManager mPhotoManager;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Context context = getContext();
        TwitterTestComponent component = DaggerTwitterTestComponent.builder().applicationModule(new ApplicationModule(context)).build();
        component.inject(this);
    }

    public void testBDownload() {
        String testURL = "https://upload.wikimedia.org/wikipedia/commons/b/bc/Rainforest_Fatu_Hiva.jpg";
        String name = "Rainforest_Fatu_Hiva.jpg";
        mPhotoManager.getAndSavePhoto(testURL, name);

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/photos/" + name);
        assertEquals(true, file.exists());
    }


    public void testCDelete() {
        File photos = new File(mPhotoManager.getPhotosFolder());
        assertEquals(1, photos.listFiles().length);

        mPhotoManager.deletePhotos();

        assertEquals(0, photos.listFiles().length);
    }


}
