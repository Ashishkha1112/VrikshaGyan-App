package np.edu.nast.vrikshagyanserver.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import np.edu.nast.vrikshagyanserver.entity.ProfileImage;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

	Optional<ProfileImage> findByUserUserId(Long userId);

}
