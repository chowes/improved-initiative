package me.colinhowes.rollinitiative.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 5/1/17.
 */

public class TestUtil {
    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake guests
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, "Beatrix");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, "red");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_CONDITION, "normal");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, 14);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, 17);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, 1);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, 3);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, 14);
        list.add(cv);

        cv = new ContentValues();
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, "Ragnar");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, "blue");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_CONDITION, "fatigued");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, 21);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, 27);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, 1);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, 4);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, 11);
        list.add(cv);

        cv = new ContentValues();
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, "Liesl");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, "blue");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_CONDITION, "normal");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, 14);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, 21);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, 1);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, 4);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, 17);
        list.add(cv);

        cv = new ContentValues();
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, "Goblin");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, "yellow");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_CONDITION, "normal");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, 15);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, 15);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, 1);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, 1);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, 10);
        list.add(cv);

        cv = new ContentValues();
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, "Goblin");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, "yellow");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_CONDITION, "normal");
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, 15);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, 15);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, 1);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, 1);
        cv.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, 20);
        list.add(cv);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (CharacterContract.CharacterEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(CharacterContract.CharacterEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}
