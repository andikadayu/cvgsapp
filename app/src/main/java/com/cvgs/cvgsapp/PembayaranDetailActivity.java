package com.cvgs.cvgsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.cvgs.cvgsapp.adapter.DetailPembayaranAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.DetailPembayaranModel;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PembayaranDetailActivity extends AppCompatActivity {

    TextView tvNama, tvDetail, tvAlamat;
    Constance constance = new Constance();
    ImageView logoApps, btnBack;

    RecyclerView recyDetailPembayaran;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<DetailPembayaranModel> detailList;


    SwipeRefreshLayout refreshLayout;

    String id_daftar, judul, detail, logo, sisa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pembayaran_detail);

        recyDetailPembayaran = findViewById(R.id.recyDetailPembayaran);
        tvNama = findViewById(R.id.tvNama);
        tvDetail = findViewById(R.id.tvDetail);
        tvAlamat = findViewById(R.id.tvAlamat);
        logoApps = findViewById(R.id.logoApps);
        btnBack = findViewById(R.id.btnBack);
        refreshLayout = findViewById(R.id.refreshLayout);

        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra("id_daftar")) {
            id_daftar = currentIntent.getStringExtra("id_daftar");
            judul = currentIntent.getStringExtra("judul");
            detail = currentIntent.getStringExtra("detail");
            logo = currentIntent.getStringExtra("logo");
            sisa = currentIntent.getStringExtra("sisa");
        } else {
            startActivity(new Intent(getApplicationContext(), PembayaranActivity.class));
            finish();
        }

        AndroidNetworking.initialize(getApplicationContext());

        detailList = new ArrayList<DetailPembayaranModel>();

        initializeProfile();

        initializeData(this);

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), PembayaranActivity.class));
            finish();
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

                                adapter = new DetailPembayaranAdapter(activity, detailList, constance.server);

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