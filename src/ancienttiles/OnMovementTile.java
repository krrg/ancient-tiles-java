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
public interface OnMovementTile
{
    public void movedOn(HumanIntelligence hi);
    public void movedOn(ArtificialIntelligence ai);
}
