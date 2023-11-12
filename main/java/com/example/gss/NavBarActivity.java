package com.example.gss;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GestureDetectorCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NavBarActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    private static final int CROP_REQUEST = 3;
    private ImageView circularUserProfile;
    private GestureDetectorCompat gestureDetector;
    private Uri croppedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navbar);

        String username = getIntent().getStringExtra("user_name");

        // Set the username to the username TextView
        TextView usernameTextView = findViewById(R.id.username);
        usernameTextView.setText(username);

        // Initialize gesture detector for swiping
        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                // Detect swipe to the right
                if (e1.getX() < e2.getX()) {
                    // Swipe to the right, finish the current activity (or start a new activity if needed)
                    finish();
                    return true;
                }
                return false;
            }
        });

        circularUserProfile = findViewById(R.id.circularUserProfile);

        // Handle circular user profile image click
        circularUserProfile.setOnClickListener(v -> checkCameraPermission());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        } else {
            openImageSourceDialog();
        }
    }

    private void openImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Take from Camera", "Select from Gallery"}, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void openCamera() {
        openCameraOrGallery(CAMERA_REQUEST);
    }

    private void openGallery() {
        openCameraOrGallery(GALLERY_REQUEST);
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                cropAndSetImage(photo);
            } else if (requestCode == GALLERY_REQUEST) {
                Uri selectedImageUri = data.getData();
                startImageCrop(selectedImageUri); // Start image cropping for gallery images
            } else if (requestCode == CROP_REQUEST) {
                if (data != null) {
                    setImageToCircularUserProfile(croppedImageUri); // Use the croppedImageUri
                }
            }
        }
    }

    private void setImageToCircularUserProfile(Uri imageUri) {
        // Load and set the cropped image to the circularUserProfile ImageView
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            circularUserProfile.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cropAndSetImage(Bitmap bitmap) {
        if (bitmap != null) {
            Uri imageUri = getImageUriFromBitmap(bitmap);
            startImageCrop(imageUri);
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        File imagePath = new File(getCacheDir(), "images");
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }
        File imageFile = new File(imagePath, "temp_image.jpg");
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", imageFile);
    }

    private void startImageCrop(Uri imageUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(imageUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 128);
        cropIntent.putExtra("outputY", 128);
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        croppedImageUri = getOutputMediaFileUri(); // Store the cropped image Uri
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
        startActivityForResult(cropIntent, CROP_REQUEST);
    }

    private Uri getOutputMediaFileUri() {
        File mediaFile = new File(getFilesDir(), "cropped_image.jpg");
        return Uri.fromFile(mediaFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageSourceDialog();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("You denied the camera permission. This permission is required to take pictures. Would you like to enable it in the app settings?");
        builder.setPositiveButton("Open Settings", (dialog, which) -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
