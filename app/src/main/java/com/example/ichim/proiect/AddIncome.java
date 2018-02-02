package com.example.ichim.proiect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class AddIncome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void Add(View view){
        EditText income=(EditText)findViewById(R.id.income);
        MainActivity.userSettings.setIncomes(MainActivity.userSettings.getIncomes()+Double.valueOf(income.getText().toString()));
        MainActivity.incomesTV.setText(String.valueOf(MainActivity.userSettings.getIncomes()));
        MainActivity.userSettings.setCurrentBalance(MainActivity.userSettings.getCurrentBalance() +
                Double.valueOf(income.getText().toString()));
        MainActivity.currentBalanceTV.setText(String.valueOf(MainActivity.userSettings.getCurrentBalance()));
        finish();
    }

}
