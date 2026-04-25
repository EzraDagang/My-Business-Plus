package com.example.mybusinessplus;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_inventory extends AppCompatActivity {

    private LinearLayout activeFoodContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        // 1. Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 2. Handle System Bar Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 3. Setup the inventory list
        LinearLayout listContainer = findViewById(R.id.inventoryListContainer);
        LayoutInflater inflater = LayoutInflater.from(this);

        // Populate cards (Sun is "Today")
        addDateCard(inflater, listContainer, "Sun, 26 Apr", true);
        addDateCard(inflater, listContainer, "Sat, 25 Apr", false);
        addDateCard(inflater, listContainer, "Fri, 24 Apr", false);

        // 4. Add New Item FAB
        findViewById(R.id.fabAdd).setOnClickListener(v -> showAddItemDialog());
    }

    private void addDateCard(LayoutInflater inflater, LinearLayout container, String date, boolean isToday) {
        View cardView = inflater.inflate(R.layout.item_inventory_card, container, false);

        LinearLayout header = cardView.findViewById(R.id.cardHeader);
        LinearLayout body = cardView.findViewById(R.id.cardBody);
        TextView tvDate = cardView.findViewById(R.id.tvDateTitle);
        ImageView chevron = cardView.findViewById(R.id.iconChevron);
        LinearLayout foodContainer = cardView.findViewById(R.id.foodRowsContainer);
        Button btnRestock = cardView.findViewById(R.id.btnRestock);

        tvDate.setText(date);

        if (isToday) { activeFoodContainer = foodContainer; }

        // Initial default data
        addFoodRow(foodContainer, "Nasi Lemak", "4.00", "100");
        addFoodRow(foodContainer, "Karipap", "2.50", "60");
        addFoodRow(foodContainer, "Chicken Porridge", "4.00", "100");

        // Expansion Logic
        body.setVisibility(isToday ? View.VISIBLE : View.GONE);
        chevron.setRotation(isToday ? 180f : 0f);

        header.setOnClickListener(v -> {
            boolean isVisible = body.getVisibility() == View.VISIBLE;
            body.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            chevron.setRotation(isVisible ? 0f : 180f);
        });

        btnRestock.setOnClickListener(v -> showRestockDialog(foodContainer));
        container.addView(cardView);
    }

    private void addFoodRow(LinearLayout container, String name, String price, String stock) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 20, 0, 20);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView tvName = new TextView(this);
        tvName.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.5f));
        tvName.setText(name);
        tvName.setTextColor(Color.parseColor("#E68A00"));
        tvName.setTextSize(14f);

        TextView tvPrice = new TextView(this);
        tvPrice.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        tvPrice.setText(price);
        tvPrice.setGravity(android.view.Gravity.CENTER);

        TextView tvStock = new TextView(this);
        tvStock.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
        tvStock.setText(stock);
        tvStock.setGravity(android.view.Gravity.CENTER);

        row.addView(tvName);
        row.addView(tvPrice);
        row.addView(tvStock);
        container.addView(row);
    }

    private void showRestockDialog(LinearLayout targetContainer) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_restock);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Fix SQUASHING: Set dialog width to 90% of screen
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        dialog.getWindow().setAttributes(lp);

        LinearLayout restockItemsContainer = dialog.findViewById(R.id.restockItemsContainer);
        restockItemsContainer.removeAllViews(); // Clear any old views

        // 1. DYNAMICALLY CREATE ROWS
        // Loop through current items in the inventory card to create restock inputs
        for (int i = 0; i < targetContainer.getChildCount(); i++) {
            View rowView = targetContainer.getChildAt(i);
            if (rowView instanceof LinearLayout) {
                TextView tvName = (TextView) ((LinearLayout) rowView).getChildAt(0);
                String itemName = tvName.getText().toString();

                LinearLayout restockRow = new LinearLayout(this);
                restockRow.setOrientation(LinearLayout.HORIZONTAL);
                restockRow.setPadding(0, 15, 0, 15);
                restockRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

                TextView label = new TextView(this);
                label.setText(itemName);
                label.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
                label.setTextColor(Color.parseColor("#E68A00"));
                label.setTypeface(null, Typeface.BOLD); // FIXED: setTypeface

                EditText input = new EditText(this);
                // Convert 80dp to pixels for the width
                int widthPx = (int) (80 * getResources().getDisplayMetrics().density);
                input.setLayoutParams(new LinearLayout.LayoutParams(widthPx, WindowManager.LayoutParams.WRAP_CONTENT));
                input.setHint("0");
                input.setGravity(android.view.Gravity.CENTER);
                input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
                input.setBackgroundResource(android.R.drawable.edit_text);

                // Store the index of the inventory row as a tag
                input.setTag(i);

                restockRow.addView(label);
                restockRow.addView(input);
                restockItemsContainer.addView(restockRow);
            }
        }

        dialog.findViewById(R.id.btnDoneRestock).setOnClickListener(v -> {
            // 2. PROCESS UPDATES
            for (int j = 0; j < restockItemsContainer.getChildCount(); j++) {
                LinearLayout dialogRow = (LinearLayout) restockItemsContainer.getChildAt(j);
                EditText input = (EditText) dialogRow.getChildAt(1);
                int itemIndex = (int) input.getTag();
                String amountStr = input.getText().toString();

                if (!amountStr.isEmpty()) {
                    updateStock(targetContainer, itemIndex, amountStr);
                }
            }
            Toast.makeText(this, "Inventory Updated!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.btnCloseDialog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateStock(LinearLayout container, int rowIndex, String addAmount) {
        View rowView = container.getChildAt(rowIndex);
        if (rowView instanceof LinearLayout) {
            TextView tvStock = (TextView) ((LinearLayout) rowView).getChildAt(2);
            try {
                int current = Integer.parseInt(tvStock.getText().toString());
                int extra = Integer.parseInt(addAmount);
                tvStock.setText(String.valueOf(current + extra));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAddItemDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set dialog width to 90%
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        dialog.getWindow().setAttributes(lp);

        EditText etName = dialog.findViewById(R.id.etNewName);
        EditText etQty = dialog.findViewById(R.id.etNewQty);
        EditText etPrice = dialog.findViewById(R.id.etNewPrice);

        dialog.findViewById(R.id.btnConfirmAdd).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String qty = etQty.getText().toString().trim();
            String price = etPrice.getText().toString().trim();

            if (!name.isEmpty() && !qty.isEmpty() && !price.isEmpty()) {
                addFoodRow(activeFoodContainer, name, price, qty);
                dialog.dismiss();
                Toast.makeText(this, name + " added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.findViewById(R.id.btnCloseAddDialog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}