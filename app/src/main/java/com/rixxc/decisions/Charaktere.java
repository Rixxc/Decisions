package com.rixxc.decisions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Charaktere extends AppCompatActivity {

    private ListView characters;
    private ArrayList<File> dateinliste;
    private ArrayList<String> characterNames;
    private Button newCharacter;
    private File obb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charaktere);
        setTitle("Charaktere");

        obb = new File(getObbDir(), "Charactere");
        if (!obb.exists()){
            obb.mkdirs();
        }

        characters = (ListView) findViewById(R.id.characterlist);
        newCharacter = (Button) findViewById(R.id.newcharacter);
        ListUpdate();

        newCharacter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.newcharacter:
                        Intent intent = new Intent(Charaktere.this, neuerCharakter.class);
                        startActivity(intent);
                        break;
                }
            }
        });

    }

    public void ListUpdate(){
        this.arrayListSetup();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Charaktere.this, android.R.layout.simple_list_item_1, characterNames);
        characters.setAdapter(arrayAdapter);
    }

    private void arrayListSetup() {
        dateinliste = new ArrayList<>();
        characterNames = new ArrayList<>();

        dateinliste.addAll(Arrays.asList(obb.listFiles()));
        Collections.sort(dateinliste);
        Collections.reverse(dateinliste);

        for(File f : dateinliste){
            characterNames.add(f.getName().substring(0,f.getName().length() - 4));
        }


    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Charaktere.this, MainActivity.class);
        startActivity(intent);
    }
}
