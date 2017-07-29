package me.esca.services.escaWS.images;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.image.tableUtils.ImagesTableDefinition;
import me.esca.model.Image;
import me.esca.utils.Connectivity;

import static me.esca.services.escaWS.Utils.GET_IMAGE_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 28/07/2017.
 */

public class FetchImageByRecipeId extends Service {
    private final IBinder mBinder = new FetchImageByRecipeId.MyBinder();
    private Long recipeId;
    private Image imageResult;
    private Context context;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.recipeId = intent.getLongExtra("recipeId", 0);
        this.context = FetchImageByRecipeId.this;
        new FetchImageByRecipeId.GetRecipeImage().execute(recipeId);
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        new FetchImageByRecipeId.GetRecipeImage().execute(recipeId);
        return mBinder;
    }

    public class MyBinder extends Binder {
        public FetchImageByRecipeId getService() {
            return FetchImageByRecipeId.this;
        }
    }

    private class GetRecipeImage extends AsyncTask<Long, Image, Image> {
        private Long imageId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Image doInBackground(Long[] params) {

            imageId = params[0];
            Cursor cursor = context.getContentResolver().query(RecipesContentProvider.CONTENT_URI_IMAGES,
                    null, ImagesTableDefinition.RECIPE_ID_COLUMN + " = ? and " +
                            ImagesTableDefinition.IS_MAIN_PICTURE_COLUMN + " = 1", new String[]{String.valueOf(params[0])},
                    null);

            if(cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                Image image = new Image(
                        cursor.getLong(cursor.getColumnIndex(ImagesTableDefinition.ID_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.ORIGINAL_NAME_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.ORIGINAL_NAME_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.DATE_CREATED_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.LAST_UPDATED_COLUMN)),
                        true,
                        null, null,
                        cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.EXTENSION_COLUMN)));
                cursor.close();
                return image;
            }
            else{
                if(Connectivity.isNetworkAvailable(FetchImageByRecipeId.this)) {
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<Image> response =
                            restTemplate.exchange(MAIN_DOMAIN_NAME + GET_IMAGE_URL.replace("{recipeId}",
                                    String.valueOf(params[0])),
                                    HttpMethod.GET, null, new ParameterizedTypeReference<Image>() {
                                    });
                    if (response != null) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ImagesTableDefinition.ID_COLUMN, response.getBody().getId());
                        contentValues.put(ImagesTableDefinition.ORIGINAL_NAME_COLUMN, response.getBody().getOriginalName());
                        contentValues.put(ImagesTableDefinition.ORIGINAL_PATH_COLUMN, response.getBody().getOriginalPath());
                        contentValues.put(ImagesTableDefinition.DATE_CREATED_COLUMN, response.getBody().getDateCreated());
                        contentValues.put(ImagesTableDefinition.LAST_UPDATED_COLUMN, response.getBody().getLastUpdated());
                        contentValues.put(ImagesTableDefinition.IS_MAIN_PICTURE_COLUMN, response.getBody().isMainPicture());
                        contentValues.put(ImagesTableDefinition.COOK_ID_COLUMN, "");
                        contentValues.put(ImagesTableDefinition.RECIPE_ID_COLUMN, String.valueOf(params[0]));
                        contentValues.put(ImagesTableDefinition.EXTENSION_COLUMN, response.getBody().getExtension());


                        context.getContentResolver().insert(RecipesContentProvider.CONTENT_URI_IMAGES, contentValues);

                    }
                    return response != null ? response.getBody() : null;
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(Image image) {
            super.onPostExecute(image);
            Intent intent = new Intent("FetchImageByRecipeId");
            intent.putExtra("imageResult", image);
            sendBroadcast(intent);
            stopSelf();
        }
    }
}
