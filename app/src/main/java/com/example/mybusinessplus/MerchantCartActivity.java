package com.example.mybusinessplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MerchantCartActivity extends AppCompatActivity {
    private double total = 0.0;
    private int totalItems = 0;
    private TextView tvTotalPrice;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_cart);

        // 1. Initialize UI Components
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        TextView tvMerchantName = findViewById(R.id.tvMerchantName);

        ImageButton btnClose = findViewById(R.id.btnClose);

        // 2. Set the click listener
        btnClose.setOnClickListener(v -> {
            // Option A: Simply close this screen and go back to where we came from
            finish();

            /* Option B: If you specifically want to trigger a fresh Intent:
            Intent intent = new Intent(MerchantCartActivity.this, QrScannerActivity.class);
            startActivity(intent);
            finish();
            */
        });

        btnCheckout.setOnClickListener(v -> {
            performCheckout();
        });

        // 2. Get Data from Intent (Passed from QrScannerActivity)
        String merchantName = getIntent().getStringExtra("MERCHANT_NAME");
        // Cast the serializable extra back to our ArrayList
        ArrayList<FoodItem> items = (ArrayList<FoodItem>) getIntent().getSerializableExtra("MENU_LIST");

        // 3. Set Header Title
        if (merchantName != null) {
            tvMerchantName.setText(merchantName);
        }

        // 4. Setup RecyclerView
        RecyclerView rv = findViewById(R.id.rvMenu);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Use the items from the Intent instead of hardcoded mock data
        if (items != null) {
            MenuAdapter adapter = new MenuAdapter(items, (priceChange, countChange) -> {
                updateTotal(priceChange, countChange);
            });
            rv.setAdapter(adapter);

            // 5. Set Initial Totals
            // We loop through the items passed in case there is more than one initial item
            for (FoodItem item : items) {
                if (item.getQuantity() > 0) {
                    updateTotal(item.getPrice() * item.getQuantity(), item.getQuantity());
                }
            }
        }
    }

    // This method handles all updates to the bottom total bar
    private void updateTotal(double priceChange, int countChange) {
        total += priceChange;
        totalItems += countChange;

        // Ensure total doesn't go below 0 due to floating point math
        if (total < 0) total = 0;
        if (totalItems < 0) totalItems = 0;

        tvTotalPrice.setText(String.format("RM %.2f", total));
        btnCheckout.setText("Check Out (" + totalItems + ")");
    }

    private void performCheckout() {
        // 1. Prevent double-clicks
        btnCheckout.setEnabled(false);

        // 2. Prepare the Request (Assuming a POST to your Render server)
        String url = "https://tnghackathon-1.onrender.com/item/purchase"; // Replace with your actual endpoint
        RequestQueue queue = Volley.newRequestQueue(this);

        // We send the 'total' to the server
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // SUCCESS: Server accepted the payment
                    handlePaymentSuccess();
                },
                error -> {
                    // ERROR: Payment failed (e.g., timeout or server error)
                    Toast.makeText(this, "Payment Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCheckout.setEnabled(true);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("amount", String.valueOf(total));
                params.put("merchant", getIntent().getStringExtra("MERCHANT_NAME"));
                return params;
            }
        };

        queue.add(postRequest);
    }

    private void handlePaymentSuccess() {
        // 3. Update the local balance in SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        float currentBalance = prefs.getFloat("balance", 100.00f); // Default 100 if empty
        float newBalance = currentBalance - (float) total;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("balance", newBalance);
        editor.apply();

        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();

        // 4. Navigate back to HomePage and clear the "back stack"
        // This ensures that clicking 'back' doesn't take you back to the Cart/Scanner
        Intent intent = new Intent(this, HomePageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}