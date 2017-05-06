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

import java.util.ArrayList;

import me.colinhowes.rollinitiative.data.CharacterContract;
import me.colinhowes.rollinitiative.data.CharacterDbHelper;
import me.colinhowes.rollinitiative.data.CharacterType;

public class CombatActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<CharacterType>>,
        CombatAdapter.CombatClickListener {

    private RecyclerView combatRecyclerView;
    private CombatAdapter combatAdapter;
    private ArrayList<CharacterType> characterList;
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

        combatAdapter = new CombatAdapter(this, characterList, this);
        combatRecyclerView.setAdapter(combatAdapter);

        /*
         * Here is where we handle swipe events
         * Swipe left - delete character
         * Swipe right - edit character
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder dragged, RecyclerView.ViewHolder target) {
                int fromIndex = dragged.getAdapterPosition();
                int toIndex = target.getAdapterPosition();

                boolean moved = combatAdapter.swapCharacters(fromIndex, toIndex);

                if (fromIndex == 0 && moved) {
                    combatAdapter.setActiveCharacter(dragged, false);
                    RecyclerView.ViewHolder activeCharacter = combatRecyclerView.
                            findViewHolderForAdapterPosition(0);
                    combatAdapter.setActiveCharacter(activeCharacter, true);
                } else if (toIndex == 0 && moved) {
                    combatAdapter.setActiveCharacter(target, false);
                    RecyclerView.ViewHolder activeCharacter = combatRecyclerView.
                            findViewHolderForAdapterPosition(0);
                    combatAdapter.setActiveCharacter(activeCharacter, true);
                }

                return moved;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                characterList = combatAdapter.getCharacterList();
                int position = (int) viewHolder.itemView.getTag();
                CharacterType character = characterList.get(position);

                if (swipeDir == ItemTouchHelper.LEFT) {
                    character.setInCombat(0);
                    characterList.remove(position);
                    CharacterDbHelper.toggleInCombat(db, character.getId());
                    combatAdapter.notifyDataSetChanged();
                } else {
                    // editCharacter(characterId);
                }

            }
        }).attachToRecyclerView(combatRecyclerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        int turnOrder = 0;
        characterList = combatAdapter.getCharacterList();

        for (CharacterType character : characterList) {

            character.setTurnOrder(turnOrder);
            CharacterDbHelper.updateCharacter(db, character);

            turnOrder++;
        }
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
            String colour = cursor.getString(
                    cursor.getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR));

            character = new CharacterType(id, name, colour, hpCurrent, hpTotal, initBonus, initScore,
                    turnOrder, inCombat);

            characterList.add(character);

        } while (cursor.moveToNext());

        return characterList;
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
                    getTurnOrder();
                    forceLoad();
                }
            }

            // load character data asynchronously
            @Override
            public ArrayList<CharacterType> loadInBackground() {

                Cursor cursor;

                try {
                    cursor = CharacterDbHelper.getCombatants(db);
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
        combatAdapter.changeList(data);
    }

    /*
     * Invalidate old cursor
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<CharacterType>> loader) {
        combatAdapter.changeList(null);
    }

    @Override
    public void onCombatClick(int position, EventType eventType) {
        characterList = combatAdapter.getCharacterList();
        CharacterType character = characterList.get(position);

        switch (eventType) {
            case INCREASE_HEALTH:
                character.setHealth(character.getHpCurrent() + 1);
                // we have to force the loader to fetch the data again
                combatAdapter.notifyItemChanged(position);
                break;
            case DECREASE_HEALTH:
                character.setHealth(character.getHpCurrent() - 1);
                // we have to force the loader to fetch the data again
                combatAdapter.notifyItemChanged(position);
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
        RecyclerView.ViewHolder viewHolder;
        viewHolder= combatRecyclerView.findViewHolderForAdapterPosition(0);
        combatAdapter.setActiveCharacter(viewHolder, false);
        combatAdapter.swapCharacters(characterList.size() - 1, 0);
        viewHolder = combatRecyclerView.findViewHolderForAdapterPosition(0);
        combatAdapter.setActiveCharacter(viewHolder, true);
    }

    public void startNextRound(MenuItem item) {
        RecyclerView.ViewHolder viewHolder;
        viewHolder = combatRecyclerView.findViewHolderForAdapterPosition(0);
        combatAdapter.setActiveCharacter(viewHolder, false);
        combatAdapter.swapCharacters(0, characterList.size() - 1);
        viewHolder = combatRecyclerView.findViewHolderForAdapterPosition(0);
        combatAdapter.setActiveCharacter(viewHolder, true);
    }

}
