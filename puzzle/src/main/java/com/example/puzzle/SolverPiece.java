package com.example.puzzle;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class SolverPiece {
    BufferedImage piecesToWorkWith;
    byte[] actualPieces;

    public SolverPiece(BufferedImage piecesToWorkWith, byte[] actualPieces) {
        this.piecesToWorkWith = piecesToWorkWith;
        this.actualPieces = actualPieces;
    }

    public BufferedImage getPiecesToWorkWith() {
        return piecesToWorkWith;
    }

    public void setPiecesToWorkWith(BufferedImage piecesToWorkWith) {
        this.piecesToWorkWith = piecesToWorkWith;
    }

    public byte[] getActualPieces() {
        return actualPieces;
    }

    public void setActualPieces(byte[] actualPieces) {
        this.actualPieces = actualPieces;
    }

    @Override
    public String toString() {
        return "1";
    }
}
