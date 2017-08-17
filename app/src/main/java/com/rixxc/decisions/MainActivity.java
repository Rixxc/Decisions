package com.rixxc.decisions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private final String DOWNLOADURL = "https://dl.dropboxusercontent.com/s/4b1rx3e15h35ybg/Decision.txt";

    public static File Dialog;
    public static boolean neuesSpiel;
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private ViewGroup mViewGroupe;
    private Button starten,neu,history,charaktere,einstellungen;
    private File obb;
    private File[] Files;
    private ProgressDialog dialog;
    private Thread down;
    private SharedPreferences settings;
    private GestureDetector gestureScanner;
    private int debug = 1;
    private Thread playMusic;
    private boolean pressedOnece;
    private SQLiteDatabase db;
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            if(v.getId() == R.id.start){
                starten(false);
            }
            if(v.getId() == R.id.neuesSpiel){
                if(!pressedOnece){
                    Toast.makeText(MainActivity.this, "Überschreibt alle Fortschritte zu diesem Abenteuer", Toast.LENGTH_LONG).show();
                    neu.setText("erneut drücken");
                    pressedOnece = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pressedOnece = false;
                            neu.setText("Neues Spiel");
                        }
                    }, 3000);
                }else {
                    starten(true);
                }
            }
            if(v.getId() == R.id.settings){
                Intent settings = new Intent(MainActivity.this, Einstellungen.class);
                startActivity(settings);
            }
            if(v.getId() == R.id.history){
            /*
            final Rect viewRect = new Rect();
            history.getGlobalVisibleRect(viewRect);

            Transition explode = new Explode();
            explode.setEpicenterCallback(new Transition.EpicenterCallback() {
                @Override
                public Rect onGetEpicenter(Transition transition) {
                    return viewRect;
                }
            });
            explode.setDuration(100);
            TransitionManager.beginDelayedTransition(mViewGroupe, explode);

            mViewGroupe.removeAllViews();
            */
            }
            if(v.getId() == R.id.charaktere){
                Intent charaktere = new Intent(MainActivity.this, Charaktere.class);
                startActivity(charaktere);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        starten = (Button) findViewById(R.id.start);
        neu = (Button) findViewById(R.id.neuesSpiel);
        history = (Button) findViewById(R.id.history);
        charaktere = (Button) findViewById(R.id.charaktere);
        einstellungen = (Button) findViewById(R.id.settings);
        mViewGroupe = (ViewGroup) findViewById(R.id.activity_main);
        pressedOnece =false;
        neu.setText("Neues Spiel");

        neu.setOnClickListener(onClickListener);
        starten.setOnClickListener(onClickListener);
        history.setOnClickListener(onClickListener);
        charaktere.setOnClickListener(onClickListener);
        einstellungen.setOnClickListener(onClickListener);

        //Initialisiere settings
        settings = getSharedPreferences("settings", MODE_PRIVATE);

        db = openOrCreateDatabase("Decisions.db", MODE_PRIVATE, null);
        try{
            db.rawQuery("SELECT * FROM charakter",null).getCount();
        }catch(SQLiteException e){
            db.execSQL("CREATE TABLE charakter (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, stärke INTEGER, ausdauer INTEGER, intelligenz INTEGER, geschicklichkeit INTEGER, mut INTEGER, punkte INTEGER, avatar Text)");
        }
        try{
            db.rawQuery("SELECT * FROM savepoints", null).getCount();
        }catch(SQLiteException e){
            db.execSQL("CREATE TABLE savepoints (id INTEGER PRIMARY KEY AUTOINCREMENT, abenteuer TEXT, charakter TEXT, eip INTEGER)");
        }


        obb = new File(getObbDir(), "Abenteuer");
        if (!obb.exists()){
            obb.mkdirs();
        }

        //Lädt das Standartabenteuer in den Abenteuerordner
        Abenteuereinbinden();

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

        /*
        //Wird ersmal nicht mehr benötigt, da das Standartabenteuer direkt in die App eingebunden wurden
        //Wird durch die Methode Abenteuereinbinden ersetzt
        boolean download = true;
        for (int i = 0; i < Files.length; i++){
            if(FileNamen[i].equals("Decision.adv")){
                download = false;
                break;
            }
        }
        if(download){
            downloaden();
        }
        */
        if(settings.getBoolean("Musik", true)) {
            try {
                AssetFileDescriptor assetFileDescriptor = getAssets().openFd("mysteriös.mp3");

                mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor());
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onResume(){
        if(!mediaPlayer.isPlaying() && settings.getBoolean("Musik", true)){
            mediaPlayer.start();
        }
        neu.setText("Neues Spiel");
        super.onResume();
    }
    @Override
    public void onPause(){
        mediaPlayer.pause();
        super.onPause();
    }
    //Läd die Datei aus dem assets Verzeichiss in den obb Ordner
    private void Abenteuereinbinden() {
        try {
            AssetManager am = getAssets();
            File temp = new File(obb, "Decision.adv");
            //WICHTIG: Datei muss mit UTF-8 ohne BOM kodiert werden, Notepad++
            InputStream in = am.open("Decision.txt");

            OutputStream out = new FileOutputStream(temp);

            byte[] buffer = new byte[1024];
            int len = in.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
            }

            in.close();
            out.flush();
            out.close();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Ein Fehler beim Abspielen des Soundtracks ist aufgetreten", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
    public void starten(boolean pNeuesSpiel){
        Dialog = null;
        String[] column = {"name"};
        String[] args = {settings.getString("Charakter", "Kein Charakter")};
        Cursor result = db.query("Charakter",column,"name=?",args,null,null,null);
        if(args[0] == "Kein Charakter" || result.getCount() == 0){
            Toast.makeText(MainActivity.this, "Kein Charakter ausgewählt", Toast.LENGTH_LONG).show();
            return;
        }
        for (int i = 0; i < Files.length; i++){
            if (Files[i].getName().equals(settings.getString("Abenteuer", "Decision.adv"))){
                Dialog = Files[i];
                break;
            }
        }
        neuesSpiel = pNeuesSpiel;
        if(Dialog != null){
            Intent starten = new Intent(MainActivity.this, spiel.class);
            starten.putExtra("Charakter", settings.getString("Charakter",null));
            startActivity(starten);
        }else{
            Toast.makeText(MainActivity.this, "Kein Abneteuer ausgewählt", Toast.LENGTH_LONG).show();
        }
    }
    //Initilisiert den download der Datei
    public void downloaden(){
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Standard Abenteuer wird heruntergeladen...");
        dialog.setCancelable(false);
        dialog.show();

        down = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadFile();
            }
        });
        down.start();
    }
    //Führt download der Datei aus
    //Wird erstmal nicht mehr benötigt, da das Standartabenteuer direkt in die App eingebunden wurde
    private void downloadFile() {
        try {
            Socket test = new Socket("www.google.com", 80);
            if (!test.isConnected()){
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Keine Internetverbindung", Toast.LENGTH_LONG).show();
                down.interrupt();
            }

            test.close();

            URL fileurl = new URL(DOWNLOADURL);
            URLConnection urlconnection = fileurl.openConnection();
            urlconnection.connect();

            InputStream inputstream = new BufferedInputStream(urlconnection.getInputStream(), 8192);

            File downloadedfile = new File(obb, "Decision.adv");

            FileWriter fr = new FileWriter(downloadedfile);
            BufferedWriter br = new BufferedWriter(fr);

            byte[] buffer = new byte[1024];
            int read;
            int x = 0;
            while ((read = inputstream.read(buffer)) != -1){
                String a = "";
                for (int i = 0; i < buffer.length; i++){
                    if(buffer[i] != -28 && buffer[i] != -4 && buffer[i] != -33 && buffer[i] != -10 && buffer[i] != -1) {
                        a += (char) buffer[i];
                    }
                    if(buffer[i] == -28){
                        a += 'ä';
                    }
                    if(buffer[i] == -4){
                        a += 'ü';
                    }
                    if(buffer[i] == -33){
                        a += 'ß';
                    }
                    if(buffer[i] == -10){
                        a += 'ö';
                    }
                }
                br.append(a);
                Log.d("String" + x, a);
                x++;
                for(int i = 0; i < buffer.length; i++){
                    buffer[i] = -1;
                }
            }

            br.close();
            fr.close();

            dialog.dismiss();

            inputstream.close();

            Intent restart = getIntent();
            finish();
            startActivity(restart);
        }catch (Exception e){
            down.interrupt();
            dialog.setCancelable(true);
            //Toast.makeText(MainActivity.this, "Ein Fehler ist aufgetreten", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
