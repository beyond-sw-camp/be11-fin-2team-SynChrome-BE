package com.Synchrome.workspace.calendar.repository;

import com.Synchrome.workspace.calendar.domain.ColorCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColorCategoryRepository extends JpaRepository<ColorCategory, Long> {
    List<ColorCategory> findByUserId(Long userId);
}
