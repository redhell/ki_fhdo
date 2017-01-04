package de.fh.environment;

import de.fh.connection.ActionEffect;
import de.fh.connection.wumpus.AgentAction;
import de.fh.connection.wumpus.AgentPercept;
import de.fh.gui.IDrawableField;
import de.fh.gui.IDrawableWorld;
import de.fh.gui.WorldVisualizerPane;
import de.fh.util.DIRECTION;
import de.fh.util.Position;

import java.util.Stack;

public class WorldInformation implements IDrawableWorld {
    private WorldVisualizerPane gui;
    private FieldInfo[][] fieldData = new FieldInfo[3][3];

    private Stack<Position> positionHistory = new Stack<>();

    /***
     * Current max map dimension
     */
    private int maxX;
    /***
     * Current max map dimension
     */
    private int maxY;

    /***
     * Current position of agent
     */
    private Position currentPos;

    /***
     * Current direction of agent
     */
    private DIRECTION currentDirection = DIRECTION.RIGHT;

    /***
     * Tracks all Wumpi
     */
    private WumpusTracker wumpusTracker = new WumpusTracker(this);

    private AgentPercept lastPercept;
    private AgentAction lastAction;
    private int lastActionEffect;



    public WorldInformation(){
        this.maxX = 3;
        this.maxY = 3;
        currentPos = new Position(1, 1);
        initField();
    }

    /***
     * @return current X coordinate of agent
     */
    public int getCurrX() {
        return currentPos.getX();
    }

    /***
     * @return current Y coordinate of agent
     */
    public int getCurrY() {
        return currentPos.getY();
    }

    /***
     * @return current position coordinate of agent
     */
    public Position getPosition(){
        return currentPos;
    }

    /***
     * @return current direction coordinate of agent
     */
    public DIRECTION getCurrentDirection() {
        return currentDirection;
    }

    /***
     * initialize startup field
     */
    private void initField(){
        for(int x = 0; x < 3; x++){
            for (int y = 0; y < 3; y++){
                fieldData[x][y] = new FieldInfo(x,y,this);
            }
        }
        fieldData[0][0].setWall();
        fieldData[0][1].setWall();
        fieldData[1][0].setWall();
        fieldData[1][1].visit();
    }

    /***
     * updates the percepts of the last move
     * @param percept
     * @param lastAction
     * @param lastActionEffect
     */
    public void update(AgentPercept percept, AgentAction lastAction, int lastActionEffect){
        this.lastPercept = percept;
        this.lastActionEffect = lastActionEffect;
        this.lastAction = lastAction;

        fixPosition();


        if(lastActionEffect == ActionEffect.INVALID_LOCATION) return;

        if(lastPercept.isBump()){
            for (DIRECTION dir :
                    DIRECTION.values()) {
                fieldData[getCurrX() + dir.xOffset][getCurrY() + dir.yOffset].setCanBeWall();
            }
        }

        if(!lastPercept.isBump()){
            for (DIRECTION dir :
                    DIRECTION.values()) {
                fieldData[getCurrX() + dir.xOffset][getCurrY() + dir.yOffset].setCantBeWall();
            }
        }

        if(lastPercept.isBreeze()){
            fieldData[getCurrX()][getCurrY()].setBreeze();
        }

        //CHECK WUMPUS
        if(lastPercept.isRumble()){
            wumpusTracker.expand();
        }
        for (int i = 0; i<100; i++){
            if(lastPercept.getWumpusStenchRadar()[i][0] != 0){
                wumpusTracker.updateWumpus(lastPercept.getWumpusStenchRadar()[i][0], lastPercept.getWumpusStenchRadar()[i][1]);
            }
        }
        wumpusTracker.finishRound();
    }

    /***
     * Returns current field information
     */
    public FieldInfo getInfo(int x, int y){
        try{
            return fieldData[x][y];
        }catch (Exception e){
            return null;
        }
    }

    public Stack<Position> getPositionHistory(){
        return positionHistory;
    }

    /***
     * updates position data after move
     */
    private void fixPosition(){
        if(lastAction == AgentAction.TURN_RIGHT){
            currentDirection = DIRECTION.turnRight(currentDirection);
        }
        if(lastAction == AgentAction.TURN_LEFT){
            currentDirection = DIRECTION.turnLeft(currentDirection);
        }

        if(lastAction == AgentAction.GO_FORWARD) {
            if (lastActionEffect == ActionEffect.INVALID_LOCATION) {
                fieldData[getCurrX() + currentDirection.xOffset][getCurrY() + currentDirection.yOffset].setWall();
            } else {
                positionHistory.push(new Position(currentPos));
                currentPos.move(currentDirection);
            }

            if (currentPos.getX() > maxX - 2) {
                maxX++;
                FieldInfo[][] tmp = fieldData;
                fieldData = new FieldInfo[maxX][maxY];
                System.arraycopy(tmp, 0, fieldData, 0, maxX - 1);
                for (int y = 0; y < maxY; y++) {
                    fieldData[maxX - 1][y] = new FieldInfo(maxX - 1, y, this);
                }
            }
            if (currentPos.getY() > maxY - 2) {
                maxY++;
                FieldInfo[][] tmp = fieldData;
                fieldData = new FieldInfo[maxX][maxY];
                for (int x = 0; x < maxX; x++) {
                    System.arraycopy(tmp[x], 0, fieldData[x], 0, maxY - 1);
                    fieldData[x][maxY - 1] = new FieldInfo(x, maxY - 1, this);
                }
            }
            fieldData[currentPos.getX()][currentPos.getY()].visit();
        }

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

    public WumpusTracker getWumpusTracker(){
        return wumpusTracker;
    }

    public boolean canBeWumpus(Position pos){
        return wumpusTracker.canBeWumpus(pos);
    }

    public FieldInfo getInfo(Position coordiante) {
        return getInfo(coordiante.getX(), coordiante.getY());
    }
    /**
     * Go Home Agent (1;1)
     */
    public void setGoHome() { 
    	fieldData[1][1] = new FieldInfo(1,1,this);
    }

    public void reinitWumpusTracker() {
        System.out.println("reinit tracker");
        wumpusTracker.clear();
        for (int i = 0; i < 100; i++) {
            if (lastPercept.getWumpusStenchRadar()[i][0] != 0) {
                wumpusTracker.updateWumpus(lastPercept.getWumpusStenchRadar()[i][0], lastPercept.getWumpusStenchRadar()[i][1]);
            }
        }
        wumpusTracker.finishRound();
    }
}
