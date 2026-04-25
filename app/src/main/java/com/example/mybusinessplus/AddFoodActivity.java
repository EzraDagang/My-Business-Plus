package com.example.mybusinessplus;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddFoodActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        EditText etName = findViewById(R.id.etFoodName);
        Button btnSave = findViewById(R.id.btnSaveFood);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            if (!name.isEmpty()) {
                Toast.makeText(this, name + " added to inventory!", Toast.LENGTH_LONG).show();
                finish(); // Goes back to the dashboard
            }
        });
    }
}