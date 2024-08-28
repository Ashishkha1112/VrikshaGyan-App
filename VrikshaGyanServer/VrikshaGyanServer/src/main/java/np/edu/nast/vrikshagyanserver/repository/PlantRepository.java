package np.edu.nast.vrikshagyanserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import np.edu.nast.vrikshagyanserver.entity.plants.Plants;

public interface PlantRepository extends JpaRepository<Plants, Long> {
	
	Plants findByEnglishName(String englishName);
	//Optional<Plants> findByEnglishName(String englishName);
	boolean existsByEnglishName(String englishName);
	List<Plants> findByStatus(int i);
	List<Plants> findByStatusAndIsDeleted(int status, boolean isDeleted);
	List<Plants> findByisDeleted(boolean isDeleted);

	@Query("SELECT COUNT(p) FROM Plants p")
    long countTotalPlants();

    @Query("SELECT COUNT(p) FROM Plants p WHERE p.status = 1")
    long countVerifiedPlants();

    @Query("SELECT COUNT(p) FROM Plants p WHERE p.status = 0")
    long countUnverifiedPlants();
    @Query("SELECT p FROM Plants p WHERE " +
		       "(:englishName IS NULL OR p.englishName LIKE %:englishName%) OR " +
		       "(:nepaliName IS NULL OR p.nepaliName LIKE %:nepaliName%) OR " +
		       "(:scientificName IS NULL OR p.scientificName LIKE %:scientificName%) OR " +
		       "(:plantCategory IS NULL OR p.plantCategory LIKE %:plantCategory%) OR " +
		       "(:partUsed IS NULL OR p.partUsed LIKE %:partUsed%) OR " +
		       "(:tharuName IS NULL OR p.tharuName LIKE %:tharuName%) OR " +
		       "(:localName IS NULL OR p.localName LIKE %:localName%) OR " +
		       "(:traditionalUse IS NULL OR p.traditionalUse LIKE %:traditionalUse%) OR " +
		       "(:medicalUses IS NULL OR p.medicalUses LIKE %:medicalUses%) OR " +
		       "(:preparationType IS NULL OR p.preparationType LIKE %:preparationType%) OR " +
		       "(:description IS NULL OR p.description LIKE %:description%) OR " +
		       "(:normalUses IS NULL OR p.normalUses LIKE %:normalUses%) AND " +
		       "p.status = 1 AND p.isDeleted = false")
		List<Plants> searchPlants(@Param("englishName") String englishName,
		                          @Param("nepaliName") String nepaliName,
		                          @Param("scientificName") String scientificName,
		                          @Param("plantCategory") String plantCategory,
		                          @Param("partUsed") String partUsed,
		                          @Param("tharuName") String tharuName,
		                          @Param("localName") String localName,
		                          @Param("traditionalUse") String traditionalUse,
		                          @Param("medicalUses") String medicalUses,
		                          @Param("preparationType") String preparationType,
		                          @Param("description") String description,
		                          @Param("normalUses") String normalUses);

	
    Optional<Plants> findByScientificName(String scientificName);

	Optional<Plants> findByTharuName(String tharuName);
}
