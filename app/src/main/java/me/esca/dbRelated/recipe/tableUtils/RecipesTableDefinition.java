package me.esca.dbRelated.recipe.tableUtils;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Me on 05/06/2017.
 */

public class RecipesTableDefinition {

    public static final String TABLE_NAME = "recipe";
    public static final String ID_COLUMN = "_id";
    public static final String TITLE_COLUMN = "title";
    public static final String INSTRUCTIONS_COLUMN = "instructions";
    public static final String INGREDIENTS_COLUMN = "ingredients";
    public static final String DATE_CREATED_COLUMN = "dateCreated";
    public static final String LAST_UPDATED_COLUMN = "dateUpdated";
    public static final String DIFFICULTY_RATING_COLUMN = "difficultyRating";
    public static final String PREP_TIME_COLUMN = "prepTime";
    public static final String PREP_COST_COLUMN = "prepCost";
    public static final String COOK_COLUMN = "cook_id";


    private static final String DATABASE_CREATION_QUERY = "create table "
            + TABLE_NAME
            + "("
            + ID_COLUMN + " integer primary key, "
            + TITLE_COLUMN + " text, "
            + INSTRUCTIONS_COLUMN + " text, "
            + INGREDIENTS_COLUMN + " text, "
            + DATE_CREATED_COLUMN + " text, "
            + LAST_UPDATED_COLUMN + " text, "
            + DIFFICULTY_RATING_COLUMN + " real, "
            + PREP_TIME_COLUMN + " real, "
            + PREP_COST_COLUMN + " real, "
            + COOK_COLUMN + " integer REFERENCES cook ON DELETE CASCADE"
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
