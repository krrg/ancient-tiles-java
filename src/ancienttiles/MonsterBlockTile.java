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
public class MonsterBlockTile extends Tile implements RestrictedMovementTile
{

    public MonsterBlockTile(Image i)
    {
        super(i);
    }

    @Override
    public boolean allowMoveFrom(HumanIntelligence hi)
    {
        return true;
    }

    @Override
    public boolean allowMoveFrom(ArtificialIntelligence ai)
    {
        return false;
    }
    
    
}
