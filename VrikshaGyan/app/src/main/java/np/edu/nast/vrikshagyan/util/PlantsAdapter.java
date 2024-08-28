package np.edu.nast.vrikshagyan.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import np.edu.nast.vrikshagyan.PlantDetailActivity;
import np.edu.nast.vrikshagyan.R;
import np.edu.nast.vrikshagyan.model.Plants;

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.PlantViewHolder> {

    private List<Plants> plants;
    private Context context;

    public PlantsAdapter(Context context, List<Plants> plants) {
        this.context = context;
        this.plants = plants;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plants, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plants plant = plants.get(position);
        holder.englishNameTextView.setText(plant.getEnglishName());
        holder.nepaliNameTextView.setText(plant.getNepaliName());
        holder.normalUsesTextView.setText(plant.getNormalUses());

        // Set up the image adapter
        ImageAdapter imageAdapter = new ImageAdapter(context, plant.getImagePaths());
        holder.imagesRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.imagesRecyclerView.setAdapter(imageAdapter);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long plantId = plant.getPlantId(); // Get the PlantId
                if (plantId != null) { // Check if PlantId is not null
                    Intent intent = new Intent(context, PlantDetailActivity.class);
                    intent.putExtra("PLANT_ID", plant.getPlantId().toString());
                    // Pass PlantId instead of EnglishName
                    context.startActivity(intent);
                    String bid = intent.getStringExtra("PLANT_ID");
                    Log.e("ashish", "chHa"+ bid);
                    Log.d("PlantAdapter", "Navigating to PlantDetailActivity with PlantId: " + plantId);
                } else {
                    Toast.makeText(context, "PlantId is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView englishNameTextView;
        TextView nepaliNameTextView;
        TextView normalUsesTextView;
        RecyclerView imagesRecyclerView;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            englishNameTextView = itemView.findViewById(R.id.englishNameTextView);
            nepaliNameTextView = itemView.findViewById(R.id.nepaliNameTextView);
            normalUsesTextView = itemView.findViewById(R.id.normalUsesTextView);
            imagesRecyclerView = itemView.findViewById(R.id.imagesRecyclerView);
        }
    }
}
