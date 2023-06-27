package com.example.puzzle;

import com.example.puzzle.db.Image;
import com.example.puzzle.db.ImageService;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class ImageSplitter {

    private final ImageService imageService;

    public ImageSplitter(ImageService imageService) {
        this.imageService = imageService;
    }
    public static String getFileExtension(String contentType) {
        switch (contentType) {
            case "image/jpg":
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            default:
                return "";
        }
    }
    public static byte[] convertToByteArray(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        baos.flush();
        byte[] byteArray = baos.toByteArray();
        baos.close();
        return byteArray;
    }

    public PuzzlePiece[] splitImageFromBlob(byte[] blobData, String contentType, int rows, int columns) throws IOException {
        Optional<Image> image = imageService.getFirstImage();
        if (image.isPresent()) {
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(image.get().getData()));

            int pieceWidth = originalImage.getWidth() / columns;
            int pieceHeight = originalImage.getHeight() / rows;

            PuzzlePiece[] puzzlePieces = new PuzzlePiece[rows * columns];

            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    int x = column * pieceWidth;
                    int y = row * pieceHeight;

                    BufferedImage pieceImage = originalImage.getSubimage(x, y, pieceWidth, pieceHeight);
                    PuzzlePiece puzzlePiece = new PuzzlePiece(convertToByteArray(pieceImage, getFileExtension(contentType)));
                    puzzlePiece.setContentType(contentType);
                    puzzlePiece.setName("piece" + String.valueOf(((rows) * (row))+column) + "." + getFileExtension(contentType));
                    puzzlePiece.setPosition(row * columns + column);
                    puzzlePieces[row * columns + column] = puzzlePiece;
                }
            }

            return puzzlePieces;
        }
        return null;
    }
}