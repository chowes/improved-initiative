package me.colinhowes.rollinitiative;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Random;

import me.colinhowes.rollinitiative.data.CharacterContract;
import me.colinhowes.rollinitiative.data.CharacterDbHelper;
import me.colinhowes.rollinitiative.data.CharacterType;

public class CharacterActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<ArrayList<CharacterType>>,
        CharacterAdapter.CharacterClickListener {

    private RecyclerView characterRecyclerView;
    private CharacterAdapter characterAdapter;
    private ArrayList<CharacterType> characterList;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        if (getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showInstructions();

        characterRecyclerView = (RecyclerView) findViewById(R.id.rv_character_select);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        characterRecyclerView.setLayoutManager(layoutManager);

        CharacterDbHelper dbHelper = new CharacterDbHelper(this);
        db = dbHelper.getWritableDatabase();

        characterAdapter = new CharacterAdapter(characterList, this);
        characterRecyclerView.setAdapter(characterAdapter);

        /*
         * Here is where we handle swipe events
         * Swipe left - delete character
         * Swipe right - edit character
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // we aren't using onMove here
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                // passed to database update functions
                int position = (int) viewHolder.itemView.getTag();

                if (swipeDir == ItemTouchHelper.LEFT) {
                    CharacterDbHelper.deleteCharacter(db, characterList.get(position).getId());
                    characterList.remove(position);
                    characterAdapter.notifyDataSetChanged();
                } else if (swipeDir == ItemTouchHelper.RIGHT){
                    editCharacter(characterList.get(position).getId());
                }

            }
        }).attachToRecyclerView(characterRecyclerView);
    }

    private ArrayList<CharacterType> createCharacterList(Cursor cursor) {
        ArrayList<CharacterType> characterList = new ArrayList<>(cursor.getCount());
        CharacterType character;

        if (!cursor.moveToFirst()) {
            return characterList;
        }

        do {
            int id = cursor.getInt(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry._ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_NAME));
            int hpCurrent = cursor.getInt(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT));
            int hpTotal = cursor.getInt(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL));
            int initBonus = cursor.getInt(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS));
            int initScore = cursor.getInt(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT));
            int turnOrder = cursor.getInt(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_TURN_ORDER));
            int inCombat = cursor.getInt(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT));
            int delayTurn = cursor.getInt(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_DELAY_TURN));
            String colour = cursor.getString(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR));

            character = new CharacterType(id, name, colour, hpCurrent, hpTotal, initBonus, initScore,
                    turnOrder, inCombat, delayTurn);

            characterList.add(character);

        } while (cursor.moveToNext());

        return characterList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the top menu
        getMenuInflater().inflate(R.menu.character_menu_top, menu);
        return true;
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

    public void getTurnOrder() {

        // need to update new values
        for (CharacterType character : characterList) {
            CharacterDbHelper.updateCharacter(db, character);
        }

        Cursor cursor = CharacterDbHelper.getCombatantsByInit(db);
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

        restartLoader();
    }

    @Override
    protected void onPause() {
        super.onPause();
        characterList = characterAdapter.getCharacterList();

        if (characterList == null) {
            return;
        }
        for (CharacterType character : characterList) {
            CharacterDbHelper.updateCharacter(db, character);
        }
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
    public Loader<ArrayList<CharacterType>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<CharacterType>>(this) {

            ArrayList<CharacterType> newCharacterData = null;

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
            public ArrayList<CharacterType> loadInBackground() {

                Cursor cursor;

                try {
                    cursor = CharacterDbHelper.getCharacters(db);
                    return createCharacterList(cursor);

                } catch (Exception e) {
                    Log.e("Load Error:", "Unable to load character data!");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(ArrayList<CharacterType> data) {
                characterList = data;
                super.deliverResult(data);
            }
        };
    }

    /*
     * After the load finishes we need to set the new cursor in the CharacterAdapter
     * Note that changeCursor closes the old cursor
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<CharacterType>> loader, ArrayList<CharacterType> data) {
        characterAdapter.changeList(data);
    }

    /*
     * Invalidate old cursor
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<CharacterType>> loader) {
        characterAdapter.changeList(null);
    }

    /*
     * Handles click events - dispatch to toggleInCombat
     */
    @Override
    public void onCharacterClick(int position, EventType eventType) {
        CharacterType character;
        try {
            character = characterList.get(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return;
        }

        switch (eventType) {
            case INCREASE_HEALTH:
                character.setHealth(character.getHpCurrent() + 1);
                // we have to force the loader to fetch the data again
                characterAdapter.notifyItemChanged(position);
                break;
            case DECREASE_HEALTH:
                character.setHealth(character.getHpCurrent() - 1);
                // we have to force the loader to fetch the data again
                characterAdapter.notifyItemChanged(position);
                break;
            case ITEM_CLICK:
                if (character.getInCombat() == 1) {
                    character.setInCombat(0);
                } else {
                    character.setInCombat(1);
                }
                characterAdapter.notifyItemChanged(position);
                break;
            default:
                break;
        }
    }

    public void rollInitiative(MenuItem menuItem) {
        int id;
        int initBonus;
        int initRoll;

        ContentValues values = new ContentValues();
        Random initRandom = new Random();


        for (CharacterType character : characterList) {
            CharacterDbHelper.updateCharacter(db, character);
        }


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
            initRoll = initRoll > 999 ? 999 : initRoll;
            initRoll = initRoll < -99 ? -99 : initRoll;
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT,
                    String.valueOf(initRoll));

            // update character with new inCombat value
            CharacterDbHelper.updateCharacter(db, id, values);
        } while (cursor.moveToNext());
        cursor.close();
        restartLoader();
    }

    public void startCombat(MenuItem menuItem) {
        Intent intent = new Intent(this, CombatActivity.class);
        getTurnOrder();
        startActivity(intent);
        finish();
    }

    public void resumeCombat(MenuItem menuItem) {
        Intent intent = new Intent(this, CombatActivity.class);
        startActivity(intent);
        finish();
    }

    public void showInstructions(MenuItem menuItem) {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        final String showAgainKey = getString(R.string.character_tutorial_pref);
        final SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(showAgainKey, true);
        prefEditor.apply();

        showInstructions();
    }

    private void showInstructions() {

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        final String showAgainKey = getString(R.string.character_tutorial_pref);
        boolean showAgain = sharedPref.getBoolean(showAgainKey, true);

        if (!showAgain) {
            return;
        }

        final SharedPreferences.Editor prefEditor = sharedPref.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.character_tutorial_dialog, null));
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                prefEditor.putBoolean(showAgainKey, true);
                prefEditor.apply();
            }
        }).setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                prefEditor.putBoolean(showAgainKey, false);
                prefEditor.apply();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
