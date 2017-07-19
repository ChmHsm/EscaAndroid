package me.esca.utils.searchViewUtils.data;

import android.content.Context;
import android.widget.Filter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Me on 19/07/2017.
 */

public class RecipesDataHelper {

    private static final String Recipes_FILE_NAME = "Recipes.json";

    private static List<ColorWrapper> sColorWrappers = new ArrayList<>();

    private static List<RecipesSuggestion> sRecipesSuggestions =
            new ArrayList<>(Arrays.asList(
                    new RecipesSuggestion("green"),
                    new RecipesSuggestion("blue"),
                    new RecipesSuggestion("pink"),
                    new RecipesSuggestion("purple"),
                    new RecipesSuggestion("brown"),
                    new RecipesSuggestion("gray"),
                    new RecipesSuggestion("Granny Smith Apple"),
                    new RecipesSuggestion("Indigo"),
                    new RecipesSuggestion("Periwinkle"),
                    new RecipesSuggestion("Mahogany"),
                    new RecipesSuggestion("Maize"),
                    new RecipesSuggestion("Mahogany"),
                    new RecipesSuggestion("Outer Space"),
                    new RecipesSuggestion("Melon"),
                    new RecipesSuggestion("Yellow"),
                    new RecipesSuggestion("Orange"),
                    new RecipesSuggestion("Red"),
                    new RecipesSuggestion("Orchid")));

    public interface OnFindRecipesListener {
        void onResults(List<ColorWrapper> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<RecipesSuggestion> results);
    }

    public static List<RecipesSuggestion> getHistory(Context context, int count) {

        List<RecipesSuggestion> suggestionList = new ArrayList<>();
        RecipesSuggestion recipesSuggestion;
        for (int i = 0; i < sRecipesSuggestions.size(); i++) {
            recipesSuggestion = sRecipesSuggestions.get(i);
            recipesSuggestion.setHistory(true);
            suggestionList.add(recipesSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (RecipesSuggestion recipesSuggestion : sRecipesSuggestions) {
            recipesSuggestion.setHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final RecipesDataHelper.OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                RecipesDataHelper.resetSuggestionsHistory();
                List<RecipesSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (RecipesSuggestion suggestion : sRecipesSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<RecipesSuggestion>() {
                    @Override
                    public int compare(RecipesSuggestion lhs, RecipesSuggestion rhs) {
                        return lhs.isHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<RecipesSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public static void findColors(Context context, String query, final RecipesDataHelper.OnFindRecipesListener listener) {

        initColorWrapperList(context);
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<ColorWrapper> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {
                    for (ColorWrapper color : sColorWrappers) {
                        if (color.getName().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {
                            suggestionList.add(color);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<ColorWrapper>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initColorWrapperList(Context context) {

        if (sColorWrappers.isEmpty()) {
            String jsonString = loadJson(context);
            sColorWrappers = deserializeColors(jsonString);
        }
    }

    private static String loadJson(Context context) {

        String jsonString;

        try {
            InputStream is = context.getAssets().open(Recipes_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonString;
    }

    private static List<ColorWrapper> deserializeColors(String jsonString) {

        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<ColorWrapper>>() {
        }.getType();
        return gson.fromJson(jsonString, collectionType);
    }

}
