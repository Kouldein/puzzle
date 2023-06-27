package com.example.puzzle;

import java.util.List;

public class SolverDTO {
    List<byte[]> puzzlePieces;
    String contentType;

    public SolverDTO(List<byte[]> puzzlePieces, String contentType) {
        this.puzzlePieces = puzzlePieces;
        this.contentType = contentType;
    }

    public List<byte[]> getPuzzlePieces() {
        return puzzlePieces;
    }

    public void setPuzzlePieces(List<byte[]> puzzlePieces) {
        this.puzzlePieces = puzzlePieces;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
