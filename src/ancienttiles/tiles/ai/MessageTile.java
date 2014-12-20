/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.AbstractGameManager;
import ancienttiles.HumanIntelligence;
import ancienttiles.OnMovementTile;
import java.awt.Image;

/**
 *
 * @author krr428
 */
public class MessageTile extends ArtificialIntelligence implements OnMovementTile
{    
    protected String message = null;

    public MessageTile(Image i)
    {
        super(i);
    }

    public MessageTile(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {        
        message = this.getMetadata().get("%msg");
        System.out.println("%msg=" + message);

         hi.getManager().getViewManager().getView().displayMessageTooltip(message, 5000, xloc, yloc);
    }

    @Override
    public void init(AbstractGameManager gm)
    {
    }

    @Override
    public void kill()
    {        

    }
}
