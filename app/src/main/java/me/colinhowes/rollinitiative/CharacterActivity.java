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
                    CharacterDbHelper.deleteCharacter(db, characterId);
                    restartLoader();
                } else {
                    editCharacter(characterId);
                }

            }
        }).attachToRecyclerView(characterRecyclerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CombatActivity.class);
        startActivity(intent);
        finish();
    }

    private void restartLoader() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    private void editCharacter(int characterId) {
        Context context = this;
        Class destinationActivity = EditActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("id", characterId);
        startActivity(intent);
        restartLoader();
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

        restartLoader();
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
                    return CharacterDbHelper.getCharacters(db);
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
                CharacterDbHelper.updateHealth(db, characterId, true);
                // we have to force the loader to fetch the data again
                restartLoader();
                break;
            case DECREASE_HEALTH:
                CharacterDbHelper.updateHealth(db, characterId, false);
                // we have to force the loader to fetch the data again
                restartLoader();
                break;
            case ITEM_CLICK:
                CharacterDbHelper.toggleInCombat(db, characterId);
                // we have to force the loader to fetch the data again
                restartLoader();
                break;
            default:
                break;
        }
    }

    public void rollInitiative(MenuItem menuItem) {
        Toast.makeText(this, "Roll Initiative", Toast.LENGTH_SHORT).show();
        int id;
        int initBonus;
        int initRoll;

        ContentValues values = new ContentValues();
        Random initRandom = new Random();

        Cursor cursor = CharacterDbHelper.getCharacters(db);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }
        do {
            initBonus = cursor.getInt(cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS));
            id = cursor.getInt(cursor.getColumnIndex(CharacterContract.CharacterEntry._ID));

            initRoll = initRandom.nextInt(20) + 1;
            initRoll += initBonus;
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT,
                    String.valueOf(initRoll));

            // update character with new inCombat value
            CharacterDbHelper.updateCharacter(db, id, values);
        } while (cursor.moveToNext());
        cursor.close();
        restartLoader();
    }
}
