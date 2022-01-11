package com.cvgs.cvgsapp;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.PendaftarAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.NotificationModel;
import com.cvgs.cvgsapp.model.PendaftarModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PendaftarActivity extends AppCompatActivity {

    RecyclerView recyPendaftar;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<PendaftarModel> pendaftarList;
    Constance constance = new Constance();

    SwipeRefreshLayout refreshLayout;


    ImageView btnBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pendaftar);

        recyPendaftar = findViewById(R.id.recyPendaftar);
        btnBack = findViewById(R.id.btnBack);
        refreshLayout = findViewById(R.id.refreshLayout);

        AndroidNetworking.initialize(getApplicationContext());
        pendaftarList = new ArrayList<PendaftarModel>();

        initialize();

        btnBack.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),ProfileActivity.class));finish();});

        refreshLayout.setOnRefreshListener(()->{
            pendaftarList = new ArrayList<>();
            initialize();
        });

    }


    private void initialize(){
        AndroidNetworking.post(constance.server+"/api/pendaftar/getPendaftar.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                layoutManager = new LinearLayoutManager(PendaftarActivity.this, RecyclerView.VERTICAL, false);
                                recyPendaftar.setLayoutManager(layoutManager);

                                recyPendaftar.addItemDecoration(new DividerItemDecoration(PendaftarActivity.this, DividerItemDecoration.VERTICAL));

                                adapter = new PendaftarAdapter(PendaftarActivity.this, pendaftarList);
                                recyPendaftar.setAdapter(adapter);

                                JSONArray ja = response.getJSONArray("data");
                                for(int i = 0;i<ja.length();i++){
                                    JSONObject jo = ja.getJSONObject(i);
                                    pendaftarList.add(new PendaftarModel(
                                            jo.getString("id_detail"),
                                            jo.getString("nama"),
                                            jo.getString("no_telp"),
                                            jo.getString("email"),
                                            jo.getString("alamat")
                                    ));

                                    adapter.notifyDataSetChanged();
                                }
                            }else{
                                Toast.makeText(PendaftarActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(PendaftarActivity.this, "ERROR RESPONSE", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(PendaftarActivity.this, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                    }
                });
    refreshLayout.setRefreshing(false);
    }
}