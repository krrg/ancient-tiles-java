/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.tiles.ai.ArtificialIntelligence;
import java.awt.Image;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author krr428
 */
public class LockTile extends Tile implements OnMovementTile, RestrictedMovementTile
{

    private Set<String> unlockingKeyTypes = null;

    public LockTile(Image i, Set<String> unlockingKeys)
    {
        super(i);
        unlockingKeyTypes = unlockingKeys;
    }

    public LockTile(Image i, String unlockingKey)
    {
        super(i);
        Set<String> validKeys = new HashSet<String>();
        validKeys.add(unlockingKey);
        unlockingKeyTypes = validKeys;
    }

    @Override
    public boolean allowMoveFrom(HumanIntelligence hi)
    {
        //Check each of our valid keys and see if the HI has any of them.

        for (String possibleKey : unlockingKeyTypes)
        {
            if (hi.getInventoryMgr().contains(possibleKey))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {
        for (String possibleKey: unlockingKeyTypes)
        {
            if (hi.getInventoryMgr().contains(possibleKey))
            {
                Tile key = hi.getInventoryMgr().getInstanceOf(possibleKey);                
                hi.getInventoryMgr().removeItem(key);
                break;
            }
        }
        
        //We need to request that we are removed from the map.
        for (String layerName: hi.getManager().getMapManager().getCurrent().getLayers())
        {
            Layer l = hi.getManager().getMapManager().getCurrent().getLayer(layerName);
            if (l.getTileAt(hi.getX(), hi.getY()) == this)
            {
                l.setTileAt(hi.getX(), hi.getY(), null);
                break;
            }
        }
    }

    @Override
    public boolean allowMoveFrom(ArtificialIntelligence ai)
    {
        return false;
    }

    
    
    @Override
    public void movedOn(ArtificialIntelligence ai)
    {
    }
    
    
}
