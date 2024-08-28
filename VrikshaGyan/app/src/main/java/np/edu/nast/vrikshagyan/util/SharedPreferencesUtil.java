package np.edu.nast.vrikshagyan.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static final String PREF_NAME = "my_app_prefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private static final String KEY_EXPIRATION_TIME = "expiration_time";

    public static void saveToken(Context context, String token, long expirationTime) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_JWT_TOKEN, token);
        editor.putLong(KEY_EXPIRATION_TIME, expirationTime);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE);
        long expirationTime = sharedPreferences.getLong(KEY_EXPIRATION_TIME, 0);
        if (expirationTime == 0) {
            // No expiration time stored or token not set
            clearToken(context);
            return null;
        }
        if (System.currentTimeMillis() > expirationTime) {
            // Token has expired
            clearToken(context);
            return null;
        }
        return sharedPreferences.getString(KEY_JWT_TOKEN, null);
    }

    public static void clearToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_JWT_TOKEN);
        editor.remove(KEY_EXPIRATION_TIME);
        editor.apply();
    }
}
