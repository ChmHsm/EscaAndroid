package me.esca.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import me.esca.R;
import me.esca.adapters.RecipesAdapter;
import me.esca.dbRelated.recipe.RecipesContentProvider;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.services.escaWS.recipes.RetrieveAllRecipes;
import me.esca.utils.Connectivity;

/**
 * Created by Me on 03/06/2017.
 */

public class FoodFeedActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_feed_activity);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecipesAdapter mAdapter = new RecipesAdapter(this, null);

//        bottomNavigationView bottomNavigationView = (bottomNavigationView)
//                findViewById(R.id.bottom_navigation);


//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.action_favorites:
//
//                            case R.id.action_schedules:
//
//                            case R.id.action_music:
//
//                        }
//                        return true;
//                    }
//                });

        callRetrieveAllRecipesService();

        mRecyclerView = (RecyclerView) findViewById(R.id.food_feed_recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    private void callRetrieveAllRecipesService(){
        if(Connectivity.isNetworkAvailable(FoodFeedActivity.this)) {
            Intent intent = new Intent(this, RetrieveAllRecipes.class);
            startService(intent);
        }
        else Toast.makeText(FoodFeedActivity.this, "Device is not connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new CursorLoader(this, RecipesContentProvider.CONTENT_URI, null,
                        null, null, null);
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 0:

                MatrixCursor mx = new MatrixCursor(new String[]
                        {RecipesTableDefinition.ID_COLUMN,
                        RecipesTableDefinition.TITLE_COLUMN,
                        RecipesTableDefinition.DIFFICULTY_RATING_COLUMN,
                        RecipesTableDefinition.PREP_TIME_COLUMN,
                        RecipesTableDefinition.PREP_COST_COLUMN,
                        RecipesTableDefinition.INGREDIENTS_COLUMN,
                        RecipesTableDefinition.INSTRUCTIONS_COLUMN,
                        RecipesTableDefinition.DATE_CREATED_COLUMN});

                fillMx(data, mx);

                ((RecipesAdapter) mRecyclerView.getAdapter()).swapCursor(mx);

                break;
            default:
                throw new IllegalArgumentException("no loader id handled!");
        }
    }

    private void fillMx(Cursor data, MatrixCursor mx) {
        if (data == null)
            return;

        data.moveToPosition(-1);
        while (data.moveToNext()) {
            mx.addRow(new Object[]{
                    data.getString(data.getColumnIndex(RecipesTableDefinition.ID_COLUMN)),
                    data.getString(data.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN)),
                    data.getString(data.getColumnIndex(RecipesTableDefinition.DIFFICULTY_RATING_COLUMN)),
                    data.getString(data.getColumnIndex(RecipesTableDefinition.PREP_TIME_COLUMN)),
                    data.getString(data.getColumnIndex(RecipesTableDefinition.PREP_COST_COLUMN)),
                    data.getString(data.getColumnIndex(RecipesTableDefinition.INGREDIENTS_COLUMN)),
                    data.getString(data.getColumnIndex(RecipesTableDefinition.INSTRUCTIONS_COLUMN)),
                    data.getString(data.getColumnIndex(RecipesTableDefinition.DATE_CREATED_COLUMN))
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//    private ArrayList<Recipe> recipes = new ArrayList<>();
//    private RecyclerView recyclerView;
//    private RecipesAdapter recipesAdapter;
//
//    private SimpleCursorAdapter adapter;
//
//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                int resultCode = bundle.getInt(RetrieveAllRecipes.RESULT);
//                if (resultCode == RESULT_OK) {
//                    Toast.makeText(FoodFeedActivity.this, "Result was OK "+bundle.getInt("RecipesSize")
//                            , Toast.LENGTH_SHORT).show();
//                    //TODO implement UI updating from database
//                } else {
//                    Toast.makeText(FoodFeedActivity.this, "Result was not OK", Toast.LENGTH_SHORT).show();
//                }
//        }
//    }};
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.food_feed_activity);
//        recyclerView = (RecyclerView) findViewById(R.id.food_feed_recycle_view);
//
//        //TODO Perhaps, for good practice, the content view should be set before calling the service
//
//        callRetrieveAllRecipesService();
//
//        recipesAdapter = new RecipesAdapter(this, null);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FoodFeedActivity.this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
//        recyclerView.setAdapter(recipesAdapter);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        registerReceiver(broadcastReceiver, new IntentFilter(RetrieveAllRecipes.NOTIFICATION));
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//    }
//
//    private void callRetrieveAllRecipesService(){
//        Intent intent = new Intent(this, RetrieveAllRecipes.class);
//        startService(intent);
//    }
//
//    private void retrieveAndFillRecipes() {
//        //TODO implement recyclerView with LoaderManager as per this link
//        // https://stackoverflow.com/questions/39825125/android-recyclerview-cursorloader-contentprovider-load-more
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
}
