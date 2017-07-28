package me.esca.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import me.esca.activities.RecipeDetailsActivity;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.image.tableUtils.ImagesTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Image;
import me.esca.model.Recipe;
import me.esca.services.escaWS.images.FetchImageByRecipeId;
import me.esca.services.escaWS.recipes.AddNewRecipeService;
import me.esca.utils.glide.GlideApp;
import me.esca.utils.searchViewUtils.adapter.RecipesSearchResultsAdapter;
import me.esca.utils.searchViewUtils.data.RecipesSuggestion;
import me.esca.utils.searchViewUtils.data.RecipesDataHelper;

import java.util.List;

import me.esca.R;
import me.esca.utils.searchViewUtils.data.SearchResultsEntity;

import static me.esca.services.escaWS.Utils.GET_IMAGE_URL;
import static me.esca.services.escaWS.Utils.MAIN_DOMAIN_NAME;

/**
 * Created by Me on 18/06/2017.
 */

public class SearchFragment extends Fragment implements ServiceConnection {

    private final String TAG = "BlankFragment";
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private FloatingSearchView mSearchView;
    private RecyclerView mSearchResultsList;
    private RecipesSearchResultsAdapter mSearchResultsAdapter;
    private boolean mIsDarkSearchTheme = false;
    private String mLastQuery = "";
    private Long searchEntityId;
    private DataUpdateReceiver dataUpdateReceiver;
    private FetchImageByRecipeId fetchImageByRecipeId;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("FetchImageByRecipeId");
        getActivity().registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) getActivity().unregisterReceiver(dataUpdateReceiver);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        FetchImageByRecipeId.MyBinder binder = (FetchImageByRecipeId.MyBinder) service;
        fetchImageByRecipeId = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        fetchImageByRecipeId = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

//        View view = inflater.inflate(R.layout.search_fragment, container, false);
        if(getActivity().getActionBar() != null )getActivity().getActionBar().hide();
        return inflater.inflate(R.layout.fragment_sliding_search_results_example_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchView = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
        mSearchResultsList = (RecyclerView) view.findViewById(R.id.search_results_list);

        setupFloatingSearch();
        setupResultsList();
        setupDrawer();
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    RecipesDataHelper.findSuggestions(getActivity(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new RecipesDataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<RecipesSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    mSearchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                RecipesSuggestion RecipesSuggestion = (RecipesSuggestion) searchSuggestion;
                RecipesDataHelper.findColors(getActivity(), RecipesSuggestion.getBody(),
                        new RecipesDataHelper.OnFindRecipesListener() {

                            @Override
                            public void onResults(List<SearchResultsEntity> results) {
                                mSearchResultsAdapter.swapData(results);
                            }

                        });
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;

                RecipesDataHelper.findColors(getActivity(), query,
                        new RecipesDataHelper.OnFindRecipesListener() {

                            @Override
                            public void onResults(List<SearchResultsEntity> results) {
                                mSearchResultsAdapter.swapData(results);
                            }

                        });
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(RecipesDataHelper.getHistory(getActivity(), 3));

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.action_change_colors) {

                    mIsDarkSearchTheme = true;

                    //demonstrate setting colors for items
                    mSearchView.setBackgroundColor(Color.parseColor("#787878"));
                    mSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setHintTextColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setActionMenuOverflowColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setMenuItemIconColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setClearBtnColor(Color.parseColor("#e9e9e9"));
                    mSearchView.setDividerColor(Color.parseColor("#BEBEBE"));
                    mSearchView.setLeftActionIconColor(Color.parseColor("#e9e9e9"));
                } else {

                    //just print action
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
                RecipesSuggestion RecipesSuggestion = (RecipesSuggestion) item;

                String textColor = mIsDarkSearchTheme ? "#ffffff" : "#000000";
                String textLight = mIsDarkSearchTheme ? "#bfbfbf" : "#787878";

                if (RecipesSuggestion.isHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));

                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);
                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = RecipesSuggestion.getBody()
                        .replaceFirst(mSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }

        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                mSearchResultsList.setTranslationY(newHeight);
            }
        });

        /*
         * When the user types some text into the search field, a clear button (and 'x' to the
         * right) of the search text is shown.
         *
         * This listener provides a callback for when this button is clicked.
         */
        mSearchView.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {

                Log.d(TAG, "onClearSearchClicked()");
            }
        });
    }

    private void setupResultsList() {
        mSearchResultsAdapter = new RecipesSearchResultsAdapter();
        mSearchResultsList.setAdapter(mSearchResultsAdapter);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchResultsAdapter.setItemsOnClickListener(new RecipesSearchResultsAdapter.OnItemClickListener() {
            @Override
            public void onClick(SearchResultsEntity searchResultsEntity) {
                if(searchResultsEntity.getEntityType() == 1){
                    if(searchResultsEntity.getId() > 0){
                        fetchImageByRecipeId = new FetchImageByRecipeId();
                        searchEntityId = searchResultsEntity.getId();
                        Intent service = new Intent(getActivity().getApplicationContext(), FetchImageByRecipeId.class);
                        service.putExtra("recipeId", searchEntityId);
                        getActivity().getApplicationContext().startService(service);
                    }

                }
                else if(searchResultsEntity.getEntityType() == 2){
                    //TODO start the profile Activity
                }
            }
        });
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("FetchImageByRecipeId")) {
                Image image = (Image) intent.getSerializableExtra("imageResult");
                Intent intent2 = new Intent(getActivity(), RecipeDetailsActivity.class);
                intent2.putExtra("recipeId", searchEntityId);

                if(image != null){
                    intent2.putExtra("imageId", image.getId());
                    intent2.putExtra("imageExtension", image.getExtension());

                }
                else{
                    //TODO Oops, we couldn't retrieve the image for some reason

                }
                startActivity(intent2);
            }
        }
    }



    private void setupDrawer() {
//        attachSearchViewActivityDrawer(mSearchView);
    }

}
