package np.edu.nast.vrikshagyanserver.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import np.edu.nast.vrikshagyanserver.entity.Address;
import np.edu.nast.vrikshagyanserver.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//	public Optional<User> findByEmail(String username);
	User findByEmail(String email);

	List<User> findByAddress(Address address);
	List<User> findByPhone(String phone);
	List<User> findByIsDeleted(boolean isDeleted);
	boolean existsByEmail(String email);
	boolean existsByPhone(String phone);

	@Query("SELECT COUNT(u) FROM User u")
	long countUsers();

	@Query("SELECT COUNT(u) FROM User u WHERE u.status = true")
	long countVerifiedUsers();

	@Query("SELECT COUNT(u) FROM User u WHERE u.status = false")
	long countUnverifiedUsers();
	
	@Query("SELECT COUNT(u) FROM User u WHERE u.isDeleted = false")
	long countActiveUsers();
}