package com.Synchrome.collabcontent.opensearch.repository;


import com.Synchrome.collabcontent.opensearch.domain.tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface tagRepository extends JpaRepository<tag, Long> {
    Optional<tag> findByName(String name);
    List<tag> findByNameContainingIgnoreCase(String keyword);
}
