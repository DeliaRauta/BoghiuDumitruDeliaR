package com.example.ichim.proiect;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ichim on 29-Nov-15.
 */
public class UserSettings implements Parcelable{

    private double monthlyIncome;
    private double expenseLimit;
    private int salaryDate;
    private double personalSavings;
    private int currentMonth;
    private double expenses=0;
    private double incomes=0;
    private double currentBalance=0;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(monthlyIncome);
        dest.writeDouble(expenseLimit);
        dest.writeInt(salaryDate);
        dest.writeDouble(personalSavings);
        dest.writeInt(currentMonth);
        dest.writeDouble(expenses);
        dest.writeDouble(incomes);
        dest.writeDouble(currentBalance);
    }

    protected UserSettings(Parcel in) {
        monthlyIncome = in.readDouble();
        expenseLimit = in.readDouble();
        salaryDate = in.readInt();
        personalSavings = in.readDouble();
        currentMonth = in.readInt();
        expenses = in.readDouble();
        incomes = in.readDouble();
        currentBalance = in.readDouble();
    }

    public static final Creator<UserSettings> CREATOR = new Creator<UserSettings>() {
        @Override
        public UserSettings createFromParcel(Parcel in) {
            return new UserSettings(in);
        }

        @Override
        public UserSettings[] newArray(int size) {
            return new UserSettings[size];
        }
    };

    public int getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(int currentMonth) {
        this.currentMonth = currentMonth;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getIncomes() {
        return incomes;
    }

    public void setIncomes(double incomes) {
        this.incomes = incomes;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public UserSettings(double personalSavings, double expenseLimit, int salaryDate, double monthlyIncome) {
        this.personalSavings = personalSavings;
        this.expenseLimit = expenseLimit;
        this.salaryDate = salaryDate;
        this.monthlyIncome = monthlyIncome;
    }

    public UserSettings() {
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public double getExpenseLimit() {
        return expenseLimit;
    }

    public void setExpenseLimit(double expenseLimit) {
        this.expenseLimit = expenseLimit;
    }

    public int getSalaryDate() {
        return salaryDate;
    }

    public void setSalaryDate(int salaryDate) {
        this.salaryDate = salaryDate;
    }

    public double getPersonalSavings() {
        return personalSavings;
    }

    public void setPersonalSavings(double personalSavings) {
        this.personalSavings = personalSavings;
    }


}
