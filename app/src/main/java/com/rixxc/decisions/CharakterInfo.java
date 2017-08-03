package com.rixxc.decisions;

import android.content.Intent;
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
    private File obb,charakter;
    private TextView name,stärke,ausdauer,intelligenz,geschicklichkeit,mut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charakter_info);
        charakterName = getIntent().getExtras().getString("Charakter");
        setTitle(charakterName);

        obb = new File(getObbDir(), "Charactere");
        charakter = new File(obb, charakterName + ".chr");

        name = (TextView) findViewById(R.id.InfoName);
        stärke = (TextView) findViewById(R.id.InfoStärke);
        ausdauer = (TextView) findViewById(R.id.InfoAusdauer);
        intelligenz = (TextView) findViewById(R.id.InfoIntelligenz);
        geschicklichkeit = (TextView) findViewById(R.id.InfoGeschicklichkeit);
        mut = (TextView) findViewById(R.id.InfoMut);

        try{
            FileReader fr = new FileReader(charakter);
            BufferedReader br = new BufferedReader(fr);

            name.setText(br.readLine());
            stärke.setText(br.readLine());
            ausdauer.setText(br.readLine());
            intelligenz.setText(br.readLine());
            geschicklichkeit.setText(br.readLine());
            mut.setText(br.readLine());
        }catch (Exception e){
            Toast.makeText(CharakterInfo.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CharakterInfo.this, Charaktere.class);
            startActivity(intent);
        }
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
                File[] files = obb.listFiles();
                for(File f : files){
                    if(f.getName().equals(charakterName + ".chr")){
                        try {
                            f.delete();
                            break;
                        }catch (Exception e){
                            Toast.makeText(CharakterInfo.this, "Es ist ein Fehler beim löschen des Charakters aufgetreten", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                Intent intent = new Intent(CharakterInfo.this, Charaktere.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
