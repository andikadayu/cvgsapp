package com.cvgs.cvgsapp;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.graphics.Color;
import android.os.Build;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.NotificationReceiver;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.cvgs.cvgsapp.model.BroadcastModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileActivity extends AppCompatActivity{

    Button btnToLogin,btnToAbout,btnLogout,btnAdminGoBrosur,btnPendaftarAdm,btnPendaftarSuper,btnPembayaranAdm,btnPembayaranSuper,btnUserTransaksi,btnProgressSuper,btnProgressUser,btnBroadcastSuper,btnBroadcastAdm;

    CardView CardNoLogin,CardHasLogin;

    LinearLayout profileTopLogout,profileTopLogin,menuSuperAdmin,menuAdmin,menuUser;

    TextView loginEmail,loginName;

    SessionManager sessionManager;

    ImageView btnBack;

    Constance constance = new Constance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        btnToLogin = findViewById(R.id.btntoLogin);
        btnToAbout = findViewById(R.id.btntoAbout);
        btnLogout = findViewById(R.id.btnLogout);
        btnAdminGoBrosur = findViewById(R.id.btnAdminGoBrosur);

        btnPendaftarAdm = findViewById(R.id.btnPendaftarAdm);
        btnPendaftarSuper = findViewById(R.id.btnPendaftarSuper);

        btnPembayaranAdm = findViewById(R.id.btnPembayaranAdm);
        btnPembayaranSuper = findViewById(R.id.btnPembayaranSuper);

        btnProgressSuper = findViewById(R.id.btnProgressSuper);


        btnUserTransaksi = findViewById(R.id.btnUserTransaksi);
        btnProgressUser = findViewById(R.id.btnProgressUser);

        btnBroadcastSuper = findViewById(R.id.btnBroadcastSuper);
        btnBroadcastAdm = findViewById(R.id.btnBroadcastAdm);

        btnBack= findViewById(R.id.btnBack);

        //CARD
        CardNoLogin = findViewById(R.id.CardNoLogin);
        CardHasLogin = findViewById(R.id.CardHasLogin);

        //TextView
        loginEmail = findViewById(R.id.loginEmail);
        loginName = findViewById(R.id.loginName);

        //LinearLayout

        profileTopLogin = findViewById(R.id.profileTopLogin);
        profileTopLogout = findViewById(R.id.profileTopLogout);
        menuSuperAdmin = findViewById(R.id.menuSuperAdmin);
        menuAdmin = findViewById(R.id.menuAdmin);
        menuUser = findViewById(R.id.menuUser);


        sessionManager = new SessionManager(getApplicationContext());

        if(sessionManager.isLoggedIn()){
            SetProfileLogin();

            if(!isMyServiceRunning(NotificationReceiver.class)){
                startService(new Intent(getApplicationContext(),NotificationReceiver.class));
            }

        }else{
            changeToDefault();
        }

        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        btnToAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AboutActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(ProfileActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure to Logout?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopService(new Intent(getApplicationContext(),NotificationReceiver.class));
                        sessionManager.logoutSession();
                        startActivity(getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY));
                        finish();
                    }
                }).show();
            }
        });

        btnBack.setOnClickListener(view->{
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        });
        btnAdminGoBrosur.setOnClickListener(view-> startActivity(new Intent(getApplicationContext(),SuperBrosur.class)));
        btnPendaftarSuper.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),PendaftarActivity.class)));
        btnPendaftarAdm.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),PendaftarActivity.class)));
        btnPembayaranAdm.setOnClickListener(view->startActivity(new Intent(getApplicationContext(),PembayaranActivity.class)));
        btnPembayaranSuper.setOnClickListener(view->startActivity(new Intent(getApplicationContext(),PembayaranActivity.class)));
        btnUserTransaksi.setOnClickListener(view->startActivity(new Intent(getApplicationContext(),TransaksiActivity.class)));
        btnProgressSuper.setOnClickListener(view->startActivity(new Intent(getApplicationContext(),ProgressAdminActivity.class)));
        btnProgressUser.setOnClickListener(view->startActivity(new Intent(getApplicationContext(),ProgressAdminActivity.class)));

        btnBroadcastSuper.setOnClickListener(view->startActivity(new Intent(getApplicationContext(), BroadcastActivity.class)));
        btnBroadcastAdm.setOnClickListener(view->startActivity(new Intent(getApplicationContext(),BroadcastActivity.class)));

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void changeToDefault(){
        CardNoLogin.setVisibility(View.GONE);
        CardHasLogin.setVisibility(View.GONE);
        profileTopLogin.setVisibility(View.GONE);
        profileTopLogout.setVisibility(View.VISIBLE);
    }

    private void SetProfileLogin(){
        CardNoLogin.setVisibility(View.GONE);
        CardHasLogin.setVisibility(View.VISIBLE);
        profileTopLogin.setVisibility(View.VISIBLE);
        profileTopLogout.setVisibility(View.GONE);

        loginName.setText(sessionManager.getUserDetail().get(SessionManager.NAMA));
        loginEmail.setText(sessionManager.getUserDetail().get(SessionManager.EMAIL));

        String role = sessionManager.getUserDetail().get(SessionManager.ROLE);
        if(role.equals("super")){
            menuSuperAdmin.setVisibility(View.VISIBLE);
        }else if(role.equals("admin")){
            menuAdmin.setVisibility(View.VISIBLE);
        }else{
            menuUser.setVisibility(View.VISIBLE);
        }

    }
}