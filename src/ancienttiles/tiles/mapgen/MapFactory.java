/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen;

import ancienttiles.LayerStack;

/**
 *
 * @author krr428
 */
public abstract class MapFactory
{

    protected int width;
    protected int height;
    
    public MapFactory(int width_, int height_)
    {
        this.width = width_;
        this.height = height_;
    }

    public abstract LayerStack generateMap();
    
    //Default behavior is to merge the two stacks together, if possible.
    public LayerStack generateMap(LayerStack ls)
    {
        LayerStack genStack = generateMap();
        for (String s: ls.getLayers())
        {
            genStack.addLayer(s, ls.getLayer(s));
        }
        return genStack;
    }
      
    
}
