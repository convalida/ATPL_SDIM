package com.example.atplsdim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InventoryOut extends AppCompatActivity {
    String name, orderNum, referenceNum;
    EditText productId, orderNumber, referenceNumber;
    Button addProduct, uploadData;
    Spinner thirdParty, pickupPerson;
    private static final int MY_PERMISSION_CAMERA=98;
    LinearLayout linearLayout;
    String selectedThirdParty,selectedPickupPerson;
    ScanFragment scanFragment;
    ArrayList<String> thirdPartyArray;
    RequestQueue requestQueue;
    ArrayList<String> pickupPersonArray;
    private static final String TAG="InventoryOut";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION=99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_out);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("User",MODE_PRIVATE);
        name=sharedPreferences.getString("User name","");
        if(getSupportActionBar()!=null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Welcome, "+name);
        }
        productId=findViewById(R.id.uniqueId);
        requestQueue= Volley.newRequestQueue(InventoryOut.this);
        orderNumber=findViewById(R.id.orderNumber);
        referenceNumber=findViewById(R.id.referenceNo);
        addProduct=findViewById(R.id.addProduct);
        uploadData=findViewById(R.id.uploadData);
        thirdParty=findViewById(R.id.thirdPartySpinner);
        linearLayout=findViewById(R.id.verticalLinearLayout);
        pickupPerson=findViewById(R.id.pickupPersonSpinner);

        orderNum=orderNumber.getText().toString();
        referenceNum=referenceNumber.getText().toString();
        thirdPartyArray = new ArrayList<>();
        thirdPartyArray.add("Select");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryOut.this,R.layout.support_simple_spinner_dropdown_item,thirdPartyArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thirdParty.setAdapter(adapter);

        getThirdPartyName();
        getPickupPersonName();

      uploadData.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Log.e(TAG,"Selected pickup person: "+selectedPickupPerson);
              Log.e(TAG,"Selected Third party: "+selectedThirdParty);
              if(!orderNum.equals("")&&referenceNum.equals("")&&!selectedThirdParty.equals("Select")&&!selectedPickupPerson.equals("Select")){
                  //TODO upload btn functionality
              }
          }
      });

      thirdParty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              selectedThirdParty=String.valueOf(thirdParty.getSelectedItem());
          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {

          }
      });

      pickupPerson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              selectedPickupPerson=String.valueOf(pickupPerson.getSelectedItem());
          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {

          }
      });

       pickupPersonArray=new ArrayList<>();
        pickupPersonArray.add("Select");
        ArrayAdapter<String> pickupPersonAdapter = new ArrayAdapter<String>(InventoryOut.this,R.layout.support_simple_spinner_dropdown_item,pickupPersonArray);
        pickupPersonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pickupPerson.setAdapter(pickupPersonAdapter);
        
        productId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });
        Intent intent = getIntent();
        if(intent.getStringExtra("ScanResultOut")!=null){
            linearLayout.setVisibility(View.VISIBLE);
            productId.setFocusable(false);
            getThirdPartyName();
            getPickupPersonName();
        }
    }

    private void getThirdPartyName() {
        final String thirdPartyUrl=Constants.BASE_URL+"GetAllThirdParty";
        Log.e(TAG, "Url is: "+thirdPartyUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,thirdPartyUrl,onPostsLoaded,onPostsError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.e(TAG,"Third party response: "+response);
            GetThirdParty getThirdParty = new GetThirdParty();
            getThirdParty.execute(response);
        }
    };

    Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
         Log.e(TAG,"Error is: "+error.toString());
            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
        }
    };

    private void getPickupPersonName(){
        final String pickupPersonUrl=Constants.BASE_URL+"GetPickupPerson";
        Log.e(TAG,"Pickup person url is: "+pickupPersonUrl);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,pickupPersonUrl,onPickupPersonPostsLoaded,onPickuPersonPostsError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    Response.Listener<String> onPickupPersonPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.e(TAG,"Pickup person response: "+response);
            GetPickUpPerson getPickUpPerson = new GetPickUpPerson();
            getPickUpPerson.execute(response);
        }
    };

    Response.ErrorListener onPickuPersonPostsError=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG,"Error is: "+error.toString());
            Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
        }
    };

    private boolean checkCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(InventoryOut.this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            }

            return false;
        }
        else {
            Intent intent=new Intent(InventoryOut.this,ScannerActivity.class);
            intent.putExtra("Scanner Out","outScan");
            startActivity(intent);
        /**    if(findViewById(R.id.fragment_container)!=null){
                scanFragment= new ScanFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,scanFragment,null);
                fragmentTransaction.commit();
            }**/
            return true;
        }
    }

    private class GetThirdParty extends AsyncTask<String,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... response) {
            thirdPartyArray = new ArrayList<>();
            thirdPartyArray.add("Select");
            try {
                JSONObject jsonObject = new JSONObject(response[0]);
                JSONArray thirdParty = jsonObject.getJSONArray("AllThirdPartyDetails");
                for(int i=0;i<thirdParty.length();i++){
                    JSONObject jsonObj=thirdParty.getJSONObject(i);
                    String thirdPartyName=jsonObj.getString("ThirdPartyName");
                    thirdPartyArray.add(thirdPartyName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return thirdPartyArray;
        }

        protected void onPostExecute(ArrayList<String> result){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryOut.this,R.layout.support_simple_spinner_dropdown_item,result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            thirdParty.setAdapter(adapter);
        }
    }

    private class GetPickUpPerson extends AsyncTask<String,Void,ArrayList<String>>{

        @Override
        protected ArrayList<String> doInBackground(String... response) {
            pickupPersonArray = new ArrayList<>();
            pickupPersonArray.add("Select");
            try {
                JSONObject jsonObject = new JSONObject(response[0]);
                JSONArray pickupPersons = jsonObject.getJSONArray("PickupPersonDetails");
                for(int i=0;i<pickupPersons.length();i++){
                    JSONObject jsonObj = pickupPersons.getJSONObject(i);
                    String pickupPersonName = jsonObj.getString("Name");
                    pickupPersonArray.add(pickupPersonName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return pickupPersonArray;
        }

        protected void onPostExecute(ArrayList<String> result){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(InventoryOut.this,R.layout.support_simple_spinner_dropdown_item,result);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pickupPerson.setAdapter(adapter);
        }
    }

}
