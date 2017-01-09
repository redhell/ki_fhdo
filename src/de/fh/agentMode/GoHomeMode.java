package de.fh.agentMode;

import de.fh.connection.wumpus.AgentAction;
import de.fh.environment.FieldInfo;
import de.fh.environment.WorldInformation;
import de.fh.search.TargetValidator;
import de.fh.search.UCS;
import de.fh.util.DIRECTION;
import de.fh.util.Position;

import java.util.Stack;

public class GoHomeMode extends AgentMode {
    private WorldInformation information;

    public GoHomeMode(WorldInformation information) {
        this.information = information;
    }

    @Override
    public AgentAction nextMove() {
        UCS goHomePath = new UCS(information, new TargetValidator() {
            @Override
            public boolean isVisitable(Position position) {
                if (position.getX() == 0 && position.getY() == 0) return false;
                FieldInfo info = information.getInfo(position);
                if (info != null) {
                    if (info.isWall()) return false;
                    if (info.canBePit()) return false;
                    if (info.canBeWumpus()) return false;
                }
                return true;
            }

            @Override
            public boolean isTarget(Position position, DIRECTION direction) {
                return position.equals(new Position(1, 1));
            }
        });
        Stack<AgentAction> actions = null;
        try {
            actions = goHomePath.getNextActions();
        } catch (Exception e) {
        }

        if (actions == null && information.getPosition().equals(new Position(1, 1))) {
            return AgentAction.EXIT_TRIAL;
        }
        if (actions != null) {
            return actions.peek();
        }

        return new RandomTarget(information).nextMove();
    }
}
