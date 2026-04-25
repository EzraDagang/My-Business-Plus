package com.example.mybusinessplus;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    static class Transaction {
        String dateTime, title, description, amount, points, monthName;
        boolean isPositive, isNewMonth;

        public Transaction(String dateTime, String title, String desc, String amount, boolean isPositive, String points, boolean isNewMonth, String monthName) {
            this.dateTime = dateTime; this.title = title; this.description = desc;
            this.amount = amount; this.isPositive = isPositive; this.points = points;
            this.isNewMonth = isNewMonth; this.monthName = monthName;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        LinearLayout container = findViewById(R.id.historyListContainer);
        LayoutInflater inflater = LayoutInflater.from(this);

        List<Transaction> history = new ArrayList<>();
        history.add(new Transaction("25 Apr, 03:21", "GO+ Daily Earnings", "To GO+ Balance", "+RM0.27", true, null, true, "APRIL 26"));
        history.add(new Transaction("24 Apr, 20:08", "NOVI KAFE", "DuitNow QR TNGD", "-RM10.80", false, "+10 points", false, null));
        history.add(new Transaction("24 Apr, 20:08", "Quick Reload Payment", "Via GO+ Balance", "+RM10.80", true, null, false, null));
        history.add(new Transaction("24 Apr, 15:54", "ISRAH CAFE", "DuitNow QR TNGD", "-RM9.00", false, "+9 points", false, null));

        for (Transaction txn : history) {
            View rowView = inflater.inflate(R.layout.item_history, container, false);

            ((TextView) rowView.findViewById(R.id.tvDateTime)).setText(txn.dateTime);
            ((TextView) rowView.findViewById(R.id.tvTitle)).setText(txn.title);
            ((TextView) rowView.findViewById(R.id.tvDescription)).setText(txn.description);

            TextView tvAmount = rowView.findViewById(R.id.tvAmount);
            tvAmount.setText(txn.amount);
            tvAmount.setTextColor(txn.isPositive ? Color.parseColor("#0053B5") : Color.parseColor("#333333"));

            if (txn.points != null) {
                TextView tvP = rowView.findViewById(R.id.tvPoints);
                tvP.setText(txn.points);
                tvP.setVisibility(View.VISIBLE);
            }

            if (txn.isNewMonth) {
                TextView tvM = rowView.findViewById(R.id.tvMonthHeader);
                tvM.setText(txn.monthName);
                tvM.setVisibility(View.VISIBLE);
            }

            container.addView(rowView);
        }
    }
}