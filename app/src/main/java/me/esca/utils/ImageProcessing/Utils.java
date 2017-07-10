package me.esca.utils.ImageProcessing;

import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by Me on 09/07/2017.
 */

public class Utils {

    public int getHeightRatioFromImageFile(String imageFileUrl, int dstWidth){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(imageFileUrl).getAbsolutePath(), options);
        int height = options.outHeight;
        int width = options.outWidth;

        return height > 0 ? dstWidth * width/height : 400;
    }
}
