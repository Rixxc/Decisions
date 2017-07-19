package com.rixxc.decisions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileFilter;

public class Einstellungen extends AppCompatActivity {

    private SharedPreferences settings;
    private SharedPreferences.Editor editsettings;
    private ToggleButton toggle;
    private Spinner spinner;
    private File obb;
    private File[] Files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);

        settings = getSharedPreferences("settings", MODE_PRIVATE);
        editsettings = settings.edit();

        toggle = (ToggleButton) findViewById(R.id.LandscapeMode);

        if(settings.getString("Orientierung", "portrait").equals("portrait")){
            toggle.setChecked(false);
        }else{
            toggle.setChecked(true);
        }
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editsettings.putString("Orientierung", "landscape");
                    editsettings.commit();
                } else {
                    editsettings.putString("Orientierung", "portrait");
                    editsettings.commit();
                }
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
    }
    @Override
    public void onBackPressed(){
        editsettings.putString("Abenteuer", spinner.getSelectedItem().toString());
        editsettings.commit();
        super.onBackPressed();
    }
}
