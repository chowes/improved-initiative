package me.colinhowes.rollinitiative;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.colinhowes.rollinitiative.data.CharacterDbHelper;
import me.colinhowes.rollinitiative.data.CharacterType;
import me.colinhowes.rollinitiative.data.TestUtil;

public class EditActivity extends AppCompatActivity {

    SQLiteDatabase characterDb;
    CharacterDbHelper dbHelper;

    EditText nameField;
    EditText hpMaxField;
    EditText initBonusField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        if (getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameField = (EditText) findViewById(R.id.et_name_input);
        hpMaxField = (EditText) findViewById(R.id.et_max_hp);
        initBonusField = (EditText) findViewById(R.id.et_init_bonus);

        dbHelper = new CharacterDbHelper(this);
        characterDb = dbHelper.getWritableDatabase();
    }

    public void saveButton(View view) {
        Context context = this;
        Class destinationActivity;

        int id = view.getId();

        switch (id) {
            case R.id.btn_save_character:
                saveCharacter();
                destinationActivity = CombatActivity.class;
                break;
            default:
                return;
        }

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    private void saveCharacter() {
        String name = nameField.getText().toString();
        String colour = "blue";
        String condition = "Normal";
        int hpCurrent;
        int hpMax;
        int initBonus;
        int init = 0;
        int inCombat = 0;

        if (name.isEmpty()) {
            name = "NoName";
        }

        try {
            hpMax = Integer.parseInt(hpMaxField.getText().toString());
        } catch (NumberFormatException e) {
            hpMax = 0;
        }
        hpCurrent = hpMax;

        try {
            initBonus = Integer.parseInt(initBonusField.getText().toString());
        } catch (NumberFormatException e) {
            initBonus = 0;
        }

        CharacterType character = new CharacterType(name, colour, condition, hpCurrent, hpMax, initBonus, init, inCombat);
        dbHelper.insertCharacter(characterDb, character);
    }
}
