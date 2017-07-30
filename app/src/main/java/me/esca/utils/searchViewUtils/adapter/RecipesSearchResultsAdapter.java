package me.esca.utils.searchViewUtils.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.esca.R;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.image.tableUtils.ImagesTableDefinition;
import me.esca.model.Image;
import me.esca.model.Recipe;
import me.esca.utils.glide.GlideApp;
import me.esca.utils.searchViewUtils.data.SearchResultsEntity;

import static me.esca.services.escaWS.Utils.GET_IMAGE_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 19/07/2017.
 */

public class RecipesSearchResultsAdapter extends RecyclerView.Adapter<RecipesSearchResultsAdapter.ViewHolder>  {

    private List<SearchResultsEntity> mDataSet = new ArrayList<>();
    private Context viewContext;
    private int mLastAnimatedItemPosition = -1;

    public interface OnItemClickListener{
        void onClick(SearchResultsEntity searchResultsEntity);
    }

    private RecipesSearchResultsAdapter.OnItemClickListener mItemsOnClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mColorName;
        public final TextView mColorValue;
        public final View mTextContainer;
        public final ImageView searchResultImage;
        public final TextView cookName;
        public final TextView cookedBy;
        public final Context context;


        public ViewHolder(View view, Context context) {
            super(view);
            mColorName = (TextView) view.findViewById(R.id.color_name);
            mColorValue = (TextView) view.findViewById(R.id.color_value);
            mTextContainer = view.findViewById(R.id.text_container);
            searchResultImage = (ImageView) view.findViewById(R.id.searchResultImage);
            cookName = (TextView) view.findViewById(R.id.cookName);
            cookedBy = (TextView) view.findViewById(R.id.cooked_by);
            this.context = context;
        }

        private class GetRecipeImage extends AsyncTask<Long, Image, Image> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Image doInBackground(Long[] params) {

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
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<Image> response =
                            restTemplate.exchange(MAIN_DOMAIN_NAME+GET_IMAGE_URL.replace("{recipeId}",
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
            }

            @Override
            protected void onPostExecute(Image image) {
                super.onPostExecute(image);
                GlideApp.with(context)
                        .load("http://escaws.s3.amazonaws.com/Image storage directory/"+image.getId()+image.getExtension())
                        .fitCenter()
                        .into(searchResultImage);
            }
        }

    }

    public void swapData(List<SearchResultsEntity> mNewDataSet) {
        mDataSet = mNewDataSet;
        notifyDataSetChanged();
    }

    public void setItemsOnClickListener(RecipesSearchResultsAdapter.OnItemClickListener onClickListener){
        this.mItemsOnClickListener = onClickListener;
    }

    @Override
    public RecipesSearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_list_item, parent, false);
        viewContext = parent.getContext();
        return new RecipesSearchResultsAdapter.ViewHolder(view, viewContext);
    }

    @Override
    public void onBindViewHolder(RecipesSearchResultsAdapter.ViewHolder holder, final int position) {

        SearchResultsEntity searchResultsEntity = mDataSet.get(position);
        holder.mColorName.setText(searchResultsEntity.getHeaderContent());
        holder.mColorValue.setText(searchResultsEntity.getDescriptionContent());


        if(searchResultsEntity.getEntityType() == 1){
            holder.mColorName.setTextColor(viewContext.getResources().getColor(R.color.colorAccent, null));
            holder.searchResultImage.setImageResource(R.mipmap.ic_launcher);
            holder.cookName.setVisibility(View.VISIBLE);
            holder.cookedBy.setVisibility(View.VISIBLE);
            holder.cookName.setText(searchResultsEntity.getCookName());
            holder. new GetRecipeImage().execute(searchResultsEntity.getId());
        }
        else if(searchResultsEntity.getEntityType() == 2){
            holder.mColorName.setTextColor(viewContext.getResources().getColor(R.color.black, null));
            holder.searchResultImage.setImageResource(R.drawable.profile_photo_cook);
            holder.cookName.setVisibility(View.GONE);
            holder.cookedBy.setVisibility(View.GONE);
        }

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

        if(mItemsOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemsOnClickListener.onClick(mDataSet.get(position));
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }
}
