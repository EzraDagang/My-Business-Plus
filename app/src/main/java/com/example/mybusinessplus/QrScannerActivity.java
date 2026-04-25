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

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import java.util.concurrent.Executors;

import android.net.Uri;
import android.webkit.URLUtil;
import androidx.browser.customtabs.CustomTabsIntent;

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

    private void handleQrResult(String data) {
        // Prevent multiple triggers from a single scan
        if (isProcessing) return;

        isProcessing = true;

        if (URLUtil.isValidUrl(data)) {
            try {
                // 1. Create a CustomTabsIntent
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

                // 2. Set the toolbar color (Note: Use R.color, not R.id)
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.tng_blue));

                // 3. Build and Launch
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(data));

                // Optional: Close the scanner once the link opens
                finish();
            } catch (Exception e) {
                Log.e("ScanError", "Could not open URL", e);
                isProcessing = false; // Reset if the browser fails to open
            }
        } else {
            // It's not a link (e.g., plain text or a serial number)
            Toast.makeText(this, "Scanned: " + data, Toast.LENGTH_LONG).show();

            // Reset the flag after a short delay so the user can scan something else
            new android.os.Handler().postDelayed(() -> {
                isProcessing = false;
            }, 2000); // 2 second "cooldown" before scanning again
        }
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