package me.esca.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.esca.R;
import me.esca.adapters.ProfileRecipesAdapter;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.followRelationship.tableUtils.FollowsTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.FollowRelationship;

import static me.esca.services.escaWS.Utils.CONNECTED_COOK;

/**
 * Created by Me on 18/06/2017.
 */

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private RecyclerView mRecyclerView;
    private TextView numberOfCookRecipes;
    private TextView numberOfFollowersTextView;
    private TextView numberOfFolloweesTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.personal_profile_layout, container, false);
        if(getActivity().getActionBar() != null )getActivity().getActionBar().show();

        LinearLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        ProfileRecipesAdapter mAdapter = new ProfileRecipesAdapter(getActivity(), null);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.profile_recipes_recycle_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        numberOfCookRecipes = (TextView) view.findViewById(R.id.number_of_cook_recipes);
        numberOfFollowersTextView = (TextView) view.findViewById(R.id.followers_nbr_text_view);
        numberOfFolloweesTextView = (TextView) view.findViewById(R.id.followees_nbr_text_view);
        getLoaderManager().initLoader(1, null, this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetFollowers().execute(CONNECTED_COOK);
        new GetFollowees().execute(CONNECTED_COOK);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 1:
                return new CursorLoader(getActivity(), RecipesContentProvider.CONTENT_URI_RECIPES, null,
                        RecipesTableDefinition.COOK_COLUMN +"= ?", new String[]{"1"}, "dateCreated desc");
            default:
                throw new IllegalArgumentException("no recipeId handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 1:

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

                ((ProfileRecipesAdapter) mRecyclerView.getAdapter()).swapCursor(mx);
                numberOfCookRecipes.setText(String.valueOf(data.getCount()));

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

    private class GetFollowers extends AsyncTask<String, Integer, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {

            Integer followersNumber = 0;

            Cursor cookCursor = getActivity().getContentResolver()
                    .query(RecipesContentProvider.CONTENT_URI_COOKS,
                            new String[]{CooksTableDefinition.ID_COLUMN},
                            CooksTableDefinition.USERNAME_COLUMN + " like ?",
                            new String[]{CONNECTED_COOK},
                            null);

            //TODO Add getting directly from WS first if connected
            if (cookCursor != null && cookCursor.getCount() > 0) {
                cookCursor.moveToNext();
                Cursor followCursor = getActivity().getContentResolver().query(RecipesContentProvider.CONTENT_URI_FOLLOWS,
                        null, FollowsTableDefinition.FOLLOWEE_COLUMN + " = ? ",
                        new String[]{String.valueOf(cookCursor
                                .getLong(cookCursor.getColumnIndex(CooksTableDefinition.ID_COLUMN)))},
                        null);

                cookCursor.close();

                if (followCursor != null && followCursor.getCount() > 0) {
                    followCursor.moveToNext();
                    followersNumber = followCursor.getCount();
                    followCursor.close();
                    return followersNumber;
                }
            }
            return followersNumber;
        }

        @Override
        protected void onPostExecute(Integer numberOfFollowers) {
            super.onPostExecute(numberOfFollowers);
            numberOfFollowersTextView.setText(String.valueOf(numberOfFollowers));
        }
    }

    private class GetFollowees extends AsyncTask<String, Integer, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {

            Integer numberOfFollowees = 0;

            Cursor cookCursor = getActivity().getContentResolver()
                    .query(RecipesContentProvider.CONTENT_URI_COOKS,
                            new String[]{CooksTableDefinition.ID_COLUMN},
                            CooksTableDefinition.USERNAME_COLUMN + " like ?",
                            new String[]{CONNECTED_COOK},
                            null);

            //TODO Add getting directly from WS first if connected
            if (cookCursor != null && cookCursor.getCount() > 0) {
                cookCursor.moveToNext();
                Cursor followCursor = getActivity().getContentResolver().query(RecipesContentProvider.CONTENT_URI_FOLLOWS,
                        null, FollowsTableDefinition.FOLLOWER_COLUMN + " = ?",
                        new String[]{String.valueOf(cookCursor
                                .getLong(cookCursor.getColumnIndex(CooksTableDefinition.ID_COLUMN)))},
                        null);

                cookCursor.close();

                if (followCursor != null && followCursor.getCount() > 0) {
                    followCursor.moveToNext();
                    numberOfFollowees = followCursor.getCount();
                    followCursor.close();
                }
            }
            return numberOfFollowees;
        }

        @Override
        protected void onPostExecute(Integer numberOfFollowees) {
            super.onPostExecute(numberOfFollowees);
            numberOfFolloweesTextView.setText(String.valueOf(numberOfFollowees));
        }
    }
}
