package de.fh.agentMode;

import de.fh.connection.wumpus.AgentAction;
import de.fh.environment.WorldInformation;
import de.fh.environment.WumpusInfo;

public class WumpusFighter extends AgentMode{
    private WorldInformation worldInformation;

    public WumpusFighter(WorldInformation information){
        this.worldInformation = information;
    }

    @Override
    public AgentAction nextMove() {
        return null;
    }


}
