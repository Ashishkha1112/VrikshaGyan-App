package np.edu.nast.vrikshagyan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import np.edu.nast.vrikshagyan.util.SharedPreferencesUtil;

public class AddPlantActivity extends AppCompatActivity {

    private static final int PICK_IMAGES = 6;
    private TextInputEditText englishName, nepaliName, tharuName, localName, scientificName, normalUses, medicalUses, height, partUsed;
    private TextInputEditText preparationType, traditionalUses, description;
    private Spinner categorySpinner;
    private Button btnSelectImages, btnSave;
    private LinearLayout imagesContainer;
    private RequestQueue requestQueue;
    private String UPLOAD_URL = "http://192.168.18.13:8080/api/single";
    private List<Bitmap> bitmaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HeaderFragment())
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container1, new FragmentDown())
                    .commit();
        }
        String jwtToken = SharedPreferencesUtil.getToken(this);
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

        requestQueue = Volley.newRequestQueue(this);

        // Initialize views
        initializeViews();
        setupSpinner();

        btnSelectImages.setOnClickListener(v -> selectImages());
        btnSave.setOnClickListener(v -> savePlant());
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.getClipData() != null) {
                                // Multiple images selected
                                int count = data.getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                    addBitmapFromUri(imageUri);
                                }
                            } else if (data.getData() != null) {
                                // Single image selected
                                Uri imageUri = data.getData();
                                addBitmapFromUri(imageUri);
                            }
                        }
                    }
                }
            });

    private void initializeViews() {
        englishName = findViewById(R.id.englishName);
        nepaliName = findViewById(R.id.nepaliName);
        tharuName = findViewById(R.id.tharuName);
        localName = findViewById(R.id.localName);
        scientificName = findViewById(R.id.scientificName);
        categorySpinner = findViewById(R.id.categorySpinner);
        normalUses = findViewById(R.id.normalUses);
        medicalUses = findViewById(R.id.medicalUses);
        traditionalUses = findViewById(R.id.traditionalUses);
        partUsed = findViewById(R.id.partUsed);
        preparationType = findViewById(R.id.preparationType);
        description = findViewById(R.id.description);
        height = findViewById(R.id.height);
        btnSelectImages = findViewById(R.id.btnSelectImages);
        btnSave = findViewById(R.id.btnSave);
        imagesContainer = findViewById(R.id.imagesContainer);
    }

    private void setupSpinner() {
        String[] categories = getResources().getStringArray(R.array.spinner_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);
    }

    private void selectImages() {
        Intent pickImagesIntent = new Intent(Intent.ACTION_PICK);
        pickImagesIntent.setType("image/*");
        pickImagesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activityResultLauncher.launch(pickImagesIntent);
    }

    private void addBitmapFromUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            bitmaps.add(bitmap);
            addImageToContainer(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImageToContainer(Bitmap bitmap) {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        int size = dpToPx(70);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMargins(dpToPx(8), 0, dpToPx(8), 0);
        imageView.setLayoutParams(params);
        imageView.setPadding(dpToPx(8), 0, dpToPx(8), 0);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imagesContainer.addView(imageView);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }



    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void savePlant() {
        // Retrieve JWT Token and input fields
        String jwtToken = SharedPreferencesUtil.getToken(this);
        String englishNameStr = englishName.getText().toString().trim();
        String nepaliNameStr = nepaliName.getText().toString().trim();
        String tharuNameStr = tharuName.getText().toString().trim();
        String localNameStr = localName.getText().toString().trim();
        String scientificNameStr = scientificName.getText().toString().trim();
        String categoryStr = categorySpinner.getSelectedItem().toString().trim();
        String normalUsesStr = normalUses.getText().toString().trim();
        String medicalUsesStr = medicalUses.getText().toString().trim();
        String traditionalUsesStr = traditionalUses.getText().toString().trim();
        String partUsedStr = partUsed.getText().toString().trim();
        String preparationTypeStr = preparationType.getText().toString().trim();
        String descriptionStr = description.getText().toString().trim();
        String heightStr = height.getText().toString().trim();

        // Check required fields
        String[] requiredFields = {englishNameStr, nepaliNameStr, scientificNameStr, categoryStr};
        for (String field : requiredFields) {
            if (field.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validate selected images
        if (bitmaps.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Encode images to Base64
        List<String> base64Images = new ArrayList<>();
        for (Bitmap bitmap : bitmaps) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            base64Images.add(encodedImage);
        }

        // Create a JSON object to send to the server
        JSONObject plantData = new JSONObject();
        try {
            plantData.put("englishName", englishNameStr);
            plantData.put("nepaliName", nepaliNameStr);
            plantData.put("tharuName", tharuNameStr);
            plantData.put("localName", localNameStr);
            plantData.put("scientificName", scientificNameStr);
            plantData.put("plantCategory", categoryStr);
            plantData.put("normalUses", normalUsesStr);
            plantData.put("medicalUses", medicalUsesStr);
            plantData.put("traditionalUse", traditionalUsesStr);
            plantData.put("partUsed", partUsedStr);
            plantData.put("preparationType", preparationTypeStr);
            plantData.put("description", descriptionStr);
            plantData.put("plantHeight", heightStr);
            plantData.put("status", 0); // Integer for status
            plantData.put("isDeleted", false); // Boolean for isDeleted
            plantData.put("imagePaths", new JSONArray(base64Images));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle successful response
                        Toast.makeText(AddPlantActivity.this, "Plant saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    byte[] responseData = error.networkResponse.data;

                    // Convert byte array to string
                    String errorMessage = new String(responseData);

                    // Handle specific error cases
                    if (statusCode == 400) {
                        if (errorMessage.contains("plant with same name already exists")) {
                            Toast.makeText(AddPlantActivity.this, "Error: Plant with same name already exists.", Toast.LENGTH_LONG).show();
                        } else if (errorMessage.contains("Required fields are missing")) {
                            Toast.makeText(AddPlantActivity.this, "Error: Required fields are missing.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AddPlantActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Handle other HTTP status codes
                        Toast.makeText(AddPlantActivity.this, "Something went wrong: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Handle cases where there is no network response
                    Toast.makeText(AddPlantActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            public byte[] getBody() {
                return plantData.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken);
                headers.put("Content-Type", "application/json"); // Correct Content-Type for JSON
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}