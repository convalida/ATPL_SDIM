package com.example.atplsdim;

import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    String scannedResult;
    ZXingScannerView zXingScannerView;
    private static final String TAG = "ScannerActivity";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        zXingScannerView = new ZXingScannerView(this);
        setContentView(zXingScannerView);
        this.setFinishOnTouchOutside(false);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
        requestQueue = Volley.newRequestQueue(ScannerActivity.this);
    }

    public void handleResult(Result result){
        Log.e(TAG,"Scanned result: "+result.getText());
        Log.e(TAG,"Barcode format: "+result.getBarcodeFormat().toString());
        scannedResult=result.getText();
        if(!isNetworkAvailable()){
            new AlertDialog.Builder(ScannerActivity.this)
                    .setMessage("Internet connection is required")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=getIntent();
                            startActivity(intent);
                        }
                    })
                    .setCancelable(false)
                    .create().show();
        }
        else{
          //  new CheckUniqueId().execute();
            if(getIntent().getStringExtra("Scanner Out")!=null){
                checkOutUID(scannedResult);
            }
            else {
                checkUID(scannedResult);
            }
        }
    }

    private void checkOutUID(String scannedResult) {
        final String urlOut=Constants.BASE_URL+"ExistUID?UniqueSku="+scannedResult+"&type=false";
        Log.e(TAG,"Url is "+urlOut);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,urlOut,onPostsOutLoaded,onPostsOutError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void checkUID(final String scannedResult) {
        final String MAIN=Constants.BASE_URL+"ExistUID?UniqueSku="+scannedResult+"&type=true";
        Log.e(TAG,"Url is "+MAIN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,MAIN,onPostsLoaded,onPostsError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
     /**  String url="http://atplsdim.autofurnish.com/inventoryPages/inventory.asmx/ExistUID";
      final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
              try {
                  JSONObject jsonObject = new JSONObject(response);
                  String message = jsonObject.getString("Message");
                  String resultCode = jsonObject.getString("ResultCode");
                  if (resultCode.equals(String.valueOf(0))) {
                      Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                  }
              } catch (JSONException e) {
                  e.printStackTrace();
              }
          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
              Toast.makeText(getApplicationContext(),"Some error occurred",Toast.LENGTH_LONG).show();
              error.printStackTrace();
              Log.e(TAG,"Error is "+error.toString());
          }
      }){
          protected Map<String,String> getParams() throws AuthFailureError{
              Map<String,String> parameters=new HashMap<>();
              parameters.put("UniqueSku",scannedResult);
              parameters.put("type","True");
              Log.e(TAG,"Parameters are: "+parameters);
              return parameters;
          }
      };
        RequestQueue rQueue = Volley.newRequestQueue(ScannerActivity.this);
        rQueue.add(stringRequest);**/
    }

    Response.Listener<String> onPostsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.e(TAG, "Response: "+response);
            CheckUniqueId checkUniqueId = new CheckUniqueId();
            checkUniqueId.execute(response);

        }
    };

    Response.ErrorListener onPostsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error is: "+error.toString());
            Toast.makeText(getApplicationContext(),"Error is: "+error.toString(),Toast.LENGTH_LONG).show();
        }
    };

    Response.Listener<String> onPostsOutLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
      Log.e(TAG,"Response: "+response);
      CheckUniqueOutId checkUniqueOutId = new CheckUniqueOutId();
      checkUniqueOutId.execute(response);
        }
    };

    Response.ErrorListener onPostsOutError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
      Log.e(TAG,"Error is: "+error.toString());
      Toast.makeText(getApplicationContext(),"Error is: "+error.toString(),Toast.LENGTH_LONG).show();
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null;
    }

    public void onPause(){
        super.onPause();
        zXingScannerView.stopCamera();
    }

    private class CheckUniqueOutId extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... response) {
            try {
                JSONObject jsonObject = new JSONObject(response[0]);
                String message = jsonObject.getString("Message");
                String resultCode = jsonObject.getString("ResultCode");
                SharedPreferences sharedPreferences = getSharedPreferences("UniqueIdResponse",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ResultCodeOut",resultCode);
                editor.putString("MessageOut",message);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            String resultCode, message;
            SharedPreferences sharedPreferences = getSharedPreferences("UniqueIdResponse",MODE_PRIVATE);
            resultCode=sharedPreferences.getString("ResultCodeOut","");
            message=sharedPreferences.getString("MessageOut","");
            if(resultCode.equals("0")||resultCode.equals("2")){
                new AlertDialog.Builder(ScannerActivity.this)
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(ScannerActivity.this,InventoryOut.class);
                                startActivity(intent);
                              //  finish();
                            }
                        })
                        .setCancelable(true)
                        .create()
                        .show();

            }
            else if(resultCode.equals("1")){
                Intent intent = new Intent(ScannerActivity.this,InventoryOut.class);
                intent.putExtra("ScanResultOut","Successful");
                startActivity(intent);
             //   finish();
            }

        }
    }

    private  class CheckUniqueId extends AsyncTask<String,Void,Void>{
        int flag = 0;
        String result="";
        String ResponseMessage="";
        @Override
        protected Void doInBackground(String... response) {
            try {
                JSONObject jsonObject = new JSONObject(response[0]);
                String message = jsonObject.getString("Message");
                String resultCode = jsonObject.getString("ResultCode");
                if(resultCode.equals(String.valueOf(0))){
                 //   Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Message is: "+message);
                }
                SharedPreferences sharedPreferences = getSharedPreferences("UniqueIdResponse",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ResultCode",resultCode);
                editor.putString("Message",message);
                editor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            String resultCode,message;
            SharedPreferences sharedPreferences = getSharedPreferences("UniqueIdResponse",MODE_PRIVATE);
            resultCode=sharedPreferences.getString("ResultCode","");
            message=sharedPreferences.getString("Message","");
          //  Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
            if(resultCode.equals("0")||resultCode.equals("2")){
                    new AlertDialog.Builder(ScannerActivity.this)
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                     Intent intent = new Intent(ScannerActivity.this,InventoryIn.class);
                                     startActivity(intent);
                                     finish();
                                }
                            })
                            .setCancelable(true)
                            .create()
                            .show();

            }
            else if(resultCode.equals("1")){
                Intent intent = new Intent(ScannerActivity.this,InventoryIn.class);
                intent.putExtra("Scan result",scannedResult);
                startActivity(intent);
                finish();
              // onBackPressed();
            }
        }
    }
}
