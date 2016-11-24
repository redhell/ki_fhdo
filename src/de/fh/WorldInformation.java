package de.fh;

import de.fh.connection.ActionEffect;
import de.fh.connection.wumpus.AgentAction;
import de.fh.connection.wumpus.AgentPercept;

public class WorldInformation implements IDrawableWorld{
    private WorldVisualizerPane gui;

    private FieldInfo[][] fieldData = new FieldInfo[3][3];

    private int maxX;
    private int maxY;



    private int currX;
    private int currY;
    private DIRECTION currDirection = DIRECTION.RIGHT;

    private AgentPercept lastPercept;
    private AgentAction lastAction;
    private int lastActionEffect;

    public WorldInformation(){
        this.maxX = 3;
        this.maxY = 3;
        currX = 1;
        currY = 1;
        initField();

    }

    public int getCurrX() {
        return currX;
    }

    public int getCurrY() {
        return currY;
    }

    public void initField(){
        for(int x = 0; x < 3; x++){
            for (int y = 0; y < 3; y++){
                fieldData[x][y] = new FieldInfo(x,y,this);
            }
        }
        fieldData[0][0].setWall(true);
        fieldData[0][1].setWall(true);
        fieldData[1][0].setWall(true);
        fieldData[1][1].visit();
    }

    public void update(AgentPercept percept, AgentAction lastAction, int lastActionEffect){
        this.lastPercept = percept;
        this.lastActionEffect = lastActionEffect;
        this.lastAction = lastAction;

        fixPosition();


        if(lastActionEffect == ActionEffect.INVALID_LOCATION) return;

        if(lastPercept.isBump()){
            for (DIRECTION dir :
                    DIRECTION.values()) {
                fieldData[currX + dir.xOffset][currY + dir.yOffset].setCanBeWall(true);
            }
        }

        if(lastPercept.isBreeze()){
            fieldData[currX][currY].setBreeze(true);
        }
    }

    public FieldInfo getInfo(int x, int y){
        return fieldData[x][y];
    }

    private void fixPosition(){
        if(lastAction == AgentAction.GO_FORWARD){
            if(lastActionEffect == ActionEffect.INVALID_LOCATION){
                fieldData[currX + currDirection.xOffset][currY + currDirection.yOffset].setWall(true);
            }else{
                currX += currDirection.xOffset;
                currY += currDirection.yOffset;
            }
        }
        if(currX > maxX - 2){
            maxX++;
            FieldInfo[][] tmp = fieldData;
            fieldData = new FieldInfo[maxX][maxY];
            System.arraycopy(tmp, 0, fieldData, 0, maxX -1);
            for(int y = 0; y < maxY; y++){
                fieldData[maxX-1][y] = new FieldInfo(maxX - 1, y, this);
            }
        }
        if(currY > maxY - 2){
            maxY++;
            FieldInfo[][] tmp = fieldData;
            fieldData = new FieldInfo[maxX][maxY];
            for(int x = 0; x < maxX; x++){
                System.arraycopy(tmp[x],0,fieldData[x],0,maxY-1);
                fieldData[x][maxY-1] = new FieldInfo(x,maxY-1, this);
            }
        }
        fieldData[currX][currY].visit();

    }



    @Override
    public int getMaxX() {
        return maxX;
    }

    @Override
    public int getMaxY() {
        return maxY;
    }

    @Override
    public IDrawableField getDrawableField(final int x, final int y) {
        return fieldData[x][y];
    }

    @Override
    public void setUpdatePane(WorldVisualizerPane pane) {
        gui = pane;
    }

    public void doUpdate(AgentPercept agentPercept, AgentAction lastAction, int lastActionEffect){
        update(agentPercept, lastAction, lastActionEffect);
        if(gui != null){
            gui.doUpdate();
        }
    }

    public void turnRight() {
        switch (currDirection){
            case DOWN:
                currDirection = DIRECTION.LEFT;
                break;
            case LEFT:
                currDirection = DIRECTION.UP;
                break;
            case UP:
                currDirection = DIRECTION.RIGHT;
                break;
            case RIGHT:
                currDirection = DIRECTION.DOWN;
        }
    }
}
