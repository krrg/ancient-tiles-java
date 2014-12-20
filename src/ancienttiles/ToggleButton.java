/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.tiles.MainTileFactory;
import ancienttiles.tiles.ai.ArtificialIntelligence;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author krr428
 */
public class ToggleButton extends ArtificialIntelligence implements OnMovementTile, ActionListener
{

    private AbstractGameManager gm = null;

    public ToggleButton(Image i)
    {
        super(i);
    }

    private void tempChangePicture()
    {
        Timer buttonPushAnimationTimer = new Timer(500, this);
        buttonPushAnimationTimer.setRepeats(false);
        this.setImage(MainTileFactory.getInstance().getImage("mayan/toggleBtnDown.png"));
        buttonPushAnimationTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.setImage(MainTileFactory.getInstance().getImage("mayan/toggleBtnUp.png"));
        if (gm != null)
        {
            gm.getMapManager().fireMapChanged();
        }
    }

    private void toggleAll()
    {
        tempChangePicture();

        if (gm != null)
        {
            //Then divide the map into quadrants.
            int x0 = 0;
            int y0 = 0;
            int halfwidth = gm.getCurrentMap().getWidth() / 2;
            int halfheight = gm.getCurrentMap().getHeight() / 2;
            int endwidth = gm.getCurrentMap().getWidth();
            int endheight = gm.getCurrentMap().getHeight();

            SearchMapThread s1 = new SearchMapThread(x0, y0, halfwidth, halfheight);
            SearchMapThread s2 = new SearchMapThread(halfwidth, y0, endwidth, halfheight);
            SearchMapThread s3 = new SearchMapThread(x0, halfheight, halfwidth, endheight);
            SearchMapThread s4 = new SearchMapThread(halfwidth, halfheight, endwidth, endheight);

            s1.start();
            s2.start();
            s3.start();
            s4.start();

        }
    }

    private class SearchMapThread extends Thread
    {

        private int i0;
        private int iend;
        private int j0;
        private int jend;
        private int numberToggles = 0;

        public int getNumberToggles()
        {
            return numberToggles;
        }

        public SearchMapThread(int i0, int j0, int iend, int jend)
        {
            this.i0 = i0;
            this.iend = iend;
            this.j0 = j0;
            this.jend = jend;
        }

        @Override
        public void run()
        {
            long time0 = System.currentTimeMillis();
            System.out.println("Beginning new thread to search from "
                    + i0 + "," + j0 + " to " + iend + "," + jend);

            for (String layerName : gm.getCurrentMap().getLayers())
            {
                for (int i = i0; i < iend; i++)
                {
                    for (int j = j0; j < jend; j++)
                    {
                        Tile t = gm.getCurrentMap().getLayer(layerName).getTileAt(i, j);
                        if (t != null && t instanceof ToggleTile)
                        {
                            ((ToggleTile) t).toggle();
                            numberToggles++;
                        }
                    }
                }
            }
            System.out.println("Ended search from"
                    + i0 + "," + j0 + " to " + iend + "," + jend);
            System.out.println("Finished searching: Toggle tiles toggled = " + numberToggles);
            System.out.println("Search time: " + (System.currentTimeMillis() - time0) + "ms");
        }
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {        
        toggleAll();
    }

    @Override
    public void movedOn(ArtificialIntelligence ai)
    {        
        toggleAll();
    }

    @Override
    public void init(AbstractGameManager gm)
    {
        this.gm = gm;
    }

    @Override
    public void kill()
    {
        //Absolutely nothing to do, we are not really a timed object.
    }
    
    
}
