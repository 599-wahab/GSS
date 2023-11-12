package com.example.gss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class AppDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GSS.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String TABLE_USERS = "users";
    private static final String TABLE_ADMIN = "admin";
    public static final String TABLE_EVENTS = "events";
    // Define your table creation SQL statements here
    private static final String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT,  user_id INTEGER, booking_date TEXT);";
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, email TEXT, phone TEXT, country_code TEXT, password TEXT);";
    private static final String CREATE_ADMIN_TABLE = "CREATE TABLE " + TABLE_ADMIN +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, image_path TEXT);";
    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT, image_path TEXT);";
    private final Context context;
    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the tables
        db.execSQL(CREATE_BOOKINGS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_ADMIN_TABLE);
        db.execSQL(CREATE_EVENTS_TABLE);
        // Insert the default admin user
        insertDefaultAdminUser(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }
    // Method to insert the default admin user
    private void insertDefaultAdminUser(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("username", "admin");
        values.put("password", "admin");
        // You can also add other details of the admin user here

        long result = db.insert(TABLE_ADMIN, null, values);

        if (result != -1) {
            // Data saved successfully
            showToast("Default admin user data saved successfully!");
        } else {
            // Data save failed
            showToast("Default admin user data save failed.");
        }
    }
    public Cursor queryAdminCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "username" };
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = { username, password };
        return db.query(TABLE_ADMIN, columns, selection, selectionArgs, null, null, null);
    }
    public Cursor queryUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"username"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};
        return db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
    }
    public long insertUserData(String email, String phone, String username, String password, String countryCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("phone", phone);
        values.put("username", username);
        values.put("password", password);
        values.put("country_code", countryCode);
        long result = db.insert(TABLE_USERS, null, values);

        if (result != -1) {
            // Data saved successfully
            showToast("User data saved successfully!");
        } else {
            // Data save failed
            showToast("User data save failed.");
        }
        return result;
    }
    public long insertBookingData(String username, int userId, String bookingDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the booking time already exists in the bookings table
        if (isBookingTimeAvailable(db, bookingDate)) {
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("user_id", userId);
            values.put("booking_date", bookingDate);
            long result = db.insert(TABLE_BOOKINGS, null, values);

            if (result != -1) {
                // Data saved successfully
                showToast("Booking data saved successfully!");
            } else {
                // Data save failed
                showToast("Booking data save failed.");
            }
            return result;
        } else {
            // Booking time already exists
            showToast("Sorry, the selected booking time is not available.");
            return -1;
        }
    }
    private boolean isBookingTimeAvailable(SQLiteDatabase db, String bookingDate) {
        String[] columns = {"id"};
        String selection = "booking_date = ?";
        String[] selectionArgs = {bookingDate};
        Cursor cursor = db.query(TABLE_BOOKINGS, columns, selection, selectionArgs, null, null, null);

        boolean isAvailable = cursor.getCount() == 0; // If count is 0, the time is available; otherwise, it's already booked
//        cursor.close();
        return isAvailable;
    }
    private void showToast(String message) {
        // Display a toast message to confirm data save status
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public Cursor checkUsernameAvailability(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"username"};
        String selection = "username = ?";
        String[] selectionArgs = {username};
        return db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
    }
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"id"};  // Assuming 'id' is the primary key in the 'users' table
        String selection = "username = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int userId = -1;  // Default to -1 if not found
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("id");
                if (idIndex != -1) {
                    userId = cursor.getInt(idIndex);
                } else {
                    // Handle the case where 'id' column is not found in the cursor
                    showToast("'id' column is not found in the cursor");
                }
            } else {
                // Handle the case where there are no rows in the cursor
                showToast("No user found for username: " + username);
            }
//            cursor.close();
        } else {
            // Handle the case where the cursor is null
            showToast("Cursor is null");
        }
        return userId;
    }
    public long insertEventData(String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("image_path", imagePath);
        long result = db.insert("events", null, values);
        if (result != -1) {
            // Data saved successfully
            showToast("Event image data saved successfully!");
        } else {
            // Data save failed
            showToast("Event image data save failed.");
        }
        return result;
    }
    public Cursor getAllEventImages() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"id", "image_path"};
        return db.query("events", columns, null, null, null, null, null);
    }
    public void deleteEventImage(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(eventId)};
        db.delete("events", whereClause, whereArgs);
        showToast("Event image deleted successfully!");
    }
}
