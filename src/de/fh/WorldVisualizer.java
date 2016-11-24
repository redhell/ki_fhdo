package de.fh;

import javax.swing.*;

public class WorldVisualizer extends JFrame {
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
