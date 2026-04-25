package com.example.mybusinessplus;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Volley Imports
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class activity_qr extends AppCompatActivity {

    private ImageView qrImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr);

        // System Bar Padding (to handle notch/status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Link UI - FIXED: We use generic 'View' to avoid ClassCastException
        qrImageView = findViewById(R.id.qrImageView);

        // Find the back button (whether it's an ImageView or Layout in XML)
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Find the share button
        View btnShare = findViewById(R.id.btnShare);
        if (btnShare != null) {
            btnShare.setOnClickListener(v ->
                    Toast.makeText(this, "Share feature coming soon!", Toast.LENGTH_SHORT).show()
            );
        }

        // 2. Start the Network Request
        // IMPORTANT: Replace with your actual backend URL during the hackathon
        String base64 = fetchQrFromServer("");
        setQrFromBase64(base64);
    }

    /**
     * Uses Volley to fetch the Base64 string from your backend
     */
    private void setQrFromBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            Toast.makeText(this, "Base64 data is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Use your utility class to convert the string to a QR bitmap
            Bitmap qrBitmap = QrUtils.generateQrFromBase64(base64String, 512);

            if (qrBitmap != null) {
                // Update the UI
                qrImageView.setImageBitmap(qrBitmap);
            } else {
                Toast.makeText(this, "Failed to generate QR bitmap", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String fetchQrFromServer(String merchantId) {
        // 1. Construct the dynamic URL
        String url = "https://tnghackathon-1.onrender.com/qr/" + merchantId;

        RequestQueue queue = Volley.newRequestQueue(this);

        // 2. Create the Request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // SUCCESS: The 'response' variable is your Base64 string
                    // Pass it directly to your conversion method
                    setQrFromBase64(response);
                },
                error -> {
                    // FAILURE: Handle errors (e.g., ID not found or server down)
                    String message = "Error: ";
                    if (error.networkResponse != null) {
                        message += "Status Code " + error.networkResponse.statusCode;
                    } else {
                        message += "Server unreachable";
                    }
                    Toast.makeText(activity_qr.this, message, Toast.LENGTH_LONG).show();
                });

        // 3. Add to queue
        queue.add(stringRequest);
        return (null);
    }
}