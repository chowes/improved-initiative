package me.colinhowes.rollinitiative;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class CombatActivity extends AppCompatActivity {

    private RecyclerView combatRecyclerView;
    private CombatAdapter combatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combat);

        combatRecyclerView = (RecyclerView) findViewById(R.id.rv_turnorder);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        combatRecyclerView.setLayoutManager(layoutManager);

        combatAdapter = new CombatAdapter(100);
        combatRecyclerView.setAdapter(combatAdapter);




    }
}
