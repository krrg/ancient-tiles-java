/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.HumanIntelligence;
import ancienttiles.OnMovementTile;
import ancienttiles.RestrictedMovementTile;
import ancienttiles.Tile;
import java.awt.Image;

/**
 *
 * @author krr428
 */
public abstract class MovingHostileAI extends MovingAI
{

    public MovingHostileAI(Image i)
    {
        super(i);
    }

    public MovingHostileAI(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {
        hi.deathSequence(this, getHIKilledMsg());
    }

    protected abstract String getHIKilledMsg();

    @Override
    protected boolean canMove(int x, int y)
    {
        if (x < 0 || y < 0 || x >= gameManager.getCurrentMap().getWidth()
                || y >= gameManager.getCurrentMap().getHeight())
        {
            return false;
        }

        boolean hasFloor = false;
        for (Tile t : gameManager.getMapManager().getCurTiles(x, y))
        {
            if (t.hasAttribute("wall"))
            {
                return false;
            }
            else if (t.hasAttribute("floor"))
            {
                hasFloor = true;
            }
            else if (t instanceof RestrictedMovementTile)
            {
                boolean allowed = ((RestrictedMovementTile) t).allowMoveFrom(this);
                if (!allowed)
                {
                    return false;
                }
            }
        }        
        
        Tile t = gameManager.getCurrentMap().getLayer(getLayerName()).getTileAt(x, y);
        if (t != null && t.hasAttribute("ai"))
        {
            return false;
        }

        return hasFloor;
    }

    @Override
    public void move()
    {
        super.move();
        for (Tile t : gameManager.getMapManager().getCurTiles(xloc, yloc))
        {
            if (t instanceof OnMovementTile)
            {
                ((OnMovementTile) t).movedOn(this);
            }
        }
    }
}
