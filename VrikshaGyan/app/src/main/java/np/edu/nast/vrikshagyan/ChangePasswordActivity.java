package np.edu.nast.vrikshagyan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import np.edu.nast.vrikshagyan.util.SharedPreferencesUtil;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputEditText txtPassword, txtConfirmPassword;
    private Button btnChangePassword;
    private ImageButton btnInfo;
    private TextView passwordStrengthText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnInfo = findViewById(R.id.btnInfo);
        passwordStrengthText = findViewById(R.id.passwordStrengthText);

        btnInfo.setOnClickListener(v -> Toast.makeText(ChangePasswordActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_LONG).show());

        btnChangePassword.setOnClickListener(v -> {
            // Get the text from the password fields
            String email = getIntent().getStringExtra("email");
            String password = txtPassword.getText().toString().trim();
            String confirmPassword = txtConfirmPassword.getText().toString().trim();

            // Check if the passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the password length is at least 8 characters
            if (password.length() < 8) {
                Toast.makeText(ChangePasswordActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            // If both checks pass, proceed with password change logic
            changePassword(email, password);
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updatePasswordStrength(String password) {
        int strength = getPasswordStrength(password);
        if (strength < 25) {
            passwordStrengthText.setText("Weak");
        } else if (strength < 50) {
            passwordStrengthText.setText("Moderate");
        } else if (strength < 75) {
            passwordStrengthText.setText("Strong");
        } else {
            passwordStrengthText.setText("Very Strong");
        }
    }

    private int getPasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) {
            strength += 25;
        }
        if (password.matches(".*[A-Z].*")) {
            strength += 25;
        }
        if (password.matches(".*[0-9].*")) {
            strength += 25;
        }
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/?].*")) {
            strength += 25;
        }
        return strength;
    }

    private void changePassword(String email, String newPassword) {
        String url = "http://10.0.2.2:8080/api/change"; // Use localhost for emulator

        // Create JSON object with email and newPassword
        JSONObject changePasswordData = new JSONObject();
        try {
            changePasswordData.put("email", email);
            changePasswordData.put("newPassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ChangePasswordActivity.this, "Error creating JSON request", Toast.LENGTH_SHORT).show();
            return; // Exit the method if JSON creation fails
        }

        // Create a StringRequest instead of JsonObjectRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT, // Use PUT method
                url,
                response -> {
                    Log.d("Response", response); // Log the response for debugging
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message");
                        Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_LONG).show();

                        // Optionally, navigate to another activity
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Optional: to finish the ChangePasswordActivity
                    } catch (JSONException e) {
                        Toast.makeText(ChangePasswordActivity.this, "Password Change Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();   }
                },
                error -> {
                    if (error instanceof AuthFailureError) {
                        Toast.makeText(ChangePasswordActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = "Password change failed: ";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String errorResponse = new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers));
                                errorMessage += errorResponse;
                            } catch (UnsupportedEncodingException | NullPointerException e) {
                                e.printStackTrace();
                                errorMessage += e.getMessage(); // Include exception message for debugging
                            }
                        } else {
                            errorMessage += error.getMessage();
                        }
                        Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }


    }) {
            @Override
            public byte[] getBody() {
                return changePasswordData.toString().getBytes(); // Override getBody to return JSON string as bytes
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Set content type to JSON
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
