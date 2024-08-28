package np.edu.nast.vrikshagyan.model;

import java.util.List;

/**
 * Represents a plant entity with details such as name, category, and images.
 */
public class Plants {

    private Long plantId;
    private String englishName;
    private String nepaliName;
    private String tharuName;
    private String localName;
    private String scientificName;
    private String plantCategory;
    private String partUsed;
    private String normalUses;
    private String plantHeight;
    private String traditionalUse;
    private String medicalUses;
    private String preparationType;
    private String description;
    private int status;
    private boolean isDeleted;
    private List<String> imagePaths;

    // Default constructor
    public Plants() {}

    // Constructor for creating a new plant with essential fields
    public Plants(Long plantId,String englishName, String nepaliName, String normalUses, List<String> imagePaths) {
        this.englishName = englishName;
        this.nepaliName = nepaliName;
        this.normalUses = normalUses;
        this.imagePaths = imagePaths;
        this.plantId= plantId;
    }

    // Full constructor
    public Plants(Long plantId, String englishName, String nepaliName, String tharuName, String localName,
                  String scientificName, String plantCategory, String partUsed, String normalUses,
                  String traditionalUse, String medicalUses, String preparationType, String plantHeight,
                  String description, int status, boolean isDeleted, List<String> imagePaths) {
        this.plantId = plantId;
        this.englishName = englishName;
        this.nepaliName = nepaliName;
        this.tharuName = tharuName;
        this.localName = localName;
        this.scientificName = scientificName;
        this.plantCategory = plantCategory;
        this.partUsed = partUsed;
        this.normalUses = normalUses;
        this.traditionalUse = traditionalUse;
        this.medicalUses = medicalUses;
        this.preparationType = preparationType;
        this.plantHeight = plantHeight;
        this.description = description;
        this.status = status;
        this.isDeleted = isDeleted;
        this.imagePaths = imagePaths;
    }

    // Getters and Setters

    public Long getPlantId() {
        return plantId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getNepaliName() {
        return nepaliName;
    }

    public void setNepaliName(String nepaliName) {
        this.nepaliName = nepaliName;
    }

    public String getTharuName() {
        return tharuName;
    }

    public void setTharuName(String tharuName) {
        this.tharuName = tharuName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getPlantCategory() {
        return plantCategory;
    }

    public void setPlantCategory(String plantCategory) {
        this.plantCategory = plantCategory;
    }

    public String getPartUsed() {
        return partUsed;
    }

    public void setPartUsed(String partUsed) {
        this.partUsed = partUsed;
    }

    public String getNormalUses() {
        return normalUses;
    }

    public void setNormalUses(String normalUses) {
        this.normalUses = normalUses;
    }

    public String getPlantHeight() {
        return plantHeight;
    }

    public void setPlantHeight(String plantHeight) {
        this.plantHeight = plantHeight;
    }

    public String getTraditionalUse() {
        return traditionalUse;
    }

    public void setTraditionalUse(String traditionalUse) {
        this.traditionalUse = traditionalUse;
    }

    public String getMedicalUses() {
        return medicalUses;
    }

    public void setMedicalUses(String medicalUses) {
        this.medicalUses = medicalUses;
    }

    public String getPreparationType() {
        return preparationType;
    }

    public void setPreparationType(String preparationType) {
        this.preparationType = preparationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
}
