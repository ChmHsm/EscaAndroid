package me.esca.services.escaWS.recipes;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
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

    //onHandleIntent is automatically executed asynchronously.
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Recipe>> rateResponse =
                restTemplate.exchange(MAIN_DOMAIN_NAME+ALL_RECIPES_URL,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Recipe>>() {
                });

        recipeList = rateResponse.getBody();

        publishResults(Activity.RESULT_OK);

    }

    private void publishResults(int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
