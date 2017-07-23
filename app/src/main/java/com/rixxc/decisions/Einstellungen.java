package com.rixxc.decisions;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileFilter;

public class Einstellungen extends AppCompatActivity {

    private SharedPreferences settings;
    private MediaPlayer mediaPlayer = MainActivity.mediaPlayer;
    private SharedPreferences.Editor editsettings;
    private Switch toggle;
    private Switch music;
    private Spinner spinner;
    private File obb;
    private File[] Files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);
        final Context test = getApplicationContext();

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
            FileNamen[i] = Files[i].getName();
        }

        // Selection of the spinner
        spinner = (Spinner) findViewById(R.id.Abenteuerwahl);


        //Apply the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, FileNamen);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);

        int Auswahl = 0;
        for(int i = 0; i < FileNamen.length; i++){
            if(FileNamen[i].equals(settings.getString("Abenteuer", "Decision.adv"))){
                Auswahl = i;
            }
        }

        spinner.setSelection(Auswahl);

        if(!mediaPlayer.isPlaying() && settings.getBoolean("Musik", true)){
            mediaPlayer.start();
        }
    }
    @Override
    public void onPause() {
        editsettings.putString("Abenteuer", spinner.getSelectedItem().toString());
        editsettings.commit();
        mediaPlayer.pause();

        super.onPause();
    }
}
