/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen.laygen;

import ancienttiles.Layer;

/**
 *
 * @author krr428
 */
public abstract class LayerGenerator
{

    protected int width;
    protected int height;

    public LayerGenerator(int _width, int _height)
    {
        this.width = _width;
        this.height = _height;
    }
    
    public abstract Layer generateLayer(String...params);
    
    
}
