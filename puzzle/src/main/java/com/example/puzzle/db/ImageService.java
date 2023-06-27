package com.example.puzzle.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Optional<Image> getFirstImage() {
        return imageRepository.findAll().stream().findFirst();
    }

    public void saveImage(Image image) {
        imageRepository.save(image);
    }

}
