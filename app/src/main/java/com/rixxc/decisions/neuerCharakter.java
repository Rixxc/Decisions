package com.rixxc.decisions;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

public class neuerCharakter extends AppCompatActivity {

    private Button create;
    private EditText name,stärke,ausdauer,intelligenz,geschicklichkeit,mut;
    private TextView punkte;
    private File obb;
    private SQLiteDatabase db;
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
    private View.OnTouchListener onTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
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

        db = openOrCreateDatabase("Decisions.db", MODE_PRIVATE, null);

        obb = new File(getObbDir(), "Charaktere");
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

        stärke.setOnTouchListener(onTouch);
        ausdauer.setOnTouchListener(onTouch);
        mut.setOnTouchListener(onTouch);
        intelligenz.setOnTouchListener(onTouch);
        geschicklichkeit.setOnTouchListener(onTouch);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.createCharakter:
                        Intent intent = new Intent(neuerCharakter.this, CharakterPicture.class);

                        String[] args = {name.getText().toString()};
                        if(db.rawQuery("SELECT * FROM charakter WHERE name=?",args).getCount() == 0){
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
                                if(name.getText().toString().equalsIgnoreCase("") || name.getText().toString() == null || name.getText().toString().contains(" ")){
                                    throw new Exception("Ungültiger Name");
                                }
                                if(istärke < 10 || imut < 10 || iausdauer < 10 || iintelligenz < 10 || igeschicklichkeit < 10){
                                    throw new Exception("Kein Wert darf unter 10 liegen");
                                }

                                intent.putExtra("name", name.getText().toString());
                                intent.putExtra("stärke", istärke);
                                intent.putExtra("ausdauer", iausdauer);
                                intent.putExtra("mut", imut);
                                intent.putExtra("intelligenz", iintelligenz);
                                intent.putExtra("geschicklichkeit", igeschicklichkeit);
                                db.close();
                                startActivity(intent);

                            }catch (Exception e) {
                                if(e.getMessage().equals("Es wurden zu viele Punkte verteilt") || e.getMessage().equals("Ungültiger Name") || e.getMessage().equals("Kein Wert darf unter 10 liegen")){
                                    Toast.makeText(neuerCharakter.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(neuerCharakter.this, "Es ist ein Fehler beim erstellen des Charakters aufgetreten", Toast.LENGTH_LONG).show();
                                }
                            }

                        }else{
                            Toast.makeText(neuerCharakter.this, "Dieser Name ist bereits vergeben", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
    }
}
