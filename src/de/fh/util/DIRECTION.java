package de.fh.util;

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

    public static DIRECTION turnRight(DIRECTION dir){
        switch (dir){
            case UP:
                return RIGHT;
            case DOWN:
                return LEFT;
            case RIGHT:
                return DOWN;
            case LEFT:
                return UP;
        }
        return null;
    }

    public static DIRECTION turnLeft(DIRECTION dir){
        switch (dir){
            case UP:
                return LEFT;
            case DOWN:
                return RIGHT;
            case RIGHT:
                return UP;
            case LEFT:
                return DOWN;
        }
        return null;
    }
}
