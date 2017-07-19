package com.rixxc.decisions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EndScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
    }
    //Zurückknopf überschreiben
    @Override
    public void onBackPressed() {
        Intent home = new Intent(EndScreen.this, MainActivity.class);
        startActivity(home);
    }
}
