package com.example.ichim.proiect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddProduct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras= getIntent().getExtras();
        if(extras!=null){
            EditText name=(EditText)findViewById(R.id.product_name);
            name.setText(extras.getString("name"));

            Spinner category=(Spinner)findViewById(R.id.category);
            category.setSelection(extras.getInt("category"));

            EditText price=(EditText)findViewById(R.id.price);
            price.setText(String.valueOf(extras.getDouble("price")));

            Button add=(Button)findViewById(R.id.add);
            add.setText("Save");
        }
    }


    public void Add(View view){
        Product product =new Product();

        EditText nume=(EditText)findViewById(R.id.product_name);
        product.setName(nume.getText().toString());

        Spinner categorie=(Spinner)findViewById(R.id.category);
        switch (categorie.getSelectedItem().toString()){
            case "Food": product.setCategory(Category.Food);
                break;
            case "Fashion": product.setCategory(Category.Fashion);
                break;
            case "Utilities": product.setCategory(Category.Utilities);
                break;
            case "Other": product.setCategory(Category.Other);
                break;
        }

        EditText price=(EditText)findViewById(R.id.price);
        product.setPrice(Double.parseDouble(price.getText().toString()));

        Intent returnIntent = new Intent();
        returnIntent.putExtra("product", product);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }
}
