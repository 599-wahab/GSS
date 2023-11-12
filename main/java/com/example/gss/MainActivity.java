package com.example.gss;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private AppDatabase appDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        appDatabase = new AppDatabase(this);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.saveButton);
        TextView signupText = findViewById(R.id.signupText);
        // Find the passwordVisibilityToggle ImageView by its ID
        final ImageView passwordVisibilityToggle = findViewById(R.id.passwordVisibilityToggle);
        final Drawable visibilityOnDrawable = getResources().getDrawable(R.drawable.ic_baseline_visibility_24);
        final Drawable visibilityOffDrawable = getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24);
        passwordVisibilityToggle.setOnClickListener(v -> {
            // Toggle the password visibility
            if (passwordEditText.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                // Password is currently hidden, show it
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                // Replace the icon with ic_baseline_visibility_on
                passwordVisibilityToggle.setImageDrawable(visibilityOnDrawable);
                int position = passwordEditText.length();
                passwordEditText.setSelection(position);
            } else {
                // Password is currently visible, hide it
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                // Replace the icon with ic_baseline_visibility_24
                passwordVisibilityToggle.setImageDrawable(visibilityOffDrawable);
                int position = passwordEditText.length();
                passwordEditText.setSelection(position);
            }
        });
        // Set an OnClickListener for the loginButton
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            // Check if username or password is empty
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else if (isValidAdminUser(username, password)) {
                // Admin login
                openAdminDashboard();
                Toast.makeText(MainActivity.this, "Admin Logged In Successfully :)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            }else if (validateUser(username, password)) {
                // User login
                Toast.makeText(MainActivity.this, "User Logged In Successfully :)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
                intent.putExtra("user_name", username);
                startActivity(intent);
            } else if (isValidUser(username, password)) {
                // Regular user login
                openUserDashboard(username);
                Toast.makeText(MainActivity.this, "User Logged In Successfully :)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
                intent.putExtra("user_name", username);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        });
        // Set an OnClickListener for the signupText TextView
        signupText.setOnClickListener(v -> {
            // Open the registration activity when the signup text is clicked
            String username = usernameEditText.getText().toString().trim();
            Intent intent = new Intent(MainActivity.this, UserRegistrationActivity.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
        });
    }
    // Validate the username and password against the database
    private boolean validateUser(String username, String password) {
        SQLiteDatabase db = appDatabase.getReadableDatabase();
        String[] columns = { "username" };
        String selection = "username = ?" + " AND " + "password = ?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        boolean isValid = cursor.moveToFirst();
//        cursor.close();
//        db.close();
        return isValid;
    }
    // Define the methods to check if a user is an admin or a regular user
    private boolean isValidAdminUser(String username, String password) {
        Cursor cursor = appDatabase.queryAdminCredentials(username, password);
        boolean isValid = cursor.moveToFirst();
//        cursor.close();
        return isValid;
    }
    private boolean isValidUser(String username, String password) {
        Cursor cursor = appDatabase.queryUserCredentials(username, password);
        boolean isValid = cursor.moveToFirst();
//        cursor.close();
        return isValid;
    }
    private void openAdminDashboard() {
        Toast.makeText(MainActivity.this, "Admin Logged In Successfully :)", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
    }

    private void openUserDashboard(String username) {
        Toast.makeText(MainActivity.this, "User Logged In Successfully :)", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, UserDashboardActivity.class);
        intent.putExtra("user_name", username);
        startActivity(intent);
    }
}