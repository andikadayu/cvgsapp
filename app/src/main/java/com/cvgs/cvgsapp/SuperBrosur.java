package com.cvgs.cvgsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.BrosurAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.BrosurModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuperBrosur extends AppCompatActivity {

    FloatingActionButton fabAdd;
    GridView girdBrosur;
    Constance constance = new Constance();
    ArrayAdapter<BrosurModel> adapter;
    ArrayList<BrosurModel> brosurList;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_brosur);

        fabAdd = findViewById(R.id.fabAddBrosur);
        girdBrosur = findViewById(R.id.gridAdminBrosur);
        btnBack = findViewById(R.id.btnBack);

        AndroidNetworking.initialize(SuperBrosur.this);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),UploadBrosurActivity.class));
            }
        });

        brosurList = new ArrayList<BrosurModel>();

        initialize();

        btnBack.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),ProfileActivity.class));finish();});

    }

    private  void initialize(){
        ProgressDialog pdg = new ProgressDialog(SuperBrosur.this);
        pdg.setCancelable(false);
        pdg.setMessage("Loading...");
        pdg.show();
        AndroidNetworking.post(constance.server+"/api/brosur/showBrosur.php")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            int count = response.getInt("count");
                            if(count > 0){
                                JSONArray ja = response.getJSONArray("data");
                                for(int i=0;i<ja.length();i++){
                                    JSONObject jo = ja.getJSONObject(i);
                                    brosurList.add(new BrosurModel(jo.getString("id_brosur"),jo.getString("tgl_brosur")));

                                }

                                adapter = new BrosurAdapter(SuperBrosur.this,brosurList,"super");
                                girdBrosur.setAdapter(adapter);

                                pdg.dismiss();

                            }else{
                                pdg.dismiss();
                                Toast.makeText(SuperBrosur.this, "Empty Brosur", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(SuperBrosur.this, "Error Response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        pdg.dismiss();
                        Toast.makeText(SuperBrosur.this, "Error Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}