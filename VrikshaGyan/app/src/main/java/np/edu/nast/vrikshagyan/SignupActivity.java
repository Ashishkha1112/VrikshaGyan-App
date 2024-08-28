package np.edu.nast.vrikshagyan;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import np.edu.nast.vrikshagyan.util.AddressService;
public class SignupActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    private TextInputEditText firstNameEditText, middleNameEditText, lastNameEditText, phoneEditText;
    private TextInputEditText emailEditText, passwordEditText, wardEditText, toleEditText,occupation;
    private AddressService addressService;
    private final Map<String, Long> provinceIdMap = new HashMap<>();
    private final Map<String, Long> districtIdMap = new HashMap<>();
    Long municipalityId;
    Long provinceId;
    Long districtId;

    private final Map<String, Long> municipalityIdMap = new HashMap<>();
    private Spinner provinceSpinner;
    private Spinner districtSpinner;
    private Spinner municipalitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = Volley.newRequestQueue(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize AddressService
        addressService = new AddressService(this);

        // Bind UI components
        firstNameEditText = findViewById(R.id.firstName);
        middleNameEditText = findViewById(R.id.middleName);
        lastNameEditText = findViewById(R.id.lastName);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        wardEditText = findViewById(R.id.ward);
        toleEditText = findViewById(R.id.tole);
        provinceSpinner = findViewById(R.id.provinceSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        municipalitySpinner = findViewById(R.id.municipalitySpinner);
        occupation=findViewById(R.id.occupation);
        Button btnSignup = findViewById(R.id.btnSignup);
        // Set up spinners
        setupProvinceSpinner();
        setupDistrictSpinner();
        setupMunicipalitySpinner();

        // Set up login button click listener
        TextView login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        ScrollView scrollView =  findViewById(R.id.scrollView);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                scrollView.getWindowVisibleDisplayFrame(r);
                int screenHeight = scrollView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // Keyboard is opened
                    // Do something when keyboard is visible
                } else {
                    // Do something when keyboard is hidden
                }
            }
        });

        // Set up signup button click listener

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(SignupActivity.this, "hi", Toast.LENGTH_LONG).show();
                signupUser();
                //validateForm();
            }
        });

    }

    private void validateForm() {
        boolean isValid = true;

        // Check if all required fields are filled
        if (TextUtils.isEmpty(firstNameEditText.getText())) {
            firstNameEditText.setError(getString(R.string.error_first_name_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(lastNameEditText.getText())) {
            lastNameEditText.setError(getString(R.string.error_last_name_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(phoneEditText.getText())) {
            phoneEditText.setError(getString(R.string.error_phone_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailEditText.setError(getString(R.string.error_email_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText()).matches()) {
            emailEditText.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }

        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordEditText.setError(getString(R.string.error_password_required));
            isValid = false;
        }


        if (isValid) {
            // If form is valid, proceed with signup process
            signupUser();
        }else {

            Toast.makeText(SignupActivity.this,"Error Validation",Toast.LENGTH_LONG).show();
        }
    }

    public void setupProvinceSpinner() {
        // Call AddressService to get provinces
        addressService.getAllProvinces(new AddressService.AddressCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                List<String> provinceNames = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject province = response.getJSONObject(i);
                        provinceId = province.getLong("provinceId");
                        String provinceName = province.getString("provinceName");
                        provinceNames.add(provinceName);
                        provinceIdMap.put(provinceName, provinceId);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SignupActivity.this,
                            android.R.layout.simple_spinner_item, provinceNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    provinceSpinner.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON for provinces: " + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupDistrictSpinner() {
        // Set up listener for province selection
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvinceName = parent.getItemAtPosition(position).toString();
                long provinceId = provinceIdMap.get(selectedProvinceName);

                addressService.getDistrictsByProvince(provinceId, new AddressService.AddressCallback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        List<String> districtNames = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject district = response.getJSONObject(i);
                                districtId = district.getLong("districtId");
                                String districtName = district.getString("districtName");
                                districtNames.add(districtName);
                                districtIdMap.put(districtName, districtId);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(SignupActivity.this,
                                    android.R.layout.simple_spinner_item, districtNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            districtSpinner.setAdapter(adapter);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON for districts: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
    }

    public void setupMunicipalitySpinner() {
        // Set up listener for district selection
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrictName = parent.getItemAtPosition(position).toString();
                long districtId = districtIdMap.get(selectedDistrictName);

                addressService.getMunicipalitiesByDistrict(districtId, new AddressService.AddressCallback<JSONArray>() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        List<String> municipalityNames = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject municipality = response.getJSONObject(i);
                                municipalityId = municipality.getLong("municipalityId");
                                String municipalityName = municipality.getString("municipalityName");
                                municipalityNames.add(municipalityName);
                                municipalityIdMap.put(municipalityName, municipalityId);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(SignupActivity.this,
                                    android.R.layout.simple_spinner_item, municipalityNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            municipalitySpinner.setAdapter(adapter);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing JSON for municipalities: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
    }
    private void signupUser() {
        // Retrieve input values
        String firstName = firstNameEditText.getText().toString().trim();
        String middleName = middleNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String ward = wardEditText.getText().toString().trim();
        String tole = toleEditText.getText().toString().trim();
        String occupationText = occupation.getText().toString().trim();


        String selectedProvince = (String) provinceSpinner.getSelectedItem();
        String selectedDistrict = (String) districtSpinner.getSelectedItem();
        String selectedMunicipality = (String) municipalitySpinner.getSelectedItem();

// Check if any of the IDs are null
        if (selectedProvince == null || selectedDistrict == null || selectedMunicipality == null) {
            Toast.makeText(this, "Please make sure all address fields are selected.", Toast.LENGTH_LONG).show();
            return;
        }

// Now fetch the IDs using your maps
        Long provinceId = provinceIdMap.get(selectedProvince);
        Long districtId = districtIdMap.get(selectedDistrict);
        Long municipalityId = municipalityIdMap.get(selectedMunicipality);

// Check if any of the IDs are still null after fetching
        if (provinceId == null || districtId == null || municipalityId == null) {
            Toast.makeText(this, "Error: IDs for province, district, or municipality not found.", Toast.LENGTH_LONG).show();
            return;
        }

// Construct JSON object
        JSONObject signupData = new JSONObject();
        try {
            signupData.put("firstName", firstName);
            signupData.put("middleName", middleName);
            signupData.put("lastName", lastName);

            JSONObject address = new JSONObject();
            address.put("province", provinceId); // Use provinceId instead of province
            address.put("district", districtId); // Use districtId instead of district
            address.put("municipality", municipalityId); // Use municipalityId instead of municipality
            address.put("wardNumber", ward);
            address.put("tole", tole);

            signupData.put("address", address);
            signupData.put("phone", phone);
            signupData.put("email", email);
            signupData.put("password", password);
            signupData.put("occupation", occupationText);
            signupData.put("role", "USER");
            signupData.put("status", false);
            signupData.put("isDeleted", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Display JSON data for debugging
      //  Toast.makeText(SignupActivity.this, "Signup Data: " + signupData.toString(), Toast.LENGTH_LONG).show();

        // Make a POST request to your backend API to register the user
        String signupUrl = "http://192.168.18.13:8080/api/signupUser";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, signupUrl, signupData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle successful signup response
                        Toast.makeText(SignupActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        byte[] responseData = error.networkResponse.data;
                        String errorMessages = new String(responseData);
                        String errorMessage = "Sign up failed: ";
                        if (error instanceof NetworkError || error instanceof NoConnectionError) {
                            errorMessage += "Network error! Please check your internet connection.";
                        } else if (error instanceof ServerError) {
                            errorMessage += "Server error! Please try again later.";
                        } else   if (errorMessages.contains("Email or phone number already exists!")) {
                            Toast.makeText(SignupActivity.this, "Error: Email or phone number already exists!", Toast.LENGTH_LONG).show();}
                        else {
                            errorMessage += error.getMessage();
                        }


                        Toast.makeText(SignupActivity.this, "All Fiels are required", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

}