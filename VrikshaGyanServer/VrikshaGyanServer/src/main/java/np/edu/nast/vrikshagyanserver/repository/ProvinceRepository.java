package np.edu.nast.vrikshagyanserver.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import np.edu.nast.vrikshagyanserver.entity.Province;


@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long>{

}