package com.example.ichim.proiect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class Test extends AppCompatActivity {

    static ArrayList<Product> products = new ArrayList<Product>();
    static ArrayList<ProductList> productLists=new ArrayList<ProductList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void AddProduct(View view) {
        Intent newActivity = new Intent(Test.this, AddProduct.class);
        startActivityForResult(newActivity, 0);

    }

    public void AddList(View view) {
        Intent newActivity = new Intent(Test.this, Products.class);
        startActivityForResult(newActivity, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Product product = data.getParcelableExtra("product");
                products.add(product);
            }
        }
        if(requestCode==1){
            if(resultCode==Activity.RESULT_OK){
                ProductList list=new ProductList();
                list.setTitle(data.getStringExtra("title"));
                ArrayList<Product> prod=data.getParcelableArrayListExtra("products");
                list.setList(prod);
                list.setValue(data.getDoubleExtra("value", 0));
                productLists.add(list);
            }
        }
    }

    public void SeeLists(View view){
        Intent newActivity = new Intent(Test.this, ListOfLists.class);
        newActivity.putParcelableArrayListExtra("lists", productLists);
        startActivity(newActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void ReadFromDB(View view){
        DBPersister persister=new DBPersister(this);
        productLists=persister.getAllListsForUser("gege");
    }

    public void WriteTODB(View view){
        DBPersister persister=new DBPersister(this);
        persister.deleteListsForUser("gege");
        persister.insertAllListsForUser(productLists,"gege");
    }
}
