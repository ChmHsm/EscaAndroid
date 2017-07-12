package me.esca.services.escaWS.recipes;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.esca.model.Image;
import me.esca.model.Recipe;

import static me.esca.services.escaWS.Utils.ADD_IMAGE_URL;
import static me.esca.services.escaWS.Utils.ADD_RECIPE_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 26/06/2017.
 */

public class AddNewRecipeService extends Service {

    private final IBinder mBinder = new MyBinder();
    private URI resultLocation;
    private Recipe recipeToBeAdded;
    private String loggedUsername = "Houssam";
    private String imageUrl;
    private Image imageToBeAdded;
    private Image imageResponse;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.recipeToBeAdded = (Recipe) intent.getSerializableExtra("recipeToBeAdded");
        this.imageToBeAdded =  (Image) intent.getSerializableExtra("imageToBeAdded");
        this.imageUrl = intent.getStringExtra("recipeImageUrl");
        new AddNewRecipe().execute();
        return Service.START_NOT_STICKY;
    }

    public URI getResultLocation() {
        return resultLocation;
    }

    public Recipe getRecipeToBeAdded() {
        return recipeToBeAdded;
    }

    public void setRecipeToBeAdded(Recipe recipeToBeAdded) {
        this.recipeToBeAdded = recipeToBeAdded;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        new AddNewRecipe().execute();
        return mBinder;
    }

    public class MyBinder extends Binder {
        public AddNewRecipeService getService() {
            return AddNewRecipeService.this;
        }
    }

    private class AddNewRecipe extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();
            List<HttpMessageConverter<?>> list = new ArrayList<>();
            list.add(new MappingJackson2HttpMessageConverter());
            restTemplate.setMessageConverters(list);

            resultLocation = restTemplate.postForLocation(MAIN_DOMAIN_NAME+ADD_RECIPE_URL.replace("{username}",
                    loggedUsername), recipeToBeAdded, Recipe.class);

            //TODO Add image to Rest WS then to the AWS S3 Bucket
            Long id = Long.parseLong(resultLocation.getPath().substring(resultLocation.getPath().lastIndexOf("/") + 1));
            imageResponse = restTemplate.postForObject(MAIN_DOMAIN_NAME+ADD_IMAGE_URL.replace("{recipeId}",
                    String.valueOf(id)), imageToBeAdded, Image.class);


            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-1:e7f51748-13c0-473d-8f34-c85f5a96fc2a", // Identity pool ID
                    Regions.US_EAST_1 // Region
            );
            AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

            ObjectMetadata myObjectMetadata = new ObjectMetadata();

            Map<String, String> userMetadata = new HashMap<String,String>();
            userMetadata.put("metadata","metadata");
            myObjectMetadata.setUserMetadata(userMetadata);
            //TODO get the image file from imageUrl
            File imageFile = new File(imageUrl);
            TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
            TransferObserver observer = transferUtility.upload(
                    "escaws",     /* The bucket to upload to */
                    String.valueOf(id),    /* The key for the uploaded object */
                    imageFile,        /* The file where the data to upload exists */
                    myObjectMetadata);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent intent = new Intent("ServiceIsDone");
            intent.putExtra("resultLocation", resultLocation.toString());
            sendBroadcast(intent);
            stopSelf();
        }
    }
}
