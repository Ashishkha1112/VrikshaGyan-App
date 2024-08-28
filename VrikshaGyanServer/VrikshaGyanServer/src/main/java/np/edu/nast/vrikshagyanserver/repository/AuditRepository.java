package np.edu.nast.vrikshagyanserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import np.edu.nast.vrikshagyanserver.entity.AuditLog;

public interface AuditRepository extends JpaRepository<AuditLog, Long>{

}
