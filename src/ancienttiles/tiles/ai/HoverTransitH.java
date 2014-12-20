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
public class HoverTransitH extends HoverTransit
{

    public HoverTransitH(Image i)
    {
        super(i);
    }

    public HoverTransitH(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    
    
    public int autoDetectLeftRight()
    {
        if (xloc == 0)
        {
            return 1;
        }
        else if (xloc >= gameManager.getCurrentMap().getWidth() - 1)
        {
            return -1;
        }
        else
        {
            if (canMove(xloc - 1, yloc))
            {
                return -1;
            }
            else// if (canMove(xloc + 1, yloc))
            {
                return 1;
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
        }
    }
    
    


    @Override
    protected void initMoveVars()
    {
        moveX = autoDetectLeftRight();
        moveY = 0;
    }

    @Override
    protected void turn()
    {
        moveX *= -1;
        setActivated(false);
        rider = null;
        initMoveVars();
    }
}
