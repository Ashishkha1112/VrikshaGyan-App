package np.edu.nast.vrikshagyanserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import np.edu.nast.vrikshagyanserver.entity.UserAuditLog;

public interface UserAuditLogRespository extends JpaRepository<UserAuditLog, Long> {

}
