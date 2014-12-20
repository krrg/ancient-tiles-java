/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.AbstractGameManager;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 *
 * @author krr428
 */
public abstract class MovingAI extends ArtificialIntelligence implements ActionListener
{

    protected Timer internalTimer = null;
    protected AbstractGameManager gameManager = null;
    private final int DEFAULT_TIMING_MS = 500;
    private final String AI_LAYER = null;   
    
    private String getAILayer()
    {
        return getLayerName();
    }
    
    protected abstract int getMoveX();

    protected abstract int getMoveY();

    protected abstract void turn();    
    
    protected int getTiming()
    {
        return DEFAULT_TIMING_MS;
    }

    public MovingAI(Image i)
    {
        super(i);
    }

    public MovingAI(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }
        
    protected abstract boolean canMove(int x, int y);
    

    private void move(int x, int y)
    {
        gameManager.getCurrentMap().getLayer(this.getLayerName()).setTileAt(xloc, yloc, null);
        gameManager.getCurrentMap().getLayer(this.getLayerName()).setTileAt(x, y, this);
        this.setXloc(x);
        this.setYloc(y);

        gameManager.getMapManager().fireMapChanged();

        if (gameManager.getHIManager().getHI().getX() == getXloc()
                && gameManager.getHIManager().getHI().getY() == getYloc())
        {
            movedOn(gameManager.getHIManager().getHI());
        }
    }

    public void move()
    {
        int newX = getXloc() + getMoveX();
        int newY = getYloc() + getMoveY();

        if (canMove(newX, newY))
        {
            move(newX, newY);
        }
        else
        {
            turn();
            newX = getXloc() + getMoveX();
            newY = getYloc() + getMoveY();
            if (canMove(newX, newY))
            {
                move(newX, newY); //Do not recurse.  In the case of being trapped between 2 walls, 
                // recursing would lead to an infinite loop.
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        move();
    }

    @Override
    public void init(AbstractGameManager gm)
    {
        internalTimer = new Timer(getTiming(), this);
        internalTimer.start();
        this.gameManager = gm;
        System.out.println("Init: " + this);
    }

    @Override
    public void kill()
    {
        if (internalTimer != null)
        {
            internalTimer.stop();
        }

        internalTimer = null;
        
        System.out.println("Power-off: " + this);
    }
}
