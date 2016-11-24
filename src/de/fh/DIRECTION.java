package de.fh;

public enum DIRECTION {
    UP(0,-1),
    DOWN(0,1),
    RIGHT(1,0),
    LEFT(-1,0);

    public final int xOffset;
    public final int yOffset;

    DIRECTION(int xOffset, int yOffset){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
}
