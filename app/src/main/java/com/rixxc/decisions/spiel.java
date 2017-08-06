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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class spiel extends AppCompatActivity {

    final private File SDIALOG = MainActivity.Dialog;
    final private String SAVEKEY = SDIALOG.getName();
    final private String SHKEY = SAVEKEY + "key";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = openOrCreateDatabase("Charakter.db", MODE_PRIVATE, null);

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

        //Initialisiere SharedPreferences keys
        keys = getSharedPreferences(SHKEY, MODE_PRIVATE);
        editkeys = keys.edit();

        //Setze Savegame zurück, falls neues Spiel ausgewählt wurde
        if (NEUESSPIEL) {
            editor.remove(SAVEKEY).commit();
            editkeys.clear().commit();
        }


        //Entscheidet ob Activity im Landscape oder Portrait Mode läuft
        if (SETTINGS.getString("Orientierung", "portrait").equals("portrait")) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_spiel_portrait);

            //Werte werden initialisiert
            eip = sharedPreferences.getInt(SAVEKEY, 1);
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
            eip = sharedPreferences.getInt(SAVEKEY, 1);
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
        try
        {
            BufferedReader buffreader = new BufferedReader(new FileReader(SDIALOG));
            String line = buffreader.readLine();

            Log.d("Datei", line);

            //Prüfe, ob die Datei mit START anfängt
            if (!line.equals("START")){
                //Wenn nicht, dann gehe zum Menü
                Toast.makeText(spiel.this, "Falsche Datei", Toast.LENGTH_LONG).show();
                Intent back = new Intent(spiel.this, MainActivity.class);
                finish();
                startActivity(back);
            }else{
                //Gebe den ersten Dialog aus
                //Toast.makeText(spiel.this, "Alles OK (Debug)", Toast.LENGTH_LONG).show();
                gebeDialog(eip);
            }
        }
        //Bei alles Fehlern: Zurück zum Menü und Toast ausgeben
        catch (FileNotFoundException e){
            Log.e("File1", e + "");
            Toast.makeText(spiel.this, "Datei nicht gefunden", Toast.LENGTH_LONG).show();
            Intent back = new Intent(spiel.this, MainActivity.class);
            finish();
            startActivity(back);
        }
        catch (Exception e) {
            Log.e("File2", e + "");
            Toast.makeText(spiel.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
            Intent back = new Intent(spiel.this, MainActivity.class);
            finish();
            startActivity(back);
        }
    }
    //Gebe den Dialog mit der entsprechenden ID aus
    public void gebeDialog(int pEip){
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(SDIALOG));
            String text = br.readLine();

            //Gehe soweit, bis die richtige ID gefunden ist
            while(!text.startsWith(pEip + ":")){
                text = br.readLine();
            }

            //Trenne den Dialog bei einem Doppelpunkt, um die ID am Anfang wegzubekommen
            String[] Ausgabe = text.split(":");
            if (!(text.length() > 2)){
                //Wenn Länge des Arrays == 2, dann gebe den Dialog aus (Ausgabe[1]). Vorher Abfrage, ob Key requested wird
                if(Ausgabe[1].startsWith("REQUEST")){
                    DialogAusgabe.setText("");
                    int next;
                    if((next = checkRequest(Ausgabe[1].substring(9))) != -1){
                        gebeDialog(next);
                        return;
                    }else{
                        Ausgabe[1] = null;
                    }
                }
                if(Ausgabe[1] != null){
                    DialogAusgabe.setText(Ausgabe[1] + "\n");
                }
            }else{
                DialogAusgabe.setText("");
                if(Ausgabe[1].startsWith("REQUEST")){
                    int next;
                    if((next = checkRequest(Ausgabe[1].substring(8))) != -1){
                        gebeDialog(next);
                        return;
                    }else{
                        Ausgabe[1] = null;
                    }
                }
                if(Ausgabe[1] != null) {
                    //Wenn mehr, dann gebe alle Fragmente hintereinander aus
                    DialogAusgabe.setText(Ausgabe[1]);
                    for (int i = 2; i < Ausgabe.length; i++) {
                        DialogAusgabe.append(":");
                        DialogAusgabe.append(Ausgabe[i]);
                    }
                    DialogAusgabe.append("\n");
                }
            }
            //Titel der Knöpfe setzen und die ID für die weiteren Dialoge setzen
            text = br.readLine();
            while(!text.contains("BUTTON") && !text.contains("SAVE")){
                DialogAusgabe.append(text + "\n");
                text = br.readLine();
            }
            if(text.startsWith("SAVE")){
                text = text.substring(5);
                editkeys.putBoolean(text, true);
                editkeys.commit();
                text = br.readLine();
            }
            //Prüfen, ob ein Ende erreicht wurde
            text = text.substring(7);
            if(text.equals("ENDE")){
                weiter1 = -1;
                weiter2 = -1;
                weiter3 = -1;
                eingabe1.setVisibility(Button.INVISIBLE);
                eingabe3.setVisibility(Button.INVISIBLE);
                eingabe2.setText("ENDE");
                return;
            }
            if(text.startsWith("WEITER")){
                eingabe1.setVisibility(Button.INVISIBLE);
                eingabe3.setVisibility(Button.INVISIBLE);
                eingabe2.setVisibility(Button.VISIBLE);
                eingabe2.setText("Weiter");
                text = text.substring(7);
                weiter1 = -1;
                weiter3 = -1;
                try {
                    weiter2 = Integer.parseInt(text);
                }catch(Exception e) {
                    Log.e("IntPars", e + "");
                    Toast.makeText(spiel.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
                    Intent back = new Intent(spiel.this, MainActivity.class);
                    startActivity(back);
                }
                return;
            }
            String[] Buttons = text.split(",");
            eingabe1.setVisibility(Button.VISIBLE);
            eingabe3.setVisibility(Button.VISIBLE);
            text = br.readLine();
            text = text.substring(9);
            String[] SWeiter = text.split(",");
            if (Buttons.length == 2){
                eingabe1.setText(Buttons[0]);
                eingabe2.setVisibility(Button.INVISIBLE);
                eingabe3.setText(Buttons[1]);
                weiter2 = -1;
                try{
                    weiter1 = Integer.parseInt(SWeiter[0]);
                    weiter3 = Integer.parseInt(SWeiter[1]);
                }catch (Exception e){
                    Log.e("IntPars", e + "");
                    Toast.makeText(spiel.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
                    Intent back = new Intent(spiel.this, MainActivity.class);
                    startActivity(back);
                }
            }
            if (Buttons.length == 3){
                eingabe1.setText(Buttons[0]);
                eingabe2.setText(Buttons[1]);
                eingabe3.setText(Buttons[2]);
                try{
                    weiter1 = Integer.parseInt(SWeiter[0]);
                    weiter2 = Integer.parseInt(SWeiter[1]);
                    weiter3 = Integer.parseInt(SWeiter[2]);
                }catch (Exception e){
                    Log.e("IntPars", e + "");
                    Toast.makeText(spiel.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
                    Intent back = new Intent(spiel.this, MainActivity.class);
                    finish();
                    startActivity(back);
                }
            }
        }
        catch (FileNotFoundException e){
            Log.e("File3", e + "");
            Toast.makeText(spiel.this, "Datei nicht gefunden", Toast.LENGTH_LONG).show();
            Intent back = new Intent(spiel.this, MainActivity.class);
            finish();
            startActivity(back);
        }
        catch (Exception e)
        {
            Log.e("File4", e + "");
            Toast.makeText(spiel.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
            Intent back = new Intent(spiel.this, MainActivity.class);
            finish();
            startActivity(back);
        }
    }
    //Speichert die aktuelle Dialog ID
    public boolean speichern(){
        try{
            int x = sharedPreferences.getInt(SAVEKEY, -1);
            if (x != -1){
                editor.remove(SAVEKEY);
                editor.putInt(SAVEKEY, eip);
                editor.commit();
            }else{
                editor.putInt(SAVEKEY, eip);
                editor.commit();
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
}
