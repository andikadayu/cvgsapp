package com.cvgs.cvgsapp;

import android.graphics.Color;
import android.os.Build;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.adapter.DetailPendaftarAdapter;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.DetailPendaftarModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PendaftarDetailActivity extends AppCompatActivity {

    Button btnCreateAccount;
    TextView tvNama,tvDetail,tvAlamat;

    String id_detail,nama,detail,alamat,email;

    Constance constance = new Constance();

    RecyclerView recyDetailPendaftar;
    RecyclerView.Adapter adapter;
    ArrayList<DetailPendaftarModel> detailList;
    RecyclerView.LayoutManager layoutManager;

    ImageView btnBack;
    TextInputLayout txtUsername,txtPassword;

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pendaftar_detail);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        tvNama = findViewById(R.id.tvNama);
        tvDetail = findViewById(R.id.tvDetail);
        tvAlamat = findViewById(R.id.tvAlamat);

        recyDetailPendaftar = findViewById(R.id.recyDetailPendaftar);

        btnBack = findViewById(R.id.btnBack);
        refreshLayout = findViewById(R.id.refreshLayout);

        Intent currentIntent = getIntent();
        if(currentIntent.hasExtra("id_detail")){
            id_detail = currentIntent.getStringExtra("id_detail");
            nama = currentIntent.getStringExtra("nama");
            detail = currentIntent.getStringExtra("detail");
            alamat = currentIntent.getStringExtra("alamat");
            email = currentIntent.getStringExtra("email");
        }else{
            startActivity(new Intent(getApplicationContext(),PendaftarActivity.class));
            finish();
        }

        AndroidNetworking.initialize(getApplicationContext());

        detailList = new ArrayList<DetailPendaftarModel>();

        initializeProfile();

        initializeData(this);

        btnBack.setOnClickListener(view -> {startActivity(new Intent(getApplicationContext(),PendaftarActivity.class));finish();});

        btnCreateAccount.setOnClickListener(view->openDialoag(this));

        refreshLayout.setOnRefreshListener(()->{
            detailList = new ArrayList<>();
            initializeData(this);
        });
    }

    private void initializeProfile(){
        tvNama.setText(nama);
        tvDetail.setText(detail);
        tvAlamat.setText(alamat);
    }

    private void initializeData(Activity activity){
        AndroidNetworking.post(constance.server+"/api/pendaftar/getCheck.php")
                .addBodyParameter("id_detail",id_detail)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean has_account = response.getBoolean("has_account");
                            if(has_account){
                                btnCreateAccount.setVisibility(View.GONE);
                            }
                            boolean status = response.getBoolean("status");
                            if(status){

                                layoutManager = new LinearLayoutManager(activity, RecyclerView.VERTICAL, false);
                                recyDetailPendaftar.setLayoutManager(layoutManager);

                                recyDetailPendaftar.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));

                                adapter = new DetailPendaftarAdapter(activity,detailList,constance.server);

                                recyDetailPendaftar.setAdapter(adapter);

                                JSONArray ja = response.getJSONArray("data");
                                for(int i=0;i<ja.length();i++){
                                    JSONObject jo = ja.getJSONObject(i);

                                    detailList.add(new DetailPendaftarModel(
                                            jo.getString("id_daftar"),
                                            jo.getString("logo"),
                                            jo.getString("judul"),
                                            jo.getString("tool"),
                                            jo.getString("status")
                                    ));

                                    adapter.notifyDataSetChanged();

                                }

                            }else{
                                Toast.makeText(activity, "No Data", Toast.LENGTH_SHORT).show();
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

        refreshLayout.setRefreshing(false);
    }

    private void openDialoag(@NonNull Activity activity){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_create_account,null);
        txtUsername = view.findViewById(R.id.txtUsername);
        txtPassword = view.findViewById(R.id.txtPassword);
        new MaterialAlertDialogBuilder(activity)
                .setView(view)
                .setTitle("Create Account")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = txtUsername.getEditText().getText().toString();
                        String password = txtPassword.getEditText().getText().toString();
                        if(username.equals("") || password.equals("")){
                            Toast.makeText(activity, "Lengkapi Data", Toast.LENGTH_SHORT).show();
                        }else{
                            createAccount(activity,username,password);
                        }
                    }
                }).show();
    }

    private void createAccount(Activity activity,String username,String password){
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        AndroidNetworking.post(constance.server+"/api/pendaftar/createAccount.php")
                .addBodyParameter("id_detail",id_detail)
                .addBodyParameter("email",email)
                .addBodyParameter("username",username)
                .addBodyParameter("password",password)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            String msg = response.getString("msg");
                            progressDialog.dismiss();
                            if(status){
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                Intent sendData = new Intent(activity,PendaftarActivity.class);
                                sendData.putExtra("id_detail",id_detail);
                                sendData.putExtra("nama",nama);
                                sendData.putExtra("detail",detail);
                                sendData.putExtra("alamat",alamat);
                                sendData.putExtra("email",email);
                                startActivity(sendData);
                                finish();
                            }else{
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(activity, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(activity, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}