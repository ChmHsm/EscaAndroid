package me.esca.services.escaWS;

/**
 * Created by Me on 26/06/2017.
 */

public class Utils {
    public static String CONNECTED_COOK = "Houssam";
    public static String MAIN_DOMAIN_NAME = "http://escaws.herokuapp.com";
    public static String ALL_RECIPES_URL = "/general/recipes/20";
    public static String ADD_RECIPE_URL = "/{username}/recipes";
    public static String ADD_IMAGE_URL = "/general/recipes/{recipeId}/recipeMainImage";
    public static String GET_IMAGE_URL = "/general/recipes/{recipeId}/recipeMainImage";
    public static String GET_LIKES_URL = "/general/recipesLikes/{recipeId}";
    public static String ADD_LIKE_TO_RECIPE_URL = "/general/recipesLikes/{recipeId}/"+ CONNECTED_COOK;
    public static String DELETE_LIKE_FROM_RECIPE_URL = "/general/recipesLikes/{likeId}";

}
