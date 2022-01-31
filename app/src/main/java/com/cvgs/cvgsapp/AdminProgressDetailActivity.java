package com.cvgs.cvgsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.DetailProgressAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.cvgs.cvgsapp.model.DetailProgressModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminProgressDetailActivity extends AppCompatActivity {

    TextView tvNama, tvDetail, tvAlamat;
    Constance constance = new Constance();
    ImageView logoApps, btnBack;
    FloatingActionButton fabAddProgress, fabShowDetail;

    RecyclerView recyProgressDetail;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<DetailProgressModel> detailList;

    String id_daftar, judul, detail, logo, progress;

    SessionManager sessionManager;

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_progress_detail);

        tvNama = findViewById(R.id.tvNama);
        tvDetail = findViewById(R.id.tvDetail);
        tvAlamat = findViewById(R.id.tvAlamat);
        logoApps = findViewById(R.id.logoApps);
        btnBack = findViewById(R.id.btnBack);
        fabAddProgress = findViewById(R.id.fabAddProgress);
        recyProgressDetail = findViewById(R.id.recyProgressDetail);
        fabShowDetail = findViewById(R.id.fabShowDetail);
        refreshLayout = findViewById(R.id.refreshLayout);

        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra("id_daftar")) {
            id_daftar = currentIntent.getStringExtra("id_daftar");
            judul = currentIntent.getStringExtra("judul");
            detail = currentIntent.getStringExtra("detail");
            logo = currentIntent.getStringExtra("logo");
            progress = currentIntent.getStringExtra("progress");
        } else {
            startActivity(new Intent(getApplicationContext(), ProgressAdminActivity.class));
            finish();
        }

        sessionManager = new SessionManager(getApplicationContext());

        detailList = new ArrayList<DetailProgressModel>();
        AndroidNetworking.initialize(getApplicationContext());

        initializeProfile();

        initializeData(this);

        if (!sessionManager.getUserDetail().get(SessionManager.ROLE).equals("pendaftar")) {
            fabAddProgress.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ProgressAdminActivity.class));
            finish();
        });
        fabAddProgress.setOnClickListener(view -> {
            Intent sendData = new Intent(getApplicationContext(), AddProgressActivity.class);
            sendData.putExtra("id_daftar", id_daftar);
            sendData.putExtra("judul", judul);
            sendData.putExtra("detail", detail);
            sendData.putExtra("logo", logo);
            sendData.putExtra("progress", progress);
            startActivity(sendData);
        });

        fabShowDetail.setOnClickListener(view -> {
            Intent sendData = new Intent(getApplicationContext(), DetailProjectActivity.class);
            sendData.putExtra("id_daftar", id_daftar);
            startActivity(sendData);
        });

        refreshLayout.setOnRefreshListener(() -> {
            detailList = new ArrayList<DetailProgressModel>();
            initializeData(this);
        });

    }

    private void initializeProfile() {
        Picasso.get().load(constance.server + logo).into(logoApps);
        tvNama.setText(judul);
        tvDetail.setText(detail);
        tvAlamat.setText(progress);
    }

    private void initializeData(Activity activity) {
        AndroidNetworking.post(constance.server + "/api/progress/getAllProgress.php")
                .addBodyParameter("id_daftar", id_daftar)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            boolean status = response.getBoolean("status");

                            if (status) {
                                layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
                                recyProgressDetail.setLayoutManager(layoutManager);

                                adapter = new DetailProgressAdapter(activity, detailList, constance.server, logo);

                                recyProgressDetail.setAdapter(adapter);

                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);

                                    detailList.add(new DetailProgressModel(
                                            jo.getString("id_progress"),
                                            jo.getString("id_daftar"),
                                            jo.getString("progress") + "%",
                                            jo.getString("isi_progress"),
                                            jo.getString("tgl_progress"),
                                            jo.getString("screenshot"),
                                            jo.getString("video")
                                    ));

                                    adapter.notifyDataSetChanged();
                                }

                            } else {
                                Toast.makeText(activity, "No Data", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "ERROR RESPONSES", Toast.LENGTH_SHORT).show();
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