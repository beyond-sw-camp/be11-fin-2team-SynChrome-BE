package com.Synchrome.collabcontent.canvas.repository;

import com.Synchrome.collabcontent.canvas.domain.Canvas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanvasRepository extends JpaRepository<Canvas, Long> {


}