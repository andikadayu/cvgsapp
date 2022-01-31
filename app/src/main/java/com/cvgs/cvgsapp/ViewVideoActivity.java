package com.cvgs.cvgsapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

public class ViewVideoActivity extends AppCompatActivity {

    StyledPlayerView playerView;
    PhotoView imageView;
    String mode, urls;
    ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_view_video);

        playerView = findViewById(R.id.playerView);
        imageView = findViewById(R.id.imageView);

        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra("mode")) {
            mode = currentIntent.getStringExtra("mode");
            urls = currentIntent.getStringExtra("urls");

            initializing();

        } else {
            startActivity(new Intent(getApplicationContext(), ProgressAdminActivity.class));
            finish();
        }
    }

    private void initializing() {
        if (mode.equals("image")) {
            imageView.setVisibility(View.VISIBLE);
            initialzeScreenshot();
        } else {
            playerView.setVisibility(View.VISIBLE);
            initialzeVideo();
        }
    }

    private void initialzeScreenshot() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child(urls).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                e.printStackTrace();
                Toast.makeText(ViewVideoActivity.this, "ERROR STORAGE", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initialzeVideo() {

        exoPlayer = new ExoPlayer.Builder(ViewVideoActivity.this).build();

        playerView.setPlayer(exoPlayer);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference.child(urls).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                MediaItem mediaItem = MediaItem.fromUri(uri);
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                e.printStackTrace();
                Toast.makeText(ViewVideoActivity.this, "ERROR STORAGE", Toast.LENGTH_SHORT).show();
            }
        });


    }
}