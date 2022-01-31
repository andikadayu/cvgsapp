package com.cvgs.cvgsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    TextView versionApps;
    Button btnOpenSource;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        versionApps = findViewById(R.id.versionApps);
        btnOpenSource = findViewById(R.id.btnToOpenLicense);

        String version;
        try {
            version = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            versionApps.setText("v " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        btnOpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), OpenSourceActivity.class));
            }
        });
    }
}