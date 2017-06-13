package me.esca.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import me.esca.R;
import me.esca.adapters.RecipesAdapter;
import me.esca.dbRelated.recipe.RecipesContentProvider;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.decorators.DividerItemDecoration;
import me.esca.model.Recipe;
import me.esca.services.escaWS.recipes.RetrieveAllRecipes;

/**
 * Created by Me on 03/06/2017.
 */

public class FoodFeedActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    public final int offset = 30;
    private int page = 0;

    private RecyclerView mRecyclerView;
    private boolean loadingMore = false;
    private Toast shortToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_feed_activity);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecipesAdapter mAdapter = new RecipesAdapter(this, null);

        callRetrieveAllRecipesService();

        mRecyclerView = (RecyclerView) findViewById(R.id.food_feed_recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        shortToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int maxPositions = layoutManager.getItemCount();

                if (lastVisibleItemPosition == maxPositions - 1) {
                    if (loadingMore)
                        return;

                    loadingMore = true;
                    page++;
                    getLoaderManager().restartLoader(0, null, FoodFeedActivity.this);

                }
            }
        });

        getLoaderManager().restartLoader(0, null, this);
    }

    private void callRetrieveAllRecipesService(){
    Intent intent = new Intent(this, RetrieveAllRecipes.class);
    startService(intent);
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

    private Handler handlerToWait = new Handler();

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 0:
                Log.d("Recipes", "onLoadFinished: loading MORE");
                shortToast.setText("loading MORE " + page);
                shortToast.show();

                Cursor cursor = ((RecipesAdapter) mRecyclerView.getAdapter()).getCursor();

                //fill all existing in adapter
                MatrixCursor mx = new MatrixCursor(new String[]{RecipesTableDefinition.ID_COLUMN,
                        RecipesTableDefinition.TITLE_COLUMN});
                fillMx(cursor, mx);

                //fill with additional result
                fillMx(data, mx);

                ((RecipesAdapter) mRecyclerView.getAdapter()).swapCursor(mx);


                handlerToWait.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingMore = false;
                    }
                }, 2000);

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
                    data.getString(data.getColumnIndex(RecipesTableDefinition.TITLE_COLUMN))
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
