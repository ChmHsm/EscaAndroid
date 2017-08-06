package me.esca.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import me.esca.R;
import me.esca.activities.RecipeDetailsActivity;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.image.tableUtils.ImagesTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Image;
import me.esca.utils.Connectivity;
import me.esca.utils.CursorRecyclerViewAdapter;
import me.esca.utils.glide.GlideApp;

import static me.esca.services.escaWS.Utils.GET_IMAGE_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 26/07/2017.
 */

public class ProfileRecipesAdapter extends CursorRecyclerViewAdapter {

    public ProfileRecipesAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_profile_recipe, parent, false);
        return new ProfileRecipesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long recipeId = ((ViewHolder) viewHolder).recipeId;
                Long imageId = ((ViewHolder) viewHolder).imageId;
                String imageExtension = ((ViewHolder) viewHolder).imageExtension;
                if (recipeId <= 0) {
                    throw new IllegalArgumentException();
                } else {
                    Intent intent = new Intent(mContext, RecipeDetailsActivity.class);
                    intent.putExtra("recipeId", recipeId);
                    intent.putExtra("imageId", imageId);
                    intent.putExtra("imageExtension", imageExtension);
                    mContext.startActivity(intent);
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
        public Long recipeId;
        public Long imageId;
        public String imageExtension;


        public ViewHolder(View view) {
            super(view);
            recipeImageView = (ImageView) view.findViewById(R.id.recipeThumbnail);
            recipeTitle = (TextView) view.findViewById(R.id.recipeTitle);
        }

        public void setData(Cursor c) {
            recipeImageView.setImageDrawable(mContext.getDrawable(R.drawable.recipe_image_placeholder));
            recipeTitle.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)));

            recipeId = c.getLong(c.getColumnIndex(RecipesTableDefinition.ID_COLUMN));
            new GetRecipeImage().execute(recipeId);

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
                    return new Image(
                            cursor.getLong(cursor.getColumnIndex(ImagesTableDefinition.ID_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.ORIGINAL_NAME_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.ORIGINAL_NAME_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.DATE_CREATED_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.LAST_UPDATED_COLUMN)),
                            true,
                            null, null,
                            cursor.getString(cursor.getColumnIndex(ImagesTableDefinition.EXTENSION_COLUMN)));
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
                else{
                    //TODO notify not connected
                }

                return null;
            }

            @Override
            protected void onPostExecute(Image image) {
                super.onPostExecute(image);
                if(image != null){
                    imageId = image.getId();
                    imageExtension = image.getExtension();
                    GlideApp.with(mContext)
                            .load("http://escaws.s3.amazonaws.com/Image storage directory/" + image.getId() + image.getExtension())
                            .fitCenter()
                            .into(recipeImageView);
                }
            }
        }
    }
}
