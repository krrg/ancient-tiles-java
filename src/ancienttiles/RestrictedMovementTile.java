/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.tiles.ai.ArtificialIntelligence;

/**
 *
 * @author krr428
 */
public interface RestrictedMovementTile
{

    public abstract boolean allowMoveFrom(HumanIntelligence hi);

    public abstract boolean allowMoveFrom(ArtificialIntelligence ai);
}
