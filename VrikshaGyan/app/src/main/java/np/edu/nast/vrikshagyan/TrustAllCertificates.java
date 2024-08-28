package np.edu.nast.vrikshagyan;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class TrustAllCertificates implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        // Trust all clients
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        // Trust all servers
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
