package com.ears.advcomp.ears3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    Button addEar;
    Button findEar;
    File earCSV;
    String mCurrentPhotoPath;

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
    }

    static final int PHOTO_ADD_EAR = 1;

    static final int PHOTO_FIND_EAR = 2;

    private void dispatchTakePictureIntent(boolean add) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("Darron", "dispatchTakePictureIntent: " + ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if(add) {
                    startActivityForResult(takePictureIntent, PHOTO_ADD_EAR);
                } else {
                    startActivityForResult(takePictureIntent, PHOTO_FIND_EAR);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
