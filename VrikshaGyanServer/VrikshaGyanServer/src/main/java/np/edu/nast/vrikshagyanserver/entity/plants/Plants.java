package np.edu.nast.vrikshagyanserver.entity.plants;

import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Table(name = "plants")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Plants {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plant_id")
    private Long plantId;
    
    @NotBlank(message = "English Name is Mandatory")
    @Size(min=2, max=30, message ="Name size must not exceed 30 characters ")
  //  @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
    @Column(nullable = false, name = "english_name")
    private String englishName;

 //   @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
    @Column(name = "nepali_name")
    @NotBlank(message = "Nepali Name is Mandatory")
    @Size(min=2, max=30, message ="Name size must not exceed 30 characters ")
    private String nepaliName;

 //   @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
    @Column(name = "tharu_name")
    private String tharuName;

 //   @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
    @Column(name = "local_name")
    private String localName;

 //   @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
    @Column(nullable = true,name = "scientific_name")
  //  @NotBlank(message = "Scientific Name is Mandatory")
 //   @Size(min=2, max=30, message ="Name size must not exceed 30 characters ")
    private String scientificName;
    
 //   @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
    @NotBlank(message = "Plant Category is Mandatory")
    @Size(min=2, max=30, message ="Category size must not exceed 30 characters ")
    @Column(nullable= false, name = "plant_category")
    private String plantCategory;
    
    @NotBlank(message = "Part Used is Mandatory")
    @Size(min=2, max=30, message ="Part used size must not exceed 30 characters ")
 //   @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
    @Column(nullable = false,name = "part_used")
    private String partUsed;
    
//  @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
  //  @NotBlank(message = "Normal Uses is Mandatory")
 //   @Size(min=2, max=30, message ="size must not exceed 30 characters")
    @Column(nullable= true,name = "normal_uses", length = 255)
    private String normalUses;
    
    
 //   @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
//    @Size(min=2, max=30, message ="size must not exceed 30 characters")
//    @NotBlank(message="Traditional Uses is Mandatory")
    @Column(name = "traditional_use", length = 255)
    private String traditionalUse;
    
//    @Pattern(regexp="^[A-Za-z]*$",message = "Invalid Input")
//    @Size(min=2, max=30, message ="size must not exceed 30 characters")
//    @NotBlank(message="medicinal Uses is Mandatory")
    @Column(name = "medical_uses", length = 255)
    private String medicalUses;

//    @Size(min=2, max=30, message ="size must not exceed 30 characters")
//    @NotBlank(message="prepration types is Mandatory")
    @Column(name = "preparation_type")
    private String preparationType;
    
    @Column(nullable = true, name = "plant_height")
//    @NotBlank(message = "Plant Height is Mandatory")
//    @Size(min=1, max= 30, message ="size cannot exceed 30 characters")
    private String plantHeight;
    
    @Column(nullable=false)
   // @Size(min= 10 , message ="size cannot exceed 5000 characters")
  //  @NotBlank(message = "Description is Mandatory")
    private String description;
    
    @Column
    private int status; 
    
    @Column
    private boolean isDeleted;
    
    @ElementCollection
    @CollectionTable(name = "plant_images", joinColumns = @JoinColumn(name = "plant_id"))
    @Column(name = "image_path")
    private List<String> imagePaths;

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

}
