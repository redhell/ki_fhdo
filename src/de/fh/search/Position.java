package de.fh.search;

import de.fh.util.DIRECTION;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int calculateDistanceTo(Position position){
        int distance = 0;
        distance += Math.abs(position.getX() - x);
        distance +=Math.abs(position.getY() - y);
        return distance;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Position getNewPosition(DIRECTION dir){
        return new Position(x + dir.xOffset, y+dir.yOffset);
    }

    public void move(DIRECTION dir){
        x += dir.xOffset;
        y += dir.yOffset;
    }

    public boolean isValid(){
        return x > 0 && y > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
