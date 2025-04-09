package com.Synchrome.workspace.drive.repository;


import com.Synchrome.workspace.drive.domain.document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface documentRepository extends JpaRepository<document, Long> {
    List<document> findByTags_Name(String name);
}
