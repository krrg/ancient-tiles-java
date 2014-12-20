/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.tiles.MainTileFactory;
import java.awt.Image;
import java.util.Random;

/**
 *
 * @author krr428
 */
public class HowlerMonkey extends MovingHostileAI
{

    private Random rand = null;
    private int moveX = 0;
    private int moveY = 0;
    protected static final int HOWLER_MONKEY_SPEED = 300;

    public HowlerMonkey(Image i)
    {
        super(i);
        initRandom();
    }

    public HowlerMonkey(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
        initRandom();
    }

    private void initRandom()
    {
        rand = new Random();
    }

    @Override
    protected int getTiming()
    {
        return HOWLER_MONKEY_SPEED; //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    protected String getHIKilledMsg()
    {
        return "Oh no! You've been eaten by an aggresive man-eating howler monkey!";
    }

    private void determineMove(int hiDirX, int hiDirY)
    {
        if (hiDirX == 0)
        {
            if (hiDirY > 0)
            {
                moveY = 1;
            }
            else if (hiDirY < 0)
            {
                moveY = -1;
            }
        }
        else if (hiDirY == 0)
        {
            if (hiDirX > 0)
            {
                moveX = 1;
            }
            else if (hiDirX < 0)
            {
                moveX = -1;
            }
        }
        else
        {
            if (rand.nextBoolean())
            {
                hiDirX = 0;                
            }
            else
            {
                hiDirY = 0;
            }
            //Recursively determine the move, having set (randomly)
            // one of the directional directives to 0.  
            determineMove(hiDirX, hiDirY);
        }
    }    
    
    private void determineMove()
    {
        int hiX = gameManager.getHIManager().getHI().getX();
        int hiY = gameManager.getHIManager().getHI().getY();

        int hiDirX = hiX - xloc;
        int hiDirY = hiY - yloc;

        determineMove(hiDirX, hiDirY);
        
        if (moveX > 0)
        {
            this.setImage(MainTileFactory.getInstance().getImage("mayan/hmonk_r.png"));
        }
        else if (moveX < 0)
        {
            this.setImage(MainTileFactory.getInstance().getImage("mayan/hmonk_l.png"));
        }
    }

    @Override
    public void move()
    {
        determineMove();        
        super.move();
        moveX = 0;
        moveY = 0;
    }

    @Override
    protected int getMoveX()
    {
        return moveX;
    }

    @Override
    protected int getMoveY()
    {
        return moveY;
    }

    @Override
    protected void turn()
    {
        determineMove();
    }
}
