package np.edu.nast.vrikshagyan;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable verbose SSL/TLS debugging
        System.setProperty("javax.net.debug", "ssl,handshake,data,trustmanager");
    }
}
