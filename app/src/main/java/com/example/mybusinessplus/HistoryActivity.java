package com.example.mybusinessplus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout historyListContainer;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        // System Bar Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        historyListContainer = findViewById(R.id.historyListContainer);
        inflater = LayoutInflater.from(this);

        setupFilterSpinner();
    }

    private void setupFilterSpinner() {
        Spinner spinner = findViewById(R.id.spinnerTimeFilter);
        String[] filters = {"Today", "This Week", "This Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Clear the list before adding new filtered data
                historyListContainer.removeAllViews();

                if (position == 0) {
                    // "Today" Data
                    addTransactionRow("26 Apr, 02:15 PM", "Hazim", "3x Nasi Lemak, 2x Karipap", "+RM17.00");
                    addTransactionRow("26 Apr, 12:30 PM", "Aimaan", "1x Chicken Porridge", "+RM4.00");
                } else if (position == 1) {
                    // "This Week" Data (Includes Today's plus older ones)
                    addTransactionRow("26 Apr, 02:15 PM", "Hazim", "3x Nasi Lemak, 2x Karipap", "+RM17.00");
                    addTransactionRow("26 Apr, 12:30 PM", "Aimaan", "1x Chicken Porridge", "+RM4.00");
                    addTransactionRow("24 Apr, 09:00 AM", "Hani", "2x Kuih Sago", "+RM2.00");
                    addTransactionRow("23 Apr, 08:45 AM", "Amni", "5x Karipap", "+RM12.50");
                } else {
                    // "This Month" Data
                    addTransactionRow("26 Apr, 02:15 PM", "Hazim", "3x Nasi Lemak, 2x Karipap", "+RM17.00");
                    addTransactionRow("15 Apr, 10:20 AM", "Nurina", "10x Nasi Lemak (Bulk Order)", "+RM40.00");
                    addTransactionRow("02 Apr, 04:00 PM", "Hazim", "2x Chicken Porridge", "+RM8.00");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void addTransactionRow(String dateTime, String customerName, String items, String amount) {
        // Inflate the custom row we built in Step 1
        View rowView = inflater.inflate(R.layout.item_history_row, historyListContainer, false);

        // Link the specific text views inside that row
        TextView tvDateTime = rowView.findViewById(R.id.tvHistoryDateTime);
        TextView tvCustomer = rowView.findViewById(R.id.tvHistoryCustomer);
        TextView tvItems = rowView.findViewById(R.id.tvHistoryItems);
        TextView tvAmount = rowView.findViewById(R.id.tvHistoryAmount);

        // Set the data
        tvDateTime.setText(dateTime);
        tvCustomer.setText(customerName);
        tvItems.setText(items);
        tvAmount.setText(amount);

        // Add the finished row to the screen
        historyListContainer.addView(rowView);
    }
}