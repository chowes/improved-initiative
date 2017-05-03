package me.colinhowes.rollinitiative;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import me.colinhowes.rollinitiative.data.CharacterDbHelper;
import me.colinhowes.rollinitiative.data.CharacterType;
import me.colinhowes.rollinitiative.data.TestUtil;

public class EditActivity extends AppCompatActivity {

    SQLiteDatabase characterDb;
    CharacterDbHelper dbHelper;

    EditText nameField;
    EditText hpCurrentField;
    EditText hpMaxField;
    EditText initBonusField;
    EditText initField;
    CheckBox inCombatCheckBox;
    Spinner colorSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        if (getSupportActionBar() != null) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        colorSpinner = (Spinner) findViewById(R.id.spinner_color);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.color_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);

        nameField = (EditText) findViewById(R.id.et_name);
        hpCurrentField = (EditText) findViewById(R.id.et_hp_current);
        hpMaxField = (EditText) findViewById(R.id.et_hp_max);
        initBonusField = (EditText) findViewById(R.id.et_init_bonus);
        initField = (EditText) findViewById(R.id.et_init);
        inCombatCheckBox = (CheckBox) findViewById(R.id.cb_in_combat);

        dbHelper = new CharacterDbHelper(this);
        characterDb = dbHelper.getWritableDatabase();
    }

    public void saveCharacter(MenuItem menuItem) {
        String name = nameField.getText().toString();
        String colour = colorSpinner.getSelectedItem().toString().toLowerCase();
        String condition = "normal";
        int inCombat = inCombatCheckBox.isChecked() ? 1 : 0;
        int hpCurrent;
        int hpMax;
        int initBonus;
        int init;

        // try to get character data from fields, on error we just assign a default
        if (name.isEmpty()) {
            name = "John Doe";
        }

        try {
            hpCurrent = Integer.parseInt(hpCurrentField.getText().toString());
        } catch (NumberFormatException e) {
            hpCurrent = 0;
        }

        try {
            hpMax = Integer.parseInt(hpMaxField.getText().toString());
        } catch (NumberFormatException e) {
            hpMax = 0;
        }

        try {
            initBonus = Integer.parseInt(initBonusField.getText().toString());
        } catch (NumberFormatException e) {
            initBonus = 0;
        }

        try {
            init = Integer.parseInt(initBonusField.getText().toString());
        } catch (NumberFormatException e) {
            init = 0;
        }

        // create a new character and add it to the database
        CharacterType character = new CharacterType(name, colour, condition, hpCurrent, hpMax, initBonus, init, inCombat);
        dbHelper.insertCharacter(characterDb, character);

        // return to character selection activity
        Context context = this;
        Class destinationActivity = CharacterActivity.class;

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    public void rollInitiative(MenuItem menuItem) {
        int initBonus;
        String initString;
        int initRoll;
        Random initRandom = new Random();
        try {
            initBonus = Integer.parseInt(initBonusField.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter Initiative Bonus First!", Toast.LENGTH_SHORT).show();
            return;
        }
        initRoll = initRandom.nextInt(20) + 1;
        initRoll += initBonus;
        initString = String.valueOf(initRoll);
        initField.setText(initString);
    }
}
