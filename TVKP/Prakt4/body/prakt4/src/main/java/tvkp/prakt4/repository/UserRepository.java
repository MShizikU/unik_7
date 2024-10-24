package tvkp.prakt4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tvkp.prakt4.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
