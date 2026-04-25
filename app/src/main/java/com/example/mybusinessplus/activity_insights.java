package com.example.mybusinessplus;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class activity_insights extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insights);

        // System Bar Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 1. Animate Progress Rings
        CircularProgressIndicator ringCurrent = findViewById(R.id.ringCurrent);
        CircularProgressIndicator ringLast = findViewById(R.id.ringLast);
        CircularProgressIndicator ringMonth = findViewById(R.id.ringMonth);

        animateRing(ringCurrent, 64);
        animateRing(ringLast, 40);
        animateRing(ringMonth, 90);

        // 2. Populate Revenue Table
        LinearLayout tableContainer = findViewById(R.id.revenueTableContainer);
        addTableRow(tableContainer, "Nasi Lemak", "410.60");
        addTableRow(tableContainer, "Chicken Porridge", "387.00");
        addTableRow(tableContainer, "Onde-Onde", "367.67");
        addTableRow(tableContainer, "Karipap", "362.20");

        // 3. Set AI Insight text
        TextView tvAiInsight = findViewById(R.id.tvAiInsight);
        tvAiInsight.setText("Your Nasi Lemak sales are peaking! Trends suggest a 15% increase in weekend demand. Try offering a 'Nasi Lemak + Karipap' combo to boost low-selling items.");
    }

    private void animateRing(CircularProgressIndicator ring, int target) {
        ObjectAnimator anim = ObjectAnimator.ofInt(ring, "progress", 0, target);
        anim.setDuration(1500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
    }

    private void addTableRow(LinearLayout container, String name, String amount) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 10, 0, 10);

        TextView tvName = new TextView(this);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        tvName.setText(name);
        tvName.setTextColor(Color.parseColor("#555555"));

        TextView tvAmount = new TextView(this);
        tvAmount.setText(amount);
        tvAmount.setTextColor(Color.parseColor("#333333"));

        row.addView(tvName);
        row.addView(tvAmount);
        container.addView(row);
    }
}