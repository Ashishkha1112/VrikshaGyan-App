package np.edu.nast.vrikshagyanserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import np.edu.nast.vrikshagyanserver.entity.plants.Plants;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;
    
    @Column(length = 2000)
    private String comment;
    
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    @JsonIgnore
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "plantId", nullable = false)
    @JsonIgnore
    private Plants plant;
}
