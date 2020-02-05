package com.example.atplsdim;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class InventoryIn extends AppCompatActivity {
    Spinner warehouseSpinner,rackNoSpinner;
    EditText uniqueId;
    Button addId;
    String name;
    RecyclerView recyclerView;
    private Gson gson;
    private RequestQueue requestQueue;
    private static final String TAG = "InventoryIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_in);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User",MODE_PRIVATE);
        name=sharedPreferences.getString("User name","");
        if(getSupportActionBar()!=null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Welcome, "+name);
        }
        warehouseSpinner = findViewById(R.id.warehouseSpinner);
        rackNoSpinner = findViewById(R.id.rackNumSpinner);
        uniqueId = findViewById(R.id.uniqueId);
        addId= findViewById(R.id.addBtn);
        recyclerView = findViewById(R.id.recyclerView);
        requestQueue = Volley.newRequestQueue(InventoryIn.this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson=gsonBuilder.create();
        fetchPosts();
    }

    private void fetchPosts() {
        final String MAIN=Constants.BASE_URL+"GetWarehoueDetails";
        Log.e(TAG, "Url is: "+MAIN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,MAIN,onPostsLoaded,onPostsError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.e(TAG, "Response: "+response);
            GetInventoryIn getInventoryIn = new GetInventoryIn();
            getInventoryIn.execute(response);
        }
    };

    Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          Log.e(TAG,"Error is: "+error.toString());
          Toast.makeText(getApplicationContext(),"Error is: "+error.toString(),Toast.LENGTH_LONG).show();
        }
    };

    boolean doubleBackToExitPressedOnce = false;
    public void onBackPressed(){
            // finishAffinity();
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        finish();
        Intent intent = new Intent(InventoryIn.this,Login.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private class GetInventoryIn extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... voids) {
            return null;
        }
    }
}
