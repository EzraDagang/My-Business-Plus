package com.example.mybusinessplus;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MerchantActivity extends AppCompatActivity {

    private TextView tvNetRevenue;
    private Button btnProfitToggle, btnLossToggle;
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
        btnProfitToggle = findViewById(R.id.btnProfitToggle);
        btnLossToggle = findViewById(R.id.btnLossToggle);
        LinearLayout inventoryContainer = findViewById(R.id.inventoryContainer);
        Spinner spinnerTimeframe = findViewById(R.id.spinnerTimeframe);

        // 2. Navigation "Bridge" Logic
        // Link to Inventory Setting (AddFoodActivity)
        findViewById(R.id.nav_inventory).setOnClickListener(v ->
                startActivity(new Intent(MerchantActivity.this, AddFoodActivity.class))
        );

        // Link to History Page
        findViewById(R.id.nav_history).setOnClickListener(v ->
                startActivity(new Intent(MerchantActivity.this, HistoryActivity.class))
        );

        // Link to Insights Page
        findViewById(R.id.nav_insights).setOnClickListener(v ->
                startActivity(new Intent(MerchantActivity.this, activity_insights.class))
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
            addViewForItem(inventoryContainer, item, sold);
        }
        dailyNetResult = rev - loss;

        // 5. Spinner Listener to update dashboard numbers
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
     * Updates the revenue text and toggles button colors based on profit/loss
     */
    private void updateDashboard(double amount) {
        tvNetRevenue.setText(String.format("%sRM %.2f", amount >= 0 ? "+" : "-", Math.abs(amount)));

        // Color: Orange for profit, Red for loss
        tvNetRevenue.setTextColor(amount >= 0 ? Color.parseColor("#FF5722") : Color.parseColor("#FF5252"));

        if (amount >= 0) {
            // Profit Active: Gold background, Navy text
            btnProfitToggle.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFD100")));
            btnProfitToggle.setTextColor(Color.parseColor("#113285"));

            // Loss Inactive: Light Gray background, Dark Gray text
            btnLossToggle.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            btnLossToggle.setTextColor(Color.parseColor("#888888"));
        } else {
            // Profit Inactive
            btnProfitToggle.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0F0F0")));
            btnProfitToggle.setTextColor(Color.parseColor("#888888"));

            // Loss Active: Red background, White text
            btnLossToggle.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5252")));
            btnLossToggle.setTextColor(Color.WHITE);
        }
    }

    /**
     * Dynamically adds inventory cards to the bottom list
     */
    private void addViewForItem(LinearLayout container, InventoryItem item, int soldQty) {
        com.google.android.material.card.MaterialCardView card = new com.google.android.material.card.MaterialCardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, 30);
        card.setLayoutParams(params);
        card.setRadius(16f);
        card.setCardBackgroundColor(Color.parseColor("#113285"));
        card.setStrokeColor(Color.WHITE);
        card.setStrokeWidth(2);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(40, 40, 40, 40);

        TextView name = new TextView(this);
        name.setText(item.name + " (Sold: " + soldQty + ")");
        name.setTextColor(Color.WHITE);
        name.setTypeface(null, Typeface.BOLD);

        TextView stats = new TextView(this);
        stats.setText("Unsold: " + item.unsoldQty + " | Revenue: RM " + String.format("%.2f", soldQty * item.price));
        stats.setTextColor(Color.parseColor("#FFD100"));

        row.addView(name);
        row.addView(stats);
        card.addView(row);
        container.addView(card);
    }
}