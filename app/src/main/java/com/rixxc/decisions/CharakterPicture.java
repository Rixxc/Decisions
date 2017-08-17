package com.rixxc.decisions;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CharakterPicture extends AppCompatActivity {

    private GridView grid;
    private ArrayList<Drawable> portraits;
    private String name;
    private int stärke,ausdauer,intelligenz,geschicklichkeit,mut;
    private String[] images;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charakter_picture);

        setTitle("Charaktere");

        db = openOrCreateDatabase("Decisions.db", MODE_PRIVATE, null);

        name = getIntent().getStringExtra("name");
        stärke = getIntent().getIntExtra("stärke", -1);
        ausdauer = getIntent().getIntExtra("ausdauer", -1);
        intelligenz = getIntent().getIntExtra("intelligenz", -1);
        geschicklichkeit = getIntent().getIntExtra("geschicklichkeit", -1);
        mut = getIntent().getIntExtra("mut", -1);

        if(stärke == -1 || ausdauer == -1 || intelligenz == -1 || geschicklichkeit == -1 || mut == -1){
            Toast.makeText(CharakterPicture.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CharakterPicture.this, Charaktere.class);
            startActivity(intent);
        }

        portraits = new ArrayList<>();
        grid = (GridView) findViewById(R.id.images);
        try {
            images = getAssets().list("portraits");

            for(String file : images){
                InputStream in = getAssets().open("portraits/" + file);

                Drawable d = Drawable.createFromStream(in, null);

                portraits.add(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageAdapter imageAdapter = new ImageAdapter(CharakterPicture.this, portraits, getWindowManager().getDefaultDisplay().getWidth() / 2);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                db.execSQL("INSERT INTO charakter(name,stärke,ausdauer,intelligenz,geschicklichkeit,mut,punkte,avatar) VALUES ('" + name + "','" + stärke + "','" + ausdauer + "','" + intelligenz + "','" + geschicklichkeit + "','" + mut + "','" + (15 - ((stärke+ausdauer+mut+intelligenz+geschicklichkeit) - 50)) + "','" + images[position] + "')");
                db.close();
                Intent intent = new Intent(CharakterPicture.this, Charaktere.class);
                startActivity(intent);
            }
        });
        grid.setNumColumns(2);
        grid.setColumnWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
        grid.setAdapter(imageAdapter);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(CharakterPicture.this, Charaktere.class);
        startActivity(intent);
    }
    @Override
    public void onDestroy(){
        db.close();
        super.onDestroy();
    }
}

