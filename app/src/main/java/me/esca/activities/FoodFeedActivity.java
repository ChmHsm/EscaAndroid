package me.esca.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import me.esca.R;
import me.esca.adapters.RecipesAdapter;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.decorators.DividerItemDecoration;
import me.esca.model.Recipe;
import me.esca.services.escaWS.recipes.RetrieveAllRecipes;

/**
 * Created by Me on 03/06/2017.
 */

public class FoodFeedActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ArrayList<Recipe> recipes = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecipesAdapter recipesAdapter;

    private SimpleCursorAdapter adapter;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(RetrieveAllRecipes.RESULT);
                if (resultCode == RESULT_OK) {
                    Toast.makeText(FoodFeedActivity.this, "Result was OK "+bundle.getInt("RecipesSize")
                            , Toast.LENGTH_SHORT).show();
                    //TODO implement UI updating from database
                } else {
                    Toast.makeText(FoodFeedActivity.this, "Result was not OK", Toast.LENGTH_SHORT).show();
                }
        }
    }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_feed_activity);
        recyclerView = (RecyclerView) findViewById(R.id.food_feed_recycle_view);

        //TODO Perhaps, for good practice, the content view should be set before calling the service

        callRetrieveAllRecipesService();

        recipesAdapter = new RecipesAdapter(this, null);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FoodFeedActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(recipesAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(RetrieveAllRecipes.NOTIFICATION));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void callRetrieveAllRecipesService(){
        Intent intent = new Intent(this, RetrieveAllRecipes.class);
        startService(intent);
    }

    private void retrieveAndFillRecipes() {
        //TODO implement recyclerView with LoaderManager as per this link https://stackoverflow.com/questions/39825125/android-recyclerview-cursorloader-contentprovider-load-more
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
