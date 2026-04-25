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
    private SalesLineChartView chartView;
    private double dailyNetResult = 0;

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

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        tvNetRevenue = findViewById(R.id.tvNetRevenue);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        chartView = findViewById(R.id.salesLineChart);
        Spinner spinnerTimeframe = findViewById(R.id.spinnerTimeframe);

        // Navigation (Matches your existing file names)
        findViewById(R.id.nav_inventory).setOnClickListener(v ->
                startActivity(new Intent(this, activity_inventory.class)));

        findViewById(R.id.nav_history).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        findViewById(R.id.nav_insights).setOnClickListener(v ->
                startActivity(new Intent(this, activity_insights.class)));

        findViewById(R.id.nav_qr).setOnClickListener(v ->
                startActivity(new Intent(this, activity_qr.class)));

        // Spinner Setup
        String[] options = {"Daily", "This Week", "This Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeframe.setAdapter(adapter);

        calculateMockData();

        spinnerTimeframe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    updateDashboard(dailyNetResult);
                    chartView.setData(new float[]{12f, 25f, 45f, 30f, 80f, 110f, 65f, 90f});
                } else if (position == 1) {
                    updateDashboard(1450.75);
                    chartView.setData(new float[]{150f, 200f, 180f, 220f, 300f, 450f, 380f});
                } else {
                    updateDashboard(5820.00);
                    chartView.setData(new float[]{1200f, 1450f, 1100f, 1800f});
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void calculateMockData() {
        List<InventoryItem> items = new ArrayList<>();
        items.add(new InventoryItem("Karipap", 1.50, 100, 20));
        items.add(new InventoryItem("Nasi Lemak", 3.00, 50, 2));

        double rev = 0; double loss = 0;
        for (InventoryItem item : items) {
            rev += ((item.startQty - item.unsoldQty) * item.price);
            loss += (item.unsoldQty * item.price);
        }
        dailyNetResult = rev - loss;
    }

    private void updateDashboard(double amount) {
        tvNetRevenue.setText(String.format("%sRM %.2f", amount >= 0 ? "+" : "-", Math.abs(amount)));
        tvNetRevenue.setTextColor(amount >= 0 ? Color.parseColor("#FFD100") : Color.parseColor("#FF6666"));

        if (amount >= 0) {
            tvStatusBadge.setText("PROFIT");
            tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFD100")));
            tvStatusBadge.setTextColor(Color.parseColor("#112349"));
        } else {
            tvStatusBadge.setText("LOSS");
            tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6666")));
            tvStatusBadge.setTextColor(Color.WHITE);
        }
    }
}