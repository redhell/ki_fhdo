package de.fh.gui;

import de.fh.gui.server.FieldPanel;
import de.fh.helper.io.FileIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class WorldVisualizerPane extends FieldPanel {


    private IDrawableWorld world;
    public static Image IMAGE_WALL;
    public static Image IMAGE_GOLD;
    public static Image IMAGE_UNKNOWN;
    //public static Image IMAGE_QUESTION_TRANSPARENT;

    WorldVisualizerPane(IDrawableWorld world) {
        super(null);
        this.initImages();
        this.updateView();
        world.setUpdatePane(this);
        this.world = world;
        doUpdate();
    }

    public void doUpdate(){
        this.updateView();
        this.setPreferredSize(new Dimension(world.getMaxX() * 33, world.getMaxY() * 33));
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.world.getMaxX() <= 100 && this.world.getMaxY() <= 100) {
            g.setColor(this.BORDERCOLOR);
            g.fillRect(0, 0, 33 * this.world.getMaxX() + 1, 33 * this.world.getMaxY() + 1);

            for(int i = 0; i < this.world.getMaxX(); ++i) {
                for(int j = 0; j < this.world.getMaxY(); ++j) {
                    BufferedImage image = new BufferedImage(32, 32, 2);
                    world.getDrawableField(i,j).drawField(image.getGraphics());
                    g.drawImage(image, i * 33 + 1, j * 33 + 1, 32, 32, null);
                }
            }

        }
    }

    public Image getPitImage() {
        return this.images[0][2];
    }

    public Image getWumpustImage() {
        return this.images[0][3];
    }

    public Image getMauerImage() {
        return this.images[0][1];
    }

    public Image getGoldImage() {
        return this.images[0][4];
    }

    @Override
    public void initImages() {
        this.images = new Image[2][6];
        BufferedImage image = new BufferedImage(32, 32, 2);
        int farbe = Color.WHITE.getRGB();

        for(int i = 0; i < 32; ++i) {
            for(int j = 0; j < 32; ++j) {
                image.setRGB(i, j, farbe);
            }
        }

        this.images[0][0] = image;
        this.images[0][1] = FileIO.readImageExtern(new File(".", "/Daten/Bilder/mauer.png"));
        IMAGE_WALL = this.images[0][1];
        this.images[0][2] = FileIO.readImageExtern(new File(".", "/Daten/Bilder/pit.png"));
        this.images[0][3] = FileIO.readImageExtern(new File(".", "/Daten/Bilder/wumpus.png"));
        this.images[0][4] = FileIO.readImageExtern(new File(".", "/Daten/Bilder/gold.png"));
        IMAGE_GOLD = this.images[0][4];
        this.images[0][5] = FileIO.readImageExtern(new File(".", "/Daten/Bilder/wumpus.png"));
        this.images[1][0] = image.getScaledInstance(16, 16, 1);
        this.images[1][1] = this.images[0][1].getScaledInstance(16, 16, 1);
        this.images[1][2] = this.images[0][2].getScaledInstance(16, 16, 1);
        this.images[1][3] = this.images[0][3].getScaledInstance(16, 16, 1);
        this.images[1][4] = this.images[0][4].getScaledInstance(16, 16, 1);
        //IMAGE_UNKNOWN = FileIO.readImageExtern(new File(".", "/Daten/Bilder/unknown.png"));
        IMAGE_UNKNOWN = FileIO.readImageExtern(new File(".", "/Daten/Bilder/FragezeichenOhneHintergrund.png"));
    }
}
