package com.example.atplsdim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;

public class Login extends AppCompatActivity {
    EditText userName,password;
    Button signInBtn;
    CheckBox checkBox;
    boolean rememberMe;

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
        if(userName.getText().toString().length()>0 && password.getText().toString().length()>0){
            Intent intent=new Intent(Login.this,InventoryIn.class);
           // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
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

    public void onBackPressed(){
        super.onBackPressed();
        finishAffinity();
    }
}
