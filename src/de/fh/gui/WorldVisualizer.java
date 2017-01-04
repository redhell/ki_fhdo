package de.fh.gui;

import de.fh.environment.WorldInformation;

import javax.swing.*;

public class WorldVisualizer extends JFrame {
    /**
     * Initializes world gui
     *
     * @param information world state modell
     */
    public WorldVisualizer(WorldInformation information){
        super("World Visualizer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(300,300);
        setVisible(true);
        WorldVisualizerPane test = new WorldVisualizerPane(information);


        setContentPane(test.getScrollPane());
        test.repaint();
        this.revalidate();
    }
}
