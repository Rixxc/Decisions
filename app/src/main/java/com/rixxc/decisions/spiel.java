package com.rixxc.decisions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class spiel extends AppCompatActivity {

    final private File SDIALOG = MainActivity.Dialog;
    final private boolean NEUESSPIEL = MainActivity.neuesSpiel;
    final private MediaPlayer mediaPlayer = MainActivity.mediaPlayer;
    private String name;
    private int stärke,ausdauer,intelligenz,geschicklichkeit,mut;
    private int eip;
    private TextView DialogAusgabe;
    private Button eingabe1,eingabe2,eingabe3;
    private int weiter1,weiter2,weiter3;
    private RelativeLayout rl;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences SETTINGS;
    private SharedPreferences.Editor EDITSETTINGS;
    private SharedPreferences keys;
    private SharedPreferences.Editor editkeys;
    private SQLiteDatabase db;
    private ArrayList<Abschnitt> Abschnitte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = openOrCreateDatabase("Decisions.db", MODE_PRIVATE, null);

        name = getIntent().getStringExtra("Charakter");
        Log.e("name",name);

        String[] column = {"stärke","ausdauer","intelligenz","geschicklichkeit","mut"};
        String[] args = {name};
        Cursor result = db.query("Charakter", column,"name=?",args,null,null,null);
        result.moveToFirst();
        stärke = result.getInt(0);
        ausdauer = result.getInt(1);
        intelligenz = result.getInt(2);
        geschicklichkeit = result.getInt(3);
        mut = result.getInt(4);

        //SharedPreferences, für saving, initialisieren
        sharedPreferences = getSharedPreferences("Gamesave", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Initialisiere SharedPreferences settings
        SETTINGS = getSharedPreferences("settings", MODE_PRIVATE);
        EDITSETTINGS = SETTINGS.edit();

        //Setze Savegame zurück, falls neues Spiel ausgewählt wurde
        if (NEUESSPIEL) {
            String[] args2 = {name,SDIALOG.getName()};
            Cursor r = db.rawQuery("SELECT * FROM savepoints WHERE charakter=? AND abenteuer=?", args2);
            if(r.getCount() == 1){
                db.execSQL("UPDATE savepoints SET eip=1 WHERE charakter='" + name + "' AND abenteuer='" + SDIALOG.getName() + "'");
            }
        }

        String[] args2 = {name,SDIALOG.getName()};
        Cursor r = db.rawQuery("SELECT * FROM savepoints WHERE charakter=? AND abenteuer=?", args2);
        if(r.getCount() == 0){
            eip = 1;
        }else if(r.getCount() == 1){
            r.moveToFirst();
            eip = r.getInt(3);
        }else{
            Toast.makeText(spiel.this, "Invalide Speicherstände",  Toast.LENGTH_LONG).show();
            Intent intent = new Intent(spiel.this, MainActivity.class);
            finish();
            startActivity(intent);
        }


        //Entscheidet ob Activity im Landscape oder Portrait Mode läuft
        if (SETTINGS.getString("Orientierung", "portrait").equals("portrait")) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_spiel_portrait);

            //Werte werden initialisiert
            rl = (RelativeLayout) findViewById(R.id.spiel_portrait);
            DialogAusgabe = (TextView) findViewById(R.id.Dialog);
            eingabe1 = (Button) findViewById(R.id.eingabe1);
            eingabe2 = (Button) findViewById(R.id.eingabe2);
            eingabe3 = (Button) findViewById(R.id.eingabe3);

            //Bindet Gestenerkennung an RelativLayout
            rl.setOnTouchListener(new View.OnTouchListener() {
                int downX, upX, downY, upY;
                @Override
                public boolean onTouch(View v, MotionEvent event){
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        downX = (int) event.getX();
                        downY = (int) event.getY();
                        Log.i("event.getX()", " downX " + downX);
                        return true;
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        upX = (int) event.getX();
                        upY = (int) event.getY();
                        Log.i("event.getX()", " upX " + downX);
                        Log.e("upX-downX",upX - downX + "");
                        Log.e("downX-upX",downX - upX + "");
                        Log.e("upY-downY",upY - downY + "");
                        Log.e("downY-upY",downY - upY + "");
                        if(upX - downX > 200 && upY - downY < 100 && upY - downY > -100) {
                            // swipe right
                            if (weiter1 != -1){
                                eip = weiter1;
                                gebeDialog(eip);
                            }
                        }
                        else if(downX - upX > 200 && upY - downY < 100 && upY - downY > -100) {
                            // swipe left
                            if (weiter3 != -1){
                                eip = weiter3;
                                gebeDialog(eip);
                            }
                        }
                        else if(downY - upY > 200 && Math.abs(upX - downX) < 100 ){
                            //swipe up
                            if (weiter2 != -1){
                                eip = weiter2;
                                gebeDialog(eip);
                            }
                        }
                        return true;

                    }
                    return false;
                }
            });

            spielInitalisieren();

            //Lock sensor
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_spiel_landscape);

            //Werte werden initialisiert
            DialogAusgabe = (TextView) findViewById(R.id.lDialog);
            rl = (RelativeLayout) findViewById(R.id.spiel_landscape);
            eingabe1 = (Button) findViewById(R.id.leingabe1);
            eingabe2 = (Button) findViewById(R.id.leingabe2);
            eingabe3 = (Button) findViewById(R.id.leingabe3);

            //Bindet Gestenerkennung an RelativLayout
            rl.setOnTouchListener(new View.OnTouchListener() {
                int downX, upX, downY, upY;
                @Override
                public boolean onTouch(View v, MotionEvent event){
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        downX = (int) event.getX();
                        downY = (int) event.getY();
                        Log.i("event.getX()", " downX " + downX);
                        return true;
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        upX = (int) event.getX();
                        upY = (int) event.getY();
                        Log.i("event.getX()", " upX " + downX);
                        if(upX - downX > 200 && upY - downY < 100 && upY - downY > -100) {
                            // swipe right
                            if (weiter1 != -1){
                                eip = weiter1;
                                gebeDialog(eip);
                            }
                        }
                        else if(downX - upX > 200 && upY - downY < 100 && upY - downY > -100) {
                            // swipe left
                            if (weiter3 != -1){
                                eip = weiter3;
                                gebeDialog(eip);
                            }
                        }
                        else if(downY - upY > 200 && Math.abs(upX - downX) < 100 ){
                            //swipe up
                            if (weiter2 != -1){
                                eip = weiter2;
                                gebeDialog(eip);
                            }
                        }
                        return true;

                    }
                    return false;
                }
            });

            spielInitalisieren();

            //Lock sensor
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }
    //Prüft welcher Button gedrückt wurde und gibt den entsprechenden Dialog aus
    public void OnClick(View v){
        switch (v.getId()) {
            case R.id.eingabe1:
                if (weiter1 == -1){
                    return;
                }
                eip = weiter1;
                gebeDialog(eip);
                break;
            case R.id.eingabe2:
                if (weiter2 == -1){
                    ende();
                    return;
                }
                eip = weiter2;
                gebeDialog(eip);
                break;
            case R.id.eingabe3:
                if (weiter3 == -1){
                    return;
                }
                eip = weiter3;
                gebeDialog(eip);
                break;
            case R.id.leingabe1:
                if (weiter1 == -1){
                    return;
                }
                eip = weiter1;
                gebeDialog(eip);
                break;
            case R.id.leingabe2:
                if (weiter2 == -1){
                    ende();
                    return;
                }
                eip = weiter2;
                gebeDialog(eip);
                break;
            case R.id.leingabe3:
                if (weiter3 == -1){
                    return;
                }
                eip = weiter3;
                gebeDialog(eip);
                break;
        }
    }
    //Zurückknopf überschreiben
    @Override
    public void onBackPressed(){
        Toast.makeText(spiel.this, "Speichern...", Toast.LENGTH_SHORT).show();
        if (!speichern()){
            Toast.makeText(spiel.this, "Fehler beim Speichern", Toast.LENGTH_LONG).show();
        }
        super.onBackPressed();
    }
    @Override
    public void onPause(){
        Toast.makeText(spiel.this, "Speichern...", Toast.LENGTH_SHORT).show();
        if (!speichern()){
            Toast.makeText(spiel.this, "Fehler beim Speichern", Toast.LENGTH_LONG).show();
        }

        mediaPlayer.pause();
        super.onPause();
    }
    @Override
    public void onResume(){
        if(!mediaPlayer.isPlaying() && SETTINGS.getBoolean("Musik", true)){
            mediaPlayer.start();
        }
        super.onResume();
    }
    //Hier wird geprüft, ob es sich bei der Abenteuerdate um eine valide Datei handelt
    public void spielInitalisieren(){
        XmlPullParserFactory pullParserFactory;
        try{
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = new FileInputStream(SDIALOG);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            this.Abschnitte = parseXML(parser);

            gebeDialog(eip);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //Gebe den Dialog mit der entsprechenden ID aus
    public void gebeDialog(int pEip){
        Abschnitt currentAbschnitt = null;
        for(Abschnitt a : Abschnitte){
            if(a.id == pEip){
                currentAbschnitt = a;
                break;
            }
        }
        if(currentAbschnitt == null){
            Toast.makeText(spiel.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG);
            Intent intent = new Intent(spiel.this, MainActivity.class);
            finish();
            startActivity(intent);
        }

        DialogAusgabe.setText(currentAbschnitt.Text);

        switch(currentAbschnitt.Buttons.length){
            case 1:
                weiter1 = currentAbschnitt.weiter[0];
                weiter2 = -1;
                weiter3 = -1;
                eingabe1.setText(currentAbschnitt.Buttons[0]);
                eingabe1.setVisibility(Button.VISIBLE);
                eingabe2.setVisibility(Button.INVISIBLE);
                eingabe3.setVisibility(Button.INVISIBLE);
                break;
            case 2:
                weiter1 = currentAbschnitt.weiter[0];
                weiter2 = currentAbschnitt.weiter[1];
                weiter3 = -1;
                eingabe1.setText(currentAbschnitt.Buttons[0]);
                eingabe2.setText(currentAbschnitt.Buttons[1]);
                eingabe1.setVisibility(Button.VISIBLE);
                eingabe2.setVisibility(Button.VISIBLE);
                eingabe3.setVisibility(Button.INVISIBLE);
                break;
            case 3:
                weiter1 = currentAbschnitt.weiter[0];
                weiter2 = currentAbschnitt.weiter[1];
                weiter3 = currentAbschnitt.weiter[2];
                eingabe1.setText(currentAbschnitt.Buttons[0]);
                eingabe2.setText(currentAbschnitt.Buttons[1]);
                eingabe3.setText(currentAbschnitt.Buttons[2]);
                eingabe1.setVisibility(Button.VISIBLE);
                eingabe2.setVisibility(Button.VISIBLE);
                eingabe3.setVisibility(Button.VISIBLE);
                break;
            default:
                Toast.makeText(spiel.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(spiel.this, MainActivity.class);
                finish();
                startActivity(intent);
                break;
        }


    }
    //Speichert die aktuelle Dialog ID
    public boolean speichern(){
        try{
            String[] args = {name,SDIALOG.getName()};
            Cursor r = db.rawQuery("SELECT * FROM savepoints WHERE charakter=? AND abenteuer=?", args);
            if(r.getCount() == 1){
                db.execSQL("UPDATE savepoints SET eip=" + eip + " WHERE charakter='" + name + "' AND abenteuer='" + SDIALOG.getName() + "'");
            }else if(r.getCount() == 0){
                db.execSQL("INSERT INTO savepoints (abenteuer, charakter, eip) VALUES ('" + SDIALOG.getName() + "','" + name + "','" + eip + "')");
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public void ende(){
        speichern();
        Intent end = new Intent(spiel.this, EndScreen.class);
        finish();
        startActivity(end);
    }
    public int checkRequest(String pValue){
        Log.d("Request", pValue);
        String[] Values = pValue.split(",");
        int erg = -1;

        try{
           erg = Integer.parseInt(Values[1]);
        }catch(Exception e){
            Toast.makeText(spiel.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
            Intent back = new Intent(spiel.this, MainActivity.class);
            finish();
            startActivity(back);
        }

        if(keys.getBoolean(Values[0], false)){
            return erg;
        }else{
            return -1;
        }
    }
    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private ArrayList<Abschnitt> parseXML(XmlPullParser parser) throws XmlPullParserException,IOException {
        ArrayList<Abschnitt> Abschnitte = null;
        int eventType = parser.getEventType();
        Abschnitt currentAbschnitt = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    Abschnitte = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Abschnitt")) {
                        currentAbschnitt = new Abschnitt();
                    } else if (currentAbschnitt != null) {
                        if (name.equalsIgnoreCase("ID")) {
                            currentAbschnitt.id = Integer.parseInt(parser.nextText());
                        } else if (name.equalsIgnoreCase("Text")) {
                            currentAbschnitt.Text = parser.nextText();
                        } else if (name.equalsIgnoreCase("Buttons")) {
                            currentAbschnitt.Buttons = parser.nextText().split(";");
                        } else if (name.equalsIgnoreCase("IDS")) {
                            int[] ids;
                            String result = parser.nextText();
                            String[] results = result.split(";");
                            ids = new int[results.length];
                            for (int i = 0; i < results.length; i++) {
                                ids[i] = Integer.parseInt(results[i]);
                            }
                            currentAbschnitt.weiter = ids;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Abschnitt") && currentAbschnitt != null) {
                        Abschnitte.add(currentAbschnitt);
                    } else if (name.equalsIgnoreCase("Decisions")){
                        return Abschnitte;
                    }
            }
            eventType = parser.next();
        }
        return Abschnitte;
    }

}
