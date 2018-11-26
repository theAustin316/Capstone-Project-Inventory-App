package com.example.vivek.inventoryapp2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.vivek.inventoryapp2.data.DeviceContract.DeviceEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the device data loader */
    private static final int DEVICE_LOADER = 0;

    RelativeLayout mEmptyView;

    /** Adapter for the ListView */
    DeviceAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = findViewById(R.id.fab_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView deviceListView = findViewById(R.id.list_view);

        mEmptyView = findViewById(R.id.empty_state_view);
        deviceListView.setEmptyView(mEmptyView);


        // Setup an Adapter to create a list item for each row of device in the Cursor.
        // There is no device stored yet (until the loader finishes) so pass in null for the Cursor.
        mAdapter = new DeviceAdapter(this, null);
        deviceListView.setAdapter(mAdapter);

        // Setup the item click listener
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                // Form the content URI that represents the specific device that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link DeviceEntry#CONTENT_URI}.
                Uri currentDeviceUri = ContentUris.withAppendedId(DeviceEntry.CONTENT_URI, id);
                intent.setData(currentDeviceUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(DEVICE_LOADER, null, this);
    }

    private void deleteAllDevices() {
        // Defines a variable to contain the number of rows deleted
        int rowsDeleted = 0;

        // Deletes the rows that match the selection criteria
        rowsDeleted = getContentResolver().delete(
                DeviceEntry.CONTENT_URI,   // the user dictionary content URI
                null,               // the column to select on
                null           // the value to compare to
        );
        if (rowsDeleted == 0) {
            // If the value of rowsDeleted is 0, then there was problem with deleting rows
            Toast.makeText(this, R.string.error_delete_devices,
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the deletion was successful
            Toast.makeText(this, R.string.device_delet__success,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {

        if (!(mEmptyView.getVisibility() == View.VISIBLE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirm_delete);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked Yes, so delete the device.
                    deleteAllDevices();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked No, so dismiss the dialog and continue editing the device.
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
            default:
                return false;
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DeviceEntry._ID,
                DeviceEntry.COLUMN_DEVICE_NAME,
                DeviceEntry.COLUMN_DEVICE_COST,
                DeviceEntry.COLUMN_DEVICE_QUANTITY,
                DeviceEntry.COLUMN_SUPPLIER_NAME,
                DeviceEntry.COLUMN_SUPPLIER_PHONE_NO,
        };

        return new CursorLoader(this,
                DeviceEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
