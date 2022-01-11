package com.cvgs.cvgsapp;

import android.graphics.Color;
import android.os.Build;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.advances.SessionManager;
import com.cvgs.cvgsapp.model.LoginModel;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout txtUser,txtPass,txtOTP;
    Button btnSend,btnLogin;
    SessionManager sessionManager;
    Constance constance = new Constance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else{
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_login);

        txtUser = findViewById(R.id.txtUsernameLogin);
        txtPass = findViewById(R.id.txtPasswordLogin);
        txtOTP = findViewById(R.id.txtOTPLogin);

        btnLogin = findViewById(R.id.btnLogin);
        btnSend = findViewById(R.id.btnSendOTP);

        sessionManager = new SessionManager(LoginActivity.this);

        AndroidNetworking.initialize(LoginActivity.this);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = txtUser.getEditText().getText().toString();
                if(!user.equals("")){
                    sendtoOTP(user);
                }else{
                    Toast.makeText(LoginActivity.this, "Insert Username First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = txtUser.getEditText().getText().toString();
                String pass = txtPass.getEditText().getText().toString();
                String otp = txtOTP.getEditText().getText().toString();
                if (!user.equals("") && !pass.equals("") && !otp.equals("")) {
                    loginAction(user,pass,otp);
                }else{
                    Toast.makeText(LoginActivity.this, "Complete the Form", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendtoOTP(String user){
        ProgressDialog pdg = new ProgressDialog(LoginActivity.this);
        pdg.setCancelable(false);
        pdg.setMessage("Loading...");
        pdg.show();
        AndroidNetworking.post(constance.server+"/api/send_email.php")
                .addBodyParameter("username",user)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String msg = response.getString("msg");
                            pdg.dismiss();
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                        }catch (Exception e){
                            e.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(LoginActivity.this, "Error Responses", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        pdg.dismiss();
                        Toast.makeText(LoginActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void loginAction(String user,String pass, String otp){
        ProgressDialog pdg = new ProgressDialog(LoginActivity.this);
        pdg.setCancelable(false);
        pdg.setMessage("Loading...");
        pdg.show();
        AndroidNetworking.post(constance.server+"/api/login.php")
                .addBodyParameter("username",user)
                .addBodyParameter("password",pass)
                .addBodyParameter("otp",otp)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            boolean status = response.getBoolean("status");
                            if(status){
                                JSONArray ja = response.getJSONArray("data");
                                LoginModel loginModel = new LoginModel();

                                for(int i= 0;i<ja.length();i++){
                                    JSONObject jo = ja.getJSONObject(i);
                                    loginModel.setEmail(jo.getString("email"));
                                    loginModel.setId_detail(jo.getString("id_detail"));
                                    loginModel.setId_user(jo.getString("id_user"));
                                    loginModel.setNama(jo.getString("nama"));
                                    loginModel.setRole(jo.getString("role"));
                                }

                                sessionManager.createLoginSession(loginModel);

                                pdg.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
                                finish();

                            }else{
                                pdg.dismiss();
                                Toast.makeText(LoginActivity.this, "Username/Password/OTP Invalid", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(LoginActivity.this, "Error Responses", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        pdg.dismiss();
                        Toast.makeText(LoginActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}