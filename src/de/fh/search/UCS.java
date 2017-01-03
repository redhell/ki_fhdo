package de.fh.search;

import de.fh.util.DIRECTION;
import de.fh.environment.WorldInformation;
import de.fh.connection.wumpus.AgentAction;
import de.fh.util.Position;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

public class UCS {
    private HashSet<Position> closedList = new HashSet<>();
    private PriorityQueue<Move> moves = new PriorityQueue<>();

    private WorldInformation information;
    private TargetValidator validator;

    public UCS(WorldInformation info, TargetValidator validator){
        this.information = info;
        this.validator = validator;
    }

    private void initialAdd(){
        //forward
        addMove(new Move(null, AgentAction.GO_FORWARD, information.getPosition().getNewPosition(information.getDir()),information.getDir()));

        //left
        Move tmp = new Move(null, AgentAction.TURN_LEFT, information.getPosition(), DIRECTION.turnLeft(information.getDir()));
        tmp = new Move(tmp, AgentAction.GO_FORWARD, information.getPosition().getNewPosition(tmp.getDir()), tmp.getDir());
        addMove(tmp);

        //right
        tmp = new Move(null, AgentAction.TURN_RIGHT, information.getPosition(), DIRECTION.turnRight(information.getDir()));
        tmp = new Move(tmp, AgentAction.GO_FORWARD, information.getPosition().getNewPosition(tmp.getDir()), tmp.getDir());
        addMove(tmp);

        //back
        tmp = new Move(null, AgentAction.TURN_RIGHT, information.getPosition(), DIRECTION.turnRight(information.getDir()));
        tmp = new Move(tmp, AgentAction.TURN_RIGHT, tmp.getPos(), DIRECTION.turnRight(tmp.getDir()));
        tmp = new Move(tmp, AgentAction.GO_FORWARD, tmp.getPos().getNewPosition(tmp.getDir()), tmp.getDir());
        addMove(tmp);
    }

    private void expandMove(Move parent){
        //forward
        addMove(new Move(parent, AgentAction.GO_FORWARD, parent.getPos().getNewPosition(parent.getDir()),parent.getDir()));

        //left
        Move tmp = new Move(parent, AgentAction.TURN_LEFT, parent.getPos(), DIRECTION.turnLeft(parent.getDir()));
        tmp = new Move(tmp, AgentAction.GO_FORWARD, parent.getPos().getNewPosition(tmp.getDir()), tmp.getDir());
        addMove(tmp);

        //right
        tmp = new Move(parent, AgentAction.TURN_RIGHT, parent.getPos(), DIRECTION.turnRight(parent.getDir()));
        tmp = new Move(tmp, AgentAction.GO_FORWARD, parent.getPos().getNewPosition(tmp.getDir()), tmp.getDir());
        addMove(tmp);

    }

    public Stack<AgentAction> getNextActions(){
        return search().getActionToThis();
    }

    public Move search(){
        initialAdd();
        while (!moves.isEmpty()){
            Move elem = moves.poll();
            if(validator.isTarget(elem.getPos())){
                return elem;
            }else{
                expandMove(elem);
            }
        }
        return null;
    }

    private void addMove(Move move){
        if(closedList.contains(move.getPos()))return;
        if(validator.isVisitable(move.getPos())){
            closedList.add(move.getPos());
            moves.add(move);
        }
    }
}
