package com.ears.advcomp.ears3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

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
                Intent addEarIntent = new Intent(getApplicationContext(),AddEarActivity.class);
                startActivity(addEarIntent);
            }
        });
        findEar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent findEarIntent = new Intent(getApplicationContext(),FindEarActivity.class);
                startActivity(findEarIntent);
            }
        });

        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            earCSV = new File(storageDir.getAbsolutePath() + "/ears.csv");
            if(!earCSV.exists()) {
                earCSV.createNewFile();


                try {
                    PrintWriter out = new PrintWriter(earCSV);
                    out.write("first_name,last_name,inv_moment\n");
                    out.close();
                } catch (FileNotFoundException e) {
                    Log.d("Darron", "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d("Darron", "Error accessing file: " + e.getMessage());
                }

            }
        } catch (IOException e){
            Log.e("Darron", "onCreate: "+e.getMessage());
        }

//        earCSV = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/ears.csv");
//        if(!earCSV.exists()) {
//            try {
//                earCSV.createNewFile();
//            } catch (IOException e){
//                Log.e("Darron", "onCreate: "+e.getMessage());
//            }
//        }


        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
}
