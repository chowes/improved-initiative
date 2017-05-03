package me.colinhowes.rollinitiative;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import me.colinhowes.rollinitiative.data.CharacterContract;
import me.colinhowes.rollinitiative.data.CharacterDbHelper;
import me.colinhowes.rollinitiative.data.TestUtil;

public class CombatActivity extends AppCompatActivity implements CombatAdapter.CharacterClickListener{

    private RecyclerView combatRecyclerView;
    private CombatAdapter combatAdapter;
    private SQLiteDatabase combatDb;

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
        combatDb = dbHelper.getWritableDatabase();
        Cursor cursor = getCombatants();

        combatAdapter = new CombatAdapter(this, cursor, this);
        combatRecyclerView.setAdapter(combatAdapter);
    }

    private Cursor getCombatants() {
        return combatDb.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                "in_combat=?",
                new String[]{"1"},
                null,
                null,
                CharacterContract.CharacterEntry.COLUMN_NAME_INIT + " DESC");
    }

    @Override
    public void onCharacterClick(int indexClicked) {
        Toast.makeText(this, String.valueOf(indexClicked), Toast.LENGTH_LONG).show();
    }

    public void startAddCharacter(MenuItem item) {
        Context context = this;
        Class destinationActivity;
        destinationActivity = CharacterActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    public void replayLastRound(MenuItem item) {
        Toast.makeText(this, "Replay Round", Toast.LENGTH_SHORT).show();
    }

    public void startNextRound(MenuItem item) {
        Toast.makeText(this, "Next Round", Toast.LENGTH_SHORT).show();
    }

}
