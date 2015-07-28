package tss.com.twitterx.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by danbozdog on 25/07/15.
 */
public class PhotoManager {


    private final Context mContext;

    @Inject
    public PhotoManager(Context context) {
        mContext = context;
    }


    public void getAndSavePhoto(String url, String name) {
        try {
            Bitmap photo = Picasso.with(mContext)
                    .load(url)
                    .centerCrop().resize(320, 320).get();
            File file = new File(getPhotosFolder() + "/" + name);
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
            ostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePhotos(){
        File file = new File(getPhotosFolder());
        if(file.isDirectory()){
            for(File f:file.listFiles()){
                f.delete();
            }
        }
    }

    public String getPhotosFolder() {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/photos/");
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getPath();
    }

    public Bitmap getPhoto(String photo){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(getPhotosFolder()+"/"+photo, options);
        return bitmap;
    }

}
