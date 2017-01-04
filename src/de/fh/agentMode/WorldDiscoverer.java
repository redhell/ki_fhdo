package de.fh.agentMode;

import de.fh.connection.wumpus.AgentAction;
import de.fh.environment.FieldInfo;
import de.fh.environment.WorldInformation;
import de.fh.search.TargetValidator;
import de.fh.search.UCS;
import de.fh.util.DIRECTION;
import de.fh.util.Position;

public class WorldDiscoverer extends AgentMode {
    private WorldInformation worldInformation;

    public WorldDiscoverer(WorldInformation information){
        worldInformation = information;
    }

    /***
     * Returns the next best option to discover all pits.
     * Null if the world has all information to target pits.
     */
    @Override
    public AgentAction nextMove() {
        UCS search = new UCS(worldInformation, new TargetValidator() {
            @Override
            public boolean isVisitable(Position position) {
                if (position.getX() == 0 && position.getY() == 0) return false;
                FieldInfo info = worldInformation.getInfo(position);
                if(info != null){
                    if(info.isWall()) return false;
                    if(info.canBePit()) return false;
                    if(info.canBeWumpus()) return false;
                }
                return true;
            }

            @Override
            public boolean isTarget(Position position, DIRECTION direction) {
                FieldInfo field = worldInformation.getInfo(position);
                //TODO: WUmpus? || field.isWumpus()
            	if(field!=null && (field.isPit() || field.isVisited() || field.canBeWumpus()))
            		return false;
                for (DIRECTION dir: DIRECTION.values()) {
                    int x = position.getX() + dir.xOffset;
                    int y = position.getY() + dir.yOffset;

                    FieldInfo neighbour = worldInformation.getInfo(x,y);
                    if(neighbour != null){
                        if (worldInformation.getInfo(position) == null || !worldInformation.getInfo(position).isVisited() || neighbour.canBePit()) {
                            return true;
                        }
                    }
                }
                for (int i = 1; i< 2; i++){
                    for (DIRECTION dir: DIRECTION.values()) {

                        int x = position.getX() + dir.xOffset * i;
                        int y = position.getY() + dir.yOffset * i;
                        FieldInfo neighbour = worldInformation.getInfo(x,y);

                        if(neighbour != null && neighbour.isVisited()){
                            return false;
                        }
                    }
                }
                return true;
            }
        });
        return search.getNextActions().pop();
    }
}
