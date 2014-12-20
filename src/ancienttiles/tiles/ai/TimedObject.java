/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.AbstractGameManager;

/**
 *
 * @author krr428
 */
public interface TimedObject
{    
    public void init(AbstractGameManager gm);
    public void kill();
}
