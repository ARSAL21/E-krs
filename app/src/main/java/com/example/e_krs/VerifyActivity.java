package com.example.e_krs;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import network.ApiClient;

public class VerifyActivity extends AppCompatActivity {

    private EditText etNim, etNama;
    private Button btnVerify;

    // Auth UID didapat dari register/login sebelumnya
    private String authUid = ""; // isi dengan uid dari FirebaseAuth.getCurrentUser().getUid()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        etNim = findViewById(R.id.etNim);
        etNama = findViewById(R.id.etNama);
        btnVerify = findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nim = etNim.getText().toString().trim();
                String nama = etNama.getText().toString().trim();

                if (nim.isEmpty() || nama.isEmpty()) {
                    Toast.makeText(VerifyActivity.this, "NIM dan Nama wajib diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyMahasiswa(nim, nama);
            }
        });
    }

    private void verifyMahasiswa(String nim, String nama) {
        Map<String, String> payload = new HashMap<>();
        payload.put("nim", nim);
        payload.put("nama", nama);
        payload.put("auth_uid", authUid);

        ApiClient.post("/api/mahasiswa/verify", payload, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                boolean success = response.optBoolean("success", false);
                String message = response.optString("message", "Unknown");

                if (success) {
                    Toast.makeText(VerifyActivity.this, "Verifikasi berhasil!", Toast.LENGTH_SHORT).show();
                    finish(); // lanjut ke halaman utama
                } else {
                    Toast.makeText(VerifyActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(VerifyActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
