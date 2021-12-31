package com.cvgs.cvgsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.FocusFinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class RegisterFormActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PERMISSION = 10;

    Button btnNextBiodata, btnPrevPaket, btnNextPaket, btnPrevPembayaran, btnNextPembayaran, btnPrevDetail,
            btnNextDetail, btnPrevSetuju, btnNextSetuju, btnPilih;
    LinearLayout layoutBiodata, layoutPaket, layoutPembayaran, layoutDetail, layoutSetuju, linearCustom;
    TextInputLayout txtNama, txtAlamat, txtTelp, txtEmail, txtHarga, txtDP, txtJudul, txtJangka, txtGaransi,
            txtMonthTrain, txtDayTrain;
    TextInputEditText txtDate;
    ImageView imgPaket, imgBukti;

    TextView tvLayanan, tvPaket, tvFile;

    Spinner spnTools;

    CheckBox cbAgree;

    String id_paket, nama_layanan, nama_paket, img_paket;

    Constance constance = new Constance();

    ArrayList<CharSequence> listTools;

    final Calendar myCalendar = Calendar.getInstance();

    Bitmap bmpBukti;

    File filePenjelasan = null;

    private File cacheDir;

    String nama, alamat, no_telp, email, harga, nominal, judul, jangka, garansi, monthTrain, dayTrain, tools, training;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        AndroidNetworking.initialize(getApplicationContext());

        enablePermission();

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "documents");
        else
            cacheDir = getApplicationContext().getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        // Get Intent
        Intent Regis = getIntent();
        id_paket = Regis.getStringExtra("id_paket");
        nama_layanan = Regis.getStringExtra("nama_layanan");
        nama_paket = Regis.getStringExtra("nama_paket");
        img_paket = Regis.getStringExtra("img_paket");

        // layout Biodata
        layoutBiodata = findViewById(R.id.layoutBiodata);
        btnNextBiodata = findViewById(R.id.btnNextBiodata);
        btnNextBiodata.setOnClickListener(this);

        txtNama = findViewById(R.id.txtNama);
        txtAlamat = findViewById(R.id.txtAlamat);
        txtTelp = findViewById(R.id.txtTelp);
        txtEmail = findViewById(R.id.txtEmail);

        // layout Paket
        layoutPaket = findViewById(R.id.layoutPaket);
        btnPrevPaket = findViewById(R.id.btnPrevPaket);
        btnNextPaket = findViewById(R.id.btnNextPaket);
        btnPrevPaket.setOnClickListener(this);
        btnNextPaket.setOnClickListener(this);

        imgPaket = findViewById(R.id.imgPaket);
        tvLayanan = findViewById(R.id.tvLayanan);
        tvPaket = findViewById(R.id.tvPaket);
        txtHarga = findViewById(R.id.txtHarga);

        // layout Pembayaran
        layoutPembayaran = findViewById(R.id.layoutPembayaran);
        btnPrevPembayaran = findViewById(R.id.btnPrevPembayaran);
        btnNextPembayaran = findViewById(R.id.btnNextPembayaran);
        btnPrevPembayaran.setOnClickListener(this);
        btnNextPembayaran.setOnClickListener(this);

        imgBukti = findViewById(R.id.imgBukti);
        txtDP = findViewById(R.id.txtDP);

        // layout Detail
        layoutDetail = findViewById(R.id.layoutDetail);
        btnPrevDetail = findViewById(R.id.btnPrevDetail);
        btnNextDetail = findViewById(R.id.btnNextDetail);
        btnPrevDetail.setOnClickListener(this);
        btnNextDetail.setOnClickListener(this);

        linearCustom = findViewById(R.id.linearCustom);

        spnTools = findViewById(R.id.spnTools);
        txtJudul = findViewById(R.id.txtJudul);
        tvFile = findViewById(R.id.tvFile);
        btnPilih = findViewById(R.id.btnPilih);
        txtJangka = findViewById(R.id.txtJangka);
        txtGaransi = findViewById(R.id.txtGaransi);
        txtMonthTrain = findViewById(R.id.txtMonthTrain);
        txtDayTrain = findViewById(R.id.txtDayTrain);
        txtDate = findViewById(R.id.txtDate);

        btnPilih.setOnClickListener(this);

        // layout Setuju
        layoutSetuju = findViewById(R.id.layoutSetuju);
        btnPrevSetuju = findViewById(R.id.btnPrevSetuju);
        btnNextSetuju = findViewById(R.id.btnNextSetuju);
        btnPrevSetuju.setOnClickListener(this);
        btnNextSetuju.setOnClickListener(this);

        cbAgree = findViewById(R.id.cbAgree);

        txtDate.setOnClickListener(view -> {
            new DatePickerDialog(RegisterFormActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthofyear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayofmonth);
                    updateLabel();
                }
            }, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        imgBukti.setOnClickListener(view -> {
            selectImage();
        });

        initializePaket();
        initializeTools();
        initializeCustom();

    }

    private void enablePermission() {
        ActivityCompat.requestPermissions(RegisterFormActivity.this,
                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA },
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

    private void selectFile() {
        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-PowerPoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-Excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"Choose File"),300);
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose From Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterFormActivity.this);
        builder.setTitle("Upload Bukti Transfer");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);
                } else if (options[i].equals("Choose From Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 200);
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
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

            } else if (requestCode == 300) {
                assert data != null;
                final Uri selected = data.getData();

                try {
                    File dest = new File(cacheDir,selected.getLastPathSegment());
                    dest.createNewFile();
                    filePenjelasan = copy(selected,dest);
                    tvFile.setText(filePenjelasan.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public File copy(Uri src, File dst) throws IOException {
        try (InputStream in = RegisterFormActivity.this.getContentResolver().openInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
        return dst;
    }


    private String getStringImage(@NonNull Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; // In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtJangka.getEditText().setText(sdf.format(myCalendar.getTime()));
    }

    private void initializePaket() {
        tvPaket.setText(nama_paket);
        tvLayanan.setText(nama_layanan);
        Picasso.get().load(img_paket).into(imgPaket);
    }

    private void initializeTools() {
        listTools = new ArrayList<>();

        AndroidNetworking.post(constance.server + "/api/register/getList.php")
                .addBodyParameter("id_paket", id_paket)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray ja = response.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                listTools.add(ja.getString(i));
                            }
                            ArrayAdapter adapter = new ArrayAdapter(RegisterFormActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, listTools);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spnTools.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterFormActivity.this, "Error Response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(RegisterFormActivity.this, "Error Connection", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void initializeCustom() {
        AndroidNetworking.post(constance.server + "/api/register/checkCustom.php")
                .addBodyParameter("id_paket", id_paket)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                linearCustom.setVisibility(View.VISIBLE);
                            } else {
                                linearCustom.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterFormActivity.this, "Error Response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(RegisterFormActivity.this, "Error Connection," +anError.getMessage() , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void gotoRegister() {

        String img_transaksi = getStringImage(bmpBukti);
        ProgressDialog pdg = new ProgressDialog(RegisterFormActivity.this);
        pdg.setCancelable(false);
        pdg.setMessage("Loading...");
        pdg.show();

        AndroidNetworking.upload(constance.server+"/api/register/addRegister.php")
                .addMultipartParameter("nama",nama)
                .addMultipartParameter("alamat",alamat)
                .addMultipartParameter("no_telp",no_telp)
                .addMultipartParameter("email",email)
                .addMultipartParameter("id_paket",id_paket)
                .addMultipartParameter("harga",harga)
                .addMultipartParameter("judul",judul)
                .addMultipartParameter("tools",tools)
                .addMultipartFile("penjelasan",filePenjelasan)
                .addMultipartParameter("jangka",jangka)
                .addMultipartParameter("training",training)
                .addMultipartParameter("garansi",garansi)
                .addMultipartParameter("img_transaksi",img_transaksi)
                .addMultipartParameter("nominal",nominal)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // Do Something
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            boolean status = response.getBoolean("status");
                            if(status){
                                pdg.dismiss();
                                Toast.makeText(RegisterFormActivity.this, "Berhasil Mendaftar, Silahkan Menunggu Informasi Lebih Lanjut", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterFormActivity.this,HomeActivity.class));
                                finish();

                            }else{
                                pdg.dismiss();
                                Toast.makeText(RegisterFormActivity.this, "Gagal Mendaftar", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(RegisterFormActivity.this, "ERROR RESPONSE", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        pdg.dismiss();
                        Toast.makeText(RegisterFormActivity.this, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkNext(@NonNull String from) {
        switch (from) {
            case "biodata":
                nama = txtNama.getEditText().getText().toString();
                alamat = txtAlamat.getEditText().getText().toString();
                no_telp = txtTelp.getEditText().getText().toString();
                email = txtEmail.getEditText().getText().toString();
                return !nama.equals("") && !alamat.equals("") && !no_telp.equals("") && !email.equals("");

            case "paket":
                harga = txtHarga.getEditText().getText().toString();
                return !harga.equals("");

            case "pembayaran":
                nominal = txtDP.getEditText().getText().toString();

                return !nominal.equals("") && bmpBukti != null;

            case "detail":
                judul = txtJudul.getEditText().getText().toString();
                tools = spnTools.getSelectedItem().toString();
                jangka = txtJangka.getEditText().getText().toString();
                garansi = txtGaransi.getEditText().getText().toString();
                monthTrain = txtMonthTrain.getEditText().toString();
                dayTrain = txtDayTrain.getEditText().toString();
                training = monthTrain + " Bulan " + dayTrain + " Hari";
                return !tools.equals("") && !judul.equals("") && filePenjelasan != null;

            case "setuju":
                return cbAgree.isChecked();

            default:
                return false;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Button b = (Button) view;
        switch (b.getId()) {
            case R.id.btnNextBiodata:
                if (checkNext("biodata")) {
                    layoutBiodata.setVisibility(View.GONE);
                    layoutPaket.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RegisterFormActivity.this, "Lengkapi Data", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnNextPaket:
                if (checkNext("paket")) {
                    layoutPaket.setVisibility(View.GONE);
                    layoutPembayaran.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RegisterFormActivity.this, "Lengkapi Data", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnPrevPaket:
                layoutPaket.setVisibility(View.GONE);
                layoutBiodata.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNextPembayaran:
                if (checkNext("pembayaran")) {
                    layoutPembayaran.setVisibility(View.GONE);
                    layoutDetail.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RegisterFormActivity.this, "Lengkapi Data", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnPrevPembayaran:
                layoutPembayaran.setVisibility(View.GONE);
                layoutPaket.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNextDetail:
                if (checkNext("detail")) {
                    layoutDetail.setVisibility(View.GONE);
                    layoutSetuju.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(RegisterFormActivity.this, "Lengkapi Data", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnPrevDetail:
                layoutDetail.setVisibility(View.GONE);
                layoutPembayaran.setVisibility(View.VISIBLE);
                break;
            case R.id.btnPrevSetuju:
                layoutSetuju.setVisibility(View.GONE);
                layoutDetail.setVisibility(View.VISIBLE);
                break;
            case R.id.btnNextSetuju:
                if (checkNext("setuju")) {
                    gotoRegister();
                } else {
                    Toast.makeText(RegisterFormActivity.this, "Setujui Peraturan Dan Ketentuan", Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            case R.id.btnPilih:
                selectFile();
                break;

            default:
                break;
        }
    }

}