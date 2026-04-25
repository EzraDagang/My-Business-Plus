package com.example.mybusinessplus;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply padding to the root container so the bottom bar stays above system nav
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
// Hide the Action Bar for a full-screen eWallet look
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        findViewById(R.id.layoutMyBusiness).setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, MerchantActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.layoutMyBusiness).setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, MerchantActivity.class);
            startActivity(intent);
        });

        View btnScan = findViewById(R.id.btnScan);

        btnScan.setOnClickListener(v -> {
            // Navigate to your scanner activity
            Intent intent = new Intent(HomePage.this, QrScannerActivity.class);
            startActivity(intent);
        });


//        RecyclerView rv = findViewById(R.id.rvRecommended);
//        rv.setLayoutManager(new GridLayoutManager(this, 4));
//
//        List<RecommendItem> items = new ArrayList<>();
////        items.add(new RecommendItem("CardMatch", R.drawable.ic_card));
//// ... add more
//
//        RecommendAdapter adapter = new RecommendAdapter(items);
//        rv.setAdapter(adapter);

        // Add Money Button Click
//        findViewById(R.id.btnAddMoney).setOnClickListener(v -> {
//            Toast.makeText(HomePage.this, "Opening Reload Screen...", Toast.LENGTH_SHORT).show();
//        });
    }
}