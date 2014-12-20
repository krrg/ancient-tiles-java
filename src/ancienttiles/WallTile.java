/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.tiles.ai.ArtificialIntelligence;
import java.awt.Image;

/**
 *
 * @author krr428
 */
public class WallTile extends Tile implements RestrictedMovementTile
{

    public WallTile(Image i)
    {
        super(i);
    }

    @Override
    public boolean allowMoveFrom(HumanIntelligence hi)
    {
        return false; //Never allow since this is a WallTile. 
    }

    @Override
    public boolean allowMoveFrom(ArtificialIntelligence ai)
    {
        return false;
    }
    
    
}
