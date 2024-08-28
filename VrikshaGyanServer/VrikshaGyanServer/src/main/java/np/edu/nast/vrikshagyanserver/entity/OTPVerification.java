package np.edu.nast.vrikshagyanserver.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class OTPVerification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	private String otp;
	private String email;
	private LocalDateTime createdAt; // New field to store the creation timestamp


	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

}
