package me.colinhowes.rollinitiative.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by colin on 4/30/17.
 */

public class CharacterDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "character.db";
    private static int DATABASE_VERSION = 1;

    public CharacterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
