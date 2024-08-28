package np.edu.nast.vrikshagyan.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import np.edu.nast.vrikshagyan.SearchActivity;

public class PlantService {

    private static final String TAG = "PlantService";
    private RequestQueue requestQueue;
    private Context context;


    public PlantService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        Log.d(TAG, "PlantService initialized");
    }

    public void getVerifiedPlants(final PlantCallback<JSONArray> callback, String jwtToken) {

        String url = "http://192.168.18.13:8080/api/plants/verified"; // Replace with your actual server URL

        if (requestQueue == null) {
            Log.e(TAG, "RequestQueue is null in getVerifiedPlants");
            callback.onError("RequestQueue is null");
            return;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error fetching plants: " + error.getMessage());
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

        requestQueue.add(jsonArrayRequest);
    }

    public void searchPlants(String url, final PlantCallback<JSONArray> callback,String jwtToken) {

        if (requestQueue == null) {
            Log.e(TAG, "RequestQueue is null in searchPlants");
            callback.onError("RequestQueue is null");
            return;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error fetching search results: " + error.getMessage());
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

        requestQueue.add(jsonArrayRequest);
    }

    public void getPlantDetails(String url, final PlantCallback<JSONObject> callback, String jwtToken) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Error fetching plant details: " + error.getMessage());
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

        requestQueue.add(jsonObjectRequest);
    }

    public interface PlantCallback<T> {
        void onSuccess(T response);
        void onError(String message);
    }
}
