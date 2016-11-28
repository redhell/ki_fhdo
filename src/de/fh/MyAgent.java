package de.fh;

import de.fh.agentI.IAction;
import de.fh.agentI.IAgentActions;
import de.fh.agentI.IAgentState;
import de.fh.agentMode.WorldDiscoverer;
import de.fh.connection.ActionEffect;
import de.fh.connection.Percept;
import de.fh.connection.StartInfo;
import de.fh.connection.client.WumpusClientConnector;
import de.fh.connection.wumpus.AgentAction;
import de.fh.connection.wumpus.AgentPercept;
import de.fh.connection.wumpus.AgentStartInfo;
import de.fh.environment.WorldInformation;
import de.fh.gui.WorldVisualizer;

//TODO: Zu spät ....
/*
 * DIESE KLASSE VERÄNDERN SIE BITTE NUR AN DEN GEKENNZEICHNETEN STELLEN
 * wenn die Bonusaufgabe bewertet werden soll.
 */
class MyAgent implements IAgentActions, IAgentState {

    private AgentAction nextAction = AgentAction.START_GAME;

    private WorldInformation info;

    public static void main(String[] args) {
        MyAgent ki = new MyAgent();
        // Client connect to the server with given predefined settings

        WumpusClientConnector wumpusClient = new WumpusClientConnector(ki, ki);
        wumpusClient.run();
    }

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

        AgentPercept agPercept = (AgentPercept) percept;

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

        //discover world
        nextAction = new WorldDiscoverer(info).nextMove();

        return nextAction;
    }
}