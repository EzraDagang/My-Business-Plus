package com.example.mybusinessplus;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MerchantActivity extends AppCompatActivity {

    private TextView tvNetRevenue, tvStatusBadge;
    private double dailyNetResult = 0;

    // Helper class for inventory math
    class InventoryItem {
        String name; double price; int startQty; int unsoldQty;
        public InventoryItem(String name, double price, int startQty, int unsoldQty) {
            this.name = name; this.price = price; this.startQty = startQty; this.unsoldQty = unsoldQty;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        // 1. Link UI Elements
        tvNetRevenue = findViewById(R.id.tvNetRevenue);
        tvStatusBadge = findViewById(R.id.tvStatusBadge); // The new singular badge
        Spinner spinnerTimeframe = findViewById(R.id.spinnerTimeframe);

        // 2. Navigation "Bridge" Logic
        findViewById(R.id.nav_inventory).setOnClickListener(v ->
                startActivity(new Intent(MerchantActivity.this, AddFoodActivity.class))
        );

        findViewById(R.id.nav_history).setOnClickListener(v ->
                startActivity(new Intent(MerchantActivity.this, HistoryActivity.class))
        );

        findViewById(R.id.nav_insights).setOnClickListener(v ->
                startActivity(new Intent(MerchantActivity.this, activity_insights.class))
        );

        findViewById(R.id.nav_qr).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, activity_qr.class))
        );

        // 3. Dropdown (Spinner) Setup
        String[] options = {"Daily", "This Week", "This Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeframe.setAdapter(adapter);

        // 4. Mock Data & Math
        List<InventoryItem> items = new ArrayList<>();
        items.add(new InventoryItem("Karipap", 1.50, 100, 20));
        items.add(new InventoryItem("Nasi Lemak", 3.00, 50, 2));
        items.add(new InventoryItem("Kuih Lapis", 1.00, 80, 0));

        double rev = 0; double loss = 0;
        for (InventoryItem item : items) {
            int sold = item.startQty - item.unsoldQty;
            rev += (sold * item.price);
            loss += (item.unsoldQty * item.price);
        }
        dailyNetResult = rev - loss;

        // 5. Spinner Listener
        spinnerTimeframe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) updateDashboard(1450.75); // Week Mock Data
                else if (position == 2) updateDashboard(5820.00); // Month Mock Data
                else updateDashboard(dailyNetResult); // Daily Calculation
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Updates the revenue text and toggles the status badge based on profit/loss
     */
    private void updateDashboard(double amount) {
        tvNetRevenue.setText(String.format("%sRM %.2f", amount >= 0 ? "+" : "-", Math.abs(amount)));

        // Gold for Profit, Light Red for Loss
        tvNetRevenue.setTextColor(amount >= 0 ? Color.parseColor("#FFD100") : Color.parseColor("#FF6666"));

        if (amount >= 0) {
            tvStatusBadge.setText("PROFIT");
            tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFD100")));
            tvStatusBadge.setTextColor(Color.parseColor("#112349")); // Dark Navy text
        } else {
            tvStatusBadge.setText("LOSS");
            tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6666")));
            tvStatusBadge.setTextColor(Color.WHITE);
        }
    }
}