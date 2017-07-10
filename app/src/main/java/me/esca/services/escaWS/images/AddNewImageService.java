package me.esca.services.escaWS.images;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import me.esca.model.Recipe;

import static me.esca.services.escaWS.recipes.Utils.ADD_RECIPE_URL;
import static me.esca.services.escaWS.recipes.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 09/07/2017.
 */

public class AddNewImageService extends Service {
    private final IBinder mBinder = new AddNewImageService.MyBinder();
    private String imageFullName;
    private String imageUrl;
    private String loggedUsername = "Houssam";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.imageFullName =  intent.getStringExtra("imageFullName");
        this.imageUrl = intent.getStringExtra("recipeImageUrl");
        new AddNewImageService.AddNewImage().execute();
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        new AddNewImageService.AddNewImage().execute();
        return mBinder;
    }

    public class MyBinder extends Binder {
        public AddNewImageService getService() {
            return AddNewImageService.this;
        }
    }

    private class AddNewImage extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            //TODO Add image to Rest WS then to the AWS S3 Bucket
//            RestTemplate restTemplate = new RestTemplate();
//            List<HttpMessageConverter<?>> list = new ArrayList<>();
//            list.add(new MappingJackson2HttpMessageConverter());
//            restTemplate.setMessageConverters(list);
//
//            resultLocation = restTemplate.postForLocation(MAIN_DOMAIN_NAME+ADD_RECIPE_URL.replace("{username}",
//                    loggedUsername), recipeToBeAdded, Recipe.class);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent("ImageServiceIsDone");
            sendBroadcast(intent);
            stopSelf();
        }
    }
}
