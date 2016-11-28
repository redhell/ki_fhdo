package de.fh.gui;

public interface IDrawableWorld {
    int getMaxX();
    int getMaxY();
    IDrawableField getDrawableField(int x, int y);
    void setUpdatePane(WorldVisualizerPane pane);
}
