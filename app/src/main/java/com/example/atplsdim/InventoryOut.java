package com.example.atplsdim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class InventoryOut extends AppCompatActivity {
    String name;
    EditText productId, orderNumber, referenceNumber;
    Button addProduct, uploadData;
    Spinner thirdParty, pickupPerson;
    private static final int MY_PERMISSION_CAMERA=98;
    LinearLayout linearLayout;
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
        orderNumber=findViewById(R.id.orderNumber);
        referenceNumber=findViewById(R.id.referenceNo);
        addProduct=findViewById(R.id.addProduct);
        uploadData=findViewById(R.id.uploadData);
        thirdParty=findViewById(R.id.thirdPartySpinner);
        linearLayout=findViewById(R.id.verticalLinearLayout);
        pickupPerson=findViewById(R.id.pickupPersonSpinner);


        
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
        }
    }

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
            return true;
        }
    }

}
