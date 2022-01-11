package com.cvgs.cvgsapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailNotificationActivity extends AppCompatActivity {

    ImageView btnBack;
    MaterialButton btnDelete;
    TextView tvTgl,tvDetail;
    String id_notification,date_notify,detail;
    Constance constance = new Constance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_notification);

        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        tvTgl = findViewById(R.id.tvTgl);
        tvDetail = findViewById(R.id.tvDetail);

        Intent currrentIntent = getIntent();
        if(currrentIntent.hasExtra("id_notification")){
            id_notification = currrentIntent.getStringExtra("id_notification");
            date_notify = currrentIntent.getStringExtra("date_notify");
            detail = currrentIntent.getStringExtra("detail");
        }else{
            startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
            finish();
        }

        AndroidNetworking.initialize(getApplicationContext());

        initializeData();

        setReadNotification(this);

        btnBack.setOnClickListener(view->{startActivity(new Intent(getApplicationContext(),NotificationActivity.class));finish();});

        btnDelete.setOnClickListener(view->{
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete this Notification")
                    .setMessage("Are you sure to delete this?")
                    .setNegativeButton("Cancel",(dialog,i)->dialog.cancel())
                    .setPositiveButton("Confirm",(dialog,i)->delete_notification(this)).show();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),NotificationActivity.class));finish();
    }

    private void initializeData(){
        tvTgl.setText(date_notify);
        tvDetail.setText(detail);
    }

    private void setReadNotification(Activity activity){
        AndroidNetworking.post(constance.server+"/api/notification/oncePost.php")
                .addBodyParameter("id_notification",id_notification)
                .addBodyParameter("purpose","read")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(!status){
                                Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
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
    }

    private void delete_notification(Activity activity){
        AndroidNetworking.post(constance.server+"/api/notification/oncePost.php")
                .addBodyParameter("id_notification",id_notification)
                .addBodyParameter("purpose","delete")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(!status){
                                Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(activity, "SUCCESS", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(activity,NotificationActivity.class));
                                finish();
                            }
                        }catch (JSONException e){
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
    }

}