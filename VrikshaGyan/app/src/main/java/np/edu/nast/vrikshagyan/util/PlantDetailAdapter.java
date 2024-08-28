package np.edu.nast.vrikshagyan.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import np.edu.nast.vrikshagyan.R;
import np.edu.nast.vrikshagyan.model.Plants;

public class PlantDetailAdapter extends RecyclerView.Adapter<PlantDetailAdapter.PlantDetailViewHolder> {
    private List<Plants> plantDetailsList;
    private Context context;

    // Constructor
    public PlantDetailAdapter(Context context, List<Plants> plantDetailsList) {
        this.context = context;
        this.plantDetailsList = plantDetailsList != null ? plantDetailsList : new ArrayList<>();
    }

    @NonNull
    @Override
    public PlantDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant_detail, parent, false);
        return new PlantDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantDetailViewHolder holder, int position) {
        if (position < plantDetailsList.size()) {
            Plants plant = plantDetailsList.get(position);

            // Bind data to views
            holder.englishNameTextView.setText(plant.getEnglishName() != null ? plant.getEnglishName() : "N/A");
            holder.nepaliNameTextView.setText(plant.getNepaliName() != null ? plant.getNepaliName() : "N/A");
            holder.scientificNameTextView.setText(plant.getScientificName() != null ? plant.getScientificName() : "N/A");
            holder.plantCategoryTextView.setText(plant.getPlantCategory() != null ? plant.getPlantCategory() : "N/A");
            holder.partUsedTextView.setText(plant.getPartUsed() != null ? plant.getPartUsed() : "N/A");
            holder.tharuNameTextView.setText(plant.getTharuName() != null ? plant.getTharuName() : "N/A");
            holder.localNameTextView.setText(plant.getLocalName() != null ? plant.getLocalName() : "N/A");
            holder.normalUsesTextView.setText(plant.getNormalUses() != null ? plant.getNormalUses() : "N/A");
            holder.traditionalUseTextView.setText(plant.getTraditionalUse() != null ? plant.getTraditionalUse() : "N/A");
            holder.medicalUsesTextView.setText(plant.getMedicalUses() != null ? plant.getMedicalUses() : "N/A");
            holder.preparationTypeTextView.setText(plant.getPreparationType() != null ? plant.getPreparationType() : "N/A");
            holder.descriptionTextView.setText(plant.getDescription() != null ? plant.getDescription() : "N/A");
            holder.heightTextView.setText(plant.getPlantHeight() != null ? plant.getPlantHeight() : "N/A");

            // Setup RecyclerView for images
            ImageAdapter imageAdapter = new ImageAdapter(context, plant.getImagePaths());
            holder.imagesRecyclerView1.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.imagesRecyclerView1.setAdapter(imageAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return plantDetailsList.size();
    }

    static class PlantDetailViewHolder extends RecyclerView.ViewHolder {
        TextView englishNameTextView;
        TextView nepaliNameTextView;
        TextView scientificNameTextView;
        TextView plantCategoryTextView;
        TextView partUsedTextView;
        TextView tharuNameTextView;
        TextView localNameTextView;
        TextView normalUsesTextView;
        TextView traditionalUseTextView;
        TextView medicalUsesTextView;
        TextView preparationTypeTextView;
        TextView descriptionTextView;
        TextView heightTextView;
        RecyclerView imagesRecyclerView1;

        PlantDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            englishNameTextView = itemView.findViewById(R.id.englishNameTextView);
            nepaliNameTextView = itemView.findViewById(R.id.nepaliNameTextView);
            scientificNameTextView = itemView.findViewById(R.id.scientificNameTextView);
            plantCategoryTextView = itemView.findViewById(R.id.plantCategoryTextView);
            partUsedTextView = itemView.findViewById(R.id.partUsedTextView);
            tharuNameTextView = itemView.findViewById(R.id.tharuNameTextView);
            localNameTextView = itemView.findViewById(R.id.localNameTextView);
            normalUsesTextView = itemView.findViewById(R.id.normalUsesTextView);
            traditionalUseTextView = itemView.findViewById(R.id.traditionalUseTextView);
            medicalUsesTextView = itemView.findViewById(R.id.medicalUsesTextView);
            preparationTypeTextView = itemView.findViewById(R.id.preparationTypeTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            heightTextView = itemView.findViewById(R.id.heightTextView);
            imagesRecyclerView1 = itemView.findViewById(R.id.imagesRecyclerView);
        }
    }
}
