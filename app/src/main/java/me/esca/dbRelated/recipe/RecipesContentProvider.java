package me.esca.dbRelated.recipe;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import me.esca.dbRelated.recipe.tableUtils.RecipesDatabaseHelper;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;

/**
 * Created by Me on 05/06/2017.
 */

public class RecipesContentProvider extends ContentProvider{

    // database
    private RecipesDatabaseHelper database;

    // used for the UriMatcher
    private static final int RECIPES = 10;
    private static final int RECIPE_ID = 20;

    private static final String AUTHORITY = "me.esca.recipes.contentprovider";

    private static final String BASE_PATH = "recipes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/recipes";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/recipe";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, RECIPES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", RECIPE_ID);
    }

    @Override
    public boolean onCreate() {
        database = new RecipesDatabaseHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(RecipesTableDefinition.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case RECIPES:
                break;
            case RECIPE_ID:
                queryBuilder.appendWhere(RecipesTableDefinition.ID_COLUMN+ "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case RECIPES:
                id = sqlDB.insert(RecipesTableDefinition.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case RECIPES:
                rowsDeleted = sqlDB.delete(RecipesTableDefinition.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case RECIPE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            RecipesTableDefinition.TABLE_NAME,
                            RecipesTableDefinition.ID_COLUMN + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            RecipesTableDefinition.TABLE_NAME,
                            RecipesTableDefinition.ID_COLUMN + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case RECIPES:
                rowsUpdated = sqlDB.update(RecipesTableDefinition.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case RECIPE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(RecipesTableDefinition.TABLE_NAME,
                            values,
                            RecipesTableDefinition.ID_COLUMN + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(RecipesTableDefinition.TABLE_NAME,
                            values,
                            RecipesTableDefinition.ID_COLUMN + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
    //TODO add content provider declaration in manifest
}
