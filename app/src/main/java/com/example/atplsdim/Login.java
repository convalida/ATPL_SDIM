package com.example.atplsdim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class Login extends AppCompatActivity {
    EditText userName,password;
    Button signInBtn;
    CheckBox checkBox;
    boolean rememberMe;
    private RequestQueue requestQueue;
    Gson gson;
    private static final String TAG="Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName=findViewById(R.id.emailAddress);
        password=findViewById(R.id.password);
        signInBtn=findViewById(R.id.signInBtn);
        checkBox=findViewById(R.id.rememberMe);

    }



    public void signIn(View view){
        String uname = userName.getText().toString();
        String pass = password.getText().toString();
        if(uname.length()>0 && pass.length()>0){
            if(CheckNetwork.isNetworkAvailable(Login.this)){
                requestQueue = Volley.newRequestQueue(Login.this);
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson=gsonBuilder.create();
                fetchPosts(uname,pass);
            }

            if (checkBox.isChecked()) {
              //  if (!userName.equals("") && !password.equals("")) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", userName.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.apply();
                //}
            }
        }
        else{
            if(userName.getText().toString().length()==0){
                userName.setError("User name is required");
                userName.requestFocus();
            }
            if(password.getText().toString().length()==0){
                password.setError("Password is required");
                password.requestFocus();
            }
        }

    }


    private void fetchPosts(String userName, String password){
        String encodedPassword = URLEncoder.encode(password);
        String encodedURL = Constants.BASE_URL+"GetLogin?Username="+userName+"&password="+encodedPassword;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,encodedURL,onPostsLoaded,onPostsError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private final Response.Listener<String> onPostsLoaded = new Response.Listener<String>(){

        @Override
        public void onResponse(String response) {
            Log.e(TAG,"Response: "+response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String result = jsonObject.getString("ResultCode");
                String message = jsonObject.getString("Message");
                Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
                if(result.equals("1")){
                    Intent intent=new Intent(Login.this,InventoryIn.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    String userName = jsonObject.getString("UserName");
                    String role = jsonObject.getString("Role");
                    SharedPreferences sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("User name",userName);
                    editor.putString("Role",role);
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private final Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        Log.e(TAG,"Error is: "+error.toString());
        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
        }
    };

    public void onBackPressed(){
        super.onBackPressed();
        finishAffinity();
    }
}
