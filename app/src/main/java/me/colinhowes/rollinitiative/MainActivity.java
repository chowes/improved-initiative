package me.colinhowes.rollinitiative;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startButtonActivity(View view) {
        Context context = MainActivity.this;
        Class destinationActivity;

        int id = view.getId();

        switch (id) {
            case R.id.combat_button:
                destinationActivity = CombatActivity.class;
                break;
            case R.id.edit_button:
                destinationActivity = CharacterActivity.class;
                break;
            default:
                return;
        }

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }
}
