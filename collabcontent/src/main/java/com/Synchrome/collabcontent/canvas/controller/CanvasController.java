package com.Synchrome.collabcontent.canvas.controller;

import com.Synchrome.collabcontent.canvas.dto.CanvasCreateReqDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasListResDto;
import com.Synchrome.collabcontent.canvas.dto.CanvasSaveReqDto;
import com.Synchrome.collabcontent.canvas.service.CanvasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/canvas")
public class CanvasController {
    private final CanvasService canvasService;

    public CanvasController(CanvasService canvasService) {
        this.canvasService = canvasService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCanvas(@RequestBody CanvasCreateReqDto request) {
        Long id = canvasService.createCanvas(request);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PostMapping("/{canvasId}/save")
    public ResponseEntity<?> saveJson(@RequestBody CanvasSaveReqDto request) {
        canvasService.saveCanvasBlocks(request);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }

    @GetMapping("/{canvasId}")
    public ResponseEntity<?> loadCanvas(@PathVariable Long canvasId) {
        Map<String, Object> map = canvasService.loadCanvasContent(canvasId);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCanvasList() {
        List<CanvasListResDto> canvasList = canvasService.getCanvasList();
        return new ResponseEntity<>(canvasList, HttpStatus.OK);
    }
}
