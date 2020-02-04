package com.example.atplsdim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    String userName, password;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login",MODE_PRIVATE);
            userName=sharedPreferences.getString("userName","");
            password=sharedPreferences.getString("password","");
            Log.e(TAG, "User name is "+userName);
            Log.e(TAG,"Password is "+password);
            if(userName.equals("")||password.equals("")){
                Intent intent = new Intent(MainActivity.this, Login.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(MainActivity.this,InventoryIn.class);
                startActivity(intent);
            }


        finish();
    }
}
