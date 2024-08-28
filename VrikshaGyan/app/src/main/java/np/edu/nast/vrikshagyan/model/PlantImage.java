package np.edu.nast.vrikshagyan.model;


public class PlantImage {
    private Long id;
    private Long plantId; // Reference to the Plant entity
    private String imagePath;

    // Constructors, Getters, and Setters
    public PlantImage() {}

    public PlantImage(Long id, Long plantId, String imagePath) {
        this.id = id;
        this.plantId = plantId;
        this.imagePath = imagePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlantId() {
        return plantId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}