package me.esca.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import me.esca.R;
import me.esca.adapters.RecipesAdapter;
import me.esca.databinding.RecipeDetailsActivityBinding;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.image.tableUtils.ImagesTableDefinition;
import me.esca.dbRelated.likeRelationship.tableUtils.LikesTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Cook;
import me.esca.model.LikeRelationship;
import me.esca.model.Recipe;
import me.esca.services.escaWS.Utils;
import me.esca.utils.glide.GlideApp;

import static me.esca.adapters.RecipesAdapter.REQUEST_CODE;
import static me.esca.services.escaWS.Utils.ADD_LIKE_TO_RECIPE_URL;
import static me.esca.services.escaWS.Utils.DELETE_LIKE_FROM_RECIPE_URL;
import static me.esca.services.escaWS.Utils.GET_LIKES_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 24/06/2017.
 */

public class RecipeDetailsActivity extends Activity {

    public static int RESULT_CODE = 1;
    public static String ACTIVITY_DETAIL_NOTIFICATION_BROADCAST = "recipesLikeStateChanged";

    private Long recipeId;
    private Long cookId;
    private Recipe recipe;
    private Cook cook;
    private ImageView recipeDetailImageView;
    private Long imageId;
    private String imageExtension;
    public SparkButton likeButton;
    public TextView numberOfLikes;
    private long likeId;
    private boolean initialLikeState;
    private boolean likeStateChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recipeId = getIntent().getLongExtra("recipeId", 0);
        imageId = getIntent().getLongExtra("imageId", 0);
        imageExtension = getIntent().getStringExtra("imageExtension");

        if (recipeId > 0) {
            RecipeDetailsActivityBinding binding = DataBindingUtil
                    .setContentView(this, R.layout.recipe_details_activity);

            recipeDetailImageView = (ImageView) findViewById(R.id.recipeDetailImageView);
            likeButton = (SparkButton) findViewById(R.id.likeButton);
            numberOfLikes = (TextView) findViewById(R.id.number_of_likes);

            likeButton.setEventListener(new SparkEventListener() {
                @Override
                public void onEvent(ImageView button, boolean buttonState) {
                    likeStateChanged = initialLikeState != buttonState;
                    if (buttonState) {
                        new AddRecipeLike().execute(recipeId);
                    } else {
                        if (likeId > 0) {
                            new DeleteRecipeLike().execute(likeId);
                        }
                    }
                }

                @Override
                public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                }

                @Override
                public void onEventAnimationStart(ImageView button, boolean buttonState) {

                }
            });

            GlideApp.with(this)
                    .load("http://escaws.s3.amazonaws.com/Image storage directory/" + imageId + imageExtension)
                    .placeholder(getDrawable(R.drawable.recipe_image_placeholder))
                    .fitCenter()
                    .into(recipeDetailImageView);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setActionBar(toolbar);
            final Drawable upArrow = getApplicationContext()
                    .getDrawable(R.drawable.abc_ic_ab_back_material);

            if (getActionBar() != null) {
                if (upArrow != null) {
                    upArrow.setColorFilter(getApplicationContext()
                            .getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                    getActionBar().setHomeAsUpIndicator(upArrow);
                }
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setDisplayShowHomeEnabled(true);
                getActionBar().setTitle(R.string.recipe);
            }
            //Retrieving the recipe
            Cursor recipeCursor = getContentResolver()
                    .query(Uri.parse(RecipesContentProvider.CONTENT_URI_RECIPES + "/" + recipeId),
                            null, null, null, null);


            if (recipeCursor != null && recipeCursor.getCount() > 0) {
                //Retrieving the cook
                recipeCursor.moveToFirst();
                cookId = recipeCursor.getLong(recipeCursor.getColumnIndex(RecipesTableDefinition.COOK_COLUMN));
                if (cookId > 0) {
                    Cursor cookCursor = getContentResolver()
                            .query(Uri.parse(RecipesContentProvider.CONTENT_URI_COOKS + "/" + cookId),
                                    null, null, null, null);

                    if (cookCursor != null && cookCursor.getCount() > 0) {
                        cookCursor.moveToFirst();
                        cook = new Cook(cookId,
                                cookCursor.getString(cookCursor.getColumnIndex(CooksTableDefinition.USERNAME_COLUMN)),
                                null, null, null, null, null);

                        cookCursor.close();

                        recipe = new Recipe(recipeCursor.getString(recipeCursor.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)),
                                recipeCursor.getInt(recipeCursor.getColumnIndex(RecipesTableDefinition.DIFFICULTY_RATING_COLUMN)),
                                recipeCursor.getInt(recipeCursor.getColumnIndex(RecipesTableDefinition.PREP_TIME_COLUMN)),
                                recipeCursor.getDouble(recipeCursor.getColumnIndex(RecipesTableDefinition.PREP_COST_COLUMN)),
                                recipeCursor.getString(recipeCursor.getColumnIndex(RecipesTableDefinition.INGREDIENTS_COLUMN)),
                                recipeCursor.getString(recipeCursor.getColumnIndex(RecipesTableDefinition.INSTRUCTIONS_COLUMN)),
                                recipeCursor.getString(recipeCursor.getColumnIndex(RecipesTableDefinition.DATE_CREATED_COLUMN)),
                                recipeCursor.getString(recipeCursor.getColumnIndex(RecipesTableDefinition.LAST_UPDATED_COLUMN)),
                                cook, null);
                        binding.setRecipe(recipe);
                        recipeCursor.close();
                    } else {
                        Toast.makeText(RecipeDetailsActivity.this,
                                "An error occurred while retrieving the requested recipe",
                                Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(RecipeDetailsActivity.this,
                        "An error occurred while retrieving the requested recipe",
                        Toast.LENGTH_LONG).show();
            }
            new GetRecipeLikes().execute(imageId);
        } else {
            Toast.makeText(RecipeDetailsActivity.this,
                    "An error occurred while retrieving the requested recipe",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ACTIVITY_DETAIL_NOTIFICATION_BROADCAST);
        intent.putExtra("recipeId", recipeId);
        intent.putExtra("recipePosition", "");
        intent.putExtra("likeChanged", likeStateChanged);
        intent.putExtra("newLikeState", likeButton.isChecked());
        sendBroadcast(intent);
    }

    private class GetRecipeLikes extends AsyncTask<Long, List<LikeRelationship>, List<LikeRelationship>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<LikeRelationship> doInBackground(Long[] params) {

            Cursor likesCursor = RecipeDetailsActivity.this.getContentResolver().query(RecipesContentProvider.CONTENT_URI_LIKES,
                    null, LikesTableDefinition.RECIPE_ID_COLUMN + " = ? ", new String[]{String.valueOf(params[0])},
                    null);

            DatabaseUtils.dumpCursorToString(likesCursor);

            if (likesCursor != null && likesCursor.getCount() > 0) {
                List<LikeRelationship> likes = new ArrayList<>();
                while (likesCursor.moveToNext()) {
                    Cursor cookCursor = RecipeDetailsActivity.this.getContentResolver().query(
                            Uri.parse(RecipesContentProvider.CONTENT_URI_COOKS + "/" +
                                    likesCursor.getLong(likesCursor.getColumnIndex(LikesTableDefinition.COOK_ID_COLUMN))),
                            null, null, null,
                            null);
                    if (cookCursor != null && cookCursor.getCount() > 0) {

                        cookCursor.moveToNext();
                        Cook cook = new Cook(likesCursor.getLong(likesCursor.getColumnIndex(LikesTableDefinition.COOK_ID_COLUMN)),
                                cookCursor.getString(cookCursor.getColumnIndex(CooksTableDefinition.USERNAME_COLUMN)),
                                null, null, null, null, null);
                        cookCursor.close();

                        LikeRelationship like = new LikeRelationship(
                                likesCursor.getLong(likesCursor.getColumnIndex(LikesTableDefinition.ID_COLUMN)),
                                null, cook);

                        likes.add(like);
                    }
                }


                likesCursor.close();
                return likes;
            } else {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<List<LikeRelationship>> response =
                        restTemplate.exchange(MAIN_DOMAIN_NAME + GET_LIKES_URL.replace("{recipeId}",
                                String.valueOf(params[0])),
                                HttpMethod.GET, null, new ParameterizedTypeReference<List<LikeRelationship>>() {
                                });
                if (response != null) {
                    for (LikeRelationship like : response.getBody()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ImagesTableDefinition.ID_COLUMN, like.getId());
                        contentValues.put(ImagesTableDefinition.COOK_ID_COLUMN, like.getCook().getId());
                        contentValues.put(ImagesTableDefinition.RECIPE_ID_COLUMN, String.valueOf(params[0]));

                        RecipeDetailsActivity.this.getContentResolver().insert(RecipesContentProvider.CONTENT_URI_LIKES, contentValues);
                    }
                }
                return response != null ? response.getBody() : null;
            }
        }

        @Override
        protected void onPostExecute(List<LikeRelationship> likes) {
            super.onPostExecute(likes);
            if (likes != null && likes.size() > 0) {
                likeButton.setChecked(false);
                likeId = 0;
                for (LikeRelationship like : likes) {
                    if (like.getCook().getUsername().equalsIgnoreCase(Utils.CONNECTED_COOK)) {
                        likeId = like.getId();
                        likeButton.setChecked(true);
                    }
                }
                numberOfLikes.setText(String.valueOf(likes.size()));
            } else {
                numberOfLikes.setText(String.valueOf(0));
                likeButton.setChecked(false);
            }

            initialLikeState = likeButton.isChecked();
        }
    }

    private class AddRecipeLike extends AsyncTask<Long, LikeRelationship, LikeRelationship> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected LikeRelationship doInBackground(Long[] params) {

            RestTemplate restTemplate = new RestTemplate();
            List<HttpMessageConverter<?>> list = new ArrayList<>();
            list.add(new MappingJackson2HttpMessageConverter());
            restTemplate.setMessageConverters(list);

            ResponseEntity<LikeRelationship> response = restTemplate
                    .postForEntity(MAIN_DOMAIN_NAME + ADD_LIKE_TO_RECIPE_URL.replace("{recipeId}",
                            String.valueOf(params[0])), null, LikeRelationship.class);

            if (response != null) {
                likeId = response.getBody().getId();
                return response.getBody();
            }
            return null;
        }

        @Override
        protected void onPostExecute(LikeRelationship like) {
            super.onPostExecute(like);
            if (like != null) {
                int likesNbr = Integer.parseInt(numberOfLikes.getText().toString());
                likesNbr++;
                numberOfLikes.setText(String.valueOf(likesNbr));
                if (likeId > 0) {
                    Cursor cookCursor = RecipeDetailsActivity.this.getContentResolver().query(
                            RecipesContentProvider.CONTENT_URI_COOKS,
                            null, CooksTableDefinition.USERNAME_COLUMN + " = ? ",
                            new String[]{Utils.CONNECTED_COOK},
                            null);
                    if (cookCursor != null && cookCursor.getCount() > 0) {

                        Bundle bundle = new Bundle();
                        bundle.putString("uri", RecipesContentProvider.CONTENT_URI_LIKES.toString());
                        bundle.putLong("likeId", like.getId());
                        bundle.putLong("likeCook", like.getCook().getId());
                        bundle.putLong("likeRecipe", recipeId);

                        RecipeDetailsActivity.this.getContentResolver().call(RecipesContentProvider.CONTENT_URI_LIKES,
                                "saveOrUpdateLike", null, bundle);
                    }
                }
            }

        }
    }

    private class DeleteRecipeLike extends AsyncTask<Long, Boolean, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Long[] params) {

            RestTemplate restTemplate = new RestTemplate();
            List<HttpMessageConverter<?>> list = new ArrayList<>();
            list.add(new MappingJackson2HttpMessageConverter());
            restTemplate.setMessageConverters(list);

            restTemplate.delete(MAIN_DOMAIN_NAME + DELETE_LIKE_FROM_RECIPE_URL.replace("{likeId}",
                    String.valueOf(params[0])), null, null);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (isSuccess) {
                int likesNbr = Integer.parseInt(numberOfLikes.getText().toString());
                if (likesNbr > 0) likesNbr--;
                numberOfLikes.setText(String.valueOf(likesNbr));
                RecipeDetailsActivity.this.getContentResolver().delete(
                        Uri.parse(RecipesContentProvider.CONTENT_URI_LIKES + "/" + likeId)
                        , null, null);
            }
        }
    }
}
