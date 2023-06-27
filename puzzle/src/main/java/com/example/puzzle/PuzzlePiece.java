package com.example.puzzle;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class PuzzlePiece {
    private byte[] imageData;
    private int rotation = 0;
    private String contentType;
    private String name;
    private int position;

    public PuzzlePiece(byte[] imageData) {
        this.imageData = imageData;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "PuzzlePiece{" +
                "imageData=" + Arrays.toString(imageData) +
                ", rotation=" + rotation +
                ", contentType='" + contentType + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}

