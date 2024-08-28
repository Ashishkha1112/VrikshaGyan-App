package np.edu.nast.vrikshagyanserver.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import np.edu.nast.vrikshagyanserver.entity.OTPVerification;

@Repository
public interface OTPVerificationRepository extends JpaRepository<OTPVerification, Long> {

	List<OTPVerification> findByEmail(String email);
	void deleteByCreatedAtBefore(LocalDateTime expiryTime);


	void deleteByEmail(String email);

	List<OTPVerification> findAllByEmail(String email);

}
