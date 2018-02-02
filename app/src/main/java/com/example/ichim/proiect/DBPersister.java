package com.example.ichim.proiect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by ichim on 22-Nov-15.
 */
public class DBPersister {

    protected static final String COL_ID="ID";

    protected static final String COL_USER="USER";
    protected static final String COL_TYPE="TYPE";
    protected static final String COL_VALUE="VALUE";

    protected static final String COL_ID_PRODUCT="ID_PRODUCT";
    protected static final String COL_TITLE="TITLE";

    protected static final String COL_RATING="RATING";

    protected static final String COL_NAME="NAME";
    protected static final String COL_CATEGORY="CATEGORY";
    protected static final String COL_PRICE="PRICE";

    protected static final String COL_ID_LIST="ID_LIST";

    protected static final String COL_MonthlyIncome="MONTHLY_INCOME";
    protected static final String COL_ExpenseLimit="EXPENSE_LIMIT";
    protected static final String COL_SalaryDate="SALARY_DATE";
    protected static final String COL_PersonalSavings="PERSONAL_SAVINGS";
    protected static final String COL_CurrentMonth="CURRENT_MONTH";
    protected static final String COL_Expenses="EXPENSES";
    protected static final String COL_Incomes="INCOMES";
    protected static final String COL_CurrentBalance="CURRENT_BALANCE";

    AccessSQLite referenceDB=null;
    protected static Context context;

    public DBPersister(Context context){
        DBPersister.context=context;
        referenceDB=new AccessSQLite(context,AccessSQLite.DATABASE,null,50);
    }

    public void CloseDB(){
        referenceDB.close();
    }

    /*public User insertUser(){

    }

    public void deleteUser(int user)*/

    public void insertList(ProductList list,String user){
        SQLiteDatabase db;
        ContentValues values=new ContentValues();
        db=referenceDB.getWritableDatabase();

        //INSERT LIST IN LISTS
        values.put(COL_TITLE,list.getTitle());
        values.put(COL_VALUE,list.getValue());
        values.put(COL_RATING,list.getRating());

        long listID=db.insert(AccessSQLite.TABLE_LISTS,null,values);

        for(Product product : list.getList()){
            //INSERT PRODUCT IN PRODUCTS
            values.clear();
            values.put(COL_NAME, product.getName());
            values.put(COL_CATEGORY, product.getCategory().toString());
            values.put(COL_PRICE,product.getPrice());
            long productID=db.insert(AccessSQLite.TABLE_PRODUCTS,null,values);

            //INSERT PRODUCT,LIST IN LISTS_PRODUCTS
            values.clear();
            values.put(COL_ID_LIST, listID);
            values.put(COL_ID_PRODUCT,productID);
            db.insert(AccessSQLite.TABLE_LISTS_PRODUCTS,null,values);
        }

        //INSERT IN PREFS WITH TYPE=TYPE.LIST
        values.clear();
        values.put(COL_USER, user);
        values.put(COL_TYPE, Type.List.toString());
        values.put(COL_VALUE, listID);
        db.insert(AccessSQLite.TABLE_PREFS,null,values);

        db.close();
    }

    public void insertAllListsForUser(ArrayList<ProductList> lists,String user){
        for(ProductList list : lists){
            insertList(list,user);
        }
    }

    //DELETE ALL LISTS AND PRODUCTS FOR A USER
    public void deleteListsForUser(String user){
        SQLiteDatabase readableDB=referenceDB.getReadableDatabase();
        SQLiteDatabase writableDB=referenceDB.getWritableDatabase();
        String getLists="SELECT "+COL_VALUE+
                " FROM "+AccessSQLite.TABLE_PREFS+
                " WHERE "+COL_USER+" = '"+user+"' AND TYPE ='"+Type.List.toString()+"'";
        Cursor resultLists=readableDB.rawQuery(getLists,null);
        for (resultLists.moveToFirst(); !resultLists.isAfterLast(); resultLists.moveToNext()) {
            int listID=resultLists.getInt(resultLists.getColumnIndex(COL_VALUE));

            String getProducts="SELECT "+ COL_ID_PRODUCT +
                    " FROM "+AccessSQLite.TABLE_LISTS_PRODUCTS+
                    " WHERE "+COL_ID_LIST+" = "+listID;
            Cursor resultProducts=readableDB.rawQuery(getProducts,null);
            for (resultProducts.moveToFirst(); !resultProducts.isAfterLast(); resultProducts.moveToNext()){
                int productID=resultProducts.getInt(resultProducts.getColumnIndex(COL_ID_PRODUCT));
                String deleteProduct="DELETE FROM "+AccessSQLite.TABLE_PRODUCTS+
                        " WHERE "+COL_ID+" = "+productID;
                writableDB.execSQL(deleteProduct);

                String deleteListsXProducts="DELETE FROM "+AccessSQLite.TABLE_LISTS_PRODUCTS+
                        " WHERE "+COL_ID_LIST+" = "+listID+" AND "+COL_ID_PRODUCT+" = "+productID;
                writableDB.execSQL(deleteListsXProducts);
            }
            resultProducts.close();
            String deleteList="DELETE FROM "+AccessSQLite.TABLE_LISTS+
                    " WHERE "+COL_ID+" = "+listID;
            writableDB.execSQL(deleteList);

            String deleteFromPrefs="DELETE FROM "+AccessSQLite.TABLE_PREFS+
                    " WHERE "+COL_USER+"='"+user+"' AND "+COL_VALUE+"='"+listID+"'"+
                    " AND "+COL_TYPE+"='"+Type.List.toString()+"'";
            writableDB.execSQL(deleteFromPrefs);
        }
        resultLists.close();
        readableDB.close();
        writableDB.close();
    }

    public ArrayList<ProductList> getAllListsForUser(String user){
        ArrayList<ProductList> lists=new ArrayList<ProductList>();
        SQLiteDatabase readableDB=referenceDB.getReadableDatabase();
        String getLists="SELECT "+COL_VALUE+
                " FROM "+AccessSQLite.TABLE_PREFS+
                " WHERE "+COL_USER+" = '"+user+"' AND TYPE ='"+Type.List.toString()+"'";
        Cursor resultLists=readableDB.rawQuery(getLists,null);
        for (resultLists.moveToFirst(); !resultLists.isAfterLast(); resultLists.moveToNext()) {
            int listID=resultLists.getInt(resultLists.getColumnIndex(COL_VALUE));
            ProductList productList=new ProductList();
            String getProducts="SELECT "+ COL_ID_PRODUCT+
                    " FROM "+AccessSQLite.TABLE_LISTS_PRODUCTS+
                    " WHERE "+COL_ID_LIST+" = "+listID;
            Cursor resultProducts=readableDB.rawQuery(getProducts,null);
            ArrayList<Product> products=new ArrayList<Product>();
            for (resultProducts.moveToFirst(); !resultProducts.isAfterLast(); resultProducts.moveToNext()){
                int productID=resultProducts.getInt(resultProducts.getColumnIndex(COL_ID_PRODUCT));
                String getProduct="SELECT "+COL_NAME+","+COL_CATEGORY+","+COL_PRICE+
                        " FROM "+AccessSQLite.TABLE_PRODUCTS+
                        " WHERE "+COL_ID+" = "+productID;
                Cursor resultProduct=readableDB.rawQuery(getProduct,null);
                resultProduct.moveToFirst();
                Product product=new Product();
                product.setName(resultProduct.getString(resultProduct.getColumnIndex(COL_NAME)));
                product.setCategory(Category.valueOf(resultProduct.getString(resultProduct.getColumnIndex(COL_CATEGORY))));
                product.setPrice(resultProduct.getDouble(resultProduct.getColumnIndex(COL_PRICE)));
                resultProduct.close();
                products.add(product);
            }
            resultProducts.close();
            productList.setList(products);
            String getListDetails="SELECT "+COL_TITLE+","+COL_VALUE+","+COL_RATING+
                    " FROM "+AccessSQLite.TABLE_LISTS+
                    " WHERE "+COL_ID+" = "+listID;
            Cursor listDetails=readableDB.rawQuery(getListDetails,null);
            listDetails.moveToFirst();
            productList.setTitle(listDetails.getString(listDetails.getColumnIndex(COL_TITLE)));
            productList.setValue(listDetails.getDouble(listDetails.getColumnIndex(COL_VALUE)));
            productList.setRating(listDetails.getFloat(listDetails.getColumnIndex(COL_RATING)));
            lists.add(productList);
        }
        resultLists.close();
        readableDB.close();
        return lists;
    }

    public void insertPasswordForUser(String user,String password){
        SQLiteDatabase db;
        ContentValues values=new ContentValues();
        db=referenceDB.getWritableDatabase();

        values.put(COL_USER,user);
        values.put(COL_TYPE,Type.Password.toString());
        values.put(COL_VALUE,password);

        db.insert(AccessSQLite.TABLE_PREFS, null, values);

        db.close();
    }

    public boolean checkPassword(String user,String password){
        SQLiteDatabase readableDB=referenceDB.getReadableDatabase();
        String getPassword="SELECT "+COL_VALUE+
                " FROM "+AccessSQLite.TABLE_PREFS+
                " WHERE "+COL_TYPE+"='"+Type.Password.toString()+"'"+
                " AND "+COL_USER+"='"+user+"'";

        Cursor result=readableDB.rawQuery(getPassword,null);
        result.moveToFirst();

        if(result!=null && result.getColumnCount()>0)
            return password.equals(result.getString(result.getColumnIndex(COL_VALUE))) ? true : false;
        return false;
    }

    public boolean checkUsername(String user){
        SQLiteDatabase readableDB=referenceDB.getReadableDatabase();
        String getUser="SELECT "+COL_USER+
                " FROM "+AccessSQLite.TABLE_PREFS+
                " WHERE "+COL_USER+"='"+user+"'";
        Cursor result=readableDB.rawQuery(getUser,null);
        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()){
            return false;
        }
        return true;
    }

    public void insertUserSettingsForUser(String user,UserSettings settings){
        SQLiteDatabase db;
        ContentValues values=new ContentValues();
        db=referenceDB.getWritableDatabase();

        values.put(COL_MonthlyIncome,settings.getMonthlyIncome());
        values.put(COL_ExpenseLimit,settings.getExpenseLimit());
        values.put(COL_SalaryDate,settings.getSalaryDate());
        values.put(COL_PersonalSavings,settings.getPersonalSavings());
        values.put(COL_CurrentMonth,settings.getCurrentMonth());
        values.put(COL_Expenses,settings.getExpenses());
        values.put(COL_Incomes,settings.getIncomes());
        values.put(COL_CurrentBalance,settings.getCurrentBalance());

        long settingsID=db.insert(AccessSQLite.TABLE_USER_SETTINGS,null,values);

        values.clear();

        values.put(COL_USER, user);
        values.put(COL_TYPE, Type.UserSettings.toString());
        values.put(COL_VALUE,settingsID);

        db.insert(AccessSQLite.TABLE_PREFS, null, values);

        db.close();
    }

    public UserSettings readUserSettingsForUser(String user){
        SQLiteDatabase readableDB=referenceDB.getReadableDatabase();

        String getUserSettingsID="SELECT "+COL_VALUE+
                " FROM "+AccessSQLite.TABLE_PREFS+
                " WHERE "+COL_TYPE+"='"+Type.UserSettings.toString()+"' AND "+COL_USER+"='"+user+"'";
        Cursor result=readableDB.rawQuery(getUserSettingsID, null);
        result.moveToFirst();

        long settingsID=Long.parseLong(result.getString(result.getColumnIndex(COL_VALUE)));

        String getUserSettings="SELECT "+COL_MonthlyIncome+","+COL_ExpenseLimit+","+COL_SalaryDate
                +","+COL_PersonalSavings+","+COL_CurrentMonth+","+COL_Expenses+","+COL_Incomes+","+COL_CurrentBalance+
                " FROM "+AccessSQLite.TABLE_USER_SETTINGS+
                " WHERE "+COL_ID+"="+settingsID;
        Cursor settings=readableDB.rawQuery(getUserSettings,null);
        settings.moveToFirst();

        UserSettings userSettings=new UserSettings();
        userSettings.setMonthlyIncome(settings.getDouble(settings.getColumnIndex(COL_MonthlyIncome)));
        userSettings.setExpenseLimit(settings.getDouble(settings.getColumnIndex(COL_ExpenseLimit)));
        userSettings.setSalaryDate(settings.getInt(settings.getColumnIndex(COL_SalaryDate)));
        userSettings.setPersonalSavings(settings.getDouble(settings.getColumnIndex(COL_PersonalSavings)));
        userSettings.setCurrentMonth(settings.getInt(settings.getColumnIndex(COL_CurrentMonth)));
        userSettings.setExpenses(settings.getDouble(settings.getColumnIndex(COL_Expenses)));
        userSettings.setIncomes(settings.getDouble(settings.getColumnIndex(COL_Incomes)));
        userSettings.setCurrentBalance(settings.getDouble(settings.getColumnIndex(COL_CurrentBalance)));

        result.close();
        settings.close();
        readableDB.close();

        return userSettings;
    }

    public void updateUserSettings(UserSettings userSettings,String user){
        SQLiteDatabase writableDB = referenceDB.getWritableDatabase();
        SQLiteDatabase readableDB=referenceDB.getReadableDatabase();
        String getUserSettingsID="SELECT "+COL_VALUE+
                " FROM "+AccessSQLite.TABLE_PREFS+
                " WHERE "+COL_TYPE+"='"+Type.UserSettings.toString()+"' AND "+COL_USER+"='"+user+"'";
        Cursor result=readableDB.rawQuery(getUserSettingsID, null);
        result.moveToFirst();

        long settingsID=Long.parseLong(result.getString(result.getColumnIndex(COL_VALUE)));

        String updateSettings="UPDATE "+AccessSQLite.TABLE_USER_SETTINGS+
                " SET "+COL_MonthlyIncome+"="+userSettings.getMonthlyIncome()+","+
                COL_ExpenseLimit+"="+userSettings.getExpenseLimit()+","+
                COL_SalaryDate+"="+userSettings.getSalaryDate()+","+
                COL_PersonalSavings+"="+userSettings.getPersonalSavings()+","+
                COL_CurrentMonth+"="+userSettings.getCurrentMonth()+","+
                COL_Expenses+"="+userSettings.getExpenses()+","+
                COL_Incomes+"="+userSettings.getIncomes()+","+
                COL_CurrentBalance+"="+userSettings.getCurrentBalance()+
                " WHERE "+COL_ID+"="+settingsID;

        writableDB.execSQL(updateSettings);

        writableDB.close();

    }
}
