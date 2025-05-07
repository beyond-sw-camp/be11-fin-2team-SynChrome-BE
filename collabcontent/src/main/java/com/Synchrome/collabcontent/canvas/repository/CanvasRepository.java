package com.Synchrome.collabcontent.canvas.repository;

import com.Synchrome.collabcontent.canvas.domain.Canvas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CanvasRepository extends JpaRepository<Canvas, Long> {


    List<Canvas> findByChannelId(Long channelId);
}