package com.example.mybusinessplus;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class manager {
    private static String loginUrl = "https://tnghackathon-1.onrender.com/auth/login";
    public static String Authorization = "Authorization";

    public static int userId = 1;
    public static String token;

    public static void performLogin(String email, String password, Context context) {
        // 3. Create the JSON Body
        JSONObject loginBody = new JSONObject();
        try {
            loginBody.put("email", email);
            loginBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 4. Create the Request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, loginUrl, loginBody,
                response -> {
                    try {
                        // 1. Extract the token from the JSON response
                        token ="Bearer " + response.getString("token");


                    } catch (JSONException e) {

                    }
                },error -> {
        }
        );

        // 5. Add to RequestQueue
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public static void mockLogin(Context context){
        userId = 2;
        performLogin("amna@um.edu.my","password123",context);
    }
}
