package me.colinhowes.rollinitiative.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.R.attr.id;

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
                CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS + " INTEGER NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_INIT + " INTEGER NOT NULL, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_TURN_ORDER + " INTEGER, " +
                CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT + " INTEGER NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("Upgrade", String.valueOf(newVersion));
        final String SQL_DROP_TABLE_STATEMENT = "DROP TABLE IF EXISTS " +
                CharacterContract.CharacterEntry.TABLE_NAME;
        db.execSQL(SQL_DROP_TABLE_STATEMENT);
    }

    public static Cursor getCharacters(SQLiteDatabase db) {
        return db.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                CharacterContract.CharacterEntry.COLUMN_NAME_NAME);
    }

    public static Cursor getCombatants(SQLiteDatabase db) {
        return db.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                "in_combat=?",
                new String[]{"1"},
                null,
                null,
                CharacterContract.CharacterEntry.COLUMN_NAME_TURN_ORDER);
    }

    public static Cursor getCombatantsByInit(SQLiteDatabase db) {
        return db.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                "in_combat=?",
                new String[]{"1"},
                null,
                null,
                CharacterContract.CharacterEntry.COLUMN_NAME_INIT + " DESC");
    }

    public static Cursor getCharacter(SQLiteDatabase db, int characterId) {
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(characterId)};

        // get the character matching characterId
        return db.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    public static void insertCharacter(SQLiteDatabase db, CharacterType character) {
        ContentValues values = new ContentValues();
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, character.name);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, character.colour);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, character.hpCurrent);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, character.hpMax);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, character.initBonus);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, character.init);
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, character.inCombat);

        db.insert(CharacterContract.CharacterEntry.TABLE_NAME, null, values);
    }

    public static void updateCharacter(SQLiteDatabase db, int characterId, ContentValues values) {
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(characterId) };

        db.update(
            CharacterContract.CharacterEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs);
    }

    public static void updateCharacter(SQLiteDatabase db, CharacterType character) {
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(character.getId()) };
        ContentValues values = new ContentValues();

        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, character.getName());
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, character.getColour());
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, character.getHpCurrent());
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, character.getHpTotal());
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, character.getInitBonus());
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, character.getInit());
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_TURN_ORDER, character.getTurnOrder());
        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, character.getInCombat());

        db.update(
                CharacterContract.CharacterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public static void deleteCharacter(SQLiteDatabase db, int characterId) {
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(characterId) };

        db.delete(CharacterContract.CharacterEntry.TABLE_NAME, selection, selectionArgs);
    }

    public static void toggleInCombat(SQLiteDatabase db, int characterId) {
        ContentValues values = new ContentValues();
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(characterId)};
        int inCombat;

        // get the character matching characterId
        Cursor cursor = db.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // there should be exactly one row in the cursor
        if (!cursor.moveToFirst()) {
            Log.e("toggleInCombat", "Database query returned no characters with ID!");
            return;
        }

        inCombat = cursor.getInt(cursor.getColumnIndex(
                CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT));
        cursor.close();

        // toggle inCombat
        inCombat = (inCombat == 1) ? 0 : 1;

        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT,
                String.valueOf(inCombat));

        // update character with new inCombat value
        db.update(
                CharacterContract.CharacterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public static void updateHealth(SQLiteDatabase db, int characterId, boolean increase) {
        ContentValues values = new ContentValues();
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(characterId) };
        int currentHealth;

        // get the character matching characterId
        Cursor cursor = db.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // there should be exactly one row in the cursor
        if (!cursor.moveToFirst()) {
            cursor.close();
            Log.e("updateHealth", "Database query returned no characters with ID!");
            return;
        }

        currentHealth = cursor.getInt(cursor.getColumnIndex(
                CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT));
        cursor.close();

        // increase or decrease health
        if (increase) {
            currentHealth += 1;
        } else {
            currentHealth -= 1;
        }

        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT,
                String.valueOf(currentHealth));

        // update character with new inCombat value
        db.update(
                CharacterContract.CharacterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        // we have to force the loader to fetch the data again
    }
}
