package com.example.gss;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GestureDetectorCompat;

public class CalendarActivity extends AppCompatActivity {
    private ImageView heading;
    private ImageView navButton;
    private CardView cardView;
    private GestureDetectorCompat gestureDetector;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender);
        heading = findViewById(R.id.heading);
        navButton = findViewById(R.id.navButton);
        cardView = findViewById(R.id.calenderView);
        heading.setVisibility(View.VISIBLE);
        navButton.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);
        // Get the user's name (you can replace this with your actual user name retrieval logic)
        String username = getIntent().getStringExtra("user_name");
        // Handle navigation button click
        navButton.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, NavBarActivity.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
        });
    }
}
