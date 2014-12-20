/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.AbstractGameManager;
import ancienttiles.HumanIntelligence;
import ancienttiles.RestrictedMovementTile;
import ancienttiles.Tile;
import java.awt.Image;

/**
 *
 * @author krr428
 */
public abstract class HoverTransit extends MovingAI//Non-threatening AI
{

    protected int moveX;
    protected int moveY;
    protected boolean activated;
    private static final int HOVER_SPEED = 100; //msss    

    public HoverTransit(Image i)
    {
        super(i);

    }

    public HoverTransit(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);

    }

    protected abstract void initMoveVars();

    @Override
    protected int getMoveX()
    {
        if (activated)
        {
            return moveX;
        }
        else
        {
            return 0;
        }

    }

    @Override
    protected int getMoveY()
    {
        if (activated)
        {
            return moveY;
        }
        else
        {
            return 0;
        }
    }

    @Override
    protected int getTiming()
    {
        return HOVER_SPEED;
    }

    protected boolean canMove(Tile t)
    {        
        if (t.hasAttribute("wall")
                || t.hasAttribute("floor"))
        {
            return false;
        }
        else if (t instanceof RestrictedMovementTile)
        {
            boolean allowedUs = ((RestrictedMovementTile) t).allowMoveFrom(this);
            boolean allowedRider = ((RestrictedMovementTile) t).allowMoveFrom(rider);
            
            if (!allowedUs || !allowedRider)
            {
                return false;
            }
        } 
        
        return true;
        
    }

    @Override
    protected boolean canMove(int x, int y)
    {
        if (x < 0 || y < 0 || x >= gameManager.getCurrentMap().getWidth()
                || y >= gameManager.getCurrentMap().getHeight())
        {
            return false;
        }

        for (Tile t : gameManager.getMapManager().getCurTiles(x, y))
        {
            if (!canMove(t))
            {
                return false;
            }
        }

        Tile t = gameManager.getCurrentMap().getLayer(getLayerName()).getTileAt(x, y);
        if (t != null && t.hasAttribute("ai"))
        {
            return false;
        }

        return true;
    }
    protected HumanIntelligence rider = null;

    @Override
    public void movedOn(HumanIntelligence hi)
    {
        if (rider != hi)
        {            
            setActivated(true);
            //Figure out what direction we are supposed to go.            
            rider = hi;            
        }
    }

    @Override
    public void move()
    {
        if (activated)
        {
            int oldX = getXloc();
            int oldY = getYloc();
            super.move();
            if (rider != null)
            {
                if (rider.getX() == oldX && rider.getY() == oldY)
                {
                    rider.getMovementMgr().requestMove(xloc, yloc);
                }
                else
                {
                    rider = null;
                }
            }
        }


    }

    @Override
    public void init(AbstractGameManager gm)
    {
        super.init(gm);
        initMoveVars();
    }

    protected void setActivated(boolean active)
    {
        activated = active;
        if (!active)
        {
            this.internalTimer.stop();
        }
        else
        {
            this.internalTimer.start();
        }
    }
}

