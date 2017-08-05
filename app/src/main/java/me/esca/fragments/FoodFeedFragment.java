package me.esca.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import me.esca.R;
import me.esca.adapters.RecipesAdapter;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.services.escaWS.recipes.RetrieveAllRecipes;
import me.esca.utils.Connectivity;

import static me.esca.activities.RecipeDetailsActivity.ACTIVITY_DETAIL_NOTIFICATION_BROADCAST;
import static me.esca.activities.RecipeDetailsActivity.RESULT_CODE;
import static me.esca.adapters.RecipesAdapter.REQUEST_CODE;

/**
 * Created by Me on 18/06/2017.
 */

public class FoodFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private RecyclerView mRecyclerView;
    private FloatingActionButton addRecipeButton;
    private DataUpdateReceiver dataUpdateReceiver;
    private ProgressDialog progressDialog;

    @Override
    public void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTIVITY_DETAIL_NOTIFICATION_BROADCAST);
        getActivity().registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null) getActivity().unregisterReceiver(dataUpdateReceiver);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTIVITY_DETAIL_NOTIFICATION_BROADCAST)) {
                getLoaderManager().initLoader(0, null, FoodFeedFragment.this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.food_feed_fragment, container, false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecipesAdapter mAdapter = new RecipesAdapter(getActivity(), null);

        if(getActivity().getActionBar() != null )getActivity().getActionBar().show();

        callRetrieveAllRecipesService();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.food_feed_recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        addRecipeButton = (FloatingActionButton) view.findViewById(R.id.add_recipe_fab);
        addRecipeButton.attachToRecyclerView(mRecyclerView);
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "FAB clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void callRetrieveAllRecipesService(){

        if(Connectivity.isNetworkAvailable(getActivity())) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Loading");
            progressDialog.show();
            Intent intent = new Intent(getActivity(), RetrieveAllRecipes.class);
            getActivity().startService(intent);
        }
        else Toast.makeText(getActivity(), "Device is not connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new CursorLoader(getActivity(), RecipesContentProvider.CONTENT_URI_RECIPES, null,
                        null, null, "dateCreated desc");
            default:
                throw new IllegalArgumentException("no recipeId handled!");
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
                                RecipesTableDefinition.DATE_CREATED_COLUMN,
                                RecipesTableDefinition.COOK_COLUMN});

                fillMx(data, mx);

                ((RecipesAdapter) mRecyclerView.getAdapter()).swapCursor(mx);
                if(progressDialog != null){
                    progressDialog.hide();
                    progressDialog.dismiss();
                }

                break;
            default:
                throw new IllegalArgumentException("no loader recipeId handled!");
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
                    data.getString(data.getColumnIndex(RecipesTableDefinition.DATE_CREATED_COLUMN)),
                    data.getString(data.getColumnIndex(RecipesTableDefinition.COOK_COLUMN)),
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
