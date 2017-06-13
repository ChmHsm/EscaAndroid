package me.esca.services.escaWS.recipes;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

        getContentResolver().delete(RecipesContentProvider.CONTENT_URI, null, null);
        insertCount = insertRecipes(recipeList);
        publishResults(Activity.RESULT_OK);
    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        Cursor cursor = getContentResolver().query(RecipesContentProvider.CONTENT_URI,
                new String[]{"_id"}, null, null, null);
        intent.putExtra("RecipesSize", cursor == null ? 0 : cursor.getCount());
        if(cursor != null) cursor.close();
        sendBroadcast(intent);
    }

    public int insertRecipes(List<Recipe> recipes) {

        ContentValues[] contentValues = new ContentValues[recipes.size()];
        for(int i = 0; i < recipes.size(); i++){
            ContentValues values = new ContentValues();
            //TODO insert all attributes in the ContentValues variable
            values.put(RecipesTableDefinition.ID_COLUMN, recipes.get(i).getId());
            values.put(RecipesTableDefinition.TITLE_COLUMN, recipes.get(i).getTitle());
            values.put(RecipesTableDefinition.INGREDIENTS_COLUMN, recipes.get(i).getIngredients());
            values.put(RecipesTableDefinition.INSTRUCTIONS_COLUMN, recipes.get(i).getInstructions());
            contentValues[i] = values;
        }
        return getContentResolver().bulkInsert(RecipesContentProvider.CONTENT_URI, contentValues);
    }
}
