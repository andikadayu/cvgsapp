package com.cvgs.cvgsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadBrosurActivity extends AppCompatActivity {

    ImageView imgBrosur;
    Button btnUpload;
    Bitmap bitmap;
    private static final int REQUEST_CODE_PERMISSION = 10;
    Constance constance = new Constance();
    String id_brosur;
    boolean is_update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_brosur);

        imgBrosur = findViewById(R.id.imgBrosur);
        btnUpload = findViewById(R.id.btnUploadBrosur);

        enablePermission();

        AndroidNetworking.initialize(UploadBrosurActivity.this);
        Intent intents = getIntent();
        if(intents.hasExtra("id_brosur")){
            id_brosur = intents.getStringExtra("id_brosur");
            is_update = true;
        }else{
            is_update = false;
        }

        imgBrosur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_update){
                    new MaterialAlertDialogBuilder(UploadBrosurActivity.this)
                            .setTitle("Update Brosur")
                            .setMessage("Are You Sure to Update Brosur?")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updateImage();
                                }
                            }).show();

                }else{
                    uploadImage();
                }
            }
        });

    }

    private void enablePermission(){
        ActivityCompat.requestPermissions(UploadBrosurActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                enablePermission();
            }
        }
    }

    private void selectImage(){
        final CharSequence[] options = {"Take Photo","Choose From Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadBrosurActivity.this);
        builder.setTitle("Add Brosur");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(options[i].equals("Take Photo")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,1);
                }else if(options[i].equals("Choose From Gallery")){
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }else{
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1){

                bitmap = (Bitmap) data.getExtras().get("data");
                imgBrosur.setImageBitmap(bitmap);

            }else if(requestCode == 2){
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    imgBrosur.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private String getStringImage(@NonNull Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){

        Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        if(bitmap.sameAs(emptyBitmap)){
            Toast.makeText(UploadBrosurActivity.this, "Choose Photo First", Toast.LENGTH_SHORT).show();
        }else{
            ProgressDialog pdg = new ProgressDialog(UploadBrosurActivity.this);
            pdg.setCancelable(false);
            pdg.setMessage("Loading...");
            pdg.show();

            String brosur = getStringImage(bitmap);
            AndroidNetworking.post(constance.server+"/api/brosur/addBrosur.php")
                    .addBodyParameter("brosur",brosur)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean status = response.getBoolean("status");
                                if(status){
                                    pdg.dismiss();
                                    Toast.makeText(UploadBrosurActivity.this, "Success Upload Brosur", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UploadBrosurActivity.this,SuperBrosur.class));
                                    finish();
                                }else{
                                    pdg.dismiss();
                                    Toast.makeText(UploadBrosurActivity.this, "Error Upload Brosur", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                pdg.dismiss();
                                Toast.makeText(UploadBrosurActivity.this, "Error Responses", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(UploadBrosurActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                        }
                    });


        }

    }

    private void updateImage(){
        Bitmap emptyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        if(bitmap.sameAs(emptyBitmap)){
            Toast.makeText(UploadBrosurActivity.this, "Choose Photo First", Toast.LENGTH_SHORT).show();
        }else{
            ProgressDialog pdg = new ProgressDialog(UploadBrosurActivity.this);
            pdg.setCancelable(false);
            pdg.setMessage("Loading...");
            pdg.show();

            String brosur = getStringImage(bitmap);
            AndroidNetworking.post(constance.server+"/api/brosur/addBrosur.php")
                    .addBodyParameter("id_brosur",id_brosur)
                    .addBodyParameter("brosur",brosur)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean status = response.getBoolean("status");
                                if(status){
                                    pdg.dismiss();
                                    Toast.makeText(UploadBrosurActivity.this, "Success Upload Brosur", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UploadBrosurActivity.this,SuperBrosur.class));
                                    finish();
                                }else{
                                    pdg.dismiss();
                                    Toast.makeText(UploadBrosurActivity.this, "Error Upload Brosur", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                pdg.dismiss();
                                Toast.makeText(UploadBrosurActivity.this, "Error Responses", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(UploadBrosurActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}