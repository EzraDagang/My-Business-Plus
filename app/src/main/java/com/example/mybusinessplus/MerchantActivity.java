package com.example.mybusinessplus;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MerchantActivity extends AppCompatActivity {

    // 1. Create a simple class to hold our item data
    class InventoryItem {
        String name;
        double price;
        int startQty;
        int unsoldQty;

        public InventoryItem(String name, double price, int startQty, int unsoldQty) {
            this.name = name;
            this.price = price;
            this.startQty = startQty;
            this.unsoldQty = unsoldQty;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        // Handle edge-to-edge screens
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Setup Hackathon Mock Data
        List<InventoryItem> items = new ArrayList<>();
        items.add(new InventoryItem("Karipap", 1.50, 100, 20));
        items.add(new InventoryItem("Nasi Lemak", 3.00, 50, 2));
        items.add(new InventoryItem("Kuih Lapis", 1.00, 80, 0));

        // 3. Link UI elements
        TextView tvSimpleReport = findViewById(R.id.tvSimpleReport);
        LinearLayout inventoryContainer = findViewById(R.id.inventoryContainer);
        Button btnAutoScan = findViewById(R.id.btnAutoScan);
        Button btnVoiceInput = findViewById(R.id.btnVoiceInput);

        // Make buttons interactive for the demo
        btnAutoScan.setOnClickListener(v -> Toast.makeText(MerchantActivity.this, "Opening AI Camera...", Toast.LENGTH_SHORT).show());
        btnVoiceInput.setOnClickListener(v -> Toast.makeText(MerchantActivity.this, "Listening for voice input...", Toast.LENGTH_SHORT).show());

        // 4. Calculate Totals
        double totalRevenue = 0;
        double totalWasteLoss = 0;
        String bestItem = items.get(0).name; // Default to first item

        for (InventoryItem item : items) {
            int soldQty = item.startQty - item.unsoldQty;
            totalRevenue += (soldQty * item.price);
            totalWasteLoss += (item.unsoldQty * item.price);

            // Build the UI for each item dynamically
            addViewForItem(inventoryContainer, item, soldQty);
        }

        // 5. Update the Simple Report text
        String reportText = String.format(
                "Great job! Today you earned RM %.2f.\n" +
                        "Your best item was Kuih Lapis (Sold Out).\n" +
                        "You lost RM %.2f from unsold food.",
                totalRevenue, totalWasteLoss
        );
        tvSimpleReport.setText(reportText);
    }

    // Helper method to create item UI dynamically without needing complex Adapters
    private void addViewForItem(LinearLayout container, InventoryItem item, int soldQty) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 30); // Bottom margin
        card.setLayoutParams(cardParams);
        card.setRadius(16f);
        card.setCardElevation(4f);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(40, 40, 40, 40);

        // Name and Price
        TextView tvName = new TextView(this);
        tvName.setText(item.name + " (RM " + String.format("%.2f", item.price) + ")");
        tvName.setTextSize(18f);
        tvName.setTextColor(Color.parseColor("#333333"));

        // Stats
        TextView tvStats = new TextView(this);
        double revenue = soldQty * item.price;
        tvStats.setText("Made: " + item.startQty + " | Unsold: " + item.unsoldQty + " | Earned: RM " + String.format("%.2f", revenue));
        tvStats.setTextSize(14f);
        tvStats.setTextColor(Color.parseColor("#555555"));
        tvStats.setPadding(0, 10, 0, 0);

        row.addView(tvName);
        row.addView(tvStats);
        card.addView(row);

        container.addView(card);
    }
}