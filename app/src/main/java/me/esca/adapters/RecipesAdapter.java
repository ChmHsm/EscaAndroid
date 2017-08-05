package me.esca.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import me.esca.activities.RecipeDetailsActivity;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.image.tableUtils.ImagesTableDefinition;
import me.esca.dbRelated.likeRelationship.tableUtils.LikesTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Cook;
import me.esca.model.Image;
import me.esca.model.LikeRelationship;
import me.esca.model.Recipe;
import me.esca.services.escaWS.Utils;
import me.esca.utils.Connectivity;
import me.esca.utils.CursorRecyclerViewAdapter;
import me.esca.utils.DateFormatting;
import me.esca.utils.glide.GlideApp;

import static me.esca.services.escaWS.Utils.ADD_LIKE_TO_RECIPE_URL;
import static me.esca.services.escaWS.Utils.ADD_RECIPE_URL;
import static me.esca.services.escaWS.Utils.DELETE_LIKE_FROM_RECIPE_URL;
import static me.esca.services.escaWS.Utils.GET_IMAGE_URL;
import static me.esca.services.escaWS.Utils.GET_LIKES_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 04/06/2017.
 */
public class RecipesAdapter extends CursorRecyclerViewAdapter {

    public static int REQUEST_CODE = 01;

    public RecipesAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final Cursor cursor) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long recipeId = ((ViewHolder) viewHolder).id;
                Long imageId = ((ViewHolder) viewHolder).imageId;
                String imageExtension = ((ViewHolder) viewHolder).imageExtension;
                if (recipeId <= 0) {
                    throw new IllegalArgumentException();
                } else {
                    Intent intent = new Intent(mContext, RecipeDetailsActivity.class);
                    intent.putExtra("recipeId", recipeId);
                    intent.putExtra("imageId", imageId);
                    intent.putExtra("imageExtension", imageExtension);
                    ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView recipeImageView;
        public TextView recipeTitle;
        public TextView recipeDescription;
        public TextView recipeDate;
        public TextView cookNameTextView;
        private TextView followTextView;
        public Long id;
        public Long imageId;
        public long likeId;
        public String imageExtension;
        public SparkButton likeButton;
        public TextView numberOfLikes;

        public ViewHolder(View view) {
            super(view);
            recipeImageView = (ImageView) view.findViewById(R.id.recipeImageView);
            recipeTitle = (TextView) view.findViewById(R.id.recipteTitleTextView);
            recipeDescription = (TextView) view.findViewById(R.id.recipe_description_text_view);
            recipeDate = (TextView) view.findViewById(R.id.recipe_date_text_view);
            cookNameTextView = (TextView) view.findViewById(R.id.cook_name_text_view);
            followTextView = (TextView) view.findViewById(R.id.follow_text_view);
            likeButton = (SparkButton) view.findViewById(R.id.likeButton);
            numberOfLikes = (TextView) view.findViewById(R.id.number_of_likes);


            likeButton.setEventListener(new SparkEventListener() {
                @Override
                public void onEvent(ImageView button, boolean buttonState) {
                    if (Connectivity.isNetworkAvailable(mContext)) {
                        if (buttonState) {
                            new AddRecipeLike().execute(id);
                        } else {
                            if (likeId > 0) {
                                new DeleteRecipeLike().execute(likeId);
                            }
                        }
                    } else {
                        //TODO notify not connected
                    }
                }

                @Override
                public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                }

                @Override
                public void onEventAnimationStart(ImageView button, boolean buttonState) {

                }
            });
        }

        public void setData(Cursor c) {
            recipeImageView.setImageDrawable(mContext.getDrawable(R.drawable.recipe_image_placeholder));

            recipeTitle.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)));
            recipeDescription.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.INSTRUCTIONS_COLUMN)));

            recipeDate.setText(DateFormatting.formatDateTime(c.getString(
                    c.getColumnIndex(RecipesTableDefinition.DATE_CREATED_COLUMN))));
            id = c.getLong(c.getColumnIndex(RecipesTableDefinition.ID_COLUMN));

            new GetRecipeImage().execute(id);

            Cursor cursor = mContext.getContentResolver().query(
                    Uri.parse(RecipesContentProvider.CONTENT_URI_COOKS + "/"
                            + c.getString(c.getColumnIndex(RecipesTableDefinition.COOK_COLUMN))),
                    new String[]{CooksTableDefinition.USERNAME_COLUMN},
                    null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                cookNameTextView.setText(cursor.getString(
                        cursor.getColumnIndex(CooksTableDefinition.USERNAME_COLUMN)));
                cursor.close();
            }

            followTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Follow button", Toast.LENGTH_SHORT).show();
                }
            });
            new GetRecipeLikes().execute(id);

        }

        private class GetRecipeImage extends AsyncTask<Long, Image, Image> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Image doInBackground(Long[] params) {

                Cursor cursor = mContext.getContentResolver().query(RecipesContentProvider.CONTENT_URI_IMAGES,
                        null, ImagesTableDefinition.RECIPE_ID_COLUMN + " = ? and " +
                                ImagesTableDefinition.IS_MAIN_PICTURE_COLUMN + " = 1", new String[]{String.valueOf(params[0])},
                        null);

                if (cursor != null && cursor.getCount() > 0) {
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
                } else if (Connectivity.isNetworkAvailable(mContext)) {
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

                        mContext.getContentResolver().insert(RecipesContentProvider.CONTENT_URI_IMAGES, contentValues);
                    }
                    return response != null ? response.getBody() : null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Image image) {
                super.onPostExecute(image);
                imageId = image.getId();
                imageExtension = image.getExtension();
                GlideApp.with(mContext)
                        .load("http://escaws.s3.amazonaws.com/Image storage directory/" + image.getId() + image.getExtension())
                        .fitCenter()
                        .into(recipeImageView);
            }
        }

        private class GetRecipeLikes extends AsyncTask<Long, List<LikeRelationship>, List<LikeRelationship>> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<LikeRelationship> doInBackground(Long[] params) {

                Cursor likesCursor = mContext.getContentResolver().query(RecipesContentProvider.CONTENT_URI_LIKES,
                        null, LikesTableDefinition.RECIPE_ID_COLUMN + " = ? ", new String[]{String.valueOf(params[0])},
                        null);

                DatabaseUtils.dumpCursorToString(likesCursor);

                if (likesCursor != null && likesCursor.getCount() > 0) {
                    List<LikeRelationship> likes = new ArrayList<>();
                    while (likesCursor.moveToNext()) {
                        Cursor cookCursor = mContext.getContentResolver().query(
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
                } else if (Connectivity.isNetworkAvailable(mContext)) {
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

                            mContext.getContentResolver().insert(RecipesContentProvider.CONTENT_URI_LIKES, contentValues);
                        }
                    }
                    return response != null ? response.getBody() : null;
                }
                return null;
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
                        Cursor cookCursor = mContext.getContentResolver().query(
                                RecipesContentProvider.CONTENT_URI_COOKS,
                                null, CooksTableDefinition.USERNAME_COLUMN + " = ? ",
                                new String[]{Utils.CONNECTED_COOK},
                                null);
                        if (cookCursor != null && cookCursor.getCount() > 0) {

                            Bundle bundle = new Bundle();
                            bundle.putString("uri", RecipesContentProvider.CONTENT_URI_LIKES.toString());
                            bundle.putLong("likeId", like.getId());
                            bundle.putLong("likeCook", like.getCook().getId());
                            bundle.putLong("likeRecipe", id);

                            mContext.getContentResolver().call(RecipesContentProvider.CONTENT_URI_LIKES,
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
                    mContext.getContentResolver().delete(
                            Uri.parse(RecipesContentProvider.CONTENT_URI_LIKES + "/" + likeId)
                            , null, null);
                }
            }
        }
    }
}
