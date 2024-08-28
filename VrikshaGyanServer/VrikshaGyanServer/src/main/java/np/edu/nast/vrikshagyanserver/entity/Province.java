package np.edu.nast.vrikshagyanserver.entity;





import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Province {
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long provinceId;
	    private String provinceName;

	    
   
    // Getters and setters
}