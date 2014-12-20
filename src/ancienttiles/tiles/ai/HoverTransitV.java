/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.HumanIntelligence;
import ancienttiles.Tile;
import java.awt.Image;

/**
 *
 * @author krr428
 */
public class HoverTransitV extends HoverTransit
{

    public HoverTransitV(Image i)
    {
        super(i);
    }

    public HoverTransitV(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    private int autodetectUpDown()
    {
        //Look at the tile above, and the tile below.
        if (yloc == 0)
        {
            return 1;
        }
        else if (yloc >= gameManager.getCurrentMap().getHeight() - 1)
        {
            return -1;
        }
        else
        {
            if (canMove(xloc, yloc + 1))
            {
                return 1;
            }
            else //(canMove(xloc, yloc - 1))
            {
                return -1;
            }
        }

    }    

    @Override
    protected void setActivated(boolean active)
    {
        super.setActivated(active);
        if (active)
        {
            initMoveVars();
        }//To change body of generated methods, choose Tools | Templates.
    }
    
    

    @Override
    protected void initMoveVars()
    {
        moveX = 0;
        moveY = autodetectUpDown();
    }

    @Override
    protected void turn()
    {
        moveY *= -1;
        setActivated(false);
        rider = null;
    }
}
