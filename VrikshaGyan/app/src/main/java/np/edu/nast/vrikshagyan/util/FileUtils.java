package np.edu.nast.vrikshagyan.util;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
                return getFileFromDocumentUri(context, uri);
            } else {
                return getFileFromContentUri(context, uri);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return new File(uri.getPath());
        }
        return null;
    }

    private static File getFileFromDocumentUri(Context context, Uri uri) throws IOException {
        String documentId = DocumentsContract.getDocumentId(uri);
        String[] split = documentId.split(":");
        String type = split[0];

        if ("image".equals(type)) {
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = { MediaStore.Images.Media.DATA };
            String selection = MediaStore.Images.Media._ID + "=?";
            String[] selectionArgs = new String[]{ split[1] };

            try (Cursor cursor = context.getContentResolver().query(contentUri, projection, selection, selectionArgs, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    return new File(filePath);
                }
            }
        }
        return null;
    }

    private static File getFileFromContentUri(Context context, Uri uri) throws IOException {
        String[] projection = { MediaStore.Images.Media.DATA };
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String filePath = cursor.getString(columnIndex);
                return new File(filePath);
            }
        }
        return null;
    }

    public static File createFileFromInputStream(Context context, Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        try (InputStream inputStream = contentResolver.openInputStream(uri)) {
            if (inputStream == null) {
                throw new IOException("Failed to open InputStream for URI: " + uri.toString());
            }

            File file = new File(context.getCacheDir(), getFileName(context, uri));
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return file;
        }
    }

    private static String getFileName(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                return cursor.getString(columnIndex);
            }
        }
        return "temp_file";
    }
}
