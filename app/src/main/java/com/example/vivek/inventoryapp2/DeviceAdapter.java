package com.example.vivek.inventoryapp2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.vivek.inventoryapp2.data.DeviceContract.DeviceEntry;

/**
 * {@link DeviceAdapter} is an adapter for a listview that uses a {@link Cursor} of device as its data source.
 * This adapter knows how to create list items for each row of device in the {@link Cursor}.
 */
public class DeviceAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link DeviceAdapter}.
     *
     * @param context The context curosr from which to get the data.
     */
    public DeviceAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    /**
     * This method binds the device data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current device can be set on the name TextView
     * in the list item layout.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView deviceNameTextView = view.findViewById(R.id.display_device_name);
        TextView devicePriceTextView = view.findViewById(R.id.label_price_display);
        TextView deviceQuantityTextView = view.findViewById(R.id.counter);
        TextView supplierNameTextView = view.findViewById(R.id.display_supplier_name);

        // Find the columns of device attributes that we're interested in
        int deviceNameColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_NAME);
        int devicePriceColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_COST);
        int deviceQuantityColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_QUANTITY);
        int supplierNameColumnnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_SUPPLIER_NAME);

        // Read the device attributes from the Cursor
        String Name = cursor.getString(deviceNameColumnIndex);
        int Price = cursor.getInt(devicePriceColumnIndex);
        int Quantity = cursor.getInt(deviceQuantityColumnIndex);
        String supplierName = cursor.getString(supplierNameColumnnIndex);

        // Update the TextViews with the attributes for the current device
        deviceNameTextView.setText(Name);
        devicePriceTextView.setText(String.valueOf(Price));
        deviceQuantityTextView.setText(String.valueOf(Quantity));
        supplierNameTextView.setText(supplierName);

        // column number of "_ID"
        int deviceIdColumnIndex = cursor.getColumnIndex(DeviceEntry._ID);

        // Read the device attributes from the Cursor for the current device for "Buy Now" button
        final long deviceId = Integer.parseInt(cursor.getString(deviceIdColumnIndex));
        final int currentDeviceQuantity = cursor.getInt(deviceQuantityColumnIndex);


        // Each list view item will have a "Buy Now" button
        // This "Buy Now" button has OnClickListener which will decrease the product quantity by one at a time.
        Button saleButton = view.findViewById(R.id.buy_now);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri currentUri = ContentUris.withAppendedId(DeviceEntry.CONTENT_URI, deviceId);

                String updatedQuantity = String.valueOf(currentDeviceQuantity - 1);

                if (Integer.parseInt(updatedQuantity) >= 0) {
                    ContentValues values = new ContentValues();
                    values.put(DeviceEntry.COLUMN_DEVICE_QUANTITY, updatedQuantity);
                    context.getContentResolver().update(currentUri, values, null, null);
                }
            }
        });
    }
}
