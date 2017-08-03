package com.rixxc.decisions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class neuerCharakter extends AppCompatActivity {

    private Button create;
    private EditText name,stärke,ausdauer,intelligenz,geschicklichkeit,mut;
    private File obb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neuer_charakter);
        setTitle("Charaktere");

        obb = new File(getObbDir(), "Charactere");
        name = (EditText) findViewById(R.id.Name);
        stärke = (EditText) findViewById(R.id.stärke);
        ausdauer = (EditText) findViewById(R.id.ausdauer);
        mut = (EditText) findViewById(R.id.mut);
        intelligenz = (EditText) findViewById(R.id.intelligenz);
        geschicklichkeit = (EditText) findViewById(R.id.geschicklichkeit);
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
                                int istärke,iausdauer,imut,iintelligenz,igeschicklichkeit;
                                istärke = Integer.parseInt(stärke.getText().toString());
                                iausdauer = Integer.parseInt(ausdauer.getText().toString());
                                imut = Integer.parseInt(mut.getText().toString());
                                iintelligenz = Integer.parseInt(intelligenz.getText().toString());
                                igeschicklichkeit = Integer.parseInt(geschicklichkeit.getText().toString());

                                if((istärke+iausdauer+imut+iintelligenz+igeschicklichkeit) > 15){
                                    throw new Exception("Es wurden zu viele Punkte verteilt");
                                }
                                if(name.getText().toString() == "" || name.getText().toString() == null || name.getText().toString().contains(" ")){
                                    throw new Exception("Ungültiger Name");
                                }

                                FileWriter fw = new FileWriter(charakter);
                                BufferedWriter bw = new BufferedWriter(fw);

                                bw.write("Name:" + name.getText().toString());
                                bw.newLine();
                                bw.write("Stärke:" + istärke);
                                bw.newLine();
                                bw.write("Ausdauer:" + iausdauer);
                                bw.newLine();
                                bw.write("Intelligenz:" + iintelligenz);
                                bw.newLine();
                                bw.write("Geschicklichkeit:" + igeschicklichkeit);
                                bw.newLine();
                                bw.write("Mut:" + imut);
                                bw.newLine();

                                bw.flush();
                                bw.close();

                                charakter.createNewFile();
                            }catch (Exception e) {
                                if(e.getMessage().equals("Es wurden zu viele Punkte verteilt") || e.getMessage().equals("Ungültiger Name")){
                                    Toast.makeText(neuerCharakter.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(neuerCharakter.this, "Es ist ein Fehler beim erstellen des Charakters aufgetreten", Toast.LENGTH_LONG).show();
                                }
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
