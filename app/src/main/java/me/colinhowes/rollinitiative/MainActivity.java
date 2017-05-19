package me.colinhowes.rollinitiative;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the top menu
        getMenuInflater().inflate(R.menu.main_menu_top, menu);
        return true;
    }



    public void startButtonActivity(View view) {
        Context context = MainActivity.this;
        Class destinationActivity;

        int id = view.getId();

        switch (id) {
            case R.id.combat_button:
                destinationActivity = CombatActivity.class;
                break;
            case R.id.character_button:
                destinationActivity = CharacterActivity.class;
                break;
            default:
                return;
        }

        Intent intent = new Intent(context, destinationActivity);
        startActivity(intent);
    }

    public void showAbout(MenuItem menuItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.about_dialog, null));
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // maybe do something fun here
            }
        });
        builder.setNegativeButton(R.string.report_bug, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String url = "https://github.com/chowes/improved-initiative/issues";
                Intent bugIntent = new Intent(Intent.ACTION_VIEW);
                bugIntent.setData(Uri.parse(url));
                startActivity(bugIntent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
