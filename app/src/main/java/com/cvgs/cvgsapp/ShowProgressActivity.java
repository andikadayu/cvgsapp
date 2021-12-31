package com.cvgs.cvgsapp;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

public class ShowProgressActivity extends AppCompatActivity {

    TextView tvNoProgress,tvTglProgress,tvIsiProgress;
    CardView cardScreenshot,cardVideo;
    ImageView imgSS,btnBack;
    VideoView videoView;
    String id_progress, progress, isi_progress,tgl_progress, screenshot, video;

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

        Intent currentIntent = getIntent();
        if(currentIntent.hasExtra("id_progress")){
            id_progress = currentIntent.getStringExtra("id_progress");
            progress = currentIntent.getStringExtra("progress");
            isi_progress = currentIntent.getStringExtra("isi_progress");
            tgl_progress = currentIntent.getStringExtra("tgl_progress");
            screenshot = currentIntent.getStringExtra("screenshot");
            video = currentIntent.getStringExtra("video");

            intializing();
        }else{
            startActivity(new Intent(getApplicationContext(),ProgressAdminActivity.class));
            finish();
        }

        imgSS.setOnClickListener(view->{
            Intent sendData = new Intent(getApplicationContext(),ViewVideoActivity.class);
            sendData.putExtra("mode","image");
            sendData.putExtra("urls",screenshot);
            startActivity(sendData);
        });

        videoView.setOnClickListener(view->{
            Intent sendData = new Intent(getApplicationContext(),ViewVideoActivity.class);
            sendData.putExtra("mode","video");
            sendData.putExtra("urls",video);
            startActivity(sendData);
        });

        btnBack.setOnClickListener(view->{
            startActivity(new Intent(getApplicationContext(),ProgressAdminActivity.class));
            finish();
        });


    }

    private void intializing(){
        tvNoProgress.setText("Progress : "+progress);
        tvTglProgress.setText(tgl_progress);
        tvIsiProgress.setText(isi_progress);

        if(!screenshot.equalsIgnoreCase("null")){
            cardScreenshot.setVisibility(View.VISIBLE);
            initialzeScreenshot();
        }

        if(!screenshot.equalsIgnoreCase("null")){
            cardVideo.setVisibility(View.VISIBLE);
        }

    }

    private void initialzeScreenshot(){
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
}