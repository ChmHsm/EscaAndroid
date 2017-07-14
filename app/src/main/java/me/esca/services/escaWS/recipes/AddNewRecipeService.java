package me.esca.services.escaWS.recipes;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.esca.model.Image;
import me.esca.model.Recipe;
import me.esca.utils.Accessors;
import me.esca.utils.security.cryptography.Encryption;

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
    private Uri imageUri;
    private Image imageToBeAdded;
    private Image imageResponse;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.recipeToBeAdded = (Recipe) intent.getSerializableExtra("recipeToBeAdded");
        this.imageToBeAdded =  (Image) intent.getSerializableExtra("imageToBeAdded");
        this.imageUrl = intent.getStringExtra("recipeImageUrl");
        this.imageUri = Uri.parse(intent.getStringExtra("recipeImageUrl")) ;
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

            Long id = Long.parseLong(resultLocation.getPath().substring(resultLocation.getPath().lastIndexOf("/") + 1));
            String imagePath = getPath(getApplicationContext(), imageUri);
            imageToBeAdded.setExtension(imagePath.substring(imagePath.lastIndexOf(".")));
            imageResponse = restTemplate.postForObject(MAIN_DOMAIN_NAME+ADD_IMAGE_URL.replace("{recipeId}",
                    String.valueOf(id)), imageToBeAdded, Image.class);

            String pool = "";
            try {
                JSONArray jsonArray = new JSONArray(Accessors.loadJSONFromAsset(getApplicationContext()));
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                pool = jsonObject.getString("syek3SSWA");
                pool = Encryption.decrypt(pool);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    pool, // Identity pool ID
                    Regions.US_EAST_1 // Region
            );
            AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

            ObjectMetadata myObjectMetadata = new ObjectMetadata();

            Map<String, String> userMetadata = new HashMap<String,String>();
            userMetadata.put("metadata","metadata");
            myObjectMetadata.setUserMetadata(userMetadata);
            //TODO get the image file from imageUrl
            File imageFile = new File(imagePath);
            TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());
            TransferObserver observer = transferUtility.upload(
                    "escaws",     /* The bucket to upload to */
                    "Image storage directory/" + String.valueOf(imageResponse.getId()
                            +imageResponse.getExtension()),
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

        public  String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }

        public String getDataColumn(Context context, Uri uri, String selection,
                                           String[] selectionArgs) {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {
                    column
            };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int column_index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(column_index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        public  boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        public  boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        public  boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

    }
}
