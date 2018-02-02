package com.example.ichim.proiect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void LogIn(View view){
        EditText username=(EditText)findViewById(R.id.etUsername);
        EditText password=(EditText)findViewById(R.id.etPassword);
        DBPersister persister=new DBPersister(this);
        if(!username.getText().equals("") && !password.getText().equals("") &&
                persister.checkPassword(username.getText().toString(),password.getText().toString()) ){
            Intent newActivity=new Intent(LogIn.this,MainActivity.class);
            newActivity.putExtra("user",username.getText().toString());
            startActivity(newActivity);
            finish();
        }
        else
            Toast.makeText(this,"Wrong username or password",Toast.LENGTH_SHORT).show();
    }

    public void CreateNewAccount(View view){
        Intent newActivity=new Intent(LogIn.this,Register.class);
        startActivity(newActivity);
    }

}
