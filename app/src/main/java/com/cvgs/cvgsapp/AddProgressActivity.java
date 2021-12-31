package com.cvgs.cvgsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddProgressActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 10;

    CheckBox cbSS,cbVideo;
    LinearLayout layoutScreenshot,layoutVideo;
    TextView tvNama,tvDetail,tvAlamat,tvFile;
    ImageView logoApps,btnBack,imgScreenshot;
    Constance constance = new Constance();
    TextInputLayout txtPercent,txtDescription;
    Button btnPilih,btnSend;
    String id_daftar,judul,detail,logo,progress;
    Bitmap bmpSS;
    Uri video;
    String namaSS,namaVideo,dateNow,proPercent,proDescription,name_video,name_screenshot = null;
    String has_screenshot,has_video = "no";
    String format_video;
    boolean done_image = true;
    boolean done_video = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_progress);

        cbSS = findViewById(R.id.cbSS);
        cbVideo = findViewById(R.id.cbVideo);
        tvNama = findViewById(R.id.tvNama);
        tvDetail = findViewById(R.id.tvDetail);
        tvAlamat = findViewById(R.id.tvAlamat);
        logoApps = findViewById(R.id.logoApps);
        btnBack = findViewById(R.id.btnBack);
        layoutScreenshot = findViewById(R.id.layoutScreenshot);
        layoutVideo = findViewById(R.id.layoutVideo);
        txtPercent = findViewById(R.id.txtPercent);
        txtDescription = findViewById(R.id.txtDescription);
        imgScreenshot = findViewById(R.id.imgScreenshot);
        btnPilih = findViewById(R.id.btnPilih);
        tvFile = findViewById(R.id.tvFile);
        btnSend = findViewById(R.id.btnSend);

        enablePermission();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss-");
        Date date = new Date();
        dateNow = dateFormat.format(date);

        Intent currentIntent = getIntent();
        if(currentIntent.hasExtra("id_daftar")){
            id_daftar = currentIntent.getStringExtra("id_daftar");
            judul = currentIntent.getStringExtra("judul");
            detail = currentIntent.getStringExtra("detail");
            logo = currentIntent.getStringExtra("logo");
            progress = currentIntent.getStringExtra("progress");
        }else{
            startActivity(new Intent(getApplicationContext(),ProgressAdminActivity.class));
            finish();
        }

        AndroidNetworking.initialize(getApplicationContext());

        imgScreenshot.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_a_photo_24,getApplicationContext().getTheme()));

        initializeProfile();

        btnBack.setOnClickListener(view->{
            Intent sendData = new Intent(getApplicationContext(),AdminProgressDetailActivity.class);
            sendData.putExtra("id_daftar",id_daftar);
            sendData.putExtra("judul",judul);
            sendData.putExtra("detail",detail);
            sendData.putExtra("logo",logo);
            sendData.putExtra("progress",progress);
            startActivity(sendData);
            finish();
        });

        cbSS.setOnClickListener(view -> {
            CheckBox current = (CheckBox) view;
            if(current.isChecked()){
                layoutScreenshot.setVisibility(View.VISIBLE);
                has_screenshot = "yes";
            }else{
                layoutScreenshot.setVisibility(View.GONE);
                clearScreenshot();
                has_screenshot = "no";
            }
        });

        cbVideo.setOnClickListener(view->{
            CheckBox current = (CheckBox) view;
            if(current.isChecked()){
                layoutVideo.setVisibility(View.VISIBLE);
                has_video = "yes";
            }else{
                layoutVideo.setVisibility(View.GONE);
                clearVideo();
                has_video = "no";
            }
        });

        imgScreenshot.setOnClickListener(view->optionScreenshot(this));

        btnPilih.setOnClickListener(view->optionVideo());

        btnSend.setOnClickListener(view -> {
            proPercent = txtPercent.getEditText().getText().toString();
            proDescription = txtDescription.getEditText().getText().toString();
            if(!progress.equals("") && !proDescription.equals("")){
                actionProgress(this);
            }else{
                Toast.makeText(this, "Complete Form", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void enablePermission() {
        ActivityCompat.requestPermissions(AddProgressActivity.this,
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA },
                REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

    private void initializeProfile(){
        Picasso.get().load(constance.server+logo).into(logoApps);
        tvNama.setText(judul);
        tvDetail.setText(detail);
        tvAlamat.setText(progress);
    }

    private void clearScreenshot(){
        bmpSS = null;
        imgScreenshot.setImageBitmap(bmpSS);
        imgScreenshot.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_a_photo_24,getApplicationContext().getTheme()));
        imgScreenshot.setBackgroundColor(Color.parseColor("#EAEAEA"));
    }

    private void clearVideo(){
        video = null;
    }

    private void optionScreenshot(Activity activity){
        String[] options = {"Take Camera","Select From Gallery","Close"};
        new MaterialAlertDialogBuilder(activity)
                .setTitle("List Options")
                .setItems(options,(dialog,i)->{
                    if(i == 0){
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 100);
                    }else if(i == 1){
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 200);
                    }else if(i == 2){
                        dialog.cancel();
                    }
                }).show();
    }

    private void optionVideo(){
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            if (requestCode == 100) {
                UUID number = UUID.randomUUID();
                namaSS = "IMG-"+number.toString().replace("-", "");
                bmpSS = (Bitmap) data.getExtras().get("data");
                imgScreenshot.setImageBitmap(bmpSS);

            } else if (requestCode == 200) {
                try {
                    Uri selectedImage = data.getData();
                    bmpSS = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    imgScreenshot.setImageBitmap(bmpSS);
                    UUID number = UUID.randomUUID();
                    namaSS = "IMG-"+number.toString().replace("-", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 300) {
                Uri selectedVideo = data.getData();
                File ehe = new File(selectedVideo.getPath());
                String names = ehe.getName();

                tvFile.setText(names);

                String[] te = names.split("\\.");

                format_video = te[1];

                video = selectedVideo;
                UUID number = UUID.randomUUID();
                namaVideo = "VID-"+number.toString().replace("-", "");
            }
        }
    }

    private void actionProgress(Activity activity){

        if(has_screenshot.equalsIgnoreCase("yes")){
            done_image = false;
        }

        if(has_video.equalsIgnoreCase("yes")){
            done_video = true;
        }

        uploadImage(activity);
    }

    private void uploadImage(Activity activity){
        String ImageSegment = "images/";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if(cbSS.isChecked() && bmpSS != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmpSS.compress(Bitmap.CompressFormat.PNG, 100, baos);

            byte[] data = baos.toByteArray();
            name_screenshot = ImageSegment+dateNow+namaSS+".png";
            UploadTask uploadTask = storage.getReference().child(name_screenshot).putBytes(data);
            ProgressDialog pgb = new ProgressDialog(activity);
            pgb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pgb.setTitle("Upload Screenshot");
            pgb.setMax(100);

            pgb.setCancelable(false);
            pgb.show();

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                    pgb.setProgress((int) progress);

                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("ISS", "Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    exception.printStackTrace();
                    pgb.dismiss();
                    Toast.makeText(activity, "Upload Image ERROR", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    // ...
                    pgb.dismiss();
                    Toast.makeText(activity, "Upload Image Done", Toast.LENGTH_SHORT).show();
                    done_image = true;
                    uploadVideo(activity);
                }
            });

        }else{
            done_image = true;
            uploadVideo(activity);
        }
    }

    private void uploadVideo(Activity activity){
        String VideoSegment = "videos/";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if(cbVideo.isChecked() && video != null){
            done_video = false;
            name_video = VideoSegment+dateNow+namaVideo+"."+format_video;

            UploadTask videoTask = storage.getReference().child(name_video).putFile(video);

            ProgressDialog pgb = new ProgressDialog(activity);
            pgb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pgb.setTitle("Upload Video");
            pgb.setMax(100);

            pgb.setCancelable(false);
            pgb.show();

            videoTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                    pgb.setProgress((int) progress);

                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("ISS", "Upload is paused");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    exception.printStackTrace();
                    pgb.dismiss();
                    Toast.makeText(activity, "Upload Video ERROR", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    // ...
                    pgb.dismiss();
                    Toast.makeText(activity, "Upload Video Done", Toast.LENGTH_SHORT).show();
                    done_video = true;
                    uploadToServer(activity);
                }
            });
        }else{
            done_video = true;
            uploadToServer(activity);
        }
    }


    private void uploadToServer(Activity activity){
        if(done_image && done_video){
            AndroidNetworking.post(constance.server+"/api/progress/addProgress.php")
                    .addBodyParameter("id_daftar",id_daftar)
                    .addBodyParameter("progress",proPercent)
                    .addBodyParameter("description",proDescription)
                    .addBodyParameter("has_screenshot",has_screenshot)
                    .addBodyParameter("has_video",has_video)
                    .addBodyParameter("screenshot",name_screenshot)
                    .addBodyParameter("video",name_video)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean status = response.getBoolean("status");

                                if(status){
                                    Toast.makeText(activity, "SUCCESS", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(activity,ProgressAdminActivity.class));
                                    finish();
                                }else{
                                    Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show();
                                }

                            }catch (JSONException e){
                                e.printStackTrace();
                                Toast.makeText(activity, "ERROR RESPONSES", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Toast.makeText(activity, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}