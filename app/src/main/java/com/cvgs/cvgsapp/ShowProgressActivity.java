package com.cvgs.cvgsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowProgressActivity extends AppCompatActivity {

    TextView tvNoProgress, tvTglProgress, tvIsiProgress;
    CardView cardScreenshot, cardVideo;
    ImageView imgSS, btnBack, btnDeleteVideo, btnDeleteSS;
    VideoView videoView;
    String id_progress, progress, isi_progress, tgl_progress, screenshot, video;
    SessionManager sessionManager;
    Constance constance = new Constance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_progress);

        tvNoProgress = findViewById(R.id.tvNoProgress);
        tvTglProgress = findViewById(R.id.tvTglProgress);
        tvIsiProgress = findViewById(R.id.tvIsiProgress);
        cardScreenshot = findViewById(R.id.cardScreenshot);
        cardVideo = findViewById(R.id.cardVideo);
        imgSS = findViewById(R.id.imgSS);
        videoView = findViewById(R.id.videoView);
        btnBack = findViewById(R.id.btnBack);
        btnDeleteVideo = findViewById(R.id.btnDeleteVideo);
        btnDeleteSS = findViewById(R.id.btnDeleteSS);


        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra("id_progress")) {
            id_progress = currentIntent.getStringExtra("id_progress");
            progress = currentIntent.getStringExtra("progress");
            isi_progress = currentIntent.getStringExtra("isi_progress");
            tgl_progress = currentIntent.getStringExtra("tgl_progress");
            screenshot = currentIntent.getStringExtra("screenshot");
            video = currentIntent.getStringExtra("video");
            sessionManager = new SessionManager(getApplicationContext());
            intializing();
        } else {
            startActivity(new Intent(getApplicationContext(), ProgressAdminActivity.class));
            finish();
        }

        imgSS.setOnClickListener(view -> {
            Intent sendData = new Intent(getApplicationContext(), ViewVideoActivity.class);
            sendData.putExtra("mode", "image");
            sendData.putExtra("urls", screenshot);
            startActivity(sendData);
        });

        videoView.setOnClickListener(view -> {
            Intent sendData = new Intent(getApplicationContext(), ViewVideoActivity.class);
            sendData.putExtra("mode", "video");
            sendData.putExtra("urls", video);
            startActivity(sendData);
        });

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ProgressAdminActivity.class));
            finish();
        });

        btnDeleteSS.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Screenshot")
                    .setMessage("Are you sure to delete the Screenshot?")
                    .setNegativeButton("Cancel", (dialog, i) -> {
                        dialog.cancel();
                    }).setPositiveButton("Confirm", (dialog, i) -> {
                        deleteCloud(this, "screenshot");
                    }).show();
        });

        btnDeleteVideo.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Video")
                    .setMessage("Are you sure to delete the Video?")
                    .setNegativeButton("Cancel", (dialog, i) -> {
                        dialog.cancel();
                    }).setPositiveButton("Confirm", (dialog, i) -> {
                        deleteCloud(this, "video");
                    }).show();
        });
    }

    private void intializing() {
        tvNoProgress.setText(String.format("Progress : %s", progress));
        tvTglProgress.setText(tgl_progress);
        tvIsiProgress.setText(isi_progress);

        if (!screenshot.equalsIgnoreCase("null") && !screenshot.equals("")) {
            cardScreenshot.setVisibility(View.VISIBLE);
            if (!sessionManager.getUserDetail().get(SessionManager.ROLE).equals("pendaftar")) {
                btnDeleteSS.setVisibility(View.VISIBLE);
            }
            initialzeScreenshot();
        }
        if (!video.equalsIgnoreCase("null") && !video.equals("")) {
            cardVideo.setVisibility(View.VISIBLE);
            if (!sessionManager.getUserDetail().get(SessionManager.ROLE).equals("pendaftar")) {
                btnDeleteVideo.setVisibility(View.VISIBLE);
            }
        }

    }

    private void initialzeScreenshot() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child(screenshot).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imgSS);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                e.printStackTrace();
                Toast.makeText(ShowProgressActivity.this, "ERROR STORAGE", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void deleteCloud(Activity activity, @NotNull String purpose) {
        String urls = null;
        if (purpose.equals("screenshot")) {
            urls = screenshot;
        } else {
            urls = video;
        }
        // Create a storage reference from our app
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        // Create a reference to the file to delete
        StorageReference desertRef = storageReference.child(urls);

        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                updateServer(activity, purpose);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "ERROR FIREBASE", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateServer(Activity activity, String purpose) {
        AndroidNetworking.post(constance.server + "/api/progress/deleteSV.php")
                .addBodyParameter("id_progress", id_progress)
                .addBodyParameter("purpose", purpose)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(activity, "SUCCESS", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(activity, ProgressAdminActivity.class));
                                finish();
                            } else {
                                Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
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