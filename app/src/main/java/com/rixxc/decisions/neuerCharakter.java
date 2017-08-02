package com.rixxc.decisions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class neuerCharakter extends AppCompatActivity {

    private Button create;
    private EditText name;
    private File obb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neuer_charakter);
        setTitle("Charaktere");

        obb = new File(getObbDir(), "Charactere");
        name = (EditText) findViewById(R.id.Name);
        create = (Button) findViewById(R.id.createCharakter);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.createCharakter:
                        File charakter = new File(obb, name.getText().toString() + ".chr");
                        boolean contains = false;
                        for(File f : obb.listFiles()){
                            if(f.getName().equals(charakter.getName())){
                                contains = true;
                            }
                        }
                        if(!contains){
                            try{
                                charakter.createNewFile();
                            }catch (Exception e) {
                                Toast.makeText(neuerCharakter.this, "Es ist ein Fehler beim erstellen des Charakters aufgetreten", Toast.LENGTH_LONG).show();
                            }
                            Intent intent = new Intent(neuerCharakter.this, Charaktere.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(neuerCharakter.this, "Dieser Name ist bereits vergeben", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
    }
}
