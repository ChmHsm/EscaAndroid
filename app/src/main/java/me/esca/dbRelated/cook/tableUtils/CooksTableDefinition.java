package me.esca.dbRelated.cook.tableUtils;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;

/**
 * Created by Me on 05/06/2017.
 */

public class CooksTableDefinition {

    public static final String TABLE_NAME = "cook";
    public static final String ID_COLUMN = "_id";
    public static final String USERNAME_COLUMN = "username";
    //TODO Remove password attribute
    public static final String PASSWORD_COLUMN = "password";
    public static final String DATE_CREATED_COLUMN = "dateCreated";
    public static final String LAST_UPDATED_COLUMN = "lastUpdated";

    private static final String DATABASE_CREATION_QUERY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + "("
            + ID_COLUMN + " integer primary key, "
            + USERNAME_COLUMN + " text, "
            + PASSWORD_COLUMN + " text, "
            + DATE_CREATED_COLUMN + " text, "
            + LAST_UPDATED_COLUMN + " text"
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
