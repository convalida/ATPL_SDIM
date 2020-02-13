package com.example.atplsdim;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
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
import android.widget.FrameLayout;
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
import java.util.HashMap;

public class InventoryIn extends AppCompatActivity {
    Spinner warehouseSpinner,rackNoSpinner;
    EditText uniqueId;
    Button addId;
    String name,role,uniqueID;
    RecyclerView recyclerView;
    private Gson gson;
    private RequestQueue requestQueue;
    private static final String TAG = "InventoryIn";
    ArrayList<String> warehouseArray;
    ArrayList<String> warehouseIdArray;
    ArrayList<String> rackArray;
    private static final int MY_PERMISSION_CAMERA=98;
    RelativeLayout lower;
    FrameLayout frameLayout;
    LinearLayout headerLayout;
    ArrayList<HashMap<String,String>> inventoryInList;
    ArrayList<HashMap<String,String>> duplicateLi;
    HashMap<String,String> hashMap;
    ScanFragment scanFragment;

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
        frameLayout=findViewById(R.id.mainLayout);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryIn.this,R.layout.support_simple_spinner_dropdown_item,warehouseArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warehouseSpinner.setAdapter(adapter);
        warehouseIdArray = new ArrayList<>();
        warehouseIdArray.add("0");
        rackArray = new ArrayList<>();
        rackArray.add("Select");

        ArrayAdapter<String> rackAdapter = new ArrayAdapter<String>(InventoryIn.this,R.layout.support_simple_spinner_dropdown_item,rackArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rackNoSpinner.setAdapter(rackAdapter);
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


       // warehouseSpinner.setOnItemSelectedListener(this);
        warehouseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

              //  if(parent.getId()==R.id.warehouseSpinner){
                    String selectedItem = String.valueOf(warehouseSpinner.getSelectedItem());
                    //  if(warehouseSpinner.con)
                    Log.e(TAG,"Item is: "+selectedItem);
                    if(selectedItem!="Select") {
                        int position = warehouseSpinner.getSelectedItemPosition();
                        Log.e(TAG, "Position of warehouse: " + position);
                        String warehouseId = warehouseIdArray.get(position);
                        Log.e(TAG, "Warehouse id is: " + warehouseId);
                        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("SpinnerItems",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("WarehouseSpinner",selectedItem);
                        editor.apply();
                        callRackList(warehouseId);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        rackNoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedWarehouse = String.valueOf(rackNoSpinner.getSelectedItem());
                if(selectedWarehouse!="Select") {
                    SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("SpinnerItems2", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    String selectedRack = String.valueOf(rackNoSpinner.getSelectedItem());
                    editor.putString("RackSpinner", selectedRack);
                    editor.apply();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        uniqueId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // uniqueId.setFocusable(true);
                checkCameraPermission();

            }
        });

      //  HashMap<String,String> hashMap= new HashMap<>();
    //  rackNoSpinner.setOnItemSelectedListener(this);
        Intent intent=getIntent();
        if(intent.getStringExtra("Scan result")!=null){
            uniqueID=intent.getStringExtra("Scan result");
           // lower.setVisibility(View.VISIBLE);
           // uniqueId.setFocusable(false);
           // uniqueId.setText(uniqueID);
            SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("SpinnerItems",MODE_PRIVATE);
            String warehouseName =sharedPrefs.getString("WarehouseSpinner","");
            SharedPreferences sharedPrefs2 = getApplicationContext().getSharedPreferences("SpinnerItems2",MODE_PRIVATE);
            String rackName = sharedPrefs2.getString("RackSpinner","");
            Log.e(TAG,"Selected warehouse is "+warehouseName);
            Log.e(TAG,"Selected rack is "+rackName);
            if(!warehouseName.equals("Select") && !rackName.equals("Select")){
                lower.setVisibility(View.VISIBLE);
                createChildList(uniqueID,warehouseName,rackName);
            }
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.clear();
            editor.commit();
            SharedPreferences.Editor editor1=sharedPrefs.edit();
            editor1.clear();
            editor1.commit();

        }
    }

    private void createChildList(String uniqueID, String warehouseName, String rackName) {
        SharedPreferences sharedPreferences=null;
        Gson gson=null;
        String json;
        // Log.e(TAG,"Warehouse "+warehouseSpinner.getSelectedItem().toString());
        sharedPreferences = getSharedPreferences("InventoryInList",MODE_PRIVATE);
        String list = sharedPreferences.getString("InList","");
        if(list.equals("")) {
            // sharedPreferences = getApplicationContext().getSharedPreferences("InventoryInList", MODE_PRIVATE);
            inventoryInList=new ArrayList<>();
        }



        hashMap=new HashMap<>();
        hashMap.put("Warehouse",warehouseName);
        hashMap.put("RackNo",rackName);
        hashMap.put("productID",uniqueID);
       // inventoryInList.add(hashMap);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        gson=new Gson();
         json=gson.toJson(inventoryInList);
        editor.putString("InList",json);
        editor.apply();

        Log.e(TAG, "InventoryIn Row"+String.valueOf(hashMap));
    }

    /**  private void createChildList(String uniqueID, String warehouseName, String ID) {
      //  if(warehouseSpinner.getSelectedItem()!="null" && warehouseSpinner.getSelectedItem()!="Select"&& rackNoSpinner.getSelectedItem()!="null" && rackNoSpinner.getSelectedItem()!="Select"){
            Log.e(TAG,"Warehouse: "+warehouseSpinner.getSelectedItem());
            Log.e(TAG,"Rack: "+rackNoSpinner.getSelectedItem());
            inventoryInList=new ArrayList<>();
            HashMap<String,String> hashMap= new HashMap<>();
           // Log.e(TAG,"Warehouse "+warehouseSpinner.getSelectedItem().toString());
            hashMap.put("Warehouse",warehouseSpinner.getSelectedItem().toString());
            hashMap.put("RackNo",rackNoSpinner.getSelectedItem().toString());
            hashMap.put("productID",ID);
            inventoryInList.add(hashMap);
            //warehouseSpinner.getSelectedItem();
      //  }

    }**/

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
          //  frameLayout.setVisibility(View.INVISIBLE);
           /** if(findViewById(R.id.fragment_container)!=null){
                scanFragment= new ScanFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,scanFragment,null);
                fragmentTransaction.commit();
            }**/

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

 /**   @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        Log.e(TAG, "Id is: "+String.valueOf(parent.getId()));
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SpinnerItems",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(parent.getId()==R.id.warehouseSpinner){
        String selectedItem = String.valueOf(warehouseSpinner.getSelectedItem());
      //  if(warehouseSpinner.con)
        Log.e(TAG,"Item is: "+selectedItem);
        if(selectedItem!="Select") {
            int position = warehouseSpinner.getSelectedItemPosition();
            Log.e(TAG, "Position of warehouse: " + position);
            String warehouseId = warehouseIdArray.get(position);
            Log.e(TAG, "Warehouse id is: " + warehouseId);

            editor.putString("WarehouseSpinner",selectedItem);
            editor.apply();
            callRackList(warehouseId);
        }
           // getRackNoDetails(position);
         /**   SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Position",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("RackPosition",String.valueOf(position));**/
         //   getRackDetails();
        /**     if(parent.getId()==R.id.rackNumSpinner) {
            String selectedRack = String.valueOf(rackNoSpinner.getSelectedItem());
            editor.putString("RackSpinner",selectedRack);
            editor.apply();
           /** if (!selectedRack.equals("null") && !selectedRack.equals("Select")) {
                uniqueId.setEnabled(true);
            }**/
        /**}
        }

    }**/

    private void callRackList(String warehouseId){
        final String url = Constants.BASE_URL+"GetWarehouseRackDetails?WarehouseID="+warehouseId;
        Log.e(TAG,"Rack url is : "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,onRackPostsLoaded,onRackPostsError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
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


 /**   @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }**/

    private class GetInventoryIn extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... response) {
            try {
                warehouseArray =new ArrayList<>();
                warehouseIdArray = new ArrayList<>();
                warehouseArray.add("Select");
                warehouseIdArray.add("0");
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
                    warehouseIdArray.add(id);
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
                JSONArray rackDetais = jsonObject.getJSONArray("Racknumberdetails");
                for(int i = 0;i<rackDetais.length();i++){
                    JSONObject jsonObj = rackDetais.getJSONObject(i);
                    String id = jsonObj.getString("RackId");
                    String name = jsonObj.getString("RackName");
                    rackArray.add(name);
                    }
               /**    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Position",MODE_PRIVATE);
                   String position = sharedPreferences.getString("Position","");
                   if(id.equals(position)){

                    }**/

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return rackArray;
        }
        protected void onPostExecute(ArrayList<String> result){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryIn.this,R.layout.support_simple_spinner_dropdown_item,result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            rackNoSpinner.setAdapter(adapter);
        }
    }

}
