package com.example.vivek.inventoryapp2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vivek.inventoryapp2.data.DeviceContract.DeviceEntry;

public class DeviceDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "devices_inventory.db";

    public DeviceDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_DEVICES_TABLE = "CREATE TABLE " + DeviceContract.DeviceEntry.TABLE_NAME + " ("
                + DeviceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DeviceEntry.COLUMN_DEVICE_NAME + " TEXT NOT NULL, "
                + DeviceEntry.COLUMN_DEVICE_COST + " INTEGER NOT NULL, "
                + DeviceEntry.COLUMN_DEVICE_QUANTITY + " INTEGER NOT NULL, "
                + DeviceEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + DeviceEntry.COLUMN_SUPPLIER_PHONE_NO + " TEXT NOT NULL );";
        sqLiteDatabase.execSQL(SQL_CREATE_DEVICES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
