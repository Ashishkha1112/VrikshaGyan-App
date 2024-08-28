package np.edu.nast.vrikshagyanserver.repository;


import np.edu.nast.vrikshagyanserver.entity.Review;
import np.edu.nast.vrikshagyanserver.entity.plants.Plants;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findByPlant(Plants plant);
 
}
