package com.example.mybusinessplus;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Manager {
    static long merchantId;
    static String token; // This is where the token will be stored
    static String url = "https://tnghackathon-1.onrender.com/auth/login";
    static boolean isProcessing = false;

    public static void login(String email, String password, Context context) {
        isProcessing = true;

        // 1. Create the JSON object
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", "amna@um.edu.my");
            jsonBody.put("password", "password123");
        } catch (JSONException e) {
            e.printStackTrace();
        }

// 2. Initialize the request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, jsonBody,
                response -> {
                    // Call the parser
                    parseTokenResponse(String.valueOf(response));

                    // Optional: Feedback for testing
                    Log.d("TOKEN_SAVED", "Token is now: " + token);
                    isProcessing = false;
                },
                error -> {
                    isProcessing = false;
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show();
                }
        );
    }

    /**
     * Specifically extracts the token from the JSON and loads it into the static variable
     */
    private static void parseTokenResponse(String jsonResponse) {
        try {
            Gson gson = new Gson();
            // Map the JSON string to our LoginResponse class
            LoginResponse data = gson.fromJson(jsonResponse, LoginResponse.class);

            if (data != null && data.token != null) {
                // LOAD THE TOKEN into the static variable
                Manager.token = data.token;
            }
        } catch (Exception e) {
            Log.e("JSON_ERROR", "Failed to parse token", e);
        }
    }


}

class LoginResponse {
    String token;
    String tokenType;
}
