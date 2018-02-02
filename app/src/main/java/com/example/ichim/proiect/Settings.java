package com.example.ichim.proiect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Settings extends AppCompatActivity {
    TextView limit;
    SeekBar sb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        limit=(TextView)findViewById(R.id.limit);
        limit.setText("0");

        sb = (SeekBar)findViewById(R.id.sExpenseLimit);

        Bundle extras= getIntent().getExtras();
        if(extras!=null){
            ((EditText)findViewById(R.id.sMonthlyIncome)).setText(String.valueOf((int)extras.getDouble("monthlyIncome")));
            sb.setMax((int) extras.getDouble("monthlyIncome"));
            sb.setProgress((int) extras.getDouble("limit"));
            limit.setText(String.valueOf(extras.getDouble("limit")));
            ((EditText)findViewById(R.id.sSalaryDate)).setText(String.valueOf(extras.getInt("salaryDate")));
            ((EditText)findViewById(R.id.sPersonalSavings)).setText(String.valueOf(extras.getDouble("personalSavings")));
        }


        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                limit.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((EditText)findViewById(R.id.sMonthlyIncome)).addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                sb.setProgress(0);
                sb.setMax(Integer.valueOf(s.toString()));
            }
        });
    }

    public void SaveSettings(View view){
        UserSettings settings=new UserSettings();

        if(((EditText) findViewById(R.id.sMonthlyIncome)).getText().toString().equals("")||
                limit.getText().toString().equals("")||
                ((EditText) findViewById(R.id.sSalaryDate)).getText().toString().equals("")||
                ((EditText) findViewById(R.id.sPersonalSavings)).getText().toString().equals(""))
            Toast.makeText(this,"Add values for all items",Toast.LENGTH_SHORT).show();
        else if( Integer.parseInt(((EditText) findViewById(R.id.sSalaryDate)).getText().toString())>31)
            Toast.makeText(this,"Salary date must be <31",Toast.LENGTH_SHORT).show();
        else {
            settings.setMonthlyIncome(Double.valueOf(((EditText) findViewById(R.id.sMonthlyIncome)).getText().toString()));
            settings.setExpenseLimit(Double.valueOf(limit.getText().toString()));
            settings.setSalaryDate(Integer.valueOf(((EditText) findViewById(R.id.sSalaryDate)).getText().toString()));
            settings.setPersonalSavings(Double.valueOf(((EditText) findViewById(R.id.sPersonalSavings)).getText().toString()));
            settings.setCurrentMonth(Calendar.getInstance().getTime().getMonth());
            settings.setIncomes(settings.getMonthlyIncome());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("settings", settings);
            setResult(Activity.RESULT_OK, resultIntent);

            finish();
        }
    }

}
