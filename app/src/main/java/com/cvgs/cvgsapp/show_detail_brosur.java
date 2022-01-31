package com.cvgs.cvgsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.cvgs.cvgsapp.advances.Constance;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class show_detail_brosur extends AppCompatActivity {

    PhotoView imgBrosur;
    Constance constance = new Constance();

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

        setContentView(R.layout.activity_show_detail_brosur);

        imgBrosur = findViewById(R.id.imgBrosurDetail);

        Intent intent = getIntent();
        String id_brosur = intent.getStringExtra("id_brosur");

        Picasso.get().load(constance.server + "/api/brosur/assets/brosur-" + id_brosur + ".png").into(imgBrosur);

    }
}