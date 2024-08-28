package np.edu.nast.vrikshagyanserver.entity.plants;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity(name = "plant_images")

public class PlantImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plants plant;

    @Column(name = "image_path")
   // @NotBlank(message = "Please Select at least 1 image")
    private String imagePath;

}