package com.rixxc.decisions;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import java.io.File;
import java.io.FileFilter;

public class Einstellungen extends AppCompatActivity {

    private SharedPreferences settings;
    private MediaPlayer mediaPlayer = MainActivity.mediaPlayer;
    private SharedPreferences.Editor editsettings;
    private Switch toggle;
    private Switch music;
    private Spinner abenteuer,charakter;
    private File obb;
    private File[] Files;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);

        setTitle("Einstellungen");

        db = openOrCreateDatabase("Charakter.db", MODE_PRIVATE, null);
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        editsettings = settings.edit();

        toggle = (Switch) findViewById(R.id.LandscapeMode);
        music = (Switch) findViewById(R.id.music);

        if(settings.getString("Orientierung", "portrait").equals("portrait")){
            toggle.setChecked(false);
        }else{
            toggle.setChecked(true);
        }
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editsettings.putString("Orientierung", "landscape");
                } else {
                    editsettings.putString("Orientierung", "portrait");
                }
                editsettings.commit();
            }
        });

        if(settings.getBoolean("Musik", true)){
            music.setChecked(true);
        }else{
            music.setChecked(false);
        }
        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editsettings.putBoolean("Musik", true);
                    try {
                        AssetFileDescriptor assetFileDescriptor = getAssets().openFd("mysteriös.mp3");

                        mediaPlayer = new MediaPlayer();
                        MainActivity.mediaPlayer = mediaPlayer;

                        mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
                        mediaPlayer.prepare();
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    editsettings.putBoolean("Musik", false);
                    mediaPlayer.stop();
                }
                editsettings.commit();
            }
        });

        obb = new File(getObbDir(), "Abenteuer");
        if (!obb.exists()){
            obb.mkdirs();
        }

        // Array of choices
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".adv");
            }
        };
        Files = obb.listFiles(ff);

        String[] FileNamen = new String[Files.length];
        for (int i = 0; i < Files.length; i++){
            FileNamen[i] = Files[i].getName().substring(0, Files[i].getName().length() - 4);
        }


        String[] name = {"name"};
        Cursor result = db.query("Charakter",name,null,null,null,null,null);
        result.moveToFirst();
        String[] CharakterNamen = new String[result.getCount()+1];
        CharakterNamen[0] = "Kein Charakter";
        for (int i = 0; i < result.getCount(); i++){
            CharakterNamen[i+1] = result.getString(0);
            result.moveToNext();
        }

        // Selection of the spinner
        abenteuer = (Spinner) findViewById(R.id.Abenteuerwahl);
        charakter = (Spinner) findViewById(R.id.Charakterauswahl);


        //Apply the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, FileNamen);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        abenteuer.setAdapter(spinnerArrayAdapter);
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, CharakterNamen);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        charakter.setAdapter(spinnerArrayAdapter2);

        FileNamen = new String[Files.length];
        for (int i = 0; i < Files.length; i++){
            FileNamen[i] = Files[i].getName();
        }

        int Auswahl = 0;
        for(int i = 0; i < FileNamen.length; i++){
            if(FileNamen[i].equals(settings.getString("Abenteuer", "Decision.adv"))){
                Auswahl = i;
            }
        }

        abenteuer.setSelection(Auswahl);

        int Auswahl2 = 0;
        for(int i = 0; i < CharakterNamen.length; i++){
            if(CharakterNamen[i].equals(settings.getString("Charakter", "Kein Charakter"))){
                Auswahl2 = i;
            }
        }
        Log.e("Auswahl", settings.getString("Charakter", "Kein Charakter"));
        charakter.setSelection(Auswahl2);

        if(!mediaPlayer.isPlaying() && settings.getBoolean("Musik", true)){
            mediaPlayer.start();
        }
    }
    @Override
    public void onPause() {
        editsettings.putString("Abenteuer", abenteuer.getSelectedItem().toString() + ".adv");
        editsettings.putString("Charakter", charakter.getSelectedItem().toString());
        editsettings.commit();
        mediaPlayer.pause();

        super.onPause();
    }
    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
