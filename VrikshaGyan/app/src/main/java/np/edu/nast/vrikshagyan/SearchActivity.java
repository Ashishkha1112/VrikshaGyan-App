package np.edu.nast.vrikshagyan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import np.edu.nast.vrikshagyan.util.PlantService;
import np.edu.nast.vrikshagyan.util.PlantsAdapter;
import np.edu.nast.vrikshagyan.util.SharedPreferencesUtil;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlantsAdapter plantsAdapter;
    private PlantService plantService;
    private final String BASE_URL ="http://192.168.18.13:8080/search";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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


        recyclerView = findViewById(R.id.plantsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        plantService = new PlantService(this);

        // Example search query
        // Retrieve the search query from the Intent
    search();


    }

    private Plants parsePlant(JSONObject plantJson) throws JSONException {
        Long plantId = plantJson.optLong("plantId");
        String englishName = plantJson.optString("englishName");
        String nepaliName = plantJson.optString("nepaliName");
        String normalUses = plantJson.optString("normalUses");

        JSONArray imagePathsArray = plantJson.optJSONArray("imagePaths");
        List<String> imagePaths = new ArrayList<>();
        if (imagePathsArray != null) {
            for (int i = 0; i < imagePathsArray.length(); i++) {
                imagePaths.add(imagePathsArray.optString(i));
            }
        }

        return new Plants(plantId, englishName, nepaliName, null, null, null, null, null, normalUses, null, null, null, null, null, 0, false, imagePaths);
    }
    private String buildSearchUrl(String baseUrl, String englishName, String nepaliName, String scientificName,
                                  String plantCategory, String partUsed, String tharuName,
                                  String localName, String traditionalUse, String medicalUses,
                                  String preparationType, String description, String normalUses) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);


        boolean firstParam = true;

        if (englishName != null && !englishName.isEmpty()) {
            appendParam(urlBuilder, firstParam, "englishName", englishName);
            firstParam = false;
        }
        if (nepaliName != null && !nepaliName.isEmpty()) {
            appendParam(urlBuilder, firstParam, "nepaliName", nepaliName);
            firstParam = false;
        }
        if (scientificName != null && !scientificName.isEmpty()) {
            appendParam(urlBuilder, firstParam, "scientificName", scientificName);
            firstParam = false;
        }
        if (plantCategory != null && !plantCategory.isEmpty()) {
            appendParam(urlBuilder, firstParam, "plantCategory", plantCategory);
            firstParam = false;
        }
        if (partUsed != null && !partUsed.isEmpty()) {
            appendParam(urlBuilder, firstParam, "partUsed", partUsed);
            firstParam = false;
        }
        if (tharuName != null && !tharuName.isEmpty()) {
            appendParam(urlBuilder, firstParam, "tharuName", tharuName);
            firstParam = false;
        }
        if (localName != null && !localName.isEmpty()) {
            appendParam(urlBuilder, firstParam, "localName", localName);
            firstParam = false;
        }
        if (traditionalUse != null && !traditionalUse.isEmpty()) {
            appendParam(urlBuilder, firstParam, "traditionalUse", traditionalUse);
            firstParam = false;
        }
        if (medicalUses != null && !medicalUses.isEmpty()) {
            appendParam(urlBuilder, firstParam, "medicalUses", medicalUses);
            firstParam = false;
        }
        if (preparationType != null && !preparationType.isEmpty()) {
            appendParam(urlBuilder, firstParam, "preparationType", preparationType);
            firstParam = false;
        }
        if (description != null && !description.isEmpty()) {
            appendParam(urlBuilder, firstParam, "description", description);
            firstParam = false;
        }
        if (normalUses != null && !normalUses.isEmpty()) {
            appendParam(urlBuilder, firstParam, "normalUses", normalUses);
            firstParam = false;
        }

        return urlBuilder.toString();
    }

    // Helper method to append parameters to URL
    private void appendParam(StringBuilder urlBuilder, boolean firstParam, String key, String value) {
        if (firstParam) {
            urlBuilder.append("?");
        } else {
            urlBuilder.append("&");
        }
        urlBuilder.append(key).append("=").append(value);
    }
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void search(){
        String jwtToken = SharedPreferencesUtil.getToken(this);
        Intent intent = getIntent();
        String searchQuery = intent.getStringExtra("SEARCH_QUERY");
        String url = buildSearchUrl(BASE_URL,searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery);
        Toast.makeText(SearchActivity.this,url,Toast.LENGTH_LONG);

        plantService.searchPlants(url, new PlantService.PlantCallback<JSONArray>() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Plants> plantList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject plantJson = response.getJSONObject(i);
                        Plants plant = parsePlant(plantJson);
                        plantList.add(plant);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                plantsAdapter = new PlantsAdapter(SearchActivity.this, plantList);
                recyclerView.setAdapter(plantsAdapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_LONG).show();
            }
        },jwtToken);
    }


}

