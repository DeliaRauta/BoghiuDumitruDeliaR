package com.example.ichim.proiect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    static ArrayList<ProductList> productLists=new ArrayList<ProductList>();
    static String user="";
    static UserSettings userSettings;
    static TextView expensesTV;
    static TextView incomesTV;
    static TextView currentBalanceTV;
    static  ProgressBar progressBar;
    DBPersister dbPersister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras= getIntent().getExtras();
        if(extras!=null){
            user=extras.getString("user");
        }

        expensesTV=(TextView)findViewById(R.id.expenses);
        incomesTV=(TextView)findViewById(R.id.incomes);
        currentBalanceTV=(TextView)findViewById(R.id.currentBalance);

        dbPersister=new DBPersister(this);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        userSettings=dbPersister.readUserSettingsForUser(user);
        if(userSettings.getCurrentMonth()!= Calendar.getInstance().getTime().getMonth()
                && userSettings.getSalaryDate()<Calendar.getInstance().getTime().getDay()){
            //maybe make a report with last month
            userSettings.setExpenses(0);
            userSettings.setIncomes(userSettings.getMonthlyIncome());
            userSettings.setCurrentMonth(Calendar.getInstance().getTime().getMonth());
            userSettings.setCurrentBalance(userSettings.getCurrentBalance() + userSettings.getMonthlyIncome());
            currentBalanceTV.setText(String.valueOf(userSettings.getCurrentBalance()));
            progressBar.setProgress(0);
        }

        productLists=dbPersister.getAllListsForUser(user);

        expensesTV.setText(String.valueOf(userSettings.getExpenses()));
        incomesTV.setText(String.valueOf(userSettings.getIncomes()));
        currentBalanceTV.setText(String.valueOf(userSettings.getCurrentBalance()));


        progressBar.setMax((int) userSettings.getExpenseLimit());
        progressBar.setProgress((int) userSettings.getExpenses());

        Button addExpense=(Button)findViewById(R.id.addExpense);

        expensesTV.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                progressBar.setProgress(Double.valueOf(s.toString()).intValue());
            }
        });

    }

    @Override
    protected void onDestroy() {
        dbPersister.deleteListsForUser(user);
        dbPersister.insertAllListsForUser(productLists, user);
        dbPersister.updateUserSettings(userSettings, user);
        dbPersister.CloseDB();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Intent newActivity=new Intent(MainActivity.this,Settings.class);
            newActivity.putExtra("monthlyIncome",userSettings.getMonthlyIncome());
            newActivity.putExtra("limit",userSettings.getExpenseLimit());
            newActivity.putExtra("salaryDate",userSettings.getSalaryDate());
            newActivity.putExtra("personalSavings",userSettings.getPersonalSavings());
            startActivityForResult(newActivity,3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AddIncome(View view){
        Intent newActivity=new Intent(MainActivity.this,AddIncome.class);
        startActivity(newActivity);
    }

    public void showPopUpMenu(View view){
        PopupMenu popupMenu=new PopupMenu(this,view);
        MenuInflater menuInflater=popupMenu.getMenuInflater();
        PopUpMenuEventHandle popUpMenuEventHandle=new PopUpMenuEventHandle(getApplicationContext());
        popupMenu.setOnMenuItemClickListener(popUpMenuEventHandle);
        menuInflater.inflate(R.menu.menu_add_expense,popupMenu.getMenu());
        popupMenu.show();
    }

    public class PopUpMenuEventHandle implements PopupMenu.OnMenuItemClickListener {
        Context context;
        public PopUpMenuEventHandle(Context context){
            this.context =context;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId()==R.id.add_product) {
                Intent newActivity = new Intent(MainActivity.this, AddProduct.class);
                startActivityForResult(newActivity, 0);
                return true;
            }
            if(item.getItemId()==R.id.choose_product){
                Intent newActivity = new Intent(MainActivity.this, Products.class);
                newActivity.putExtra("fromJSON",true);
                startActivityForResult(newActivity, 0);
                return true;
            }
            if(item.getItemId()==R.id.choose_list){
                Intent newActivity = new Intent(MainActivity.this, ListOfLists.class);
                newActivity.putParcelableArrayListExtra("lists", productLists);
                startActivityForResult(newActivity, 1);
            }
            if(item.getItemId()==R.id.add_list){
                Intent newActivity = new Intent(MainActivity.this, Products.class);
                startActivityForResult(newActivity, 2);
            }
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Product product = data.getParcelableExtra("product");
                UpdateExpenses(product.getPrice());
            }
        }
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ProductList productList=data.getParcelableExtra("productList");
                UpdateExpenses(productList.getValue());
            }
        }
        if(requestCode==2){
            if(resultCode==Activity.RESULT_OK){
                ProductList list=new ProductList();
                list.setTitle(data.getStringExtra("title"));
                ArrayList<Product> prod=data.getParcelableArrayListExtra("products");
                list.setList(prod);
                list.setValue(data.getDoubleExtra("value", 0));
                productLists.add(list);
                UpdateExpenses(list.getValue());
            }
        }
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                UserSettings settings = data.getParcelableExtra("settings");
                userSettings.setMonthlyIncome(settings.getMonthlyIncome());
                userSettings.setExpenseLimit(settings.getExpenseLimit());
                userSettings.setSalaryDate(settings.getSalaryDate());
                userSettings.setCurrentBalance(userSettings.getCurrentBalance() - userSettings.getPersonalSavings() + settings.getPersonalSavings());
                currentBalanceTV.setText(String.valueOf(userSettings.getCurrentBalance()));
                userSettings.setPersonalSavings(settings.getPersonalSavings());

                progressBar.setProgress(0);
                progressBar.setMax((int) userSettings.getExpenseLimit());
                progressBar.setProgress((int) userSettings.getExpenses());
            }
        }
    }

    public void UpdateExpenses(double expense){
        userSettings.setExpenses(userSettings.getExpenses() + expense);
        expensesTV.setText(String.valueOf(userSettings.getExpenses()));
        userSettings.setCurrentBalance(userSettings.getCurrentBalance() - expense);
        currentBalanceTV.setText(String.valueOf(userSettings.getCurrentBalance()));
    }
}
