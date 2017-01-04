package de.fh.agentMode;

import de.fh.connection.wumpus.AgentAction;
import de.fh.environment.FieldInfo;
import de.fh.environment.WorldInformation;
import de.fh.search.TargetValidator;
import de.fh.search.UCS;
import de.fh.util.DIRECTION;
import de.fh.util.Position;

public class RandomTarget extends AgentMode {
    private WorldInformation worldInformation;

    public RandomTarget(WorldInformation worldInformation) {

        this.worldInformation = worldInformation;
    }

    @Override
    public AgentAction nextMove() {
        UCS randomPath = new UCS(worldInformation, new TargetValidator() {
            @Override
            public boolean isVisitable(Position position) {
                if (position.getX() == 0 && position.getY() == 0) return false;
                for (DIRECTION direction : DIRECTION.values()) {
                    Position neighbour = position.getNewPosition(direction);
                    if (worldInformation.canBeWumpus(neighbour)) {
                        return false;
                    }
                }
                FieldInfo info = worldInformation.getInfo(position);
                if (info != null) {
                    if (info.isWall()) return false;
                    if (info.canBePit()) return false;
                    if (info.canBeWumpus()) return false;
                }
                return true;
            }

            @Override
            public boolean isTarget(Position position, DIRECTION direction) {
                return !worldInformation.getPosition().equals(position);
            }
        });
        AgentAction action = null;
        try {
            action = randomPath.getNextActions().peek();
        } catch (Exception e) {

        }

        if (action != null) {
            System.out.println("CALCULATED BY: Random");
            return action;
        }
        UCS randomPathRisk = new UCS(worldInformation, new TargetValidator() {
            @Override
            public boolean isVisitable(Position position) {
                if (position.getX() == 0 && position.getY() == 0) return false;
                FieldInfo info = worldInformation.getInfo(position);
                if (info != null) {
                    if (info.isWall()) return false;
                    if (info.canBePit()) return false;
                    if (info.canBeWumpus()) return false;
                }
                return true;
            }

            @Override
            public boolean isTarget(Position position, DIRECTION direction) {
                return !worldInformation.getPosition().equals(position);
            }
        });
        try {
            System.out.println("CALCULATED BY: RandomRisk");
            return randomPathRisk.getNextActions().peek();
        } catch (Exception e) {
            return null;
        }
    }
}
