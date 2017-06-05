package com.ears.advcomp.ears3;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "Darron";
    static final String EAR_CSV = "/ears.csv";

    Button addEar;
    Button findEar;
    File earCSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();

        addEar = (Button)findViewById(R.id.addEar);
        findEar = (Button)findViewById(R.id.findEar);

        addEar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddEarActivity.class);
                startActivity(intent);
//                dispatchTakePictureIntent(true);
            }
        });
        findEar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),FindEarActivity.class);
                startActivity(intent);
//                dispatchTakePictureIntent(false);
            }
        });

        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            try {
                earCSV = new File(storageDir.getAbsolutePath() + EAR_CSV);
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Absolute path for Documents directory not found");
            }
            if(!earCSV.exists()) {
                earCSV.createNewFile();
                try {
                    PrintWriter out = new PrintWriter(earCSV);
                    out.write("first_name,last_name,inv_moment\n");
                    out.close();
                } catch (FileNotFoundException e) {
                    Log.d(LOG_TAG, "File not found: " + e.getMessage());
                }
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "Error accessing file in onCreate(): " + e.getMessage());
        }
    }

    protected void checkPermissions(){
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

}
