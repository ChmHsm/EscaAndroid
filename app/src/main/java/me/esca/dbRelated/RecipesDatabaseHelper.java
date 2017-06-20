package me.esca.dbRelated;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.esca.dbRelated.cook.tableUtils.CooksTableDefinition;
import me.esca.dbRelated.image.tableUtils.ImagesTableDefinition;
import me.esca.dbRelated.recipe.tableUtils.RecipesTableDefinition;

/**
 * Created by Me on 05/06/2017.
 */

public class RecipesDatabaseHelper extends SQLiteOpenHelper{


    private static final String DATABASE_NAME = "recipe.db";
    private static final int DATABASE_VERSION = 1;

    public RecipesDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        RecipesTableDefinition.onCreate(db);
        ImagesTableDefinition.onCreate(db);
        CooksTableDefinition.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RecipesTableDefinition.onUpgrade(db, oldVersion, newVersion);
        ImagesTableDefinition.onUpgrade(db, oldVersion, newVersion);
        CooksTableDefinition.onUpgrade(db, oldVersion, newVersion);
    }

//    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
//        if (!db.isReadOnly()) {
//            // Enable foreign key constraints
//            db.execSQL("PRAGMA foreign_keys=ON;");
//        }
//    }
}
