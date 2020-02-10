package com.example.atplsdim;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InventoryIn extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner warehouseSpinner,rackNoSpinner;
    EditText uniqueId;
    Button addId;
    String name,role;
    RecyclerView recyclerView;
    private Gson gson;
    private RequestQueue requestQueue;
    private static final String TAG = "InventoryIn";
    ArrayList<String> warehouseArray;
    ArrayList<String> warehouseIdArray;
    ArrayList<String> rackArray;
    private static final int MY_PERMISSION_CAMERA=98;
    RelativeLayout lower;
    LinearLayout headerLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_in);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User",MODE_PRIVATE);
        name=sharedPreferences.getString("User name","");
        role=sharedPreferences.getString("Role","");
        if(getSupportActionBar()!=null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Welcome, "+name);
        }
        headerLayout=findViewById(R.id.headerLayout);
        warehouseSpinner = findViewById(R.id.warehouseSpinner);
        rackNoSpinner = findViewById(R.id.rackNumSpinner);
        uniqueId = findViewById(R.id.uniqueId);
        addId= findViewById(R.id.addBtn);
        lower=findViewById(R.id.lowerLayout);
        recyclerView = findViewById(R.id.recyclerView);
        requestQueue = Volley.newRequestQueue(InventoryIn.this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson=gsonBuilder.create();
        warehouseArray =new ArrayList<>();
        warehouseArray.add("Select");
        rackArray = new ArrayList<>();
        rackArray.add("String");
        if(role.equals("SuperAdmin")){
            Button button = new Button(this);
            button.setText("Inventory Out");
            button.setBackgroundColor(Color.parseColor("#D4151A"));
            button.setTextColor(Color.parseColor("#FFFFFF"));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10,10,10,10);
            headerLayout.addView(button,layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InventoryIn.this,InventoryOut.class);
                    startActivity(intent);
                }
            });
        }
        fetchPosts();
        warehouseSpinner.setOnItemSelectedListener(this);
        uniqueId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // uniqueId.setFocusable(true);
                checkCameraPermission();

            }
        });
    //  rackNoSpinner.setOnItemSelectedListener(this);
        Intent intent=getIntent();
        if(intent.getStringExtra("Scan result")!=null){
            lower.setVisibility(View.VISIBLE);
            uniqueId.setFocusable(false);
        }
    }

    private boolean checkCameraPermission() {
      //  Toast.makeText(getApplicationContext(),"Camera permission check",Toast.LENGTH_LONG).show();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(InventoryIn.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }

            return false;
        }
        else {
            Intent intent=new Intent(InventoryIn.this,ScannerActivity.class);
            startActivity(intent);
            return true;
        }
    }

    private void fetchPosts() {
        final String MAIN=Constants.BASE_URL+"GetWarehouseDetails";
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = String.valueOf(warehouseSpinner.getSelectedItem());
      //  if(warehouseSpinner.con)
        Log.e(TAG,"Item is: "+selectedItem);
        if(selectedItem!="Select"){
           int position = warehouseSpinner.getSelectedItemPosition();
            Log.e(TAG,"Position of warehouse: "+position);
           // getRackNoDetails(position);
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Position",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("RackPosition",String.valueOf(position));
         //   getRackDetails();
        }

    }

    private void getRackDetails() {
        final String MAIN=Constants.BASE_URL+"GetWarehoueDetails";
        Log.e(TAG, "Url is: "+MAIN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,MAIN,onRackPostsLoaded,onRackPostsError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
    Response.Listener<String> onRackPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.e(TAG, "Response: "+response);
            GetRacksIn getRacksIn = new GetRacksIn();
            getRacksIn.execute(response);
        }
    };

    Response.ErrorListener onRackPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG,"Error is: "+error.toString());
            Toast.makeText(getApplicationContext(),"Error is: "+error.toString(),Toast.LENGTH_LONG).show();
        }
    };


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class GetInventoryIn extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... response) {
            try {
                warehouseArray =new ArrayList<>();
                warehouseIdArray = new ArrayList<>();
                warehouseArray.add("Select");
                JSONObject jsonObject = new JSONObject(response[0]);
                JSONArray warehouseDetails = jsonObject.getJSONArray("AllWarehouseDetails");
                for(int i = 0; i<warehouseDetails.length();i++){
                    JSONObject jsonObj = warehouseDetails.getJSONObject(i);
                    String id = jsonObj.getString("WarehouseId");
                    String name = jsonObj.getString("WarehouseName");
                 /**   JSONArray rackArray = jsonObj.getJSONArray("RackNoDetails");
                    for (int j = 0; j<rackArray.length();j++){
                        JSONObject jsonObje = rackArray.getJSONObject(j);
                        String rackId = jsonObje.getString("RackId");
                        String rackName = jsonObje.getString("RackName");
                    }**/
                    warehouseArray.add(name);
                 //   warehouseIdArray
                }
                Log.e(TAG, "Warehouse array is: "+warehouseArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return warehouseArray;
        }

        protected void onPostExecute(ArrayList<String> result){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryIn.this,R.layout.support_simple_spinner_dropdown_item,result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            warehouseSpinner.setAdapter(adapter);
        }
    }

    private class GetRacksIn extends AsyncTask<String,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... response) {
            rackArray = new ArrayList<>();
            rackArray.add("Select");
            try {
                JSONObject jsonObject = new JSONObject(response[0]);
                JSONArray warehouseDetails = jsonObject.getJSONArray("AllWarehouseDetails");
                for(int i = 0;i<warehouseDetails.length();i++){
                    JSONObject jsonObj = warehouseDetails.getJSONObject(i);
                    String id = jsonObj.getString("WarehouseId");
                   /** String name = jsonObj.getString("WarehouseName");
                    JSONArray rackArray = jsonObj.getJSONArray("RackNoDetails");
                    for (int j=0;j<rackArray.length();j++){
                        JSONObject jsonObje = rackArray.getJSONObject(j);
                        String rackId = jsonObje.getString("RackId");
                        String rackName = jsonObje.getString("RackName");
                    }**/
                   SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Position",MODE_PRIVATE);
                   String position = sharedPreferences.getString("Position","");
                   if(id.equals(position)){

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
