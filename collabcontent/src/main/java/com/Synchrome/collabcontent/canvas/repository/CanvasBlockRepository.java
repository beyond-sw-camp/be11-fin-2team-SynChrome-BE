package com.Synchrome.collabcontent.canvas.repository;

import com.Synchrome.collabcontent.canvas.domain.CanvasBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CanvasBlockRepository extends JpaRepository<CanvasBlock, Long> {
    List<CanvasBlock> findAllByCanvasIdOrderByOrderKeyAsc(Long canvasId);

    void deleteAllByCanvasId(Long canvasId);
}
