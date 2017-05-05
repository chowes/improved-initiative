package me.colinhowes.rollinitiative;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import me.colinhowes.rollinitiative.data.CharacterContract;
import me.colinhowes.rollinitiative.data.CharacterDbHelper;

public class CombatActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        CombatAdapter.CombatClickListener {

    private RecyclerView combatRecyclerView;
    private CombatAdapter combatAdapter;
    private Cursor combatCursor;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combat);

        if (getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        combatRecyclerView = (RecyclerView) findViewById(R.id.rv_turnorder);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        combatRecyclerView.setLayoutManager(layoutManager);

        CharacterDbHelper dbHelper = new CharacterDbHelper(this);
        db = dbHelper.getWritableDatabase();

        combatAdapter = new CombatAdapter(this, combatCursor, this);
        combatRecyclerView.setAdapter(combatAdapter);

        /*
         * Here is where we handle swipe events
         * Swipe left - delete character
         * Swipe right - edit character
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                // passed to database update functions
                int characterId = (int) viewHolder.itemView.getTag();

                if (swipeDir == ItemTouchHelper.LEFT) {
                    CharacterDbHelper.toggleInCombat(db, characterId);
                    restartLoader();
                } else {
                    // editCharacter(characterId);
                }

            }
        }).attachToRecyclerView(combatRecyclerView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void restartLoader() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    public void getTurnOrder() {
        Cursor cursor = CharacterDbHelper.getCombatants(db);
        ContentValues values = new ContentValues();
        int characterId;
        int turnOrder = 0;

        if (!cursor.moveToFirst()) {
            return;
        }

        do {
            characterId = cursor.getInt(cursor.getColumnIndex(
                    CharacterContract.CharacterEntry._ID));
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_TURN_ORDER, turnOrder);
            CharacterDbHelper.updateCharacter(db, characterId, values);
            turnOrder++;
        } while (cursor.moveToNext());
        cursor.close();
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
                    getTurnOrder();
                    forceLoad();
                }
            }

            // load character data asynchronously
            @Override
            public Cursor loadInBackground() {

                try {
                    return CharacterDbHelper.getCombatants(db);
                } catch (Exception e) {
                    Log.e("Load Error:", "Unable to load character data!");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                combatCursor = data;
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
        combatAdapter.changeCursor(data);
    }

    /*
     * Invalidate old cursor
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        combatAdapter.changeCursor(null);
    }

    @Override
    public void onCombatClick(int characterId, EventType eventType) {
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
                // we have to force the loader to fetch the data again
                // restartLoader();
                break;
            default:
                break;
        }
    }

    public void startAddCharacter(MenuItem item) {
        Context context = this;
        Class destinationActivity;
        destinationActivity = CharacterActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    public void replayLastRound(MenuItem item) {
        CharacterDbHelper.lastTurn(db);
        restartLoader();
    }

    public void startNextRound(MenuItem item) {
        CharacterDbHelper.nextTurn(db);
        restartLoader();
    }

}
