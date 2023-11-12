package com.example.gss;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class UserDashboardActivity extends AppCompatActivity {
    private ImageView heading;
    private ImageView navButton;
    private CardView cardView;
    @Override
    public void onBackPressed() {
        // Display a confirmation dialog when the back button is pressed
        new AlertDialog.Builder(this)
                .setMessage("Do you want to logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle logout action
                        logout();
                    }
                })
                .setNegativeButton("Cancel", null) // Do nothing when canceled
                .show();
    }
    private void logout() {
        // Implement your logout logic here, such as clearing the session, and navigating to the login screen
        // For example:
        Intent intent = new Intent(UserDashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);

        // Initialize UI elements
        heading = findViewById(R.id.heading);
        navButton = findViewById(R.id.navButton);
        cardView = findViewById(R.id.cardView);
        // Set the initial visibility
        heading.setVisibility(View.VISIBLE);
        navButton.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);

        // Get the user's name (you can replace this with your actual user name retrieval logic)
        String username = getIntent().getStringExtra("user_name");
        // Set the welcome message and username to the respective TextView elements
        TextView welcomeTextView = findViewById(R.id.userName);
        welcomeTextView.setText("Welcome " + username); // A default message for guests

        // Handle navigation button click
        navButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, NavBarActivity.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
        });

        // Handle button clicks for different activities
        findViewById(R.id.bookingLayout).setOnClickListener(v ->{
            Intent intent = new Intent(UserDashboardActivity.this,BookingActivity.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
        });
        findViewById(R.id.calenderLayout).setOnClickListener(v ->{
            Intent intent = new Intent(UserDashboardActivity.this,CalendarActivity.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
        });
        findViewById(R.id.evntsLayout).setOnClickListener(v ->{
            Intent intent = new Intent(UserDashboardActivity.this,EventsActivity.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
        });
        findViewById(R.id.galleryLayout).setOnClickListener(v ->{
            Intent intent = new Intent(UserDashboardActivity.this,GalleryActivity.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
        });
    }
    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

}
