package com.example.gss;

import static com.example.gss.AppDatabase.TABLE_BOOKINGS;
import static java.text.MessageFormat.format;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private ListView bookingList, userList, eventsList, galleryList;
    private ImageView heading;
    private LinearLayout ll1 , ll2, l1;
    private Button saveButton, addButton;
    private AlertDialog userDetailsDialog;
    // Additional fields for handling image display and addition
    private List<Uri> eventImagesList;  // List to store image URIs
    private ArrayAdapter<Uri> eventsListAdapter;  // Adapter for the events ListView
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    private static final int CROP_REQUEST = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);
        heading = findViewById(R.id.heading);
        bookingList = findViewById(R.id.bookinglist);
        userList = findViewById(R.id.userlist);
        eventsList = findViewById(R.id.evebtslist);
        galleryList = findViewById(R.id.gallerylist);
        saveButton = findViewById(R.id.saveButton);
        addButton = findViewById(R.id.addButton);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        l1 = findViewById(R.id.l1);
        // Set onClickListeners for the buttons
        findViewById(R.id.bookingdetails).setOnClickListener(v -> {
            // Show booking list, hide others
            userList.setVisibility(View.GONE);
            eventsList.setVisibility(View.GONE);
            galleryList.setVisibility(View.GONE);
            l1.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            heading.setVisibility(View.GONE);
            displayBookingList();
        });

        findViewById(R.id.userdetails).setOnClickListener(v -> {
            // Show user list, hide others
            bookingList.setVisibility(View.GONE);
            userList.setVisibility(View.VISIBLE);
            eventsList.setVisibility(View.GONE);
            galleryList.setVisibility(View.GONE);
            l1.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            heading.setVisibility(View.GONE);
            // Display the list of user usernames
            displayUserList();
        });

        findViewById(R.id.addevents).setOnClickListener(v -> {
            // Show events list, hide others
            bookingList.setVisibility(View.GONE);
            userList.setVisibility(View.GONE);
            eventsList.setVisibility(View.VISIBLE);
            galleryList.setVisibility(View.GONE);
            l1.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            heading.setVisibility(View.GONE);
            // Call getAllEventImages to display the list of saved events images
//            displayEventImages();
        });

        findViewById(R.id.gallerdetails).setOnClickListener(v -> {
            // Show gallery list, hide others
            bookingList.setVisibility(View.GONE);
            userList.setVisibility(View.GONE);
            eventsList.setVisibility(View.GONE);
            galleryList.setVisibility(View.VISIBLE);
            l1.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            heading.setVisibility(View.GONE);
        });

        // Set an OnClickListener for the save button
        saveButton.setOnClickListener(v -> {
            // Perform save action here
            // Show gallery list, hide others
            bookingList.setVisibility(View.GONE);
            userList.setVisibility(View.GONE);
            eventsList.setVisibility(View.GONE);
            galleryList.setVisibility(View.GONE);
            l1.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            ll1.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.VISIBLE);
            heading.setVisibility(View.VISIBLE);
        });
        // Set an OnClickListener for the user list items
        userList.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected username from the clicked item
            Cursor cursor = (Cursor) userList.getItemAtPosition(position);
            if (cursor != null) {
                // Make sure to use the correct column name
                @SuppressLint("Range") String selectedUsername = cursor.getString(cursor.getColumnIndex("username"));
                if (selectedUsername != null) {
                    // Query the database to get the user details based on the selected username
                    displayUserDetailsPopup(selectedUsername);
                } else {
                    // Handle the case where "username" column is missing or empty
                    Toast.makeText(AdminDashboardActivity.this, "Invalid username", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case where the Cursor is null or doesn't contain data
                Toast.makeText(AdminDashboardActivity.this, "Cursor is null or empty", Toast.LENGTH_SHORT).show();
            }
        });
        // Set a long click listener to delete a user
        userList.setOnItemLongClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) userList.getItemAtPosition(position);
            if (cursor != null) {
                @SuppressLint("Range") String selectedUsername = cursor.getString(cursor.getColumnIndex("username"));
                // Show a confirmation dialog for user deletion
                showDeleteUserConfirmation(selectedUsername);
            }
            return true;
        });
        // Set an OnClickListener for the booking list items
        bookingList.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected username from the clicked item
            Cursor cursor = (Cursor) bookingList.getItemAtPosition(position);
            if (cursor != null) {
                @SuppressLint("Range") String selectedUsername = cursor.getString(cursor.getColumnIndex("username"));
                if (cursor != null) {
                    // Query the database to get the booking details based on the selected username
                    displayBookingDetailsPopup(selectedUsername);
                } else {
                    // Handle the case where "username" column is missing or empty
                    Toast.makeText(AdminDashboardActivity.this, "No Booking Details username", Toast.LENGTH_SHORT).show();
                }
            }else {
                // Handle the case where the Cursor is null or doesn't contain data
                Toast.makeText(AdminDashboardActivity.this, "Cursor is null or empty", Toast.LENGTH_SHORT).show();
            }
        });
        // Initialize the list of event images
        eventImagesList = new ArrayList<>();
        // Initialize the ArrayAdapter for the events ListView
        eventsListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventImagesList);
        // Set the adapter for the events ListView
        eventsList.setAdapter(eventsListAdapter);
        addButton.setOnClickListener(v -> {
            // Handle the logic for adding a new image to the events list
            openImagePickerDialog();
        });
    }
    private void displayEventImages() {
        // Retrieve the list of event images from the database
        AppDatabase appDatabase = new AppDatabase(this);
        List<Uri> eventImagesList;
        eventImagesList = (List<Uri>) appDatabase.getAllEventImages();

        // Update the eventsListAdapter with the new data
        eventsListAdapter.clear();
        eventsListAdapter.addAll(eventImagesList);
        eventsListAdapter.notifyDataSetChanged();
    }
    private void openImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        String[] options = {"Gallery", "Camera"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Open gallery
                openGallery();
            } else if (which == 1) {
                // Open camera (you need to implement camera functionality)
                openCamera();
            }
        });
        builder.show();
    }
    private void openCamera() {
        openCameraOrGallery(CAMERA_REQUEST);
    }
    // Function to open the gallery for image selection
    private void openCameraOrGallery(int requestCode) {
        Intent intent = new Intent();
        if (requestCode == CAMERA_REQUEST) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        } else if (requestCode == GALLERY_REQUEST) {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
        }
        startActivityForResult(intent, requestCode);
    }
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    // Override onActivityResult to handle the result from the gallery Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST && data != null) {
                // Handle the selected image from the gallery
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Add the selected image URI to the events list
                    eventImagesList.add(selectedImageUri);
                    // Insert the event data into the events table
                    AppDatabase appDatabase = new AppDatabase(this);
                    appDatabase.insertEventData(String.valueOf(selectedImageUri));
                    // Notify the adapter that the data set has changed
                    eventsListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "Failed to get image from gallery", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAMERA_REQUEST && data != null) {
                // Handle the captured image from the camera
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        // Convert the Bitmap to Uri
                        Uri imageUri = getImageUri(imageBitmap);
                        // Add the captured image URI to the events list
                        eventImagesList.add(imageUri);
                        // Insert the event data into the events table
                        AppDatabase appDatabase = new AppDatabase(this);
                        appDatabase.insertEventData(String.valueOf(imageUri));
                        // Notify the adapter that the data set has changed
                        eventsListAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to capture image from camera", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    private void showDeleteUserConfirmation(String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the user: " + username + "?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Delete the user
                    deleteUser(username);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    // Dismiss the dialog, no action needed
                });
        builder.create().show();
    }
    private void deleteUser(String username) {
        // Perform the deletion of the user's data from the database
        AppDatabase appDatabase = new AppDatabase(this);
        SQLiteDatabase db = appDatabase.getWritableDatabase();
        String whereClause = "username = ?";
        String[] whereArgs = {username};
        int deletedRows = db.delete("users", whereClause, whereArgs);
        if (deletedRows > 0) {
            // User deleted successfully
            Toast.makeText(this, "User deleted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // User deletion failed
            Toast.makeText(this, "User deletion failed.", Toast.LENGTH_SHORT).show();
        }
    }
    private void displayUserList() {
        AppDatabase appDatabase = new AppDatabase(this);
        SQLiteDatabase db = appDatabase.getReadableDatabase();
        // Query to get all usernames from the users table
        String[] columns = { "id AS _id", "username" }; // Alias "id" as "_id"
        Cursor cursor = db.query("users", columns, null, null, null, null, null);
        // Check if there are users
        if (cursor != null && cursor.getCount() > 0) {
            // Create an adapter to display the usernames
            String[] fromColumns = { "username" };
            int[] toViews = { android.R.id.text1 }; // You may need to adjust this depending on your list item layout
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, fromColumns, toViews, 0);
            // Set the adapter for the ListView
            userList.setAdapter(adapter);
            // Show the user list
            userList.setVisibility(View.VISIBLE);
        } else {
            // No users exist, show a message
            userList.setVisibility(View.GONE); // Hide the ListView
            l1.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            // Display a message when there are no User
            TextView noUserTextView = new TextView(this);
            noUserTextView.setText("No users exist! :)");
            noUserTextView.setGravity(Gravity.CENTER);
            ((ViewGroup) userList.getParent()).addView(noUserTextView);
        }
    }
    private void displayBookingList() {
        AppDatabase appDatabase = new AppDatabase(this);
        SQLiteDatabase db = appDatabase.getReadableDatabase();
        // Query to get all columns from the bookings table
        String[] columns = {"id AS _id", "username"}; // Include "username" in the columns
        Cursor cursor = db.query(TABLE_BOOKINGS, columns, null, null, null, null, null);
        // Check if there are bookings
        if (cursor != null && cursor.getCount() > 0) {
            // Create an adapter to display the booking details
            String[] fromColumns = {"username"}; // Include "username" in fromColumns
            int[] toViews = {android.R.id.text1};
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, fromColumns, toViews, 0);
            // Set the adapter for the ListView
            bookingList.setAdapter(adapter);
            bookingList.setVisibility(View.VISIBLE);
        } else {
            bookingList.setVisibility(View.GONE);
            l1.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            addButton.setVisibility(View.GONE);
            // Display a message when there are no bookings
            TextView noBookingsTextView = new TextView(this);
            noBookingsTextView.setText("No bookings available.");
            noBookingsTextView.setGravity(Gravity.CENTER);
            // Add the TextView to the bookingList
            ((ViewGroup) bookingList.getParent()).addView(noBookingsTextView);
        }
    }
    private void displayUserDetailsPopup(String username) {
        // Create an AlertDialog to display the user details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View userDetailsView = getLayoutInflater().inflate(R.layout.user_details_popup, null);
        builder.setView(userDetailsView);
        TextView userDetailsUsername = userDetailsView.findViewById(R.id.userDetailsUsername);
        TextView userDetailsEmail = userDetailsView.findViewById(R.id.userDetailsEmail);
        TextView userDetailsPhone = userDetailsView.findViewById(R.id.userDetailsPhone);
        // Query the database to get the user details based on the selected username
        AppDatabase appDatabase = new AppDatabase(this);
        SQLiteDatabase db = appDatabase.getReadableDatabase();
        String[] columns = {"username", "email", "country_code","phone"};
        String selection = "username = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String userEmail = cursor.getString(cursor.getColumnIndex("email"));
            @SuppressLint("Range") String country_code = cursor.getString(cursor.getColumnIndex("country_code"));
            @SuppressLint("Range") String userPhone = cursor.getString(cursor.getColumnIndex("phone"));
            Toast.makeText(AdminDashboardActivity.this, "User found!", Toast.LENGTH_SHORT).show();
            userDetailsUsername.setText(format("Username: {0}", username));
            userDetailsEmail.setText(MessageFormat.format("Email: {0}", userEmail));
            userDetailsPhone.setText(MessageFormat.format("Phone: {0} {1}", country_code, userPhone));
            // Create and show the details dialog
            userDetailsDialog = builder.create();
            userDetailsDialog.show();
        } else {
            Toast.makeText(AdminDashboardActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
        }
    }
    private void displayBookingDetailsPopup(String username) {
        // Create an AlertDialog to display the booking details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View bookingDetailsView = getLayoutInflater().inflate(R.layout.booking_details_popup, null);
        builder.setView(bookingDetailsView);
        TextView bookingDetailsUsername = bookingDetailsView.findViewById(R.id.bookingDetailsUsername);
        TextView bookingDetailsDate = bookingDetailsView.findViewById(R.id.bookingDetailsDate);
        Button arriveButton = bookingDetailsView.findViewById(R.id.arivebtn);
        // Query the database to get the booking details based on the selected username
        AppDatabase appDatabase = new AppDatabase(this);
        SQLiteDatabase db = appDatabase.getReadableDatabase();
        String[] columns = {"id", "booking_date"}; // Include the primary key (_id) in the columns
        String selection = "username = ?";
        String[] selectionArgs = {username};
        // Use a separate Cursor for the booking details
        try (Cursor bookingCursor = db.query(TABLE_BOOKINGS, columns, selection, selectionArgs, null, null, null, null)) {
            if (bookingCursor != null && bookingCursor.moveToFirst()) {
                @SuppressLint("Range") int bookingId = bookingCursor.getInt(bookingCursor.getColumnIndex("id")); // Get the booking ID
                @SuppressLint("Range") String bookingDate = bookingCursor.getString(bookingCursor.getColumnIndex("booking_date"));
                Toast.makeText(AdminDashboardActivity.this, "Booking found!", Toast.LENGTH_SHORT).show();
                bookingDetailsUsername.setText(MessageFormat.format("Username: {0}", username)); // Display the username
                bookingDetailsDate.setText(MessageFormat.format("Booking Date: {0}", bookingDate));
                // Set OnClickListener for the Arrive button
                arriveButton.setOnClickListener(v -> {
                    // Perform the deletion of the booking record
                    deleteBookingRecord(bookingId);
                    // Dismiss the dialog after deletion
                    userDetailsDialog.dismiss();
                });
                // Create and show the booking details dialog
                userDetailsDialog = builder.create();
                userDetailsDialog.show();
            } else {
                Toast.makeText(AdminDashboardActivity.this, "Booking not found!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle any exceptions that might occur while querying the database
            e.printStackTrace();
            Toast.makeText(AdminDashboardActivity.this, "Error retrieving booking details.", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteBookingRecord(int bookingId) {
        // Perform the deletion of the booking record from the database
        AppDatabase appDatabase = new AppDatabase(this);
        SQLiteDatabase db = appDatabase.getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(bookingId)};
        int deletedRows = db.delete(TABLE_BOOKINGS, whereClause, whereArgs);
        if (deletedRows > 0) {
            // Booking record deleted successfully
            Toast.makeText(this, "Booking record deleted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Booking record deletion failed
            Toast.makeText(this, "Booking record deletion failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
