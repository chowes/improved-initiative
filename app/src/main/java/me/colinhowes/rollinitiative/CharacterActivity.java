package me.colinhowes.rollinitiative;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CharacterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        if (getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    public void startButtonActivity(View view) {
        Context context = this;
        Class destinationActivity;

        int id = view.getId();

        switch (id) {
            case R.id.btn_add_character:
                destinationActivity = EditActivity.class;
                break;
            default:
                return;
        }

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }
}
