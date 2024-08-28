package np.edu.nast.vrikshagyan;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends JsonRequest<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final File mFile;
    private final String mToken;
    private static final String BOUNDARY = "apiclient-" + System.currentTimeMillis();

    public MultipartRequest(int method, String url, File file, String token, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mFile = file;
        this.mToken = token;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + BOUNDARY;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + mToken);
        headers.put("Content-Type", getBodyContentType());
        return headers;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            writeContentDisposition(bos);
            writeFileContent(bos);
            bos.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    private void writeContentDisposition(ByteArrayOutputStream bos) throws IOException {
        String contentType = getMimeType(mFile.getName());
        String contentDisposition = "--" + BOUNDARY + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + mFile.getName() + "\"\r\n"
                + "Content-Type: " + contentType + "\r\n\r\n";
        bos.write(contentDisposition.getBytes());
    }

    private void writeFileContent(ByteArrayOutputStream bos) throws IOException {
        FileInputStream fis = new FileInputStream(mFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        fis.close();
    }

    private String getMimeType(String fileName) {
        String mimeType = "application/octet-stream"; // Default
        if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".png")) {
            mimeType = "image/png";
        } else if (fileName.toLowerCase().endsWith(".gif")) {
            mimeType = "image/gif";
        }
        return mimeType;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }
}
