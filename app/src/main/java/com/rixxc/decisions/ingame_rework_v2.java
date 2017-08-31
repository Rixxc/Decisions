package com.rixxc.decisions;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Marvin on 30.08.2017.
 */

public class ingame_rework_v2 extends AppCompatActivity {
   ImageButton act;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingame_rework_v2);
        act = (ImageButton) findViewById(R.id.Actbtn);
        act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.animate().translationY(-50).translationX(-20).scaleX(-15).scaleY(-15);
            }

        });

    }
}
