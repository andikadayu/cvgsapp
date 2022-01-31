package com.cvgs.cvgsapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PayActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 10;

    TextView tvNama, tvDetail, tvAlamat;
    Constance constance = new Constance();
    ImageView logoApps, btnBack, imgBukti;
    TextInputLayout txtNominal;
    Bitmap bmpBukti;
    String id_daftar, judul, detail, logo, sisa;
    Button btnKirim;
    String nominal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);

        tvNama = findViewById(R.id.tvNama);
        tvDetail = findViewById(R.id.tvDetail);
        tvAlamat = findViewById(R.id.tvAlamat);
        logoApps = findViewById(R.id.logoApps);
        btnBack = findViewById(R.id.btnBack);
        imgBukti = findViewById(R.id.imgBukti);
        txtNominal = findViewById(R.id.txtNominal);
        btnKirim = findViewById(R.id.btnKirim);

        enablePermission();

        Intent currentIntent = getIntent();
        if (currentIntent.hasExtra("id_daftar")) {
            id_daftar = currentIntent.getStringExtra("id_daftar");
            judul = currentIntent.getStringExtra("judul");
            detail = currentIntent.getStringExtra("detail");
            logo = currentIntent.getStringExtra("logo");
            sisa = currentIntent.getStringExtra("sisa");
        } else {
            startActivity(new Intent(getApplicationContext(), TransaksiActivity.class));
            finish();
        }

        AndroidNetworking.initialize(getApplicationContext());

        initializeProfile();

        btnBack.setOnClickListener(view -> {
            Intent sendData = new Intent(getApplicationContext(), TransaksiDetailActivity.class);
            sendData.putExtra("id_daftar", id_daftar);
            sendData.putExtra("judul", judul);
            sendData.putExtra("detail", detail);
            sendData.putExtra("logo", logo);
            sendData.putExtra("sisa", sisa);
            startActivity(sendData);
            finish();
        });

        imgBukti.setOnClickListener(view -> selectImage());

        btnKirim.setOnClickListener(view -> {
            if (checkSend()) {
                sendData();
            } else {
                Toast.makeText(getApplicationContext(), "Complete Form", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void enablePermission() {
        ActivityCompat.requestPermissions(PayActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                enablePermission();
            }
        }
    }

    private void initializeProfile() {
        Picasso.get().load(constance.server + logo).into(logoApps);
        tvNama.setText(judul);
        tvDetail.setText(detail);
        tvAlamat.setText(sisa);
    }

    private void selectImage() {
        String[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
        new MaterialAlertDialogBuilder(PayActivity.this)
                .setTitle("Upload Bukti Transfer")
                .setItems(options, ((dialogInterface, i) -> {
                    if (i == 2) {
                        dialogInterface.cancel();
                    } else if (i == 1) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 200);
                    } else if (i == 0) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 100);
                    }
                })).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            if (requestCode == 100) {
                bmpBukti = (Bitmap) data.getExtras().get("data");
                imgBukti.setImageBitmap(bmpBukti);

            } else if (requestCode == 200) {
                Uri selectedImage = data.getData();
                try {
                    bmpBukti = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    imgBukti.setImageBitmap(bmpBukti);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Boolean checkSend() {
        nominal = txtNominal.getEditText().getText().toString();
        return !nominal.equals("") && bmpBukti != null;
    }

    private String getStringImage(@NonNull Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void sendData() {
        String img_transaksi = getStringImage(bmpBukti);
        ProgressDialog pdg = new ProgressDialog(PayActivity.this);
        pdg.setCancelable(false);
        pdg.setMessage("Loading...");
        pdg.show();
        AndroidNetworking.post(constance.server + "/api/pembayaran/pay.php")
                .addBodyParameter("id_daftar", id_daftar)
                .addBodyParameter("img_bukti", img_transaksi)
                .addBodyParameter("nominal", nominal)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                pdg.dismiss();
                                Toast.makeText(PayActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PayActivity.this, TransaksiActivity.class));
                                finish();
                            } else {
                                pdg.dismiss();
                                Toast.makeText(PayActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(PayActivity.this, "ERROR RESPONSE", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        pdg.dismiss();
                        Toast.makeText(PayActivity.this, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}