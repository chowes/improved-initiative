package me.colinhowes.rollinitiative;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import me.colinhowes.rollinitiative.data.CharacterContract;
import me.colinhowes.rollinitiative.data.CharacterDbHelper;

public class CharacterActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        CharacterAdapter.CharacterClickListener {

    private RecyclerView characterRecyclerView;
    private CharacterAdapter characterAdapter;
    private Cursor characterCursor;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        if (getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        characterRecyclerView = (RecyclerView) findViewById(R.id.rv_character_select);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        characterRecyclerView.setLayoutManager(layoutManager);

        CharacterDbHelper dbHelper = new CharacterDbHelper(this);
        db = dbHelper.getWritableDatabase();

        characterAdapter = new CharacterAdapter(this, characterCursor, this);
        characterRecyclerView.setAdapter(characterAdapter);

        /*
         * Here is where we handle swipe events
         * Swipe left - delete character
         * Swipe right - edit character
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // we aren't using onMove here
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                // passed to database update functions
                int characterId = (int) viewHolder.itemView.getTag();

                if (swipeDir == ItemTouchHelper.LEFT) {
                    deleteCharacter(characterId);
                } else {
                    editCharacter(characterId);
                }

            }
        }).attachToRecyclerView(characterRecyclerView);
    }

    // get all the characters in the database
    private Cursor getCharacters() {
        return db.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                CharacterContract.CharacterEntry.COLUMN_NAME_NAME);
    }

    // toggles whether a character is in combat
    private void toggleInCombat(int characterId) {
        ContentValues values = new ContentValues();
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(characterId) };
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

        // we have to force the loader to fetch the data again
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    // toggles whether a character is in combat
    private void updateHealth(int characterId, boolean increase) {
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
            Log.e("updateHealth", "Database query returned no characters with ID!");
            return;
        }

        currentHealth = cursor.getInt(cursor.getColumnIndex(
                CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT));
        cursor.close();

        // increase or decrease health
        if (increase == true) {
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
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private void deleteCharacter(int characterId) {
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(characterId) };

        db.delete(CharacterContract.CharacterEntry.TABLE_NAME, selection, selectionArgs);

        // we have to force the loader to fetch the data again
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private void editCharacter(int characterId) {
        Toast.makeText(this, "Edit Character " + characterId, Toast.LENGTH_SHORT).show();
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    /*
     * Launch the character creation activity
     */
    public void createCharacter(MenuItem menuItem) {
        Context context = this;
        Class destinationActivity = EditActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(0, null, this);
    }

    /*
     * Get the character data asynchronously
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor newCharacterData = null;

            @Override
            protected void onStartLoading() {
                if (newCharacterData != null) {
                    // Delivers previously loaded data if it exists
                    deliverResult(newCharacterData);
                } else {
                    // Otherwise, load new data
                    forceLoad();
                }
            }

            // load character data asynchronously
            @Override
            public Cursor loadInBackground() {

                try {
                    return getCharacters();
                } catch (Exception e) {
                    Log.e("Load Error:", "Unable to load character data!");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                characterCursor = data;
                super.deliverResult(data);
            }
        };
    }

    /*
     * After the load finishes we need to set the new cursor in the CharacterAdapter
     * Note that changeCursor closes the old cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        characterAdapter.changeCursor(data);
    }

    /*
     * Invalidate old cursor
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        characterAdapter.changeCursor(null);
    }

    /*
     * Handles click events - dispatch to toggleInCombat
     */
    @Override
    public void onCharacterClick(int characterId, EventType eventType) {
        switch (eventType) {
            case INCREASE_HEALTH:
                updateHealth(characterId, true);
                break;
            case DECREASE_HEALTH:
                updateHealth(characterId, false);
                break;
            case ITEM_CLICK:
                toggleInCombat(characterId);
                break;
            default:
                return;
        }
    }
}
