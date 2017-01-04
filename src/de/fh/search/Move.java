package de.fh.search;

import de.fh.connection.wumpus.AgentAction;
import de.fh.util.DIRECTION;
import de.fh.util.Position;

import java.util.Stack;

/***
 * Wrapper for search algorithms
 */
public class Move implements Comparable<Move>{
    private Move parent;
    /**
     * Action to get from parent to this move
     */
    private AgentAction action;
    /**
     * Costs until this move
     */
    private int cost;
    /**
     * Position after this move
     */
    private Position currentPos;
    /**
     * Direction after this move
     */
    private DIRECTION currentDir;

    public Move(Move parent, AgentAction agentAction, Position pos, DIRECTION dir){
        this.parent = parent;
        if(parent == null){
            cost = 1;
        }else{
            cost = parent.getCost() + 1;
        }
        this.action = agentAction;
        this.currentDir = dir;
        this.currentPos = pos;
    }

    public Position getPos() {
        return currentPos;
    }

    public DIRECTION getDir() {
        return currentDir;
    }

    public Move getParent(){
        return parent;
    }

    public AgentAction getAction(){
        return action;
    }

    public int getCost(){
        return cost;
    }

    public Stack<AgentAction> getActionToThis(){
        Stack<AgentAction> actionStack = new Stack<>();
        Move curr = this;
        while (curr != null){
            actionStack.add(curr.getAction());
            curr = curr.getParent();
        }
        return actionStack;
    }

    @Override
    public int compareTo(Move o) {
        if(this.cost< o.cost) return -1;
        if(this.cost> o.cost) return 1;
        return 0;
    }
}
