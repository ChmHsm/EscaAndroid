package me.esca.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.esca.R;
import me.esca.model.Recipe;

/**
 * Created by Me on 04/06/2017.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private List<Recipe> recipesArrayList = new ArrayList<>();

    public RecipesAdapter(ArrayList<Recipe> recipes){
        this.recipesArrayList = recipes;
    }

    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row_recipe, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipesAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipesArrayList.get(position);
        holder.recipeTitle.setText(recipe.getTitle());
        holder.recipedescription.setText(recipe.getInstructions());

    }

    @Override
    public int getItemCount() {
//         this.recipesArrayList == null ? return 0 : return this.recipesArrayList.size();
        return this.recipesArrayList == null ? 0 : this.recipesArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeTitle;
        public TextView recipedescription;

        public ViewHolder(View view) {
            super(view);
            recipeTitle = (TextView) view.findViewById(R.id.recipteTitleTextView);
            recipedescription = (TextView) view.findViewById(R.id.recipeDescriptionTextView);
        }
    }
}
