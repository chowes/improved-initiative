package me.colinhowes.rollinitiative;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import me.colinhowes.rollinitiative.data.CharacterContract;
import me.colinhowes.rollinitiative.data.CharacterDbHelper;
import me.colinhowes.rollinitiative.data.CharacterType;
import me.colinhowes.rollinitiative.data.TestUtil;

public class EditActivity extends AppCompatActivity {

    SQLiteDatabase characterDb;
    CharacterDbHelper dbHelper;

    int characterId;
    boolean newCharacter;

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
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
            R.array.color_array, R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(spinnerAdapter);

        nameField = (EditText) findViewById(R.id.et_name);
        hpCurrentField = (EditText) findViewById(R.id.et_hp_current);
        hpMaxField = (EditText) findViewById(R.id.et_hp_max);
        initBonusField = (EditText) findViewById(R.id.et_init_bonus);
        initField = (EditText) findViewById(R.id.et_init);
        inCombatCheckBox = (CheckBox) findViewById(R.id.cb_in_combat);

        dbHelper = new CharacterDbHelper(this);
        characterDb = dbHelper.getWritableDatabase();
        characterId = getIntent().getIntExtra("id", -1);

        if (characterId != -1) {
            newCharacter = false;
            String selection = CharacterContract.CharacterEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(characterId) };

            Cursor cursor = characterDb.query(CharacterContract.CharacterEntry.TABLE_NAME, null,
                    selection, selectionArgs, null, null, null);

            cursor.moveToFirst();

            String name = cursor.getString(cursor.
                    getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_NAME));
            int hpCurrent = cursor.getInt(cursor.
                    getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT));
            int hpMax = cursor.getInt(cursor.
                    getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL));
            int initBonus = cursor.getInt(cursor.
                    getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS));
            int init = cursor.getInt(cursor.
                    getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_INIT));
            Log.d("init", String.valueOf(init));
            int inCombat = cursor.getInt(cursor.
                    getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT));
            String color = cursor.getString(cursor.
                    getColumnIndex(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR));

            cursor.close();

            nameField.setText(name);
            hpCurrentField.setText(String.valueOf(hpCurrent));
            hpMaxField.setText(String.valueOf(hpMax));
            initBonusField.setText(String.valueOf(initBonus));
            initField.setText(String.valueOf(init));
            colorSpinner.setSelection(spinnerAdapter.getPosition(color));
            if (inCombat == 1) {
                inCombatCheckBox.setChecked(true);
            } else {
                inCombatCheckBox.setChecked(false);
            }

        } else {
            newCharacter = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CharacterActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveCharacter(MenuItem menuItem) {
        String name = nameField.getText().toString();
        String colour = colorSpinner.getSelectedItem().toString();
        int inCombat = inCombatCheckBox.isChecked() ? 1 : 0;
        int hpCurrent;
        int hpMax;
        int initBonus;
        int init;

        // try to get character data from fields, on error we just assign a default
        if (name.isEmpty()) {
            name = "Sir Gygax";
        }

        try {
            hpCurrent = Integer.parseInt(hpCurrentField.getText().toString());
        } catch (NumberFormatException e) {
            hpCurrent = 8;
        }

        try {
            hpMax = Integer.parseInt(hpMaxField.getText().toString());
        } catch (NumberFormatException e) {
            hpMax = 8;
        }

        try {
            initBonus = Integer.parseInt(initBonusField.getText().toString());
        } catch (NumberFormatException e) {
            initBonus = 0;
        }

        try {
            init = Integer.parseInt(initField.getText().toString());
        } catch (NumberFormatException e) {
            init = 0;
        }

        // create a new character and add it to the database
        if (newCharacter == true) {
            CharacterType character = new CharacterType(name, colour, hpCurrent, hpMax, initBonus, init, inCombat);
            dbHelper.insertCharacter(characterDb, character);
        } else {
            ContentValues values = new ContentValues();

            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_NAME, name);
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_COLOUR, colour);
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_CURRENT, String.valueOf(hpCurrent));
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_HP_TOTAL, String.valueOf(hpMax));
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT_BONUS, String.valueOf(initBonus));
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_INIT, String.valueOf(init));
            values.put(CharacterContract.CharacterEntry.COLUMN_NAME_IN_COMBAT, String.valueOf(inCombat));

            CharacterDbHelper.updateCharacter(characterDb, characterId, values);
        }

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
