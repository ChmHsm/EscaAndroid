package me.esca.activities;

import android.app.Activity;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;


import me.esca.R;
import me.esca.databinding.RecipeDetailsActivityBinding;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Cook;
import me.esca.model.Recipe;
import me.esca.utils.glide.GlideApp;

/**
 * Created by Me on 24/06/2017.
 */

public class RecipeDetailsActivity extends Activity{
    private Long recipeId;
    private Long cookId;
    private Recipe recipe;
    private Cook cook;
    private ImageView recipeDetailImageView;
    private Long imageId;
    private String imageExtension;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recipeId = getIntent().getLongExtra("recipeId", 0);
        imageId = getIntent().getLongExtra("imageId", 0);
        imageExtension = getIntent().getStringExtra("imageExtension");
        if(recipeId > 0){
            RecipeDetailsActivityBinding binding = DataBindingUtil
                    .setContentView(this, R.layout.recipe_details_activity);

            recipeDetailImageView = (ImageView) findViewById(R.id.recipeDetailImageView);

            GlideApp.with(this)
                    .load("http://escaws.s3.amazonaws.com/Image storage directory/"+imageId+imageExtension)
                    .placeholder(getDrawable(R.drawable.recipe_image_placeholder))
                    .fitCenter()
                    .into(recipeDetailImageView);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setActionBar(toolbar);
            final Drawable upArrow = getApplicationContext()
                    .getDrawable(R.drawable.abc_ic_ab_back_material);

            if(getActionBar() != null){
                if(upArrow != null) {
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
                    .query(Uri.parse(RecipesContentProvider.CONTENT_URI_RECIPES+"/"+ recipeId),
                            null, null, null, null);


            if(recipeCursor != null && recipeCursor.getCount() > 0){
                //Retrieving the cook
                recipeCursor.moveToFirst();
                cookId = recipeCursor.getLong(recipeCursor.getColumnIndex(RecipesTableDefinition.COOK_COLUMN));
                if(cookId > 0){
                    Cursor cookCursor = getContentResolver()
                            .query(Uri.parse(RecipesContentProvider.CONTENT_URI_COOKS+"/"+ cookId),
                                    null, null, null, null);

                    if(cookCursor != null && cookCursor.getCount() > 0){
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
                    }
                    else{
                        Toast.makeText(RecipeDetailsActivity.this,
                                "An error occurred while retrieving the requested recipe",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
            else{
                Toast.makeText(RecipeDetailsActivity.this,
                        "An error occurred while retrieving the requested recipe",
                        Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(RecipeDetailsActivity.this,
                    "An error occurred while retrieving the requested recipe",
                    Toast.LENGTH_LONG).show();
        }
    }
}
