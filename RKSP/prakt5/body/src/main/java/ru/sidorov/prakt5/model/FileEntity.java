package ru.sidorov.prakt5.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Sidorov SD
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String fileName;
    private Long fileSize;
    @Lob
    private byte[] fileData;
}

