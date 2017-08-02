package com.rixxc.decisions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class CharakterInfo extends AppCompatActivity {

    String charakterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charakter_info);
        charakterName = getIntent().getExtras().getString("Charakter");
        setTitle(charakterName);

        Toast.makeText(CharakterInfo.this, charakterName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(CharakterInfo.this, Charaktere.class);
        startActivity(intent);
    }
}
