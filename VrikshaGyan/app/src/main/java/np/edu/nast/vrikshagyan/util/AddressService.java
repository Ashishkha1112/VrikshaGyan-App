package np.edu.nast.vrikshagyan.util;

import static com.android.volley.VolleyLog.TAG;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class AddressService {

    private RequestQueue requestQueue;
    private Context context;

    public AddressService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        Log.d(TAG, "AddressService initialized");
    }

    public void getAllProvinces(final AddressCallback<JSONArray> callback) {
        String url = "http://192.168.18.13:8080/api/provinces";

        if (requestQueue == null) {
            Log.e(TAG, "RequestQueue is null in getAllProvinces");
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
                        callback.onError("Error fetching provinces: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    public void getDistrictsByProvince(long provinceId, final AddressCallback<JSONArray> callback) {
        String url = "http://192.168.18.13:8080/api/provinces/" + provinceId;

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
                        callback.onError("Error fetching districts: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    public void getMunicipalitiesByDistrict(long districtId, final AddressCallback<JSONArray> callback) {
        String url = "http://192.168.18.13:8080/api/district/" + districtId;

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
                        callback.onError("Error fetching municipalities: " + error.getMessage());
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    // Additional methods can be added for other address components (wards, toles, etc.)

    public interface AddressCallback<T> {
        void onSuccess(T response);
        void onError(String message);
    }
}
