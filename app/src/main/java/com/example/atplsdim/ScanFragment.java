package com.example.atplsdim;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends Fragment implements ZXingScannerView.ResultHandler {
    String scannedResult;
    ZXingScannerView zXingScannerView;
    private static final String TAG = "ScanFragment";
    RequestQueue requestQueue;
    private QRCodeReaderView qrCodeReaderView;
    Context context;
    ScanFragment scanFragment;


    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_scan, container, false);
      //  zXingScannerView = new ZXingScannerView(getActivity());
      //  setContentView(zXingScannerView);
       // inflater.inflate(zXingScannerView,container,false);
      ////  zXingScannerView.setResultHandler(ScanFragment.this);
       // zXingScannerView.startCamera();
        qrCodeReaderView=view.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String text, PointF[] points) {
                scannedResult=text;
                //else{
                    //  new CheckUniqueId().execute();
                  /**  if(get().getStringExtra("Scanner Out")!=null){
                        checkOutUID(scannedResult);
                    }
                    else {
                        checkUID(scannedResult);
                    }**/
                  checkUID(scannedResult);
                }
            //}
        });
        requestQueue = Volley.newRequestQueue(getActivity());
        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

        return view;

    }

    private void checkUID(String scannedResult) {
        final String urlOut=Constants.BASE_URL+"ExistUID?UniqueSku="+scannedResult+"&type=true";
        Log.e(TAG,"Url is "+urlOut);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,urlOut,onPostsOutLoaded,onPostsOutError);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

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
          //  Toast.makeText(getApplicationContext(),"Error is: "+error.toString(),Toast.LENGTH_LONG).show();
        }
    };


  /**  private void checkUID(String scannedResult) {
    }**/

    @Override
    public void handleResult(Result result) {

    }
    private class CheckUniqueOutId extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... response) {
            try {
                JSONObject jsonObject = new JSONObject(response[0]);
                String message = jsonObject.getString("Message");
                String resultCode = jsonObject.getString("ResultCode");
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UniqueIdResponse",MODE_PRIVATE);
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
            String resultCode, message;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UniqueIdResponse",MODE_PRIVATE);
            resultCode=sharedPreferences.getString("ResultCode","");
            message=sharedPreferences.getString("Message","");
            if(resultCode.equals("0")||resultCode.equals("2")){
               /** new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                             //   Intent intent = new Intent(ScannerActivity.this,InventoryOut.class);
                               // startActivity(intent);
                               // qrCodeReaderView.stopCamera();
                                FragmentManager fragmentManager=getFragmentManager();
                                fragmentManager.popBackStack();

                            }
                        })
                        .setCancelable(true)
                        .create()
                        .show();**/
               Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
          //     FragmentManager fragmentManager=getFragmentManager().beginTransaction().remove(scanFragment).commit();

            }
            else if(resultCode.equals("1")){
              //  Intent intent = new Intent(ScannerActivity.this,InventoryOut.class);
                //intent.putExtra("ScanResultOut","Successful");
                //startActivity(intent);
            }

        }
    }

}
