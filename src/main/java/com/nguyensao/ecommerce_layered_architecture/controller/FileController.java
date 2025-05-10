package com.nguyensao.ecommerce_layered_architecture.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nguyensao.ecommerce_layered_architecture.service.FileService;

@RestController
@RequestMapping("/api/v1/public/file")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload-image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile image)
            throws IOException {
        String imageUrl = fileService.uploadImage(image);
        Map<String, String> response = new HashMap<>();
        response.put("data", imageUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/upload-images", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, List<String>>> uploadImages(@RequestParam("file") MultipartFile[] image)
            throws IOException {
        List<String> imageUrls = fileService.uploadMultipleImages(image);
        Map<String, List<String>> response = new HashMap<>();
        response.put("data", imageUrls);
        return ResponseEntity.ok(response);
    }

}
