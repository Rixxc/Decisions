package com.rixxc.decisions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;

public class CharakterInfo extends AppCompatActivity {

    private String charakterName;
    private File obb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charakter_info);
        charakterName = getIntent().getExtras().getString("Charakter");
        setTitle(charakterName);

        obb = new File(getObbDir(), "Charactere");
        Toast.makeText(CharakterInfo.this, charakterName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(CharakterInfo.this, Charaktere.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.löschen:
                File[] files = obb.listFiles();
                for(File f : files){
                    if(f.getName().equals(charakterName + ".chr")){
                        try {
                            f.delete();
                            break;
                        }catch (Exception e){
                            Toast.makeText(CharakterInfo.this, "Es ist ein Fehler beim löschen des Charakters aufgetreten", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                Intent intent = new Intent(CharakterInfo.this, Charaktere.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
