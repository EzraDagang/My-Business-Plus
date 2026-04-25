package com.example.mybusinessplus;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_insights extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insights);

        // Handle the system bar insets so content doesn't overlap with the notch/status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the back button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Closes this activity and returns to the previous one (MainActivity)
            finish();
        });
    }
}