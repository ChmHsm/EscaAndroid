package me.esca.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.esca.R;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Cook;
import me.esca.model.Recipe;
import me.esca.utils.CursorRecyclerViewAdapter;

/**
 * Created by Me on 04/06/2017.
 */

//public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {
//
//    private List<Recipe> recipesArrayList = new ArrayList<>();
//
//    public RecipesAdapter(ArrayList<Recipe> recipes){
//        this.recipesArrayList = recipes;
//    }
//
//    @Override
//    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.recycler_row_recipe, parent, false);
//        return new ViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(RecipesAdapter.ViewHolder holder, int position) {
//        Recipe recipe = recipesArrayList.get(position);
//        holder.recipeTitle.setText(recipe.getTitle());
//        holder.recipedescription.setText(recipe.getInstructions());
//
//    }
//
//    @Override
//    public int getItemCount() {
////         this.recipesArrayList == null ? return 0 : return this.recipesArrayList.size();
//        return this.recipesArrayList == null ? 0 : this.recipesArrayList.size();
//
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public TextView recipeTitle;
//        public TextView recipedescription;
//
//        public ViewHolder(View view) {
//            super(view);
//            recipeTitle = (TextView) view.findViewById(R.id.recipteTitleTextView);
//            recipedescription = (TextView) view.findViewById(R.id.recipeDescriptionTextView);
//        }
//    }
//}

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
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        ViewHolder holder = (ViewHolder) viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);
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
        public TextView recipeTitle;
        public TextView recipeDescription;
        public TextView recipeDate;
        public TextView cookNameTextView;

        public ViewHolder(View view) {
            super(view);
            recipeTitle = (TextView) view.findViewById(R.id.recipteTitleTextView);
            recipeDescription = (TextView) view.findViewById(R.id.recipe_description_text_view);
            recipeDate = (TextView) view.findViewById(R.id.recipe_date_text_view);
            cookNameTextView = (TextView) view.findViewById(R.id.cook_name_text_view);
        }

        public void setData(Cursor c) {
            recipeTitle.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)));
            recipeDescription.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.INSTRUCTIONS_COLUMN)));
            recipeDate.setText(c.getString(c.getColumnIndex(RecipesTableDefinition.DATE_CREATED_COLUMN)));
            Cursor cursor = mContext.getContentResolver().query(
                    Uri.parse(RecipesContentProvider.CONTENT_URI_COOKS+"/"
                            +c.getString(c.getColumnIndex(RecipesTableDefinition.COOK_COLUMN))),
                    new String[]{CooksTableDefinition.USERNAME_COLUMN},
                    null, null, null);
            if(cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                cookNameTextView.setText(cursor.getString(
                        cursor.getColumnIndex(CooksTableDefinition.USERNAME_COLUMN)));
            }
        }
    }
}
