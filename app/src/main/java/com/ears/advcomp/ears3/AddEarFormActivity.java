package com.ears.advcomp.ears3;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static com.ears.advcomp.ears3.MainActivity.EAR_CSV;
import static com.ears.advcomp.ears3.MainActivity.LOG_TAG;

public class AddEarFormActivity extends AppCompatActivity {

    EditText firstName;
    EditText lastName;
    ArrayList<String> imagePaths;
    File earCSV;
    int id;
    Button addEar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ear_form);
        firstName = (EditText) findViewById(R.id.firstNameText);
        lastName = (EditText) findViewById(R.id.lastNameText);
        imagePaths = getIntent().getStringArrayListExtra("imagePaths");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        try {
            earCSV = new File(storageDir.getAbsolutePath() + EAR_CSV);
            Scanner inputStream = new Scanner(earCSV);
            id = 0;
            while(inputStream.hasNext()){
                String[] data = inputStream.next().split(",");
                if(Integer.parseInt(data[0]) >= id){
                    id++;
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Absolute path for Documents directory not found");
        }

        addEar = (Button) findViewById(R.id.submitEar);
        addEar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEar();
            }
        });

    }

    private void addEar(){
        try {
            Log.e(LOG_TAG, "addEar: Adding Ear." );
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            earCSV = new File(storageDir.getAbsolutePath() + "/ears.csv");
            PrintWriter out = new PrintWriter(earCSV);
            Log.e(LOG_TAG, "addEar: "+imagePaths.size() );
            for(int i = 0; i < imagePaths.size(); i++) {
                String path = imagePaths.get(i);
                out.append(String.valueOf(id) + ","+firstName.getText() + "_" + lastName.getText() + "," + path + "\n");
            }
            out.close();
        } catch (IOException e){
            Log.e("Darron", "onCreate: "+e.getMessage());
        }
        finish();
    }
}
