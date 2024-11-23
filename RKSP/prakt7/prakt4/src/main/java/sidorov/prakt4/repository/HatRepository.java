package sidorov.prakt4.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import sidorov.prakt4.model.Hat;

@Repository
public interface HatRepository extends R2dbcRepository<Hat, Long> {
}
