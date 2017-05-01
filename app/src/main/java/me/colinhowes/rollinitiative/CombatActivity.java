package me.colinhowes.rollinitiative;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import me.colinhowes.rollinitiative.data.CharacterContract;
import me.colinhowes.rollinitiative.data.CharacterDbHelper;
import me.colinhowes.rollinitiative.data.TestUtil;

public class CombatActivity extends AppCompatActivity {

    private RecyclerView combatRecyclerView;
    private CombatAdapter combatAdapter;
    private SQLiteDatabase combatDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combat);

        combatRecyclerView = (RecyclerView) findViewById(R.id.rv_turnorder);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        combatRecyclerView.setLayoutManager(layoutManager);

        CharacterDbHelper dbHelper = new CharacterDbHelper(this);
        combatDb = dbHelper.getWritableDatabase();
        TestUtil.insertFakeData(combatDb);
        Cursor cursor = getAllCombatants();

        combatAdapter = new CombatAdapter(this, cursor);
        combatRecyclerView.setAdapter(combatAdapter);
    }

    private Cursor getAllCombatants() {
        return combatDb.query(
                CharacterContract.CharacterEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                CharacterContract.CharacterEntry.COLUMN_NAME_INIT + " DESC");
    }
}
