package me.esca.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.esca.R;
import me.esca.activities.RecipeDetailsActivity;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Cook;
import me.esca.model.Recipe;
import me.esca.utils.CursorRecyclerViewAdapter;
import me.esca.utils.glide.GlideApp;

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
                Long recipeId;
                recipeId = ((ViewHolder) viewHolder).id;
                if(recipeId <= 0){
                    throw new IllegalArgumentException();
                }
                else{
                    Intent intent = new Intent(mContext, RecipeDetailsActivity.class);
                    intent.putExtra("recipeId",recipeId);
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
            recipeImageView.setImageDrawable(null);
            GlideApp.with(mContext)
                    .load("http://lorempixel.com/400/200/food/")
                    .placeholder(mContext.getDrawable(R.drawable.tagliatelles_legumes))
                    .fitCenter()
                    .into(recipeImageView);
            recipeTitle.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)));
            recipeDescription.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.INSTRUCTIONS_COLUMN)));
            recipeDate.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.DATE_CREATED_COLUMN)));
            id = c.getLong(c.getColumnIndex(RecipesTableDefinition.ID_COLUMN));
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
    }
}
