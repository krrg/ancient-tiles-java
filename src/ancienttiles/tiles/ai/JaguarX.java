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
public class JaguarX extends MovingHostileAI
{

    public JaguarX(Image i)
    {
        super(i);
    }

    public JaguarX(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    private int moveX = -1;
    
    @Override
    protected int getMoveX()
    {
        return moveX;
    }

    @Override
    protected int getMoveY()
    {
        return 0;
    }

    @Override
    protected void turn()
    {
        moveX *= -1;
        if (moveX > 0)
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
