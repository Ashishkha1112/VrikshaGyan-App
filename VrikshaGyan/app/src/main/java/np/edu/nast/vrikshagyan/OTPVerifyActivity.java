package np.edu.nast.vrikshagyan;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

import np.edu.nast.vrikshagyan.util.RequestQueueSingleton;

public class OTPVerifyActivity extends AppCompatActivity {
    private static final long COUNTDOWN_TIME_MS = 2 * 60 * 1000; // 2 minutes in milliseconds
    private CountDownTimer countDownTimer;
    private TextView timerTextView;
    private TextInputEditText otp1;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);

        timerTextView = findViewById(R.id.timerTextView);
        otp1 = findViewById(R.id.otp1);


        Button confirmOtp = findViewById(R.id.confirmOtp);
        confirmOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getIntent().getStringExtra("email");
                String otp = otp1.getText().toString();
                verifyOTP(email, otp);
            }
        });


        startCountdownTimer();

        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }



    private void startCountdownTimer() {
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                String timerText = String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60);
                timerTextView.setText(timerText);
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                // Handle actions upon timer finish (e.g., enable resend OTP button)
                navigateToNextActivity();
            }
        };
        countDownTimer.start();
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(OTPVerifyActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
        finish(); // Finish current activity to prevent going back to OTPVerifyActivity
    }

    private void verifyOTP(String email, String otp) {
        String url = "http://10.0.2.2:8080/api/verify?email=" + email + "&otp=" + otp;
        Toast.makeText(OTPVerifyActivity.this, email, Toast.LENGTH_SHORT).show();
        Toast.makeText(OTPVerifyActivity.this, otp, Toast.LENGTH_SHORT).show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(OTPVerifyActivity.this, response, Toast.LENGTH_SHORT).show();
                        if (response.contains("OTP verified successfully")) {
                            navigateToChangePasswordActivity(); // Navigate on successful OTP verification
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Error response: " + error.toString());
                        String errorMessage = "OTP verification failed: ";
                        if (error instanceof AuthFailureError) {
                            errorMessage += "Invalid OTP or email.";
                        } else {
                            errorMessage += "Network or server error.";
                        }
                        Toast.makeText(OTPVerifyActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return null; // No body needed as parameters are in URL
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void navigateToChangePasswordActivity() {

        Intent intent = new Intent(OTPVerifyActivity.this, ChangePasswordActivity.class);
        String email = getIntent().getStringExtra("email");

        intent.putExtra("email",email);
        startActivity(intent);
        finish(); // Finish current activity to prevent going back to OTPVerifyActivity
    }
}
