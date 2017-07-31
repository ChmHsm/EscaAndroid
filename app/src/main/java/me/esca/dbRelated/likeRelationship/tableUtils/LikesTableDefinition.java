package me.esca.dbRelated.likeRelationship.tableUtils;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;

/**
 * Created by Me on 31/07/2017.
 */

public class LikesTableDefinition {

    public static final String TABLE_NAME = "like";
    public static final String ID_COLUMN = "_id";
    public static final String COOK_ID_COLUMN = "cookId";
    public static final String RECIPE_ID_COLUMN = "recipeId";

    private static final String DATABASE_CREATION_QUERY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + "("
            + ID_COLUMN + " integer primary key, "
            + COOK_ID_COLUMN + " integer, "
            + RECIPE_ID_COLUMN + " integer"
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
