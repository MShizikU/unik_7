package ru.sidorov.prakt5.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sidorov.prakt5.model.FileEntity;

//Sidorov SD
@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {
}
