package com.cvgs.cvgsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.DetailTransaksiAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.DetailPembayaranModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransaksiDetailActivity extends AppCompatActivity {

    TextView tvNama, tvDetail, tvAlamat;
    Constance constance = new Constance();
    ImageView logoApps, btnBack;
    FloatingActionButton fabBayar;

    RecyclerView recyDetailPembayaran;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<DetailPembayaranModel> detailList;

    String id_daftar, judul, detail, logo, sisa;

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transaksi_detail);

        recyDetailPembayaran = findViewById(R.id.recyDetailPembayaran);
        tvNama = findViewById(R.id.tvNama);
        tvDetail = findViewById(R.id.tvDetail);
        tvAlamat = findViewById(R.id.tvAlamat);
        logoApps = findViewById(R.id.logoApps);
        btnBack = findViewById(R.id.btnBack);
        fabBayar = findViewById(R.id.fabBayar);
        refreshLayout = findViewById(R.id.refreshLayout);

        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra("id_daftar")) {
            id_daftar = currentIntent.getStringExtra("id_daftar");
            judul = currentIntent.getStringExtra("judul");
            detail = currentIntent.getStringExtra("detail");
            logo = currentIntent.getStringExtra("logo");
            sisa = currentIntent.getStringExtra("sisa");
        } else {
            startActivity(new Intent(getApplicationContext(), TransaksiActivity.class));
            finish();
        }

        AndroidNetworking.initialize(getApplicationContext());

        detailList = new ArrayList<DetailPembayaranModel>();

        initializeProfile();

        initializeData(this);

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), TransaksiActivity.class));
            finish();
        });

        fabBayar.setOnClickListener(view -> {
            Intent sendData = new Intent(getApplicationContext(), PayActivity.class);
            sendData.putExtra("id_daftar", id_daftar);
            sendData.putExtra("judul", judul);
            sendData.putExtra("detail", detail);
            sendData.putExtra("logo", logo);
            sendData.putExtra("sisa", sisa);
            startActivity(sendData);
        });

        refreshLayout.setOnRefreshListener(() -> {
            detailList = new ArrayList<DetailPembayaranModel>();
            initializeData(this);
        });

    }

    private void initializeProfile() {
        Picasso.get().load(constance.server + logo).into(logoApps);
        tvNama.setText(judul);
        tvDetail.setText(detail);
        tvAlamat.setText(sisa);
        if (sisa.equalsIgnoreCase("Pembayaran Lunas")) {
            fabBayar.setVisibility(View.GONE);
        } else {
            fabBayar.setVisibility(View.VISIBLE);
        }
    }

    private void initializeData(Activity activity) {
        AndroidNetworking.post(constance.server + "/api/pembayaran/getAdmin.php")
                .addBodyParameter("id_daftar", id_daftar)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");

                            if (status) {
                                layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
                                recyDetailPembayaran.setLayoutManager(layoutManager);

                                recyDetailPembayaran.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

                                adapter = new DetailTransaksiAdapter(activity, detailList, constance.server);

                                recyDetailPembayaran.setAdapter(adapter);

                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    detailList.add(new DetailPembayaranModel(
                                            jo.getString("id_transaksi"),
                                            jo.getString("image"),
                                            jo.getString("nominal"),
                                            jo.getString("tgl_transaksi"),
                                            jo.getString("status")
                                    ));
                                    adapter.notifyDataSetChanged();
                                }

                            } else {
                                Toast.makeText(activity, "No Data", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "ERROR RESPONSE", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(activity, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                    }
                });

        refreshLayout.setRefreshing(false);
    }

}