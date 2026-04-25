package com.example.mybusinessplus;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_qr extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr);

        // System Bar Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Back Button
        LinearLayout btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 2. Generate and Display QR
        ImageView qrImageView = findViewById(R.id.qrImageView);

        // This Base64 translates to "Hello UM!"
        String myBase64Data = "SGVsbG8gVU0h";

        try {
            Bitmap qrBitmap = QrUtils.generateQrFromBase64(myBase64Data, 512);
            if (qrBitmap != null) {
                qrImageView.setImageBitmap(qrBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load QR", Toast.LENGTH_SHORT).show();
        }

        // 3. Share Button (Placeholder logic for now)
        Button btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "Share feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
}