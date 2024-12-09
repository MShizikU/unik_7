package mirea.sidorov.auth.repository;

import mirea.sidorov.auth.model.Token;
import mirea.sidorov.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    void deleteByUser(User user);
}
