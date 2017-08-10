package com.rixxc.decisions;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class CharakterInfo extends AppCompatActivity {

    private String charakterName;
    private TextView name,stärke,ausdauer,intelligenz,geschicklichkeit,mut,punkte;
    private SQLiteDatabase db;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charakter_info);
        charakterName = getIntent().getExtras().getString("Charakter");
        setTitle(charakterName);

        name = (TextView) findViewById(R.id.InfoName);
        stärke = (TextView) findViewById(R.id.InfoStärke);
        ausdauer = (TextView) findViewById(R.id.InfoAusdauer);
        intelligenz = (TextView) findViewById(R.id.InfoIntelligenz);
        geschicklichkeit = (TextView) findViewById(R.id.InfoGeschicklichkeit);
        mut = (TextView) findViewById(R.id.InfoMut);
        punkte = (TextView) findViewById(R.id.InfoPunkte);

        db = openOrCreateDatabase("Decisions.db", MODE_PRIVATE, null);

        String EName = getIntent().getStringExtra("Charakter");

        String[] column = {"stärke","ausdauer","intelligenz","geschicklichkeit","mut","punkte","id"};
        String[] args = {EName};
        Cursor result = db.query("Charakter", column,"name=?",args,null,null,null);
        result.moveToFirst();

        name.setText("Name: " + EName);
        stärke.setText("Stärke: " + result.getInt(0));
        ausdauer.setText("Ausdauer: " + result.getInt(1));
        intelligenz.setText("Intelligenz: " + result.getInt(2));
        geschicklichkeit.setText("Geschicklichkeit" + result.getInt(3));
        mut.setText("Mut: " + result.getInt(4));
        punkte.setText("Verbleibende Punkte: " + result.getInt(5));
        id = result.getInt(6);

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(CharakterInfo.this, Charaktere.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.löschen:
                db.execSQL("DELETE FROM Charakter WHERE id=" + id);
                Intent intent = new Intent(CharakterInfo.this, Charaktere.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
