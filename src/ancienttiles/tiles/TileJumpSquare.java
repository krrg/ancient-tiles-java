/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles;

import ancienttiles.HumanIntelligence;
import ancienttiles.OnMovementTile;
import ancienttiles.Tile;
import ancienttiles.tiles.ai.ArtificialIntelligence;
import java.awt.Image;
import java.net.URL;

/**
 * Similar to an exit square, but does not clear the contents.
 *
 * @author krr428
 */
public class TileJumpSquare extends Tile implements OnMovementTile
{

    public TileJumpSquare(Image i)
    {
        super(i);
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {
        String filename = null;
        if (! this.getMetadata().containsKey("%exit"))
        {
            System.out.println("Returning to parent...");
            hi.getManager().getMapManager().loadParent();
            return;
        }
        else
        {
            filename = this.getMetadata().get("%exit");
        }
        
        URL mapURL = MainTileFactory.class.getResource(filename);
        //Try to load the map.
        
        hi.getManager().getMapManager().loadMap(mapURL);
    }

    @Override
    public void movedOn(ArtificialIntelligence ai)
    {
    }
    
    
}
