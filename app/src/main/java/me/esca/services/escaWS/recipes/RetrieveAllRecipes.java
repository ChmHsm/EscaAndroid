package me.esca.services.escaWS.recipes;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import me.esca.dbRelated.recipe.RecipesContentProvider;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Recipe;

/**
 * Created by Me on 02/06/2017.
 */
//TODO Implement database updating after data is retrieved
public class RetrieveAllRecipes extends IntentService {

    public static String MAIN_DOMAIN_NAME = "http://escaws.herokuapp.com";
    private static String ALL_RECIPES_URL = "/general/recipes";

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

    private Uri recipesUri;
    private int insertCount;

    //onHandleIntent is automatically executed asynchronously.
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Recipe>> rateResponse =
                restTemplate.exchange(MAIN_DOMAIN_NAME+ALL_RECIPES_URL,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Recipe>>() {
                });

        recipeList = rateResponse.getBody();

        recipesUri = Uri.parse(RecipesContentProvider.CONTENT_TYPE);

//        for (Recipe recipe : recipeList) {
//            ContentValues values = new ContentValues();
//            values.put(RecipesTableDefinition.INSTRUCTIONS_COLUMN, recipe.getInstructions());
//            values.put(RecipesTableDefinition.TITLE_COLUMN, recipe.getTitle());
//            getContentResolver().insert(RecipesContentProvider.CONTENT_URI, values);
//        }


        //Deletion, otherwise entries will be duplicated.
        getContentResolver().delete(RecipesContentProvider.CONTENT_URI, null, null);
        insertCount = bulkInsertRecipes(recipeList);
        publishResults(Activity.RESULT_OK);
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        intent.putExtra("RecipesSize", insertCount);
        sendBroadcast(intent);
    }

    public int bulkInsertRecipes(List<Recipe> recipes) {
        // insert only if data is set correctly
        if (recipes.size() == 0)
            return 0;

        try {
            ContentValues[] valueList = new ContentValues[recipes.size()];
            int i = 0;
            for (Recipe recipe : recipes) {
                ContentValues values = new ContentValues();
                values.put(RecipesTableDefinition.TITLE_COLUMN, recipe.getTitle());
                valueList[i++] = values;
            }
            insertCount = getContentResolver().bulkInsert(RecipesContentProvider.CONTENT_URI, valueList);

        } catch (Exception e) {
            Log.e("RECIPES: ", "Could not perform batch insertion transaction query on " +
                    "table recipes. Exception message:" + e.getMessage());
        }
        return insertCount;
    }
}
