package me.colinhowes.rollinitiative.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by colin on 4/30/17.
 */

public class CharacterDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "character.db";
    private static final int DATABASE_VERSION = 1;

    public CharacterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_STATEMENT = "CREATE TABLE " +
                CharacterContract.CharacterEntry.TABLE_NAME + " (" +
                CharacterContract.CharacterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR + " TEXT NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT + " INTEGER NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL + " INTEGER NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_CONDITION + " STRING NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS + " INTEGER NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_INIT + " INTEGER NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT + " INTEGER NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " +
                CharacterContract.CharacterEntry.TABLE_NAME;
        db.execSQL(SQL_DROP_TABLE_STATEMENT);
    }

    public void insertCharacter(SQLiteDatabase db, CharacterType character) {
        ContentValues values = new ContentValues();
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, character.name);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, character.colour);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_CONDITION, character.condition);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, character.hpCurrent);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, character.hpMax);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, character.initBonus);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, character.init);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, character.inCombat);

        db.insert(CharacterContract.CharacterEntry.TABLE_NAME, null, values);
    }
}
