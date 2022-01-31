package com.cvgs.cvgsapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.ExploreAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.NotificationReceiver;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.cvgs.cvgsapp.model.ExploreModel;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ImageSlider bannerBrosur;
    Constance constance = new Constance();
    List<SlideModel> brosurList;
    GridView gridHome;
    ArrayAdapter<ExploreModel> adapter;
    ArrayList<ExploreModel> exploreList;
    LinearLayout linearBawah;
    ImageView homeProfile;
    FrameLayout homeNotif;
    TextView homeName, tvNotification;
    SessionManager sessionManager;
    String nama, role, id_detail;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        bannerBrosur = findViewById(R.id.bannerBrosur);
        gridHome = findViewById(R.id.gridHome);
        linearBawah = findViewById(R.id.linearBawah);

        homeName = findViewById(R.id.homeName);
        homeProfile = findViewById(R.id.homeProfile);
        homeNotif = findViewById(R.id.homeNotif);
        refreshLayout = findViewById(R.id.refreshLayout);

        tvNotification = findViewById(R.id.tvNotification);

        brosurList = new ArrayList<SlideModel>();
        exploreList = new ArrayList<ExploreModel>();

        sessionManager = new SessionManager(HomeActivity.this);

        AndroidNetworking.initialize(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            nama = sessionManager.getUserDetail().get(SessionManager.NAMA);
            role = sessionManager.getUserDetail().get(SessionManager.ROLE);
            id_detail = sessionManager.getUserDetail().get(SessionManager.ID_DETAIL);

            if (!isMyServiceRunning(NotificationReceiver.class)) {
                startService(new Intent(getApplicationContext(), NotificationReceiver.class));
            }

            homeNotif.setVisibility(View.VISIBLE);
            homeName.setText(nama);
            getNotificationNumber();
        } else {
            homeNotif.setVisibility(View.GONE);
            homeName.setText(getString(R.string.not_logged_in));
        }


        loadData();

        initialize();

        homeProfile.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        homeName.setOnClickListener(view -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        homeNotif.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), NotificationActivity.class)));

        refreshLayout.setOnRefreshListener(() -> {
            brosurList = new ArrayList<SlideModel>();
            exploreList = new ArrayList<ExploreModel>();
            loadData();
            initialize();
            if (sessionManager.isLoggedIn()) {
                getNotificationNumber();
            }
            refreshLayout.setRefreshing(false);
        });

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

    private void getNotificationNumber() {
        AndroidNetworking.post(constance.server + "/api/notification/getNumber.php")
                .addBodyParameter("role", role)
                .addBodyParameter("id_detail", id_detail)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                String jumlah = response.getString("jumlah");
                                tvNotification.setText(jumlah);
                                tvNotification.setVisibility(View.VISIBLE);
                            } else {
                                tvNotification.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    private void loadData() {
        AndroidNetworking.post(constance.server + "/api/brosur/showBrosur.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int count = response.getInt("count");
                            if (count > 0) {
                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    String url = constance.server + "/api/brosur/assets/brosur-" + jo.getString("id_brosur") + ".png";
                                    brosurList.add(new SlideModel(url));
                                }

                                bannerBrosur.setImageList(brosurList, true);
                                bannerBrosur.setItemClickListener(position -> {
                                    SlideModel slideModel = (SlideModel) brosurList.get(position);
                                    String url = slideModel.getImageUrl();
                                    assert url != null;
                                    String[] cls = url.split("brosur-");
                                    String[] clss = cls[1].split("\\.");
                                    String id = clss[0];
                                    Intent kirimData = new Intent(HomeActivity.this, show_detail_brosur.class);
                                    kirimData.putExtra("id_brosur", id);
                                    startActivity(kirimData);
                                });


                            } else {
                                Toast.makeText(HomeActivity.this, "Empty Brosur", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(HomeActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initialize() {


        AndroidNetworking.post(constance.server + "/api/explore/showExplore.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int heights = 100;

                            JSONArray ja = response.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                exploreList.add(new ExploreModel(
                                        jo.getString("id_layanan"),
                                        jo.getString("nama_layanan"),
                                        jo.getString("isi_layanan"),
                                        jo.getString("logo_layanan"),
                                        "#" + jo.getString("color_layanan")
                                ));

                                heights += 100;

                            }
                            adapter = new ExploreAdapter(HomeActivity.this, exploreList);
                            gridHome.setAdapter(adapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Error Response", Toast.LENGTH_SHORT).show();
                            initialize();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(HomeActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                        initialize();
                    }
                });

    }
}