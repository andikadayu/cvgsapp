package com.cvgs.cvgsapp;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.PembayaranAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.cvgs.cvgsapp.model.NotificationModel;
import com.cvgs.cvgsapp.model.PembayaranModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PembayaranActivity extends AppCompatActivity {

    RecyclerView recyPembayaran;
    RecyclerView.Adapter adapter;
    ArrayList<PembayaranModel> pembayaranList;
    RecyclerView.LayoutManager layoutManager;
    Constance constance = new Constance();
    ImageView btnBack;
    SessionManager sessionManager;
    String role = null;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pembayaran);

        recyPembayaran = findViewById(R.id.recyPembayaran);
        btnBack = findViewById(R.id.btnBack);
        refreshLayout = findViewById(R.id.refreshLayout);

        AndroidNetworking.initialize(getApplicationContext());
        pembayaranList = new ArrayList<PembayaranModel>();

        sessionManager = new SessionManager(getApplicationContext());
        role = sessionManager.getUserDetail().get(SessionManager.ROLE);

        initialize();

        btnBack.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),ProfileActivity.class));finish();});

        refreshLayout.setOnRefreshListener(()->{
            pembayaranList = new ArrayList<PembayaranModel>();
            initialize();
        });
    }

    private void initialize(){
        AndroidNetworking.post(constance.server+"/api/pembayaran/getData.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                       try {

                           boolean status = response.getBoolean("status");

                           if(status){

                               layoutManager = new LinearLayoutManager(PembayaranActivity.this, RecyclerView.VERTICAL, false);
                               recyPembayaran.setLayoutManager(layoutManager);

                               recyPembayaran.addItemDecoration(new DividerItemDecoration(PembayaranActivity.this, DividerItemDecoration.VERTICAL));

                               adapter = new PembayaranAdapter(PembayaranActivity.this,pembayaranList,constance.server,role);

                               recyPembayaran.setAdapter(adapter);

                               JSONArray ja = response.getJSONArray("data");
                               for(int i=0;i<ja.length();i++){
                                   JSONObject jo = ja.getJSONObject(i);

                                   pembayaranList.add(new PembayaranModel(
                                           jo.getString("id_daftar"),
                                           jo.getString("logo"),
                                           jo.getString("judul"),
                                           jo.getString("detail"),
                                           jo.getString("sisa")
                                   ));

                                   adapter.notifyDataSetChanged();
                               }


                           }else{
                               Toast.makeText(PembayaranActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                           }

                       }catch (JSONException e){
                           e.printStackTrace();
                            Toast.makeText(PembayaranActivity.this, "ERROR RESPONSE", Toast.LENGTH_SHORT).show();
                       }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(PembayaranActivity.this, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                    }
                });

        refreshLayout.setRefreshing(false);
    }
}