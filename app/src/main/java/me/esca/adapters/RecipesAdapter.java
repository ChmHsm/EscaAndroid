package me.esca.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import me.esca.R;
import me.esca.activities.RecipeDetailsActivity;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Image;
import me.esca.utils.CursorRecyclerViewAdapter;
import me.esca.utils.DateFormatting;
import me.esca.utils.glide.GlideApp;

import static me.esca.services.escaWS.Utils.GET_IMAGE_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 04/06/2017.
 */
public class RecipesAdapter extends CursorRecyclerViewAdapter {

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
                if(recipeId <= 0){
                    throw new IllegalArgumentException();
                }
                else{
                    Intent intent = new Intent(mContext, RecipeDetailsActivity.class);
                    intent.putExtra("recipeId",recipeId);
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
        public TextView recipeDescription;
        public TextView recipeDate;
        public TextView cookNameTextView;
        private TextView followTextView;
        public Long id;
        public Long imageId;
        public String imageExtension;

        public ViewHolder(View view) {
            super(view);
            recipeImageView = (ImageView) view.findViewById(R.id.recipeImageView);
            recipeTitle = (TextView) view.findViewById(R.id.recipteTitleTextView);
            recipeDescription = (TextView) view.findViewById(R.id.recipe_description_text_view);
            recipeDate = (TextView) view.findViewById(R.id.recipe_date_text_view);
            cookNameTextView = (TextView) view.findViewById(R.id.cook_name_text_view);
            followTextView = (TextView) view.findViewById(R.id.follow_text_view);
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
                    Uri.parse(RecipesContentProvider.CONTENT_URI_COOKS+"/"
                            +c.getString(c.getColumnIndex(RecipesTableDefinition.COOK_COLUMN))),
                    new String[]{CooksTableDefinition.USERNAME_COLUMN},
                    null, null, null);
            if(cursor != null && cursor.getCount() > 0){
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

        }

        private class GetRecipeImage extends AsyncTask<Long, Image, Image>{

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Image doInBackground(Long[] params) {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Image> response =
                        restTemplate.exchange(MAIN_DOMAIN_NAME+GET_IMAGE_URL.replace("{recipeId}",
                                String.valueOf(params[0])),
                                HttpMethod.GET, null, new ParameterizedTypeReference<Image>() {
                                });

                return response != null ? response.getBody() : null;
            }

            @Override
            protected void onPostExecute(Image image) {
                super.onPostExecute(image);
                imageId = image.getId();
                imageExtension = image.getExtension();
                GlideApp.with(mContext)
                        .load("http://escaws.s3.amazonaws.com/Image storage directory/"+image.getId()+image.getExtension())
                        .fitCenter()
                        .into(recipeImageView);
            }
        }
    }
}
