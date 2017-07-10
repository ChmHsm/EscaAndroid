package me.esca.utils.ImageProcessing;

import android.graphics.Bitmap;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Me on 09/07/2017.
 */

public class ImageResize extends SimpleTarget<Bitmap> {

    private String fileName;
    private Bitmap.CompressFormat format;
    private int quality;


    public ImageResize(String fileName, int width, int height) {
        this(fileName, width, height, Bitmap.CompressFormat.JPEG, 70);
    }
    private ImageResize(String fileName, int width, int height, Bitmap.CompressFormat format, int quality) {
        super(width, height);
        this.fileName = fileName;
        this.format = format;
        this.quality = quality;
    }

    @Override
    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            bitmap.compress(format, quality, out);
            out.flush();
            out.close();
            onFileSaved();
        } catch (IOException e) {
            e.printStackTrace();
            onSaveException(e);
        }
    }
    private void onFileSaved() {

    }
    private void onSaveException(Exception e) {

    }
}
