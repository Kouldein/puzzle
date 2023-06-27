package com.example.puzzle;

import com.example.puzzle.db.Image;
import com.example.puzzle.db.ImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
public class PuzzleController {
    private final ImageService imageService;
    private final ImageSplitter imageSplitter;

    public PuzzleController(ImageService imageService, ImageSplitter imageSplitter) {
        this.imageService = imageService;
        this.imageSplitter = imageSplitter;
    }

    @PostMapping("/images")
    public ResponseEntity<String> uploadOrUpdateImage(@RequestParam("file") MultipartFile file) {
        try {
            Image image = new Image();
            image.setName(file.getOriginalFilename());
            image.setData(file.getBytes());
            image.setContentType(file.getContentType());

            Optional<Image> existingImageOptional = imageService.getFirstImage();
            if (existingImageOptional.isPresent()) {
                Image existingImage = existingImageOptional.get();
                existingImage.setName(image.getName());
                existingImage.setData(image.getData());
                existingImage.setContentType(image.getContentType());
                imageService.saveImage(existingImage);
            } else {
                imageService.saveImage(image);
            }

            return ResponseEntity.ok().body("{\"message\": \"Image uploaded or updated successfully\"}");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload or update image");
        }
    }


    @GetMapping("/images/{cuts}")
    public ResponseEntity<PuzzlePiece[]> getFirstImage(@PathVariable("cuts") int cuts) throws IOException {
        Optional<Image> imageOptional = imageService.getFirstImage();

        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            PuzzlePiece[] puzzlePieces = imageSplitter.splitImageFromBlob(image.getData(), image.getContentType(),cuts, cuts);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE));

            return new ResponseEntity<>(puzzlePieces, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
