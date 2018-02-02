package com.example.ichim.proiect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ichim on 22-Nov-15.
 */
public class AccessSQLite extends SQLiteOpenHelper {

    public static String DATABASE="shopping.db";
    public static String TABLE_PREFS="PREFS";
    public static String TABLE_LISTS="LISTS";
    public static String TABLE_PRODUCTS="PRODUCTS";
    public static String TABLE_LISTS_PRODUCTS="LISTS_PRODUCTS";
    public static String TABLE_USER_SETTINGS="USER_SETTINGS";

    public static String CREATE_TABLE_PREFS="CREATE TABLE "+TABLE_PREFS+
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "USER TEXT,"+
            "TYPE TEXT,"+
            "VALUE TEXT);";

    public static String CREATE_TABLE_LISTS="CREATE TABLE "+TABLE_LISTS+
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "VALUE REAL," +
            "TITLE TEXT,"+
            "RATING REAL);";

    public static String CREATE_TABLE_PRODUCTS="CREATE TABLE "+TABLE_PRODUCTS+
            " (ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "NAME TEXT,"+
            "CATEGORY TEXT,"+
            "PRICE REAL);";

    public static String CREATE_TABLE_LISTS_PRODUCTS="CREATE TABLE "+TABLE_LISTS_PRODUCTS+
            "(ID_LIST INTEGER,"+
            "ID_PRODUCT INTEGER,"+
            "PRIMARY KEY (ID_LIST,ID_PRODUCT));";

    public static String CREATE_TABLE_USER_SETTINGS="CREATE TABLE "+TABLE_USER_SETTINGS+
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "MONTHLY_INCOME REAL," +
            "EXPENSE_LIMIT REAL," +
            "SALARY_DATE INTEGER," +
            "PERSONAL_SAVINGS REAL," +
            "CURRENT_MONTH REAL," +
            "EXPENSES REAL," +
            "INCOMES REAL," +
            "CURRENT_BALANCE REAL );";

    public AccessSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PREFS);
        db.execSQL(CREATE_TABLE_LISTS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_LISTS_PRODUCTS);
        db.execSQL(CREATE_TABLE_USER_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PREFS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LISTS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LISTS_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER_SETTINGS);


        db.execSQL(CREATE_TABLE_PREFS);
        db.execSQL(CREATE_TABLE_LISTS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_LISTS_PRODUCTS);
        db.execSQL(CREATE_TABLE_USER_SETTINGS);
    }
}
