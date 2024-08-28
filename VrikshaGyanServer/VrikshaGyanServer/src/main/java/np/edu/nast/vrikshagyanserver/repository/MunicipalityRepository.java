package np.edu.nast.vrikshagyanserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import np.edu.nast.vrikshagyanserver.entity.Municipality;
@Repository
public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {

	

	List<Municipality> findByDistrictDistrictId(Long districtId);

}
