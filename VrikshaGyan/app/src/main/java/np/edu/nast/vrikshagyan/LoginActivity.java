package np.edu.nast.vrikshagyan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import np.edu.nast.vrikshagyan.util.SharedPreferencesUtil;

public class LoginActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private TextInputEditText txtEmail, txtPassword;
    private Button btnSignIn;
    private TextView txtForgotPassword,txtSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);
        txtSignUp =findViewById(R.id.txtSignUp);
        txtForgotPassword= findViewById(R.id.txtForgotPassword);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(v -> {
            String username = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            loginUser(username, password);
        });
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String username, String password) {
        String url = "http://192.168.18.13:8080/authenticate"; // Use localhost for emulator

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("username", username);
            loginData.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, loginData,
                response -> {
                    try {
                        String token = response.getString("jwtToken");
                        long expirationTime = System.currentTimeMillis() + (5 * 24 * 60 * 60 * 1000L); // 5 days in milliseconds
                        SharedPreferencesUtil.saveToken(LoginActivity.this, token, expirationTime);
                        //Toast.makeText(LoginActivity.this, token, Toast.LENGTH_LONG).show();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Optional: to finish the LoginActivity and prevent going back to it
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error instanceof AuthFailureError) {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


}
