package com.example.mybusinessplus;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class WeeklyDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_dashboard);
        // In a real app, you would use a Library like MPAndroidChart here.
        // For the hackathon, the XML handles the visual "fake" chart.
    }
}