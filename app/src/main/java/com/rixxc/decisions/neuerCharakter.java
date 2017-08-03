package com.rixxc.decisions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private TextView punkte;
    private File obb;
    private TextView.OnEditorActionListener onEdit = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            int istärke,iausdauer,imut,iintelligenz,igeschicklichkeit;
            istärke = Integer.parseInt(stärke.getText().toString());
            iausdauer = Integer.parseInt(ausdauer.getText().toString());
            imut = Integer.parseInt(mut.getText().toString());
            iintelligenz = Integer.parseInt(intelligenz.getText().toString());
            igeschicklichkeit = Integer.parseInt(geschicklichkeit.getText().toString());

            int sum = 15 - ((istärke+iausdauer+imut+iintelligenz+igeschicklichkeit) - 50);

            if(istärke < 10 || imut < 10 || iausdauer < 10 || iintelligenz < 10 || igeschicklichkeit < 10){
                punkte.setText("Keiner der Werte darf unter 10 liegen");
            }else if(sum < 0){
                punkte.setText("Es wurden zu viele Punkte ausgegeben");
            }else{
                punkte.setText("Verfügbare Punkte : " + sum);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neuer_charakter);
        setTitle("Charaktere");

        obb = new File(getObbDir(), "Charactere");
        punkte = (TextView) findViewById(R.id.LPunkte);
        name = (EditText) findViewById(R.id.Name);
        stärke = (EditText) findViewById(R.id.stärke);
        ausdauer = (EditText) findViewById(R.id.ausdauer);
        mut = (EditText) findViewById(R.id.mut);
        intelligenz = (EditText) findViewById(R.id.intelligenz);
        geschicklichkeit = (EditText) findViewById(R.id.geschicklichkeit);
        create = (Button) findViewById(R.id.createCharakter);

        stärke.setText("10");
        ausdauer.setText("10");
        mut.setText("10");
        intelligenz.setText("10");
        geschicklichkeit.setText("10");

        stärke.setOnEditorActionListener(onEdit);
        ausdauer.setOnEditorActionListener(onEdit);
        mut.setOnEditorActionListener(onEdit);
        intelligenz.setOnEditorActionListener(onEdit);
        geschicklichkeit.setOnEditorActionListener(onEdit);

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

                                if((istärke+iausdauer+imut+iintelligenz+igeschicklichkeit) > 65){
                                    throw new Exception("Es wurden zu viele Punkte verteilt");
                                }
                                if(name.getText().toString() == "" || name.getText().toString() == null || name.getText().toString().contains(" ")){
                                    throw new Exception("Ungültiger Name");
                                }
                                if(istärke < 10 || imut < 10 || iausdauer < 10 || iintelligenz < 10 || igeschicklichkeit < 10){
                                    throw new Exception("Kein Wert darf unter 10 liegen");
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
                                if(e.getMessage().equals("Es wurden zu viele Punkte verteilt") || e.getMessage().equals("Ungültiger Name") || e.getMessage().equals("Kein Wert darf unter 10 liegen")){
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
