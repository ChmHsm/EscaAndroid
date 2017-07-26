package me.esca.dbRelated.contentProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.security.InvalidKeyException;

import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.RecipesDatabaseHelper;
import me.esca.dbRelated.image.tableUtils.ImagesTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;
import me.esca.model.Cook;
import me.esca.model.Image;
import me.esca.model.Recipe;

/**
 * Created by Me on 05/06/2017.
 */

public class RecipesContentProvider extends ContentProvider {

    // database
    private RecipesDatabaseHelper database;

    // used for the UriMatcher
    private static final int RECIPES = 10;
    private static final int RECIPE_ID = 20;
    private static final int COOKS = 11;
    private static final int COOK_ID = 21;
    private static final int IMAGES = 12;
    private static final int IMAGE_ID = 22;

    private static final String AUTHORITY_RECIPES = "me.esca.recipes.contentprovider";

    private static final String BASE_PATH_RECIPES = "recipes";
    private static final String BASE_PATH_COOKS = "cooks";
    private static final String BASE_PATH_IMAGES = "images";

    public static final Uri CONTENT_URI_RECIPES = Uri.parse("content://" + AUTHORITY_RECIPES + "/" + BASE_PATH_RECIPES);
    public static final Uri CONTENT_URI_COOKS = Uri.parse("content://" + AUTHORITY_RECIPES + "/" + BASE_PATH_COOKS);
    public static final Uri CONTENT_URI_IMAGES = Uri.parse("content://" + AUTHORITY_RECIPES + "/" + BASE_PATH_IMAGES);

//    public static final String CONTENT_TYPE_RECIPES = ContentResolver.CURSOR_DIR_BASE_TYPE + "/recipes";
//    public static final String CONTENT_ITEM_TYPE_RECIPES = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/recipe";
//    public static final String CONTENT_TYPE_COOKS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/cooks";
//    public static final String CONTENT_ITEM_TYPE_COOKS = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/cook";
//    public static final String CONTENT_TYPE_IMAGES = ContentResolver.CURSOR_DIR_BASE_TYPE + "/images";
//    public static final String CONTENT_ITEM_TYPE_IMAGES = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/image";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY_RECIPES, BASE_PATH_RECIPES, RECIPES);
        sURIMatcher.addURI(AUTHORITY_RECIPES, BASE_PATH_RECIPES + "/#", RECIPE_ID);
        sURIMatcher.addURI(AUTHORITY_RECIPES, BASE_PATH_COOKS, COOKS);
        sURIMatcher.addURI(AUTHORITY_RECIPES, BASE_PATH_COOKS + "/#", COOK_ID);
        sURIMatcher.addURI(AUTHORITY_RECIPES, BASE_PATH_IMAGES, IMAGES);
        sURIMatcher.addURI(AUTHORITY_RECIPES, BASE_PATH_IMAGES + "/#", IMAGE_ID);
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

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case RECIPES:
                queryBuilder.setTables(RecipesTableDefinition.TABLE_NAME);
                break;
            case COOKS:
                queryBuilder.setTables(CooksTableDefinition.TABLE_NAME);
                break;
            case IMAGES:
                queryBuilder.setTables(me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME);
                break;
            case RECIPE_ID:
                queryBuilder.setTables(RecipesTableDefinition.TABLE_NAME);
                queryBuilder.appendWhere(RecipesTableDefinition.ID_COLUMN + "=" + uri.getLastPathSegment());
                break;
            case COOK_ID:
                queryBuilder.setTables(CooksTableDefinition.TABLE_NAME);
                queryBuilder.appendWhere(CooksTableDefinition.ID_COLUMN + "=" + uri.getLastPathSegment());
                break;
            case IMAGE_ID:
                queryBuilder.setTables(me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME);
                queryBuilder.appendWhere(me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.ID_COLUMN + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        if (getContext() != null) cursor.setNotificationUri(getContext().getContentResolver(), uri);

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
        long id;
        switch (uriType) {
            case RECIPES:
                id = sqlDB.insert(RecipesTableDefinition.TABLE_NAME, null, values);
                break;
            case COOKS:
                id = sqlDB.insert(CooksTableDefinition.TABLE_NAME, null, values);
                break;
            case IMAGES:
                id = sqlDB.insert(ImagesTableDefinition.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH_RECIPES + "/" + id);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int uriType;
        int insertCount = 0;
        try {

            uriType = sURIMatcher.match(uri);
            SQLiteDatabase sqlDB = database.getWritableDatabase();

            try {
                sqlDB.beginTransaction();
                for (ContentValues value : values) {
                    Long id;
                    switch (uriType) {
                        case RECIPES:
                            id = sqlDB.insert(RecipesTableDefinition.TABLE_NAME, null, value);
                            break;
                        case COOKS:
                            id = sqlDB.insert(CooksTableDefinition.TABLE_NAME, null, value);
                            break;
                        case IMAGES:
                            id = sqlDB.insert(me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME, null, value);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown URI: " + uri);
                    }
                    if (id > 0) insertCount++;
                }
                sqlDB.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("RECIPES BULK INSERT: ", "Could not perform batch insertion transaction query on " +
                        "table " + uriType + ". Exception message:" + e.getMessage());
            } finally {
                sqlDB.endTransaction();
            }

            if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            Log.e("RECIPES: ", "Could not perform batch insertion transaction query on " +
                    "table recipes. Exception message:" + e.getMessage());
        }
        return insertCount;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted;
        String id;
        switch (uriType) {
            case RECIPES:
                rowsDeleted = sqlDB.delete(RecipesTableDefinition.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case COOKS:
                rowsDeleted = sqlDB.delete(CooksTableDefinition.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case IMAGES:
                rowsDeleted = sqlDB.delete(me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME, selection,
                        selectionArgs);
                break;
            //TODO **********Could be optimized************
            case RECIPE_ID:
                id = uri.getLastPathSegment();
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
            case COOK_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            CooksTableDefinition.TABLE_NAME,
                            CooksTableDefinition.ID_COLUMN + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            CooksTableDefinition.TABLE_NAME,
                            CooksTableDefinition.ID_COLUMN + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case IMAGE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME,
                            me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.ID_COLUMN + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME,
                            me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.ID_COLUMN + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            //TODO **********End of Could be optimized************
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        String id;
        switch (uriType) {
            case RECIPES:
                rowsUpdated = sqlDB.update(RecipesTableDefinition.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case COOKS:
                rowsUpdated = sqlDB.update(CooksTableDefinition.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case IMAGES:
                rowsUpdated = sqlDB.update(me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case RECIPE_ID:
                id = uri.getLastPathSegment();
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
            case COOK_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CooksTableDefinition.TABLE_NAME,
                            values,
                            CooksTableDefinition.ID_COLUMN + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(CooksTableDefinition.TABLE_NAME,
                            values,
                            CooksTableDefinition.ID_COLUMN + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case IMAGE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME,
                            values,
                            me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.ID_COLUMN + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.TABLE_NAME,
                            values,
                            me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.ID_COLUMN + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (getContext() != null) getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    //TODO test method (Couldn't test it for accessibility constraints)
    public int getCount(@NonNull Uri uri, @Nullable String selection,
                        @Nullable String[] selectionArgs) {
        int count;
        String[] projection = new String[]{RecipesTableDefinition.ID_COLUMN};

        Cursor cursor = query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
            return count;
        }
        return 0;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {

        if (extras != null) {
            if(extras.getString("uri") != null){
                Uri uri = Uri.parse(extras.getString("uri"));
                if (method.equals("saveOrUpdateRecipe")) {

                    Recipe recipe = (Recipe) extras.getSerializable("recipe");
                    if (recipe != null) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(RecipesTableDefinition.ID_COLUMN, recipe.getId());
                        contentValues.put(RecipesTableDefinition.TITLE_COLUMN, recipe.getTitle());
                        contentValues.put(RecipesTableDefinition.DIFFICULTY_RATING_COLUMN, recipe.getDifficultyRating());
                        contentValues.put(RecipesTableDefinition.PREP_TIME_COLUMN, recipe.getPrepTime());
                        contentValues.put(RecipesTableDefinition.PREP_COST_COLUMN, recipe.getPrepCost());
                        contentValues.put(RecipesTableDefinition.INGREDIENTS_COLUMN, recipe.getIngredients());
                        contentValues.put(RecipesTableDefinition.INSTRUCTIONS_COLUMN, recipe.getInstructions());
                        contentValues.put(RecipesTableDefinition.DATE_CREATED_COLUMN, recipe.getDateCreated());
                        contentValues.put(RecipesTableDefinition.COOK_COLUMN, recipe.getCook().getId());
                        try {
                            saveOrUpdate(contentValues, uri);
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (method.equals("saveOrUpdateCook")) {

                    Cook cook = (Cook) extras.getSerializable("cook");
                    if (cook != null) {
                        ContentValues cookEntityValues = new ContentValues();
                        cookEntityValues.put(CooksTableDefinition.ID_COLUMN, cook.getId());
                        cookEntityValues.put(CooksTableDefinition.USERNAME_COLUMN, cook.getUsername());
                        cookEntityValues.put(CooksTableDefinition.DATE_CREATED_COLUMN, cook.getDateCreated());

                        try {
                            saveOrUpdate(cookEntityValues, uri);
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (method.equals("saveOrUpdateImage")) {
                    Image image = (Image) extras.getSerializable("image");
                    if (image != null) {
                        ContentValues imageEntityValues = new ContentValues();
                        imageEntityValues.put(ImagesTableDefinition.ID_COLUMN, image.getId());
                        imageEntityValues.put(ImagesTableDefinition.DATE_CREATED_COLUMN, image.getDateCreated());
                        imageEntityValues.put(ImagesTableDefinition.EXTENSION_COLUMN, image.getExtension());
                        try {
                            saveOrUpdate(imageEntityValues, uri);
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (method.equals("bulkSaveOrUpdateRecipe")) {

                    ContentValues[] recipes = (ContentValues[]) extras.getSerializable("recipes");
                    if (recipes != null) bulkSaveOrUpdate(recipes, uri);
                }
            }
            else{
                //TODO throw an exception for missing Uri
            }

        }
        return null;
    }

    public Uri saveOrUpdate(@NonNull ContentValues values, Uri uri) throws InvalidKeyException {
        int uriType = sURIMatcher.match(uri);
        String idColumn = "";
        String baseUri = "";
        switch (uriType) {
            case RECIPES:
                idColumn = RecipesTableDefinition.ID_COLUMN;
                baseUri = BASE_PATH_RECIPES;
                break;
            case COOKS:
                idColumn = CooksTableDefinition.ID_COLUMN;
                baseUri = BASE_PATH_COOKS;
                break;
            case IMAGES:
                idColumn = me.esca.dbRelated.image.tableUtils.ImagesTableDefinition.ID_COLUMN;
                baseUri = BASE_PATH_IMAGES;
                break;
        }

        if (!values.containsKey(idColumn))
            throw new NullPointerException("recipeId attribute not found while" +
                    " attempting saveOrUpdate on recipes table: the saveOrUpdate method's ContentValues " +
                    "parameter should contain an \"recipeId\" attribute.");

        long id = (Long) values.get(idColumn);

        //CONTENT_URI_RECIPES = uri;

        if (id <= 0) {
            return insert(uri, values);
        } else {
            Cursor cursor = query(Uri.parse(uri + "/" + id), new String[]
                    {idColumn}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                update(Uri.parse(uri + "/" + id), values, null, null);
                cursor.close();

                return Uri.parse(baseUri + "/" + id);
            } else {
                if (cursor != null) cursor.close();
                return insert(uri, values);
            }
        }
    }

    public int bulkSaveOrUpdate(@NonNull ContentValues[] values, @NonNull Uri uri) {

            int insertCount = 0;
            try {
                SQLiteDatabase sqlDB = database.getWritableDatabase();

                try {
                    sqlDB.beginTransaction();
                    for (ContentValues value : values) {
                        Uri uri1 = saveOrUpdate(value, uri);
                        Long id = Long.parseLong(uri1.getLastPathSegment());
                        if (id > 0) insertCount++;
                    }
                    sqlDB.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.e("RECIPES: ", "Could not perform batch insertion transaction query on " +
                            "table recipes. Exception message:" + e.getMessage());
                } finally {
                    sqlDB.endTransaction();
                }
            } catch (Exception e) {
                Log.e("RECIPES: ", "Could not perform batch insertion transaction query on " +
                        "table recipes. Exception message:" + e.getMessage());
            }
            return insertCount;
        }
}
