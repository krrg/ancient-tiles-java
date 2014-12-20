/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles;

import ancienttiles.Tile;

/**
 *
 * @author krr428
 */

public interface TileFactory
{
    @Deprecated
    public Tile constructTile(String name); 
    @Deprecated
    public Tile constructTile(String name, String...attr);
}
