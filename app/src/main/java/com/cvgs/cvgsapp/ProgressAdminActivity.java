package com.cvgs.cvgsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.ProgressAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.cvgs.cvgsapp.model.ProgressModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProgressAdminActivity extends AppCompatActivity {

    Constance constance = new Constance();
    ImageView btnBack;
    RecyclerView recyProgress;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<ProgressModel> progressList;
    SessionManager sessionManager;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_progress_admin);

        btnBack = findViewById(R.id.btnBack);
        recyProgress = findViewById(R.id.recyProgress);
        refreshLayout = findViewById(R.id.refreshLayout);

        AndroidNetworking.initialize(getApplicationContext());
        progressList = new ArrayList<ProgressModel>();

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.getUserDetail().get(SessionManager.ROLE).equals("pendaftar")) {
            initializeUser(this);
        } else {
            initialize(this);
        }

        refreshLayout.setOnRefreshListener(() -> {
            progressList = new ArrayList<ProgressModel>();
            if (sessionManager.getUserDetail().get(SessionManager.ROLE).equals("pendaftar")) {
                initializeUser(this);
            } else {
                initialize(this);
            }
        });

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            finish();
        });
    }

    private void initialize(Activity activity) {
        AndroidNetworking.post(constance.server + "/api/progress/getAllProject.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
                                recyProgress.setLayoutManager(layoutManager);
                                recyProgress.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

                                adapter = new ProgressAdapter(activity, progressList, constance.server);

                                recyProgress.setAdapter(adapter);

                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);

                                    progressList.add(new ProgressModel(
                                            jo.getString("id_daftar"),
                                            jo.getString("judul"),
                                            jo.getString("detail"),
                                            jo.getString("logo"),
                                            jo.getString("progress")
                                    ));

                                    adapter.notifyDataSetChanged();

                                }

                            } else {
                                Toast.makeText(activity, "NO DATA", Toast.LENGTH_SHORT).show();
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

    private void initializeUser(Activity activity) {
        AndroidNetworking.post(constance.server + "/api/progress/getOnceProject.php")
                .addBodyParameter("id_detail", sessionManager.getUserDetail().get(SessionManager.ID_DETAIL))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
                                recyProgress.setLayoutManager(layoutManager);
                                recyProgress.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

                                adapter = new ProgressAdapter(activity, progressList, constance.server);

                                recyProgress.setAdapter(adapter);

                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);

                                    progressList.add(new ProgressModel(
                                            jo.getString("id_daftar"),
                                            jo.getString("judul"),
                                            jo.getString("detail"),
                                            jo.getString("logo"),
                                            jo.getString("progress")
                                    ));

                                    adapter.notifyDataSetChanged();

                                }

                            } else {
                                Toast.makeText(activity, "NO DATA", Toast.LENGTH_SHORT).show();
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