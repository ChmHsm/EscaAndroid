package me.esca.Services.escaWS.recipes;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import me.esca.model.Recipe;

/**
 * Created by Me on 02/06/2017.
 */

public class RetrieveAllRecipes extends IntentService {

    public static String MAIN_DOMAIN_NAME = "escaws.herokuapp.com";
    private static String ALL_RECIPES_URL = "/general/recipes";

    private int result = Activity.RESULT_CANCELED;
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "me.esca.services.escaWS";

    private List<Recipe> recipeList = new ArrayList<>();

    public RetrieveAllRecipes(){
        super("RetrieveAllRecipes");
    }

    //onHandleIntent is automatically executed asynchronously.
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //TODO hans't been tested yet (this service is already called in FoodFeedActivity)
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Recipe[]> responseEntity = restTemplate.getForEntity(MAIN_DOMAIN_NAME+ALL_RECIPES_URL, Recipe[].class);
        Recipe[] recipes = responseEntity.getBody();
        MediaType contentType = responseEntity.getHeaders().getContentType();
        HttpStatus statusCode = responseEntity.getStatusCode();

        publishResults(Activity.RESULT_OK);

    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
