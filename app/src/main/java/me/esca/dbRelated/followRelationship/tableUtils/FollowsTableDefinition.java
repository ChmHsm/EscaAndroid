package me.esca.dbRelated.followRelationship.tableUtils;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;

/**
 * Created by Me on 12/08/2017.
 */

public class FollowsTableDefinition {

    public static final String TABLE_NAME = "follow";
    public static final String ID_COLUMN = "_id";
    public static final String FOLLOWER_COLUMN = "follower";
    public static final String FOLLOWEE_COLUMN = "followee";

    private static final String DATABASE_CREATION_QUERY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + "("
            + ID_COLUMN + " integer primary key, "
            + FOLLOWER_COLUMN + " integer, "
            + FOLLOWEE_COLUMN + " integer "
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
