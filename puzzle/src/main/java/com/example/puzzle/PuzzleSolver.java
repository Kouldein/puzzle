package com.example.puzzle;

import com.example.puzzle.db.Image;
import com.example.puzzle.db.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class PuzzleSolver {
    ImageSplitter imageSplitter;
    ImageService imageService;
    List<SolverPiece> puzzlePieces;
    List<Integer> remainingPieces;
    SolverDTO solve;
    int cuts;

    public PuzzleSolver(ImageService imageService, ImageSplitter imageSplitter) {
        this.imageService = imageService;
        this.imageSplitter = imageSplitter;
    }

    public double compareSides(List<SolverPiece> solverPieces, BufferedImage center, int row, int col, int cuts) {

        double topAccuracy = 0.0;
        double bottomAccuracy = 0.0;
        double leftAccuracy = 0.0;
        double rightAccuracy = 0.0;

        double sideCounter = 0.0;

        if(row != 0) {
            topAccuracy = compareVerticalSide(center, 0, solverPieces.get((row - 1) * cuts + col).getPiecesToWorkWith(), solverPieces.get((row - 1) * cuts + col).getPiecesToWorkWith().getHeight() - 1);
            sideCounter += 1.0;
        }
        if(row != cuts-1 && solverPieces.size() == this.puzzlePieces.size()) {
            bottomAccuracy = compareVerticalSide(center, center.getHeight() - 1, solverPieces.get((row + 1) * cuts + col).getPiecesToWorkWith(), 0);
            sideCounter += 1.0;
        }
        if(col != 0) {
            System.out.println(row);
            System.out.println(col);
            leftAccuracy = compareHorizontalSide(center, 0, solverPieces.get(row * cuts + (col-1)).getPiecesToWorkWith(), solverPieces.get(row * cuts + (col-1)).getPiecesToWorkWith().getWidth()-1);
            sideCounter += 1.0;
        }
        if(col != cuts-1 && solverPieces.size() == this.puzzlePieces.size()) {
            rightAccuracy = compareHorizontalSide(center, center.getWidth() - 1, solverPieces.get(row * cuts + (col+1)).getPiecesToWorkWith(), 0);
            sideCounter += 1.0;
        }
        double overallAccuracy = (topAccuracy + bottomAccuracy + leftAccuracy + rightAccuracy) / sideCounter;

        return overallAccuracy;

    }

    private double compareHorizontalSide(BufferedImage side1, int index1, BufferedImage side2, int index2) {
        int length = side1.getHeight();
        long overallAccuracy = 0;
        for (int i = 0; i < length; i++) {
            overallAccuracy += Math.abs(side1.getRGB(index1, i) - side2.getRGB(index2, i));
        }

        double accuracy = (double) overallAccuracy / length;
        return accuracy;
    }

    private double compareVerticalSide(BufferedImage side1, int index1, BufferedImage side2, int index2) {
        int length = side1.getWidth();
        long overallAccuracy = 0;

        for (int i = 0; i < length; i++) {
            overallAccuracy += Math.abs(side1.getRGB(i, index1) - side2.getRGB(i, index2));
        }

        double accuracy = (double) overallAccuracy / length;
        return accuracy;
    }

    public List<SolverPiece> proceedNextStep(List<SolverPiece> pieces, List<SolverPiece> piecesRemaining){
        if(piecesRemaining.size() == 0){
            return pieces;
        }
        Map.Entry<SolverPiece, Double> pair = new AbstractMap.SimpleEntry<>(null, 9999999999.9);
        for(int i = 0; i < piecesRemaining.size(); i++){
            List<SolverPiece> temp = new ArrayList<>(pieces);
            temp.add(piecesRemaining.get(i));
            double accuracy = compareSides(temp, temp.get(temp.size() - 1).getPiecesToWorkWith(),(temp.size() - 1)/4, (temp.size() - 1)%4, 4);
            if(accuracy < pair.getValue()){
                pair = new AbstractMap.SimpleEntry<>(piecesRemaining.get(i), accuracy);
            }
        }
        pieces.add(pair.getKey());
        piecesRemaining.remove(pair.getKey());
        return proceedNextStep(pieces, piecesRemaining);
    }

    public List<SolverPiece> solvePuzzle() {
        List<SolverPiece> solverPieces = this.puzzlePieces;
        boolean isItBetter = true;
        int counter = 0;
        List<List<SolverPiece>> solved = new ArrayList<>();
        for (int i = 0; i < solverPieces.size(); i++) {
            ArrayList<SolverPiece> temp = new ArrayList<>();
            temp.add(this.puzzlePieces.get(i));
            ArrayList<SolverPiece> withoutFirst = new ArrayList<>(this.puzzlePieces);
            withoutFirst.remove(i);
            solved.add(proceedNextStep(temp, withoutFirst));
        }
        double overallAccuracy = 0.0;
        Map.Entry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(-1, 9999999999.9);
        for(int i = 0; i < solved.size(); i++){
            overallAccuracy = 0.0;
            for(int j = 0; j < solved.get(i).size(); j++){
                overallAccuracy += compareSides(solved.get(i),solved.get(i).get(j).getPiecesToWorkWith(),j/4,j%4,4);
            }
            if(overallAccuracy < pair.getValue()){
                pair = new AbstractMap.SimpleEntry<>(i, overallAccuracy);
            }
        }
        return solved.get(pair.getKey());
    }

    public static SolverPiece convertToTwoDimensionalByteArray(BufferedImage image, String format) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        baos.flush();
        byte[] array = baos.toByteArray();
        baos.close();

        SolverPiece solverPiece = new SolverPiece(image, array);

        return solverPiece;
    }


    public static String getFileExtension(String contentType) {
        switch (contentType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            default:
                return "";
        }
    }

    @GetMapping("/solve")
    public ResponseEntity<SolverDTO> returnSolve() throws IOException {
        int cuts = 4;
        this.cuts = cuts;
        Optional<Image> imageOptional = imageService.getFirstImage();
        List<Double> accuracy = new ArrayList<>();
        if (imageOptional.isPresent()) {
            this.puzzlePieces = new ArrayList<>();
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageOptional.get().getData()));

            int pieceWidth = originalImage.getWidth() / cuts;
            int pieceHeight = originalImage.getHeight() / cuts;

            for (int row = 0; row < cuts; row++) {
                for (int column = 0; column < cuts; column++) {
                    int x = column * pieceWidth;
                    int y = row * pieceHeight;

                    BufferedImage pieceImage = originalImage.getSubimage(x, y, pieceWidth, pieceHeight);
                    SolverPiece puzzlePiece = convertToTwoDimensionalByteArray(pieceImage, getFileExtension(imageOptional.get().getContentType()));
                    this.puzzlePieces.add(puzzlePiece);
                }
            }
            Collections.shuffle(this.puzzlePieces);
            List<SolverPiece> solved = solvePuzzle();
            this.solve = new SolverDTO(solved.stream().map(SolverPiece::getActualPieces).collect(Collectors.toList()), imageOptional.get().getContentType());

        }
        return new ResponseEntity<>(this.solve, HttpStatus.OK);
    }

}

















