package me.esca.utils.searchViewUtils.data;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
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

import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Recipe;

/**
 * Created by Me on 19/07/2017.
 */

public class RecipesDataHelper {

    private static List<Recipe> recipesList = new ArrayList<>();

    private static List<RecipesSuggestion> sRecipesSuggestions =
            new ArrayList<>();

    public interface OnFindRecipesListener {
        void onResults(List<Recipe> results);
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

    public static void findSuggestions(final Context context, String query, final int limit, final long simulatedDelay,
                                       final RecipesDataHelper.OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                RecipesDataHelper.resetSuggestionsHistory();
                List<RecipesSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    Cursor cursor = context.getContentResolver().query(
                            RecipesContentProvider.CONTENT_URI_RECIPES,
                            new String[]{RecipesTableDefinition.TITLE_COLUMN},
                            RecipesTableDefinition.TITLE_COLUMN + " like " + "?" ,
                            new String[]{"%"+constraint.toString()+"%"}, RecipesTableDefinition.TITLE_COLUMN);
                    if(cursor != null){
                        while(cursor.moveToNext()){
                            RecipesSuggestion recipesSuggestion =
                                    new RecipesSuggestion(cursor.getString(
                                            cursor.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)));
                            suggestionList.add(recipesSuggestion);
                        }
                    }
                }

                FilterResults results = new FilterResults();
//                Collections.sort(suggestionList, new Comparator<RecipesSuggestion>() {
//                    @Override
//                    public int compare(RecipesSuggestion lhs, RecipesSuggestion rhs) {
//                        return lhs.isHistory() ? -1 : 0;
//                    }
//                });
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
                List<Recipe> recipeSuggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {
                    for (Recipe recipe : recipesList) {
                        if (recipe.getTitle().toUpperCase()
                                .contains(constraint.toString().toUpperCase())) {
                            recipeSuggestionList.add(recipe);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = recipeSuggestionList;
                results.count = recipeSuggestionList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<Recipe>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initColorWrapperList(Context context) {
        recipesList.clear();

        Cursor cursor = context.getContentResolver().query(RecipesContentProvider.CONTENT_URI_RECIPES,
                null, null, null, RecipesTableDefinition.TITLE_COLUMN);
        if(cursor != null){
            while(cursor.moveToNext()){
                Recipe recipe = new Recipe(
                        cursor.getLong(
                                cursor.getColumnIndex(RecipesTableDefinition.ID_COLUMN)),
                        cursor.getString(
                                cursor.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)),
                        cursor.getInt(
                                cursor.getColumnIndex(RecipesTableDefinition.DIFFICULTY_RATING_COLUMN)),
                        cursor.getInt(
                                cursor.getColumnIndex(RecipesTableDefinition.PREP_TIME_COLUMN)),
                        cursor.getInt(
                                cursor.getColumnIndex(RecipesTableDefinition.PREP_COST_COLUMN)),
                        cursor.getString(
                                cursor.getColumnIndex(RecipesTableDefinition.INGREDIENTS_COLUMN)),
                        cursor.getString(
                                cursor.getColumnIndex(RecipesTableDefinition.INSTRUCTIONS_COLUMN)),
                        null, null);

                recipesList.add(recipe);
            }
        }
    }

}
