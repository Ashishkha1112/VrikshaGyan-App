package np.edu.nast.vrikshagyan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import np.edu.nast.vrikshagyan.util.SharedPreferencesUtil;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_GALLERY = 1;
    private static final int PICK_IMAGE_CAMERA = 2;

    private ImageView profileImagePopup;
    private RequestQueue requestQueue;
    private Uri imageUri;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        requestQueue = Volley.newRequestQueue(this);
        TextView profileNamePopup = findViewById(R.id.profileName);
        profileImagePopup = findViewById(R.id.profileImage);
        Button btnLogout = findViewById(R.id.btnLogOut);
        ImageButton imageButton = findViewById(R.id.btn_choose_image);
        Button update= findViewById(R.id.btnUpdateProfile);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HeaderFragment())
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container1, new FragmentDown())
                    .commit();
        }
        String jwtToken = SharedPreferencesUtil.getToken(this);

        // Check if token is available
        if (jwtToken != null) {
            // Token is available, proceed with using the token
            // Toast.makeText(this, "JWT Token: " + jwtToken, Toast.LENGTH_SHORT).show();
            // Example: Make API calls using the token, etc.
        } else {
            // Token is not available or expired, handle accordingly
            // Example: Navigate to LoginActivity or show a message
            Toast.makeText(this, "Token not available, redirecting to login", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        imageButton.setOnClickListener(this::showPopupMenu);
        fetchUserProfile(profileNamePopup, profileImagePopup);
    }

    private void fetchUserProfile(final TextView profileNamePopup, final ImageView profileImagePopup) {
        String url = "http://192.168.18.13:8080/api/profile";
        String jwtToken = SharedPreferencesUtil.getToken(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject userData = data.getJSONObject("user");

                            // Set user profile name
                            String profileName = userData.getString("firstName") + " "
                                    + userData.getString("middleName") + " "
                                    + userData.getString("lastName");
                            profileNamePopup.setText(profileName);

                            // Set profile image (if available)
//                            if (data.has("profileImage")) {
//                                String imageUrl = data.getString("profileImage");
//                                // Load image into ImageView using Glide
//                                Glide.with(ProfileActivity.this)
//                                        .load(imageUrl)
//                                        .apply(new RequestOptions().placeholder(R.drawable.ic_profile))
//                                        .into(profileImagePopup);
//                            }
                            if (data.has("profileImage")) {
                                String base64Image = data.getString("profileImage");

                                // Remove "data:image/jpeg;base64," or "data:image/png;base64," if it exists
                                if (base64Image.contains(",")) {
                                    base64Image = base64Image.split(",")[1];
                                }

                                // Decode Base64 string to Bitmap
                                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Log.e("hello","hw"+decodedByte);
                                // Set the Bitmap to the ImageView
                                profileImagePopup.setImageBitmap(decodedByte);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Error fetching profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken); // Example of adding an authorization token
                headers.put("Content-Type", "application/json"); // Example of setting content type
                return headers;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_GALLERY && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Display the selected image using Glide
                    Glide.with(this)
                            .load(selectedImageUri)
                            .apply(new RequestOptions().centerCrop())
                            .into(profileImagePopup);

                    // Convert URI to Bitmap if needed
                    bitmap = uriToBitmap(selectedImageUri);
                    // Perform further processing with the bitmap if needed
                    compressAndSaveBitmap(bitmap);
                }
            } else if (requestCode == PICK_IMAGE_CAMERA) {
                if (imageUri != null) {
                    // Display the captured image using Glide
                    Glide.with(this)
                            .load(imageUri)
                            .apply(new RequestOptions().centerCrop())
                            .into(profileImagePopup);

                    // Convert URI to Bitmap if needed
                    bitmap = uriToBitmap(imageUri);
                    // Perform further processing with the bitmap if needed
                    compressAndSaveBitmap(bitmap);
                }
            }
        }
    }
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.image_picker_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.gallery) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_GALLERY);
                } else {
                    openGallery();
                }
                return true;
            } else if (item.getItemId() == R.id.camera) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PICK_IMAGE_CAMERA);
                } else {
                    openCamera();
                }
                return true;
            } else {
                return false;
            }
        });

        popup.show();
    }



    public void logout() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        SharedPreferencesUtil.clearToken(this);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_GALLERY);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        if (photoFile != null) {
            imageUri = FileProvider.getUriForFile(this, "np.edu.nast.vrikshagyan.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PICK_IMAGE_CAMERA);
        }
    }
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap uriToBitmap(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void compressAndSaveBitmap(Bitmap bitmap) {
        try {
            File compressedFile = createImageFile();
            if (compressedFile != null) {
                FileOutputStream fos = new FileOutputStream(compressedFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos); // Compress to 80% quality
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateProfile() {
        if (bitmap != null) {
            String base64Image = encodeBitmapToBase64(bitmap);
           // Toast.makeText(ProfileActivity.this, base64Image, Toast.LENGTH_LONG).show();

            // Prepare the request parameters
            String url = "http://192.168.18.13:8080/api/uploadProfilePictures"; // Ensure this matches your backend endpoint
            String jwtToken = SharedPreferencesUtil.getToken(this);

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                    response -> {
                        // Handle success
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    },
                    error -> {
                        // Handle error
                        Toast.makeText(ProfileActivity.this, "Error updating profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("base64Image", base64Image);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + jwtToken);
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }
            };

            // Add the request to the RequestQueue
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(ProfileActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


}
