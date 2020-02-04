package com.example.atplsdim;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class InventoryIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_in);
        if(getSupportActionBar()!=null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Inventory In (Warehouse)");
        }
    }

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
}
