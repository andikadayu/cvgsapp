package com.cvgs.cvgsapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.PaketAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.cvgs.cvgsapp.model.PaketModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PilihPaketActivity extends AppCompatActivity {

    Constance constance = new Constance();
    GridView gridPaket;
    ArrayAdapter<PaketModel> adapter;
    ArrayList<PaketModel> paketList;
    String id_layanan, nama_layanan;
    SessionManager sessionManager;
    boolean is_logged_in = false;
    String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_paket);

        gridPaket = findViewById(R.id.gridPaket);

        paketList = new ArrayList<PaketModel>();

        sessionManager = new SessionManager(getApplicationContext());

        AndroidNetworking.initialize(getApplicationContext());

        is_logged_in = sessionManager.isLoggedIn();
        role= sessionManager.getUserDetail().get(SessionManager.ROLE);

        Intent inThis = getIntent();
        id_layanan = inThis.getStringExtra("id_layanan");
        nama_layanan = inThis.getStringExtra("nama_layanan");

        if (id_layanan.equals("") || nama_layanan.equals("")) {
            startActivity(new Intent(PilihPaketActivity.this, HomeActivity.class));
            finish();
        } else {
            initialize();
        }

    }

    private void initialize() {
        AndroidNetworking.post(constance.server + "/api/paket/showPaket.php")
                .addBodyParameter("id_layanan", id_layanan)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray ja = response.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                paketList.add(new PaketModel(
                                        jo.getString("id_paket"),
                                        jo.getString("id_layanan"),
                                        jo.getString("nama_paket"),
                                        jo.getString("isi_paket"),
                                        jo.getString("harga_paket"),
                                        jo.getString("logo_paket"),
                                        "#" + jo.getString("color_paket")
                                ));

                            }
                            adapter = new PaketAdapter(PilihPaketActivity.this, paketList,nama_layanan,is_logged_in,role);
                            gridPaket.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(PilihPaketActivity.this, "Error Response", Toast.LENGTH_SHORT).show();
                            initialize();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(PilihPaketActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                        initialize();
                    }
                });
    }
}