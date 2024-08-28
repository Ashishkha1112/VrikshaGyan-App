package np.edu.nast.vrikshagyan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import np.edu.nast.vrikshagyan.model.Plants;
import np.edu.nast.vrikshagyan.util.PlantDetailAdapter;
import np.edu.nast.vrikshagyan.util.PlantService;
import np.edu.nast.vrikshagyan.util.SharedPreferencesUtil;

public class PlantDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView,allComment;
    private PlantDetailAdapter plantsAdapter;
    private PlantService plantService;
    private final String BASE_URL = "http://192.168.18.13:8080/api/plants/editPlant/";
    private EditText comment;
    private Long plantId;

    private ImageButton btnComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ensure this is needed for your design
        setContentView(R.layout.activity_plant_detail);

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


        // Initialize RecyclerView
        recyclerView = findViewById(R.id.plantsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        comment= findViewById(R.id.comment);
        btnComment =  findViewById(R.id.btnComment);
        allComment= findViewById(R.id.allComments);
        plantService = new PlantService(this);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentPlant();
            }
        });
        allComment.setLayoutManager(new LinearLayoutManager(this));

        List<String[]> commentList = new ArrayList<>();
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(android.R.layout.simple_list_item_2, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                String[] commentData = commentList.get(position);
                TextView userNameTextView = holder.itemView.findViewById(android.R.id.text1);
                TextView commentTextView = holder.itemView.findViewById(android.R.id.text2);

                userNameTextView.setText(commentData[0]);
                commentTextView.setText(commentData[1]);
            }

            @Override
            public int getItemCount() {
                return commentList.size();
            }
        };
       getDetails();
    }

    private Plants parsePlant(JSONObject plantJson) throws JSONException {
        Long plantId = plantJson.optLong("plantId");
        String englishName = plantJson.optString("englishName");
        String nepaliName = plantJson.optString("nepaliName");
        String scientificName = plantJson.optString("scientificName");
        String plantCategory = plantJson.optString("plantCategory");
        String partUsed = plantJson.optString("partUsed");
        String tharuName = plantJson.optString("tharuName");
        String localName = plantJson.optString("localName");
        String normalUses = plantJson.optString("normalUses");
        String traditionalUse = plantJson.optString("traditionalUse");
        String medicalUses = plantJson.optString("medicalUses");
        String preparationType = plantJson.optString("preparationType");
        String description = plantJson.optString("description");
        String plantHeight = plantJson.optString("plantHeight");
        JSONArray imagePathsArray = plantJson.optJSONArray("imagePaths");
        List<String> imagePaths = new ArrayList<>();
        if (imagePathsArray != null) {
            for (int i = 0; i < imagePathsArray.length(); i++) {
                imagePaths.add(imagePathsArray.optString(i));
             //   Log.e("base64", "imagePaths"+imagePathsArray);
            }
        }
        return new Plants(
                plantId, englishName, nepaliName, tharuName, localName, scientificName, plantCategory,
                partUsed, normalUses, traditionalUse, medicalUses, preparationType, plantHeight, description,
                1, false, imagePaths
        );
    }

    // Utility method to URL encode the plant name
    private String encodeURIComponent(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //public void getDetails(){
//        String jwtToken = SharedPreferencesUtil.getToken(this);
//        // Retrieve the search query from the Intent
//        Intent intent = getIntent();
//        String englishName = intent.getStringExtra("ENGLISH_NAME");
//
//        // Ensure englishName is not null or empty
//        if (englishName != null && !englishName.isEmpty()) {
//            plantService.getPlantDetails(BASE_URL + englishName, new PlantService.PlantCallback<JSONObject>() {
//                @Override
//                public void onSuccess(JSONObject response) {
//                    List<Plants> plantList = new ArrayList<>();
//                    try {
//                        JSONObject data = response.getJSONObject("data");
//                        Plants plant = parsePlant(data);
//                        plantList.add(plant);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    plantsAdapter = new PlantDetailAdapter(PlantDetailActivity.this, plantList);
//                    recyclerView.setAdapter(plantsAdapter);
//                }
//
//                @Override
//                public void onError(String message) {
//                    Toast.makeText(PlantDetailActivity.this, message, Toast.LENGTH_LONG).show();
//                }
//            },jwtToken);
//
//
//        } else {
//            Toast.makeText(this, "No plant name provided", Toast.LENGTH_SHORT).show();
//        }
//    }
public void getDetails() {
    String jwtToken = SharedPreferencesUtil.getToken(this);
    String id = getIntent().getStringExtra("PLANT_ID");
    Log.e("ashish", "Received Plant ID: " +id);
    //  Log.e("ashish","kha"+id);
    if (id != null) {
        Long plantIds;
        try {
            // Convert the String ID to Long
            plantIds = Long.parseLong(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid PlantId format", Toast.LENGTH_SHORT).show();
            return; // Exit the method if ID is invalid
        }

        // Construct the full URL with plantId
        String url = BASE_URL + plantIds; // Ensure this URL format is correct for your API
     //   Log.d("PlantDetailActivity", "Request URL:+url);


        plantService.getPlantDetails(url, new PlantService.PlantCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                List<Plants> plantList = new ArrayList<>();
                try {
                    JSONObject data = response.getJSONObject("data");
                    Plants plant = parsePlant(data);
                    plantList.add(plant);
                    plantId = plant.getPlantId(); // Ensure plantId is set here

                    // Initialize the plant detail adapter and set it to the RecyclerView
                    plantsAdapter = new PlantDetailAdapter(PlantDetailActivity.this, plantList);
                    recyclerView.setAdapter(plantsAdapter);

                    // Now that plantId is set, fetch comments
                    List<String[]> commentList = new ArrayList<>();
                    RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                        @NonNull
                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(android.R.layout.simple_list_item_2, parent, false);
                            return new RecyclerView.ViewHolder(view) {};
                        }

                        @Override
                        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                            String[] commentData = commentList.get(position);
                            TextView userNameTextView = holder.itemView.findViewById(android.R.id.text1);
                            TextView commentTextView = holder.itemView.findViewById(android.R.id.text2);

                            userNameTextView.setText(commentData[0]);
                            commentTextView.setText(commentData[1]);
                        }

                        @Override
                        public int getItemCount() {
                            return commentList.size();
                        }
                    };

                    allComment.setAdapter(adapter);

                    fetchComments(commentList, adapter); // Fetch comments after setting plantId

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(PlantDetailActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }, jwtToken);
    } else {
        Toast.makeText(this, "No plant name provided", Toast.LENGTH_SHORT).show();
    }
}


    public void commentPlant() {
        String jwtToken = SharedPreferencesUtil.getToken(this);
        String url = "http://192.168.18.13:8080/api/comment"; // Corrected URL endpoint
        String commentStr = comment.getText().toString().trim();
        Long id = plantId;

        // Create a JSON object to send to the server
        JSONObject data = new JSONObject();
        try {
            data.put("comment", commentStr);
            // Note: plantId should be passed as a query parameter, not in the JSON body
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "?plantId=" + id, // Append plantId as query parameter
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error here
                        error.printStackTrace();
                        Toast.makeText(PlantDetailActivity.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public byte[] getBody() {
                return data.toString().getBytes();
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
    private void fetchComments(List<String[]> commentList, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        if (plantId == null) {
            Toast.makeText(PlantDetailActivity.this, "Plant ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("PlantDetailActivity", "Fetching comments for plantId: " + plantId);

        String url = "http://192.168.18.13:8080/api/comments/" + plantId;
        RequestQueue queue = Volley.newRequestQueue(this);

        String jwtToken = SharedPreferencesUtil.getToken(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            commentList.clear(); // Clear existing comments before adding new ones
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject commentObject = response.getJSONObject(i);
                                String userName = commentObject.getString("userName");
                                String comment = commentObject.getString("comment");

                                commentList.add(new String[]{userName, comment});
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PlantDetailActivity.this, "Failed to fetch comments", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken);
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }


}
