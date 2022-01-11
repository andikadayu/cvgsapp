package com.cvgs.cvgsapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.NotificationAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.cvgs.cvgsapp.model.NotificationModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    Constance constance = new Constance();
    ImageView btnBack;
    SessionManager sessionManager;
    MaterialButton btnClear;

    RecyclerView recyNotification;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<NotificationModel> notificationList;

    SwipeRefreshLayout refreshLayout;

    String id_detail,role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        btnBack = findViewById(R.id.btnBack);
        btnClear = findViewById(R.id.btnClear);
        recyNotification = findViewById(R.id.recyNotification);
        refreshLayout = findViewById(R.id.refreshLayout);

        sessionManager = new SessionManager(getApplicationContext());
        id_detail = sessionManager.getUserDetail().get(SessionManager.ID_DETAIL);
        role = sessionManager.getUserDetail().get(SessionManager.ROLE);

        AndroidNetworking.initialize(getApplicationContext());

        notificationList = new ArrayList<NotificationModel>();

        initialize(this);

        btnBack.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),HomeActivity.class));finish();});

        btnClear.setOnClickListener(view->{
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Clear Notification")
                    .setMessage("Are you sure to clear all notification?")
                    .setNegativeButton("Cancel",(dialog,i)->dialog.cancel())
                    .setPositiveButton("Confirm",(dialog,i)->clearAllNotification(this)).show();
        });

        refreshLayout.setOnRefreshListener(()->{
            notificationList = new ArrayList<NotificationModel>();
            initialize(this);
        });

    }

    private void initialize(Activity activity){
        AndroidNetworking.post(constance.server+"/api/notification/getListNotification.php")
                .addBodyParameter("id_detail",id_detail)
                .addBodyParameter("role",role)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
                                recyNotification.setLayoutManager(layoutManager);

                                recyNotification.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
                                adapter = new NotificationAdapter(activity,notificationList);

                                recyNotification.setAdapter(adapter);

                                JSONArray ja = response.getJSONArray("data");
                                for(int i = 0;i<ja.length();i++){
                                    JSONObject jo =ja.getJSONObject(i);

                                    notificationList.add(new NotificationModel(
                                       jo.getString("id_notification"),
                                            jo.getString("description"),
                                            jo.getString("details"),
                                            jo.getString("date_notify"),
                                            jo.getString("datetime_notify"),
                                            jo.getBoolean("is_read")
                                    ));

                                    adapter.notifyDataSetChanged();
                                }

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

    private void clearAllNotification(Activity activity){
        AndroidNetworking.post(constance.server+"/api/notification/clearAll.php")
                .addBodyParameter("id_detail",id_detail)
                .addBodyParameter("role",role)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                Toast.makeText(activity, "SUCCESS CLEAR", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(activity,NotificationActivity.class));
                                finish();
                            }else{
                                Toast.makeText(activity, "ERROR CLEAR", Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
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
    }
}