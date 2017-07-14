package me.esca.utils;

import android.app.Activity;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.esca.activities.FoodFeedActivity;

/**
 * Created by Me on 14/07/2017.
 */

public class Accessors {

    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is =  context.getAssets().open("syek");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
