package de.fh;

import de.fh.agentI.IAction;
import de.fh.agentI.IAgentActions;
import de.fh.agentI.IAgentState;
import de.fh.agentMode.WorldDiscoverer;
import de.fh.agentMode.WumpusFighter;
import de.fh.connection.ActionEffect;
import de.fh.connection.Percept;
import de.fh.connection.StartInfo;
import de.fh.connection.client.WumpusClientConnector;
import de.fh.connection.wumpus.AgentAction;
import de.fh.connection.wumpus.AgentPercept;
import de.fh.connection.wumpus.AgentStartInfo;
import de.fh.environment.WorldInformation;
import de.fh.gui.WorldVisualizer;
import de.fh.util.Position;

//TODO: Zu spät ....
/*
 * DIESE KLASSE VERÄNDERN SIE BITTE NUR AN DEN GEKENNZEICHNETEN STELLEN
 * wenn die Bonusaufgabe bewertet werden soll.
 */
class MyAgent implements IAgentActions, IAgentState {

    private AgentAction nextAction = AgentAction.START_GAME;

    private int actionCounter = 0;
    
    private AgentPercept agPercept;

    private WorldInformation info;
    
    private Scoreboard sb = new Scoreboard(1000);

    private int lastWumpusId = 0;

    public static void main(String[] args) {
        MyAgent ki = new MyAgent();
        // Client connect to the server with given predefined settings

        WumpusClientConnector wumpusClient = new WumpusClientConnector(ki, ki);
        wumpusClient.run();    }

    /**
     * Diese Funktion wird einmal aufgerufen und übergibt
     * alle Startinformationen über die Welt
     *
     * @param startInfo Startinfo-Objekt mit alle Startinformationen
     */
    @Override
    public void setStartInfo(StartInfo startInfo) {
        info = new WorldInformation();

        Thread guiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                WorldVisualizer wv = new WorldVisualizer(info);

            }
        });
        guiThread.start();

        /**
         *In dem Startinfo Objekt befinden sich alle Startinformationen,
         *auf die die "interne Welt", die Wissenbasis aufbaut
         * Achtung: Die Feldgröße ist im Standard unbekannt also -1
         */
        AgentStartInfo agentStartInfo = (AgentStartInfo) startInfo;

        //Startinformationen einmal ausgeben
        System.out.println(agentStartInfo.toString());
    }


    /**
     * In dieser Methode kann das Wissen über die Welt (der State, der Zustand)
     * entsprechend der aktuellen Wahrnehmungen anpasst, und die "interne Welt",
     * die Wissensbasis, des Agenten kontinuierlich ausgebaut werden.
     *
     * Wichtig: Diese Methode wird aufgerufen, bevor der Agent handelt, d.h.
     * bevor die chooseAction()-Methode aufgerufen wird...
     *
     * @param percept      Aktuelle Wahrnehmung
     * @param actionEffect Reaktion des Servers
     */
    @Override
    public void updateState(Percept percept, int actionEffect) {

        agPercept = (AgentPercept) percept;

        info.doUpdate(agPercept, nextAction, actionEffect);


        // Alle möglichen Serverrückmeldungen:
        switch (actionEffect) {
            case ActionEffect.SUCCESS:
                break;
            case ActionEffect.GOLD_FOUND:
                break;
            case ActionEffect.GOLD_NOT_FOUND:
                break;
            case ActionEffect.INVALID_LOCATION:
                break;
            case ActionEffect.WUMPUS_KILLED:
            	sb.changeScore(100);
            	info.getWumpusTracker().wumpusKilled(lastWumpusId);
                break;
            case ActionEffect.WUMPUS_NOT_KILLED:
                break;
            default:
                //Startsituation - aller erster Aufruf!
                break;
        }
    }

    /**
     * Die chooseAction-Methode erweitern Sie so, dass die nächste sinnvolle Aktion,
     * auf Basis der vorhandenen Zustandsinformationen und gegebenen Zielen, ausgeführt wird.
     * Die chooseAction-Methode soll den Agenten so intelligent wie möglich handeln lassen.
     *
     * Beispiel: Wenn die letzte Wahrnehmung
     * "percept.isGold() == true" enthielt, ist "Action.GRAB" eine
     * geeignete Tätigkeit. Wenn Sie wissen, dass ein Quadrat "unsicher"
     * ist, können Sie wegziehen
     *
     * @return Die nächste Pacman-Action die vom Server ausgeführt werden soll
     */
    @Override
    public IAction chooseAction() {
    	
    	if(agPercept.isGold()){
    		sb.changeScore(100);
    		nextAction = AgentAction.GRAB;
    		return nextAction;
    	}

    	//Fight
        WumpusFighter fighter = new WumpusFighter(info);
        nextAction = fighter.nextMove();
        if(nextAction == AgentAction.SHOOT){
            lastWumpusId = fighter.getCurrentTarget().getId();
        }

        if(nextAction != null){
            return nextAction;
        }

        //discover world
        try{
            nextAction = new WorldDiscoverer(info).nextMove();
        }catch(Exception e){
            if(info.getWumpusTracker().getFirstWumpus() == null){
                nextAction = AgentAction.NO_ACTION;
                // Zurück zum Start +100 Pt.
                if(!info.getPosition().equals(new Position(1,1))) {
                    info.setGoHome();
                } else {
                    sb.changeScore(100);
                    nextAction = AgentAction.EXIT_TRIAL;
                }
            }else{
                info.getWumpusTracker().clear();
            }



        }
   
        
        sb.changeScore(-1);
        System.out.println(sb.getScore());
        actionCounter++;
        if(nextAction == null){
            System.out.println("Uppss");
            return chooseAction();
        }
        return nextAction;
    }
}