package np.edu.nast.vrikshagyan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import np.edu.nast.vrikshagyan.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> imageBase64List;
    private Context context;

    public ImageAdapter(Context context, List<String> imageBase64List) {
        this.context = context;
        this.imageBase64List = imageBase64List;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String base64Image = imageBase64List.get(position);
        Bitmap bitmap = decodeBase64ToBitmap(base64Image);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return imageBase64List.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

//    private Bitmap decodeBase64ToBitmap(String base64String) {
//        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
//    }
private Bitmap decodeBase64ToBitmap(String base64String) {
    try {
        // Clean the Base64 string
        base64String = base64String.trim().replace("\n", "").replace("\r", "");

        // Decode the Base64 string
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

        // Convert to Bitmap
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
        Log.e("ImageAdapter", "Base64 Decoding Error: " + e.getMessage());
        return null;
    }
}

}
