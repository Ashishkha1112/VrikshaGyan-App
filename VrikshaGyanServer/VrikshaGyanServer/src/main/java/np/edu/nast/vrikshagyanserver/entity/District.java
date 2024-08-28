package np.edu.nast.vrikshagyanserver.entity;



import jakarta.persistence.*;
import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class District {
	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long districtId;
	    private String districtName;

	    @ManyToOne
	    @JoinColumn(name = "provinceId")
	    private Province province;

	    

}
