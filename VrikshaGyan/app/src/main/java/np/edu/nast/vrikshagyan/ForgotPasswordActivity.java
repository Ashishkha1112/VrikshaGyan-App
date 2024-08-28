package np.edu.nast.vrikshagyan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        TextInputEditText txtEmail = findViewById(R.id.txtEmail);
        Button btnContinue = findViewById(R.id.btnContinue);
        TextView txtLogin = findViewById(R.id.txtLogin);

        requestQueue = Volley.newRequestQueue(this);

        txtLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnContinue.setOnClickListener(v -> {
            String username = txtEmail.getText().toString().trim();
            btnContinue.setEnabled(false);

            // Send OTP
            sendOTP(username);

            // Enable the button after some delay or after the email is sent
            new Handler().postDelayed(() -> btnContinue.setEnabled(true), 2000); // Adjust the delay as needed
        });
    }

        private void sendOTP(String email) {
        String url = "http://10.0.2.2:8080/api/send/" + email;

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle response from server
                        Toast.makeText(ForgotPasswordActivity.this, response, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, OTPVerifyActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error responses
                        Log.e("VolleyError", "Error response: " + error.toString());
                        String errorMessage = "OTP send failed: ";
                        if (error instanceof AuthFailureError) {
                            errorMessage += "Invalid email! Please enter registered email.";
                        } else if (error instanceof NetworkError || error instanceof NoConnectionError) {
                            errorMessage += "Network error! Please check your internet connection.";
                        } else if (error instanceof TimeoutError) {
                            errorMessage += "Request timed out! Please try again.";
                        } else if (error instanceof ServerError) {
                            errorMessage += "Server error! Please try again later.";
                        } else {
                            errorMessage += error.getMessage();
                        }
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
        requestQueue.add(stringRequest);
    }


}
