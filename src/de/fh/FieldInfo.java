package de.fh;

import java.awt.*;

public class FieldInfo implements IDrawableField   {
    private WorldInformation wi;
    private int x;
    private int y;
    private boolean isBreeze = false;
    private boolean isPit = false;
    private boolean isWall = false;
    private boolean canBeWall = false;
    private boolean visited = false;

    public FieldInfo(int x, int y, WorldInformation wi){
        this.x = x;
        this.y = y;
        this.wi = wi;
        if(x == 0 || y == 0) setWall(true);
    }

    public boolean isBreeze(){
        return isBreeze;
    }

    public void setBreeze(boolean value){
        this.isBreeze = value;
    }

    public boolean canBePit(){
        if(visited) return false;
        boolean canBePit = false;
        boolean hasBreeze = false;
        for (DIRECTION direction :
                DIRECTION.values()) {

            if(wi.getInfo(x + direction.xOffset, y + direction.yOffset).isBreeze()){
                if(!hasBreeze){
                    canBePit = true;
                    hasBreeze = true;
                }
            }
            if(wi.getInfo(x + direction.xOffset, y + direction.yOffset).isVisited() &&
                    !wi.getInfo(x + direction.xOffset, y + direction.yOffset).isBreeze()){
                canBePit = false;
            }
        }
        return canBePit;
    }

    public void setWall(boolean value){
        this.isWall = value;
    }

    public boolean isWall (){
        return isWall;
    }

    public boolean canBeWall(){
        if(visited) return false;
        if(isWall) return true;
        return canBeWall;
    }

    public void setCanBeWall(boolean value){
        canBeWall = value;
    }

    public void visit(){
        visited = true;
    }

    public boolean isVisited(){
        return visited;
    }


    @Override
    public void drawField(Graphics g, int maxX, int maxY) {
        if(wi.getCurrX() == x && wi.getCurrY() == y){
            g.setColor(Color.blue);
            g.fillRect(4,4,24,24);
            return;
        }

        if(visited){
            g.setColor(Color.white);
            g.fillRect(0,0,32,32);
            return;
        }


        if(isWall()){
            g.drawImage(WorldVisualizerPane.IMAGE_WALL, 0, 0, null);
            return;
        }

        if(canBeWall()){
            g.drawImage(WorldVisualizerPane.IMAGE_WALL, 0, 0, null);
            g.drawImage(WorldVisualizerPane.IMAGE_UNKNOWN, 0, 0, null);
            return;
        }

        g.drawImage(WorldVisualizerPane.IMAGE_UNKNOWN, 0, 0, null);

    }
}
