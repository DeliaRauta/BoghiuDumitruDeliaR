package com.example.ichim.proiect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    UserSettings userSettings=null;
    EditText name;
    EditText password;
    DBPersister persister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name=(EditText)findViewById(R.id.etName);
        password=(EditText)findViewById(R.id.etPassword);

        persister=new DBPersister(this);

    }

    public void CreateAccount(View view){
        String user=name.getText().toString();
        String pass=password.getText().toString();
        if(userSettings==null)
            Toast.makeText(Register.this, "Please configure your settings", Toast.LENGTH_SHORT).show();
        else if(user.equals("")||pass.equals(""))
            Toast.makeText(Register.this,"Add a username and password",Toast.LENGTH_SHORT).show();
        else if(!persister.checkUsername(name.getText().toString()))
            Toast.makeText(Register.this,"Username taken",Toast.LENGTH_SHORT).show();
        else{

            //write user in db
            persister.insertPasswordForUser(user,pass);
            persister.insertUserSettingsForUser(user,userSettings);

            finish();
        }
    }

    public void CreateSettings(View view){
        Intent newActivity=new Intent(Register.this,Settings.class);
        startActivityForResult(newActivity,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                userSettings = data.getParcelableExtra("settings");
                userSettings.setCurrentBalance(userSettings.getPersonalSavings());
            }
        }
    }
}
