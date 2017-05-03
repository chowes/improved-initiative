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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                int id = (int) viewHolder.itemView.getTag();

                if (swipeDir == ItemTouchHelper.LEFT) {
                    // delete
                } else {
                    toggleInCombat(id);
                }

            }
        }).attachToRecyclerView(characterRecyclerView);

    }

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

    private void toggleInCombat(int characterId) {
        ContentValues values = new ContentValues();
        String selection = CharacterContract.CharacterEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(characterId) };
        int inCombat;

        Cursor cursor = db.query(
            CharacterContract.CharacterEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null);

        if (!cursor.moveToFirst()) {
            Log.e("TAG", "Here!");
            return;
        }
        inCombat = cursor.getInt(cursor.getColumnIndex(
                CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT));
        cursor.close();

        inCombat = (inCombat == 1) ? 0 : 1;

        values.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT,
                String.valueOf(inCombat));

        db.update(
            CharacterContract.CharacterEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs);

        getSupportLoaderManager().restartLoader(0, null, this);
    }

    public void createCharacter(MenuItem menuItem) {
        Context context = this;
        Class destinationActivity = EditActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    public int rollInitiative(MenuItem menuItem) {
        int initBonus = 0;
        int initRoll;

        Random initRandom = new Random();
        initRoll = initRandom.nextInt(20) + 1;
        initRoll += initBonus;

        return initRoll;
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(0, null, this);
    }

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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        characterAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        characterAdapter.changeCursor(null);
    }

    @Override
    public void onCharacterClick(int characterId) {
        toggleInCombat(characterId);
    }
}
