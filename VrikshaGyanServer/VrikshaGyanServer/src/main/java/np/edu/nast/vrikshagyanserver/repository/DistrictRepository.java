package np.edu.nast.vrikshagyanserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import np.edu.nast.vrikshagyanserver.entity.District;
@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {

	

	List<District> findByProvinceProvinceId(Long provinceId);

}
