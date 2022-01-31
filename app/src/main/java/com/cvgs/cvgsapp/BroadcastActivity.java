package com.cvgs.cvgsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.BroadcastModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BroadcastActivity extends AppCompatActivity {

    ImageView btnBack;
    TextInputLayout txtDescription;
    MaterialButton btnSend;
    ListView lvListPendaftar;
    ArrayList<BroadcastModel> modelArrayList;
    Constance constance = new Constance();
    ArrayAdapter adapter;
    MaterialCheckBox cbAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        btnBack = findViewById(R.id.btnBack);
        txtDescription = findViewById(R.id.txtDescription);
        btnSend = findViewById(R.id.btnSend);
        lvListPendaftar = findViewById(R.id.lvListPendaftar);
        cbAll = findViewById(R.id.cbAll);

        AndroidNetworking.initialize(getApplicationContext());

        lvListPendaftar.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        modelArrayList = new ArrayList<>();

        initializeData(this);

        cbAll.setOnClickListener((view) -> {
            if (cbAll.isChecked()) {
                for (int a = 0; a < modelArrayList.size(); a++) {
                    lvListPendaftar.setItemChecked(a, true);
                }
            } else {
                for (int a = 0; a < modelArrayList.size(); a++) {
                    lvListPendaftar.setItemChecked(a, false);
                }
            }
        });

        btnBack.setOnClickListener((view) -> {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });

        btnSend.setOnClickListener((view -> printSelectedItem(this)));
    }

    private void initializeData(Activity activity) {
        AndroidNetworking.post(constance.server + "/api/notification/getListPendaftar.php")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    modelArrayList.add(new BroadcastModel(jo.getString("id_detail"), jo.getString("name")));
                                }

                                adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_multiple_choice, modelArrayList);
                                lvListPendaftar.setAdapter(adapter);

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
    }

    private void printSelectedItem(Activity activity) {
        SparseBooleanArray sp = lvListPendaftar.getCheckedItemPositions();
        ArrayList<String> allDetail = new ArrayList<>();
        String description = txtDescription.getEditText().getText().toString();
        if (sp.size() > 0) {
            for (int i = 0; i < sp.size(); i++) {
                if (sp.valueAt(i)) {
                    BroadcastModel broadcastModel = (BroadcastModel) lvListPendaftar.getItemAtPosition(i);
                    allDetail.add(broadcastModel.getId_detail());
                }
            }

            String allDetails = allDetail.toString();
            String detail = allDetails.replace("[", "").replace("]", "");
            if (!description.equals("") && !allDetail.isEmpty()) {
                sendToServer(activity, detail, description);
            } else {
                Toast.makeText(activity, "Complete Form", Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(activity, "Complete Form", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToServer(Activity activity, String detail, String description) {
        AndroidNetworking.post(constance.server + "/api/notification/addBrodcast.php")
                .addBodyParameter("description", description)
                .addBodyParameter("detail", detail)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(activity, "Send Broadcast Success", Toast.LENGTH_SHORT).show();
                                txtDescription.getEditText().setText("");
                                cbAll.setChecked(false);
                                for (int a = 0; a < modelArrayList.size(); a++) {
                                    lvListPendaftar.setItemChecked(a, false);
                                }
                            } else {
                                Toast.makeText(activity, "Send Broadcast Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "ERRPR RESPONSE", Toast.LENGTH_SHORT).show();
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