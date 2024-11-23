package sidorov.prakt4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sidorov.prakt4.model.Hat;

@Repository
public interface HatRepository extends JpaRepository<Hat, Integer> {
    Hat findHatById(Long id);
}
