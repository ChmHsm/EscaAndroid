package me.esca.utils.searchViewUtils.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Cook;
import me.esca.model.Recipe;

/**
 * Created by Me on 19/07/2017.
 */

public class RecipesDataHelper {

    public static int SEARCH_LIST_LIMIT_COUNT_PER_ENTITY = 3;
    public static int SUGGESTION_LIST_LIMIT_COUNT_PER_ENTITY = 2;

    private static List<Recipe> recipesList = new ArrayList<>();

    private static List<RecipesSuggestion> sRecipesSuggestions =
            new ArrayList<>();

    public interface OnFindRecipesListener {
        void onResults(List<SearchResultsEntity> results);
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
                    int count;
                    Cursor recipesCursor = context.getContentResolver().query(
                            RecipesContentProvider.CONTENT_URI_RECIPES,
                            new String[]{RecipesTableDefinition.TITLE_COLUMN},
                            RecipesTableDefinition.TITLE_COLUMN + " like " + "?",
                            new String[]{"%" + constraint.toString() + "%"}, RecipesTableDefinition.TITLE_COLUMN);

                    if (recipesCursor != null && recipesCursor.getCount() > 0) {
                        count = 1;
                        while (recipesCursor.moveToNext() && count <= SUGGESTION_LIST_LIMIT_COUNT_PER_ENTITY) {
                            RecipesSuggestion recipesSuggestion =
                                    new RecipesSuggestion(recipesCursor.getString(
                                            recipesCursor.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)));
                            suggestionList.add(recipesSuggestion);
                            count++;
                        }
                    }

                    Cursor cooksCursor = context.getContentResolver().query(
                            RecipesContentProvider.CONTENT_URI_COOKS,
                            new String[]{CooksTableDefinition.USERNAME_COLUMN},
                            CooksTableDefinition.USERNAME_COLUMN + " like " + "?",
                            new String[]{"%" + constraint.toString() + "%"}, CooksTableDefinition.USERNAME_COLUMN);

                    if (cooksCursor != null && cooksCursor.getCount() > 0) {
                        count = 1;
                        while (cooksCursor.moveToNext() && count <= SUGGESTION_LIST_LIMIT_COUNT_PER_ENTITY) {
                            RecipesSuggestion recipesSuggestion =
                                    new RecipesSuggestion(cooksCursor.getString(
                                            cooksCursor.getColumnIndex(CooksTableDefinition.USERNAME_COLUMN)));
                            suggestionList.add(recipesSuggestion);
                            count++;
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

    public static void findEntities(final Context context, String query, final RecipesDataHelper.OnFindRecipesListener listener) {

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<SearchResultsEntity> searchSuggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {
                    int count;
                    Cursor recipesCursor = context.getContentResolver().query(
                            RecipesContentProvider.CONTENT_URI_RECIPES,
                            new String[]{RecipesTableDefinition.ID_COLUMN, RecipesTableDefinition.TITLE_COLUMN,
                                    RecipesTableDefinition.INSTRUCTIONS_COLUMN, RecipesTableDefinition.COOK_COLUMN},
                            RecipesTableDefinition.TITLE_COLUMN + " like " + "?",
                            new String[]{"%" + constraint.toString() + "%"}, RecipesTableDefinition.TITLE_COLUMN);

                    if (recipesCursor != null) {
                        count = 1;
                        while (recipesCursor.moveToNext() && count <= SEARCH_LIST_LIMIT_COUNT_PER_ENTITY) {
                            Cursor cooksCursor = context.getContentResolver().query(
                                    Uri.parse(RecipesContentProvider.CONTENT_URI_COOKS + "/"+ String.valueOf(recipesCursor.getLong(
                                            recipesCursor.getColumnIndex(RecipesTableDefinition.COOK_COLUMN)))),
                                    new String[]{CooksTableDefinition.USERNAME_COLUMN},
                                    null,
                                    null, null);
                            String cookUsername = "";
                            if(cooksCursor != null && cooksCursor.getCount() > 0){
                                cooksCursor.moveToNext();
                                cookUsername = cooksCursor.getString(cooksCursor.getColumnIndex(CooksTableDefinition.USERNAME_COLUMN));
                            }

                            SearchResultsEntity searchResultsEntity =
                                    new SearchResultsEntity(recipesCursor.getLong(
                                            recipesCursor.getColumnIndex(RecipesTableDefinition.ID_COLUMN)),
                                            recipesCursor.getString(
                                                    recipesCursor.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)),
                                            recipesCursor.getString(
                                                    recipesCursor.getColumnIndex(RecipesTableDefinition.INSTRUCTIONS_COLUMN)),
                                            1, cookUsername);

                            searchSuggestionList.add(searchResultsEntity);
                            count++;
                        }
                    }

                    Cursor cooksCursor = context.getContentResolver().query(
                            RecipesContentProvider.CONTENT_URI_COOKS,
                            new String[]{CooksTableDefinition.ID_COLUMN, CooksTableDefinition.USERNAME_COLUMN},
                            CooksTableDefinition.USERNAME_COLUMN + " like " + "?",
                            new String[]{"%" + constraint.toString() + "%"}, CooksTableDefinition.USERNAME_COLUMN);

                    if (cooksCursor != null) {
                        count = 1;
                        while (cooksCursor.moveToNext() && count <= SEARCH_LIST_LIMIT_COUNT_PER_ENTITY) {
                            SearchResultsEntity searchResultsEntity =
                                    new SearchResultsEntity(cooksCursor.getLong(
                                            cooksCursor.getColumnIndex(CooksTableDefinition.ID_COLUMN)),
                                            cooksCursor.getString(
                                                    cooksCursor.getColumnIndex(CooksTableDefinition.USERNAME_COLUMN)),
                                            null,
                                            2, null);
                            searchSuggestionList.add(searchResultsEntity);
                            count++;
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = searchSuggestionList;
                results.count = searchSuggestionList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<SearchResultsEntity>) results.values);
                }
            }
        }.filter(query);

    }

}
