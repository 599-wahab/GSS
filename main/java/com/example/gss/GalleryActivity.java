package com.example.gss;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class GalleryActivity extends AppCompatActivity {
    private ImageView[] galleryImages;
    private ImageView navButton;
    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        navButton = findViewById(R.id.navButton);
        navButton.setVisibility(View.VISIBLE);
        LinearLayout gallery1Layout = findViewById(R.id.gallery1Layout);
        LinearLayout gallery2Layout = findViewById(R.id.gallery2Layout);
        LinearLayout gallery3Layout = findViewById(R.id.gallery3Layout);
        LinearLayout gallery4Layout = findViewById(R.id.gallery4Layout);

        galleryImages = new ImageView[]{
                (ImageView) gallery1Layout.getChildAt(0),
                (ImageView) gallery2Layout.getChildAt(0),
                (ImageView) gallery3Layout.getChildAt(0),
                (ImageView) gallery4Layout.getChildAt(0)
        };

        for (ImageView image : galleryImages) {
            image.setVisibility(View.VISIBLE);
            image.setOnClickListener(v -> showFullResolutionImage(image));
        }
        // Get the user's name (you can replace this with your actual user name retrieval logic)
        String username = getIntent().getStringExtra("user_name");
        // Handle navigation button click
        navButton.setOnClickListener(v -> {
            Intent intent = new Intent(GalleryActivity.this, NavBarActivity.class);
            intent.putExtra("user_name", username);
            startActivity(intent);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showFullResolutionImage(final ImageView galleryImage) {
        // Create a Dialog
        final Dialog dialog = new Dialog(this);

        // Create an ImageView and set its content to the gallery image
        final ImageView dialogImageView = new ImageView(this);
        dialogImageView.setImageDrawable(galleryImage.getDrawable());

        // Add the ImageView to the dialog
        dialog.setContentView(dialogImageView);

        // Set touch event listeners to detect swipe gestures
        dialogImageView.setOnTouchListener(new View.OnTouchListener() {
            private float x1;
            private static final float MIN_SWIPE_DISTANCE = 150;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        float x2 = event.getX();
                        float deltaX = x2 - x1;

                        // Detect swipe to the right
                        if (Math.abs(deltaX) > MIN_SWIPE_DISTANCE) {
                            if (deltaX < 0) {
                                // Swipe to the left
                                currentImageIndex = (currentImageIndex + 1) % galleryImages.length;
                            } else {
                                // Swipe to the right
                                currentImageIndex = (currentImageIndex - 1 + galleryImages.length) % galleryImages.length;
                            }

                            // Update the dialog image
                            dialogImageView.setImageDrawable(galleryImages[currentImageIndex].getDrawable());
                        }
                        break;
                }
                return true;
            }
        });

        // Set a click listener to close the dialog when the full-resolution image is clicked
        dialogImageView.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
