package com.rixxc.decisions;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
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
    private File obb,obb2;
    private File[] Files,Files2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);

        setTitle("Einstellungen");

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
        obb2 = new File(getObbDir(), "Charaktere");
        if (!obb2.exists()){
            obb2.mkdirs();
        }

        // Array of choices
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".adv");
            }
        };
        Files = obb.listFiles(ff);
        FileFilter ff2 = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".chr");
            }
        };
        Files2 = obb2.listFiles(ff2);

        String[] FileNamen = new String[Files.length];
        for (int i = 0; i < Files.length; i++){
            FileNamen[i] = Files[i].getName().substring(0, Files[i].getName().length() - 4);
        }
        String[] FileNamen2 = new String[Files2.length+1];
        FileNamen2[0] = "Kein Charakter";
        for (int i = 0; i < Files2.length; i++){
            FileNamen2[i+1] = Files2[i].getName().substring(0,Files2[i].getName().length() - 4);
        }

        // Selection of the spinner
        abenteuer = (Spinner) findViewById(R.id.Abenteuerwahl);
        charakter = (Spinner) findViewById(R.id.Charakterauswahl);


        //Apply the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, FileNamen);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        abenteuer.setAdapter(spinnerArrayAdapter);
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, FileNamen2);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        charakter.setAdapter(spinnerArrayAdapter2);

        FileNamen = new String[Files.length];
        for (int i = 0; i < Files.length; i++){
            FileNamen[i] = Files[i].getName();
        }
        FileNamen2 = new String[Files2.length+1];
        FileNamen2[0] = "Kein Charakter";
        for (int i = 0; i < Files2.length; i++){
            FileNamen2[i+1] = Files2[i].getName();
        }

        int Auswahl = 0;
        for(int i = 0; i < FileNamen.length; i++){
            if(FileNamen[i].equals(settings.getString("Abenteuer", "Decision.adv"))){
                Auswahl = i;
            }
        }

        abenteuer.setSelection(Auswahl);

        int Auswahl2 = 0;
        for(int i = 0; i < FileNamen2.length; i++){
            if(FileNamen2[i].equals(settings.getString("Charakter", "Kein Charakter"))){
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
        editsettings.putString("Charakter", charakter.getSelectedItem().toString() + ".chr");
        editsettings.commit();
        mediaPlayer.pause();

        super.onPause();
    }
}
