/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.tiles.MainTileFactory;
import java.awt.Image;

/**
 *
 * @author krr428
 */
public class JaguarY extends MovingHostileAI
{

    public JaguarY(Image i)
    {
        super(i);
    }

    public JaguarY(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }
    
    private int moveY = -1;

    @Override
    protected int getMoveX()
    {
        return 0;
    }

    @Override
    protected int getMoveY()
    {
        return moveY;
    }

    @Override
    protected void turn()
    {
        moveY *= -1;
        if (moveY > 0)
        {
            this.setImage(MainTileFactory.getInstance().getImage("mayan/jag1_right.png"));
        }
        else
        {
            this.setImage(MainTileFactory.getInstance().getImage("mayan/jag1_left.png"));
        }
    }

    @Override
    protected String getHIKilledMsg()
    {
        return "Oh no! You've been eaten by a hungry Jaguar!";
    }
    
}
