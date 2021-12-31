package com.cvgs.cvgsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.cvgs.cvgsapp.advances.Constance;
import com.squareup.picasso.Picasso;

public class show_detail_brosur extends AppCompatActivity {

    ImageView imgBrosur;
    Constance constance = new Constance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail_brosur);

        imgBrosur = findViewById(R.id.imgBrosurDetail);

        Intent intent = getIntent();
        String id_brosur = intent.getStringExtra("id_brosur");

        Picasso.get().load(constance.server+"/api/brosur/assets/brosur-"+id_brosur+".png").into(imgBrosur);

    }
}