package de.fh;

import de.fh.agentI.IAction;
import de.fh.agentI.IAgentActions;
import de.fh.agentI.IAgentState;
import de.fh.agentMode.RandomTarget;
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
    private AgentPercept agPercept;
    private WorldInformation info;


    //Statistics
    private boolean goldFound = false;
    private int arrowsShoot = 0;
    private Scoreboard sb = new Scoreboard(1000);
    private int lastWumpusId = 0;
    private int wumpusKilled = 0;

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

        /*
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

        //Update State-Modell
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
                wumpusKilled++;
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
        sb.changeScore(-1);
        //grab gold
        if(agPercept.isGold()){
    		sb.changeScore(100);
    		goldFound = true;
    		nextAction = AgentAction.GRAB;
    		return nextAction;
    	}
        


        //try to calculateFightPosition against wumpus
        //solange weniger Pfeile verschossen wurden als man hat
        if (wumpusKilled == 0 && arrowsShoot <= calculateArrows()) {
            WumpusFighter fighter = new WumpusFighter(info);
            nextAction = fighter.nextMove();
            if (nextAction == AgentAction.SHOOT) {
                lastWumpusId = fighter.getCurrentTarget().getId();
                arrowsShoot++;
            }
            if (nextAction != null) {
                System.out.println("CALCULATED BY: WumpusFighter");
                return nextAction;
            }
        }


        //discover world, if no calculateFightPosition is going on
        try{
            try {
                nextAction = new WorldDiscoverer(info).nextMove();
            }catch (Exception e){
                info.reinitWumpusTracker();
            }
            nextAction = new WorldDiscoverer(info).nextMove();
        }catch(Exception e){
            if (wumpusKilled > 0) {
                nextAction = AgentAction.NO_ACTION;
                // Zurück zum Start +100 Pt.
                if(!info.getPosition().equals(new Position(1,1))) {
                    info.setGoHome();
                    nextAction = null;
                } else {
                    sb.changeScore(100);
                    nextAction = AgentAction.EXIT_TRIAL;
                    endInfo();
                }
            }else{
                nextAction = null;
            }
        }

        System.out.println("Current Score " + sb.getScore());
        if(nextAction == null){
            nextAction = new RandomTarget(info).nextMove();
            if (nextAction == null) {
                //gibe it up
                return AgentAction.EXIT_TRIAL;
            }

        }
        System.out.println("CALCULATED BY: WorldDiscoverer");
        return nextAction;
    }

    /**
     * Prints endInfo
     */
    private void endInfo() {
        System.out.println("#####################################################");
        System.out.println("Ich hoffe ich habe die Map gut geschafft! :)");
        System.out.println("Restpunktzahl: " + sb.getScore());
        System.out.println("Das Gold habe ich " + (goldFound ? "gefunden." : "nicht gefunden."));
        System.out.println("Pfeile verschossen: " + arrowsShoot);
        System.out.println("#####################################################");
    }
    
    private int calculateArrows(){
    	int arrows = 5;
    	
    	//Pfeile verdoppeln sich fuer jeden existierenden Wumpus
    	for(int i = 0; i < agPercept.getWumpusStenchRadar().length; i++){
    		if(agPercept.getWumpusStenchRadar()[i][0] != 0)
    			arrows*=2;;
    	}
    	
    	return arrows;
    }
}