package me.esca.services.escaWS.recipes;

import android.app.Activity;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Recipe;

import static me.esca.services.escaWS.Utils.ALL_RECIPES_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;


/**
 * Created by Me on 02/06/2017.
 */
public class RetrieveAllRecipes extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "me.esca.services.escaWS";

    private List<Recipe> recipeList = new ArrayList<>();
    public RetrieveAllRecipes(){
        super("RetrieveAllRecipes");
    }
    public List<Recipe> getRecipeList() {
        return recipeList;
    }

    //onHandleIntent is automatically executed asynchronously.
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<List<Recipe>> response =
                    restTemplate.exchange(MAIN_DOMAIN_NAME+ALL_RECIPES_URL,
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<Recipe>>() {
                            });
            recipeList = response.getBody();

//            getContentResolver().delete(RecipesContentProvider.CONTENT_URI_RECIPES, null, null);
            insertEntities(recipeList);
            publishResults(Activity.RESULT_OK);
        }
        catch (Exception e){
            publishResults(Activity.RESULT_CANCELED);
        }
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        Cursor cursor = getContentResolver().query(RecipesContentProvider.CONTENT_URI_RECIPES,
                new String[]{"_id"}, null, null, null);
        intent.putExtra("RecipesSize", cursor == null ? 0 : cursor.getCount());
        if(cursor != null) cursor.close();
        sendBroadcast(intent);
        stopSelf();
    }

    public int insertEntities(List<Recipe> recipes) {

        ContentValues[] recipesContentValues = new ContentValues[recipes.size()];
        Bundle bundle = new Bundle();

        for(int i = 0; i < recipes.size(); i++){
            ContentValues recipeEntityValues = new ContentValues();
            recipeEntityValues.put(RecipesTableDefinition.ID_COLUMN, recipes.get(i).getId());
            recipeEntityValues.put(RecipesTableDefinition.TITLE_COLUMN, recipes.get(i).getTitle());
            recipeEntityValues.put(RecipesTableDefinition.DIFFICULTY_RATING_COLUMN, recipes.get(i).getDifficultyRating());
            recipeEntityValues.put(RecipesTableDefinition.PREP_TIME_COLUMN, recipes.get(i).getPrepTime());
            recipeEntityValues.put(RecipesTableDefinition.PREP_COST_COLUMN, recipes.get(i).getPrepCost());
            recipeEntityValues.put(RecipesTableDefinition.INGREDIENTS_COLUMN, recipes.get(i).getIngredients());
            recipeEntityValues.put(RecipesTableDefinition.INSTRUCTIONS_COLUMN, recipes.get(i).getInstructions());
            recipeEntityValues.put(RecipesTableDefinition.DATE_CREATED_COLUMN, recipes.get(i).getDateCreated());
            recipeEntityValues.put(RecipesTableDefinition.COOK_COLUMN, recipes.get(i).getCook().getId());
            recipesContentValues[i] = recipeEntityValues;

            bundle.clear();
            bundle.putSerializable("cook", recipes.get(i).getCook());
            bundle.putString("uri", RecipesContentProvider.CONTENT_URI_COOKS.toString());
            getContentResolver().call(RecipesContentProvider.CONTENT_URI_COOKS, "saveOrUpdateCook", null, bundle);

        }


        bundle.clear();
        bundle.putSerializable("recipes", recipesContentValues);
        bundle.putString("uri", RecipesContentProvider.CONTENT_URI_RECIPES.toString());
        getContentResolver().call(RecipesContentProvider.CONTENT_URI_RECIPES, "bulkSaveOrUpdateRecipe", null, bundle);
        return 0;
    }
}
