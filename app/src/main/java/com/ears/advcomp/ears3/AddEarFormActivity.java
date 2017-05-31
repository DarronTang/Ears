package com.ears.advcomp.ears3;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class AddEarFormActivity extends AppCompatActivity {

    File earCSV;
    EditText firstName;
    EditText lastName;
    float invMoment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ear_form);
        firstName = (EditText) findViewById(R.id.firstNameText);
        lastName = (EditText) findViewById(R.id.lastNameText);

    }

    private void addEar(){
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            earCSV = new File(storageDir.getAbsolutePath() + "/ears.csv");
            PrintWriter out = new PrintWriter(earCSV);
            out.append(firstName.getText()+","+lastName.getText()+","+invMoment+"\n");
            out.close();
        } catch (IOException e){
            Log.e("Darron", "onCreate: "+e.getMessage());
        }
    }
}
