package np.edu.nast.vrikshagyanserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import np.edu.nast.vrikshagyanserver.entity.ProfileImage;
import np.edu.nast.vrikshagyanserver.entity.plants.PlantImage;

public interface PlantImageRepository extends JpaRepository<PlantImage, Long> {

	Optional<PlantImage> findFirstByOrderByPlant_PlantIdDesc();

}
