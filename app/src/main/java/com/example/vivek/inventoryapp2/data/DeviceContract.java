package com.example.vivek.inventoryapp2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DeviceContract {

    public static final String CONTENT_AUTHORITY = "com.example.vivek.inventoryapp2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_DEVICES = "devices";

    // non instantiable
    private DeviceContract() {

    }

    public static abstract class DeviceEntry implements BaseColumns {

        // non instantiable
        private DeviceEntry() {

        }

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEVICES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEVICES;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DEVICES);

        public static final String TABLE_NAME = "devices";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_DEVICE_NAME = "Device_Name";
        public static final String COLUMN_DEVICE_COST = "Cost";
        public static final String COLUMN_DEVICE_QUANTITY = "Quantity";
        public static final String COLUMN_SUPPLIER_NAME = "Supplier_Name";
        public static final String COLUMN_SUPPLIER_PHONE_NO = "Supplier_Phone_No";
    }
}
