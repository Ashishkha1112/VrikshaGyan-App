package np.edu.nast.vrikshagyan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import np.edu.nast.vrikshagyan.model.Plants;
import np.edu.nast.vrikshagyan.util.ImageAdapter;
import np.edu.nast.vrikshagyan.util.PlantAdapter;
import np.edu.nast.vrikshagyan.util.PlantService;
import np.edu.nast.vrikshagyan.util.SharedPreferencesUtil;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView plantsRecyclerView;
    private PlantAdapter plantAdapter;
    private List<Plants> plantsList = new ArrayList<>();
    private PlantService plantService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Add fragments if not savedInstanceState
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
      try {
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
      }catch (Exception e){
          Log.d("HomeActivity", "Received JSON: " + e);
      }
        plantsRecyclerView = findViewById(R.id.plantsRecyclerView);
        plantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantAdapter = new PlantAdapter(this, plantsList);
        plantsRecyclerView.setAdapter(plantAdapter);

        plantService = new PlantService(this);
        fetchVerifiedPlants();
    }

    private void fetchVerifiedPlants() {
        String jwtToken = SharedPreferencesUtil.getToken(this);
        plantService.getVerifiedPlants(new PlantService.PlantCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    Log.d("HomeActivity", "Received JSON: " + response.toString());

                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject plantJson = response.getJSONObject(i);
                            Long plantId = plantJson.getLong("plantId");
                            String englishName = plantJson.optString("englishName", "Unknown");
                            String nepaliName = plantJson.optString("nepaliName", "Unknown");
                            String normalUses = plantJson.optString("normalUses", "Unknown");
                            JSONArray imageArray = plantJson.optJSONArray("imagePaths");
                            List<String> imageUrls = new ArrayList<>();

                            if (imageArray != null) {
                                for (int j = 0; j < imageArray.length(); j++) {
                                    imageUrls.add(imageArray.getString(j));
                                    Log.e("base64", "imagePaths"+imageUrls);
                                }
                            }

                            Plants plant = new Plants(  plantId,englishName, nepaliName, normalUses, imageUrls);
                            plantsList.add(plant);
                        }

                        plantAdapter.notifyDataSetChanged();
                    } else {
                        // Handle no plants case
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                }
            }

            @Override
            public void onError(String message) {

            }
        },jwtToken );
    }
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
         startActivity(intent);
        finish();
    }




}
