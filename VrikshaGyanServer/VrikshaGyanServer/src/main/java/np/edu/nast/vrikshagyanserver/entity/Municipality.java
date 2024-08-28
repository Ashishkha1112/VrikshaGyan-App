package np.edu.nast.vrikshagyanserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Municipality {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long municipalityId;
	    private String municipalityName;

	    @ManyToOne
	    @JoinColumn(name = "district_id")
	    private District district;
}
