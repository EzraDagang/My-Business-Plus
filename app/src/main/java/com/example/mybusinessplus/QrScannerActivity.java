package com.example.mybusinessplus;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import android.webkit.URLUtil;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class QrScannerActivity extends AppCompatActivity {

    private View scanLine;

    private boolean isProcessing = false;
    private PreviewView viewFinder;
    private static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Enable Edge-to-Edge and set Layout
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qr_scanner);

        // 2. Initialize UI Components
        viewFinder = findViewById(R.id.viewFinder);
        scanLine = findViewById(R.id.scanLine);
        View btnBack = findViewById(R.id.btnBack);

        // 3. Handle System Bar Padding (Insets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 4. Back Button Logic
        btnBack.setOnClickListener(v -> finish());

        // 5. Start Scan Line Animation
        startScanAnimation();

        // 6. Camera Permission Check
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // 1. Preview Use Case (What the user sees)
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                // 2. Image Analysis Use Case (What the "brain" sees)
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), imageProxy -> {
                    processImageProxy(imageProxy);
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                // Bind BOTH preview and analysis to the lifecycle
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e("CameraX", "Binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void processImageProxy(ImageProxy imageProxy) {
        if (imageProxy.getImage() != null) {
            InputImage image = InputImage.fromMediaImage(
                    imageProxy.getImage(),
                    imageProxy.getImageInfo().getRotationDegrees()
            );

            BarcodeScanner scanner = BarcodeScanning.getClient();

            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            // SUCCESS! Handle the QR content here
                            handleQrResult(rawValue);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("MLKit", "Scan failed", e))
                    .addOnCompleteListener(task -> imageProxy.close()); // CRITICAL: Always close the proxy
        }
    }

    private void handleQrResult(String url) {
        if (isProcessing) return;
        isProcessing = true;

        // 1. Show a loading state (Optional but recommended)
        Toast.makeText(this, "Scanning Merchant...", Toast.LENGTH_SHORT).show();

        // 2. Initialize the Request Queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // 3. Create the GET Request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // SUCCESS: Send the raw JSON to your parser
                    parseAndNavigate(response);
                },
                error -> {
                    // ERROR: Handle timeout or 404s
                    isProcessing = false;
                    String message = "Error: ";
                    if (error.networkResponse != null) {
                        message += error.networkResponse.statusCode;
                    } else {
                        message += "Check internet connection";
                    }
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // 4. Add the request to the queue
        queue.add(stringRequest);
    }
    private void parseAndNavigate(String response) {
        Log.d("SERVER_RESPONSE", response); // Check this in Logcat!
        try {
            Gson gson = new Gson();
            CartItemResponse networkData = gson.fromJson(response, CartItemResponse.class);

            // FIX: We no longer look for networkData.itemName
            // Instead, we get the menu list from the response
            List<FoodItem> scannedMenu = networkData.menu;

            if (scannedMenu != null && !scannedMenu.isEmpty()) {
                // Pass the merchant name and the WHOLE list to the next screen
                navigateToMerchant(networkData.merchantName, new ArrayList<>(scannedMenu));
            } else {
                Toast.makeText(this, "Menu is empty!", Toast.LENGTH_SHORT).show();
                isProcessing = false;
            }

        } catch (Exception e) {
            Log.e("JSON_ERROR", "Parsing failed. Check if JSON matches POJO structure.", e);
            isProcessing = false;
        }
    }

    private void navigateToMerchant(String merchantName, ArrayList<FoodItem> menuList) {
        // Note: Ensure your next activity is named correctly (MerchantCart or MerchantActivity)
        Intent intent = new Intent(this, MerchantCartActivity.class);

        intent.putExtra("MERCHANT_NAME", merchantName);
        intent.putExtra("MENU_LIST", menuList); // This works because FoodItem is Serializable

        startActivity(intent);
        finish();
    }

    private void startScanAnimation() {
        // Moves the line down and back up continuously
        ObjectAnimator animator = ObjectAnimator.ofFloat(scanLine, "translationY", 0f, 1200f);
        animator.setDuration(2000); // 2 seconds per cycle
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to scan.", Toast.LENGTH_LONG).show();
                finish(); // Close activity if permission denied
            }
        }
    }
}