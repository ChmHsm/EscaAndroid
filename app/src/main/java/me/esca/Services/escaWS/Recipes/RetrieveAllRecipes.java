package me.esca.Services.escaWS.Recipes;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Me on 02/06/2017.
 */
// TODO add service declaration in manifest
public class RetrieveAllRecipes extends IntentService {

    public static String MAIN_DOMAIN_NAME = "escaws.herokuapp.com";
    private static String ALL_RECIPES_URL = "/general/recipes";

    public RetrieveAllRecipes(){
        super("RetrieveAllRecipes");
    }

    //onHandleIntent is automatically executed asynchronously.
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //TODO implement retrieval all recipes retrieval process
    }
}
