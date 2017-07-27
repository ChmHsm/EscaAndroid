package me.esca.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.esca.R;
import me.esca.adapters.ProfileRecipesAdapter;
import me.esca.dbRelated.contentProvider.RecipesContentProvider;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;

/**
 * Created by Me on 18/06/2017.
 */

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private RecyclerView mRecyclerView;

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
        getLoaderManager().initLoader(1, null, this);

        return view;
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
