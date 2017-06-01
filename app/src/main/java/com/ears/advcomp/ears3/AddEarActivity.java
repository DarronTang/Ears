package com.ears.advcomp.ears3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddEarActivity extends AppCompatActivity {

    ConstraintLayout cameraPreviewLayout;
    CameraPreview cameraP = null;
    Button addEarButton;
    String mCurrentPhotoPath;
    Camera camera = Camera.open();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ear);
        camera.setDisplayOrientation(90);
        cameraPreviewLayout = (ConstraintLayout) findViewById(R.id.addEarCameraPreview);
        cameraP = new CameraPreview(this,camera);
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(previewSizes, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        parameters.setPictureSize(pictureSizes.get(1).width,pictureSizes.get(1).height);
        parameters.setJpegQuality(100);
        parameters.setRotation(90);
        camera.setParameters(parameters);
        cameraPreviewLayout.addView(cameraP);
        setListener();
    }

    @Override
    protected void onStop() {
        // Release camera preview
        if (cameraP != null) {
            camera.stopPreview();
            cameraP = null;
        }
        super.onStop();
    }

    private void setListener(){
        addEarButton = (Button) findViewById(R.id.addEarTakePicture);
        addEarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, mPicture);
                File root = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                Bitmap bMap = BitmapFactory.decodeFile(root+mCurrentPhotoPath);
                //TODO Perform IRT
                //TODO Perform Invariant moment calculation

                //TODO Open AddEarFormActivity with inv moment in intent
                Intent addEarFormIntent = new Intent(getApplicationContext(),AddEarFormActivity.class);
                startActivity(addEarFormIntent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        camera.startPreview();
        cameraP = new CameraPreview(this, camera);
        setListener();

    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile;
            try {
                pictureFile = createImageFile();
                if (pictureFile == null){
                    Log.e("Darron", "Error creating media file, check storage permissions ");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Log.e("Darron", "onPictureTaken: "+ pictureFile.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    Log.e("Darron", "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.e("Darron", "Error accessing file: " + e.getMessage());
                }

            } catch(Exception e){
                Log.e("Darron", "onPictureTaken:" + e.getMessage());
            }
        }
    };

    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w/h;

        if (sizes==null) return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Find size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    public File createImageFile() throws IOException {
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
}
