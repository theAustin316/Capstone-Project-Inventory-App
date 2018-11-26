package com.example.vivek.inventoryapp2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


import com.example.vivek.inventoryapp2.data.DeviceContract.DeviceEntry;

public class DeviceProvider extends ContentProvider {

    /** URI matcher for the content URI for the devices table */
    public static final int DEVICES = 100;

    /** URI matcher for the content URI for a single device in the devices table */
    public static final int DEVICES_ID = 101;

    public static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI(DeviceContract.CONTENT_AUTHORITY, DeviceContract.PATH_DEVICES, DEVICES);
        mUriMatcher.addURI(DeviceContract.CONTENT_AUTHORITY, DeviceContract.PATH_DEVICES + "/#", DEVICES_ID);
    }

    public static final String LOG_TAG = DeviceProvider.class.getSimpleName();

    private DeviceDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new DeviceDBHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     * @return Cursor object depending on provided uri.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {


        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        Cursor cursor;

        int match = mUriMatcher.match(uri);
        switch (match) {
            case DEVICES:
                cursor = database.query(DeviceEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DEVICES_ID:
                selection = DeviceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(DeviceEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(" Can't query on an unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int matching = mUriMatcher.match(uri);
        switch (matching) {
            case DEVICES:
                return DeviceEntry.CONTENT_LIST_TYPE;
            case DEVICES_ID:
                return DeviceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + matching);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     * @return Uri appended with the id of row.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int matching = mUriMatcher.match(uri);
        switch (matching) {
            case DEVICES:
                return insertDevice(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for the following" + uri);
        }
    }

    private Uri insertDevice(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = db.insert(DeviceEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Perform the delete operation for the given URI. Use the given projection, selection, selection arguments.
     * @return rowsDeleted - number of rows deleted.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        final int match = mUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case DEVICES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(DeviceEntry.TABLE_NAME, selection, selectionArgs);
                // If 1 or more rows were deleted, then notify all listeners that the data at the
                // given URI has changed
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case DEVICES_ID:
                // Delete a single row given by the ID in the URI
                selection = DeviceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(DeviceEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for the following" + uri);

        }
    }

    /**
     * Update the existing data into the provider with the given ContentValues.
     * @return rowsUpdated - number of rows updated
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int matching = mUriMatcher.match(uri);
        switch (matching) {
            case DEVICES:
                return updateDevice(uri, contentValues, selection, selectionArgs);
            case DEVICES_ID:
                selection = DeviceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateDevice(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for the following" + uri);
        }
    }

    private int updateDevice(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDBHelper.getWritableDatabase();

        int rowsUpdated = database.update(DeviceEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
