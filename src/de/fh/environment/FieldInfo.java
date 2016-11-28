package de.fh.environment;

import de.fh.gui.IDrawableField;
import de.fh.gui.WorldVisualizerPane;
import de.fh.util.DIRECTION;

import java.awt.*;

public class FieldInfo implements IDrawableField {
    private WorldInformation worldInformation;
    private int x;
    private int y;
    private boolean isBreeze = false;
    private boolean isWall = false;
    private boolean canBeWall = false;
    private boolean visited = false;

    public FieldInfo(int x, int y, WorldInformation wi){
        this.x = x;
        this.y = y;
        this.worldInformation = wi;
        if(x == 0 || y == 0) setWall();
    }

    public boolean isBreeze(){
        return isBreeze;
    }

    public void setBreeze(){
        this.isBreeze = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean canBePit(){
        if(visited) return false;
        boolean canBePit = false;
        boolean hasBreeze = false;
        for (DIRECTION direction :
                DIRECTION.values()) {

            FieldInfo field = worldInformation.getInfo(x + direction.xOffset, y + direction.yOffset);
            if(field == null) continue;
            if(field.isBreeze()){
                if(!hasBreeze){
                    canBePit = true;
                    hasBreeze = true;
                }
            }
            if(worldInformation.getInfo(x + direction.xOffset, y + direction.yOffset).isVisited() &&
                    !worldInformation.getInfo(x + direction.xOffset, y + direction.yOffset).isBreeze()){
                canBePit = false;
            }
        }
        return canBePit;
    }

    public void setWall(){
        this.isWall = true;
    }

    public boolean isWall (){
        return isWall;
    }

    public boolean canBeWall() {
        if (visited) return false;
        return isWall || canBeWall;
    }

    public void setCanBeWall(){
        canBeWall = true;
    }

    public void visit(){
        visited = true;
    }

    public boolean isVisited(){
        return visited;
    }


    @Override
    public void drawField(Graphics g) {
        if(worldInformation.getCurrX() == x && worldInformation.getCurrY() == y){
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
