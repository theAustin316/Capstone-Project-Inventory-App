package com.example.vivek.inventoryapp2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vivek.inventoryapp2.data.DeviceContract.DeviceEntry;
import com.example.vivek.inventoryapp2.data.DeviceDBHelper;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int MIN_VALUE = 0;

    private final int MAX_VALUE = 999;

    /**
     * Boolean flag that keeps track of whether the device has been edited (true) or not (false)
     */
    private boolean deviceChanged = false;

    /**
     * Supplier contact number will be save in supplierContact variable
     **/
    private String supplierContact;

    /**
     * Identifier for the device data loader
     */
    private static final int EXISTING_DEVICE_LOADER = 1;

    /**
     * Content URI for the existing device
     */
    private Uri currentDevUri;

    private EditText deviceName;

    private EditText devicePrice;

    private EditText deviceQuantity;

    private EditText supplierName;

    private EditText supplierConta;

    private Button subtractQuantity;

    private Button addQuantity;

    public DeviceDBHelper mdbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new device or editing an existing one.
        Intent intent = getIntent();
        currentDevUri = intent.getData();

        // If the intent DOES NOT contain a device content URI, then we know that we are creating a new device.
        if (currentDevUri == null) {
            // This is a new device, so change the app bar to say "Add a Device"
            setTitle("Add a device");
            // Invalidate the options menu, so the "Delete" and "Contact Supplier" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing devcie,change app bar to "Edit Device Information"
            setTitle("Edit Device information");
            getLoaderManager().initLoader(EXISTING_DEVICE_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        deviceName = findViewById(R.id.device_name_editText);
        devicePrice = findViewById(R.id.device_cost_editText);
        deviceQuantity = findViewById(R.id.device_quantity_editText);
        supplierName = findViewById(R.id.supplier_name__editText);
        supplierConta = findViewById(R.id.supplier_phone_editText);
        subtractQuantity = findViewById(R.id.subtract);
        addQuantity = findViewById(R.id.addition);
        subtractQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantityString = deviceQuantity.getText().toString();
                int currentQuantityInt;
                if (currentQuantityString.length() == 0) {
                    currentQuantityInt = 0;
                    deviceQuantity.setText(String.valueOf(currentQuantityInt));
                } else {
                    currentQuantityInt = Integer.parseInt(currentQuantityString) - 1;
                    if (currentQuantityInt >= MIN_VALUE) {
                        deviceQuantity.setText(String.valueOf(currentQuantityInt));
                    }
                }

            }
        });
        addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentQuantityString = deviceQuantity.getText().toString();
                int currentQuantityInt;
                if (currentQuantityString.length() == 0) {
                    currentQuantityInt = 1;
                    deviceQuantity.setText(String.valueOf(currentQuantityInt));
                } else {
                    currentQuantityInt = Integer.parseInt(currentQuantityString) + 1;
                    if (currentQuantityInt <= MAX_VALUE) {
                        deviceQuantity.setText(String.valueOf(currentQuantityInt));
                    }
                }

            }
        });

        mdbHelper = new DeviceDBHelper(this);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        deviceName.setOnTouchListener(mTouchListener);
        devicePrice.setOnTouchListener(mTouchListener);
        deviceQuantity.setOnTouchListener(mTouchListener);
        subtractQuantity.setOnTouchListener(mTouchListener);
        addQuantity.setOnTouchListener(mTouchListener);
        supplierName.setOnTouchListener(mTouchListener);
        supplierConta.setOnTouchListener(mTouchListener);
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the deviceHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            deviceChanged = true;
            return false;
        }

    };

    private void saveDevice() {
        // Use trim to eliminate leading or trailing white space
        String deviceNameString = deviceName.getText().toString().trim();
        String devicePriceString = devicePrice.getText().toString().trim();
        String deviceQuantityString = deviceQuantity.getText().toString().trim();
        String supplierNameString = supplierName.getText().toString().trim();
        String supplierContactString = supplierConta.getText().toString().trim();


        if (TextUtils.isEmpty(deviceNameString)) {
            deviceName.setError(getString(R.string.reqd_field));
            return;
        }

        if (TextUtils.isEmpty(devicePriceString)) {
            devicePrice.setError(getString(R.string.reqd_field));
            return;
        }
        if (TextUtils.isEmpty(deviceQuantityString)) {
            deviceQuantity.setError(getString(R.string.reqd_field));
            return;
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            supplierName.setError(getString(R.string.reqd_field));
            return;
        }
        if (TextUtils.isEmpty(supplierContactString)) {
            supplierConta.setError(getString(R.string.reqd_field));
            return;
        }

        int devicePriceInt = Integer.parseInt(devicePriceString);
        int deviceQuantityInt = Integer.parseInt(deviceQuantityString);

        if (devicePriceInt < 0) {
            devicePrice.setError(getString(R.string.cost_neg));
            return;
        }
        if (deviceQuantityInt < 0) {
            deviceQuantity.setError(getString(R.string.quantneg));
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and device attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(DeviceEntry.COLUMN_DEVICE_NAME, deviceNameString);
        values.put(DeviceEntry.COLUMN_DEVICE_COST, devicePriceInt);
        values.put(DeviceEntry.COLUMN_DEVICE_QUANTITY, deviceQuantityInt);
        values.put(DeviceEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(DeviceEntry.COLUMN_SUPPLIER_PHONE_NO, supplierContactString);

        // Determine if this is a new or existing device by checking if currentDeviceUri is null or not
        if (currentDevUri == null) {
            // This is a NEW device, so insert a new device into the provider
            Uri newUri = getContentResolver().insert(DeviceEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.entry_unsuccess, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful
                Toast.makeText(this, R.string.entru_success, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING device, so update the device with content URI
            int rowAffected = getContentResolver().update(currentDevUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.info_notSaved,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful
                Toast.makeText(this, R.string.changes_success,
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteDevice() {
        if (currentDevUri != null) {
            int rowsDeleted = 0;

            // Deletes the words that match the selection criteria
            rowsDeleted = getContentResolver().delete(
                    currentDevUri,   // the user dictionary content URI
                    null,     // the column to select on
                    null // the value to compare to
            );
            if (rowsDeleted == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.device_del_fail,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful
                Toast.makeText(this, R.string.dev_Del_success,
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void callSupplier() {
        Intent supplierNumberIntent = new Intent(Intent.ACTION_DIAL);
        supplierNumberIntent.setData(Uri.parse("tel:" + supplierContact));
        startActivity(supplierNumberIntent);
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new device, hide the "Delete" menu item.
        if (currentDevUri == null) {
            MenuItem menuItem;
            menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
            menuItem = menu.findItem(R.id.action_contact_supplier);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save device to database
                saveDevice();
                return true;
            // Respond to a click on the "Contact Supplier" menu option
            case R.id.action_contact_supplier:
                // Contact the supplier via intent
                callSupplier();
                break;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                //Allow user to confirm for deleting the entry
                showDeleteConfirmationDialog();
                break;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the device hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!deviceChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all device attributes, define a projection that contains
        // all columns from the devices table
        String[] projection = {
                DeviceEntry._ID,
                DeviceEntry.COLUMN_DEVICE_NAME,
                DeviceEntry.COLUMN_DEVICE_COST,
                DeviceEntry.COLUMN_DEVICE_QUANTITY,
                DeviceEntry.COLUMN_SUPPLIER_NAME,
                DeviceEntry.COLUMN_SUPPLIER_PHONE_NO,
        };

        return new CursorLoader(this,
                currentDevUri,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Find the columns of device attributes that we're interested in
            int productNameColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_COST);
            int productQuantityColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_SUPPLIER_NAME);
            int supplierContactColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_SUPPLIER_PHONE_NO);

            // Extract out the value from the Cursor for the given column index
            String Name = cursor.getString(productNameColumnIndex);
            int Price = cursor.getInt(productPriceColumnIndex);
            int Quantity = cursor.getInt(productQuantityColumnIndex);
            String s_Name = cursor.getString(supplierNameColumnIndex);
            supplierContact = cursor.getString(supplierContactColumnIndex);

            // Update the views on the screen with the values from the database
            deviceName.setText(Name);
            devicePrice.setText(String.valueOf(Price));
            deviceQuantity.setText(String.valueOf(Quantity));
            supplierName.setText(String.valueOf(s_Name));
            supplierConta.setText(String.valueOf(supplierContact));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        deviceName.setText("");
        devicePrice.setText("");
        deviceQuantity.setText("");
        supplierName.setText("");
        supplierConta.setText("");
    }

    @Override
    public void onBackPressed() {
        // If the entry hasn't changed, continue with handling back button press
        if (!deviceChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.yes, discardButtonClickListener);
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

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_individual_device);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Yes, so delete the device.
                deleteDevice();
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
