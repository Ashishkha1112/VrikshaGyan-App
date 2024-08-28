package np.edu.nast.vrikshagyanserver.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import np.edu.nast.vrikshagyanserver.entity.plants.Plants;
import np.edu.nast.vrikshagyanserver.repository.PlantRepository;

@RestController
@CrossOrigin(origins = "*")
public class SearchController {
	String path = "C:/Users/rakes/eclipse-workspace/VrikshaGyanServer/VrikshaGyanServer/src/main/resources/static/";
	@Autowired
	PlantRepository plantRepository;

	 @GetMapping("/search")
	    public ResponseEntity<List<Plants>> searchPlants(
	            @RequestParam(required = false) String englishName,
	            @RequestParam(required = false) String nepaliName,
	            @RequestParam(required = false) String scientificName,
	            @RequestParam(required = false) String plantCategory,
	            @RequestParam(required = false) String partUsed,
	            @RequestParam(required = false) String tharuName,
	            @RequestParam(required = false) String localName,
	            @RequestParam(required = false) String traditionalUse,
	            @RequestParam(required = false) String medicalUses,
	            @RequestParam(required = false) String preparationType,
	            @RequestParam(required = false) String description,
	            @RequestParam(required = false) String normalUses) {

	        try {
	            List<Plants> plants = plantRepository.searchPlants(
	                    englishName, nepaliName, scientificName, plantCategory, partUsed, tharuName, localName,
	                    traditionalUse, medicalUses, preparationType, description, normalUses);

	            // Iterate through each plant and convert its image paths to Base64
	            for (Plants plant : plants) {
	                List<String> base64Images = new ArrayList<>();
	                if (plant.getImagePaths() != null && !plant.getImagePaths().isEmpty()) {
	                    for (String imagePath : plant.getImagePaths()) {
	                        try {
	                            String fullImagePath = path + imagePath; // For each image path
	                            byte[] imageBytes = Files.readAllBytes(Paths.get(fullImagePath));
	                            String base64Image = Base64Utils.encodeToString(imageBytes);
	                            base64Images.add(base64Image);
	                        } catch (Exception e) {
	                            e.printStackTrace(); // Log the exception
	                            base64Images.add(""); // Add an empty string or handle as needed
	                        }
	                    }
	                    plant.setImagePaths(base64Images); // Assuming setImagePaths expects List<String>
	                }
	            }
	            return new ResponseEntity<>(plants, HttpStatus.OK);
	        } catch (Exception e) {
	            e.printStackTrace(); // Log the exception if needed
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
}
