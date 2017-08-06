package com.rixxc.decisions;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class Charaktere extends AppCompatActivity {

    private ListView characters;
    public static ArrayList<String> characterNames;
    private Button newCharacter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charaktere);
        setTitle("Charaktere");

        db = openOrCreateDatabase("Charakter.db", MODE_PRIVATE, null);

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

        characters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Charaktere.this, CharakterInfo.class);
                intent.putExtra("Charakter", characterNames.get(position));
                startActivity(intent);
            }
        });

    }

    public void ListUpdate(){
        this.arrayListSetup();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Charaktere.this, android.R.layout.simple_list_item_1, characterNames);
        characters.setAdapter(arrayAdapter);
    }

    private void arrayListSetup() {
        characterNames = new ArrayList<>();

        String[] columns = {"name"};
        Cursor result = db.query("charakter", columns,null,null,null,null,null);

        result.moveToFirst();
        for(int i = 0; i < result.getCount(); i++){
            characterNames.add(result.getString(0));
            result.moveToNext();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Charaktere.this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
