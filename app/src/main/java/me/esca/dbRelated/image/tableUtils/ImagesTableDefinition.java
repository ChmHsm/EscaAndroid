package me.esca.dbRelated.image.tableUtils;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;

/**
 * Created by Me on 05/06/2017.
 */

public class ImagesTableDefinition {

    public static final String TABLE_NAME = "image";
    public static final String ID_COLUMN = "_id";
    public static final String ORIGINAL_NAME_COLUMN = "originalName";
    public static final String ORIGINAL_PATH_COLUMN = "originalPath";
    public static final String DATE_CREATED_COLUMN = "dateCreated";
    public static final String LAST_UPDATED_COLUMN = "lastUpdated";
    public static final String IS_MAIN_PICTURE_COLUMN = "isMainPicture";
    public static final String EXTENSION_COLUMN = "extension";
    public static final String COOK_ID_COLUMN = "cookId";
    public static final String RECIPE_ID_COLUMN = "recipeId";

    private static final String DATABASE_CREATION_QUERY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + "("
            + ID_COLUMN + " integer primary key, "
            + ORIGINAL_NAME_COLUMN + " text, "
            + ORIGINAL_PATH_COLUMN + " text, "
            + DATE_CREATED_COLUMN + " text, "
            + LAST_UPDATED_COLUMN + " text, "
            + IS_MAIN_PICTURE_COLUMN + " numeric, "
            + EXTENSION_COLUMN + " integer, "
            + COOK_ID_COLUMN + " integer references cook on delete Cascade, "
            + RECIPE_ID_COLUMN + " integer references recipe on delete cascade "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATION_QUERY);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(RecipesTableDefinition.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
