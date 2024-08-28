package np.edu.nast.vrikshagyan.util;

import android.graphics.Bitmap;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MultipartRequest extends Request<JSONObject> {
    private static final String BOUNDARY = "----BoundaryString";
    private static final String FILE_PART_NAME = "images";
    private final Response.Listener<JSONObject> mListener;
    private final List<Bitmap> mImages;
    private final JSONObject jsonObject;

    public MultipartRequest(List<Bitmap> images, JSONObject jsonObject,
                            Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, "https://192.168.18.13:8080/api/plants/single", errorListener);
        mListener = listener;
        mImages = images;
        this.jsonObject = jsonObject;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        return headers;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(bos)) {
            // Add JSON data as a form field
            writeFormField(dos, "plantData", jsonObject.toString());

            // Add each image file
            for (int i = 0; i < mImages.size(); i++) {
                writeFileField(dos, FILE_PART_NAME, "image" + i + ".jpeg", mImages.get(i));
            }

            // End boundary
            dos.writeBytes("--" + BOUNDARY + "--\r\n");
            dos.flush(); // Ensure all data is written out
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    private void writeFormField(DataOutputStream dos, String name, String value) throws IOException {
        dos.writeBytes("--" + BOUNDARY + "\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"\r\n");
        dos.writeBytes("Content-Type: application/json; charset=UTF-8\r\n\r\n"); // Added charset
        dos.writeBytes(value + "\r\n");
    }

    private void writeFileField(DataOutputStream dos, String name, String filename, Bitmap bitmap) throws IOException {
        dos.writeBytes("--" + BOUNDARY + "\r\n");
        dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n");
        dos.writeBytes("Content-Type: image/jpeg\r\n\r\n");

        ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageBaos);
        dos.write(imageBaos.toByteArray());

        dos.writeBytes("\r\n");
    }




    @Override
    protected void deliverResponse(JSONObject response) {
        mListener.onResponse(response);
    }
}
