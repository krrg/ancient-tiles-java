/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.OnMovementTile;
import ancienttiles.Tile;
import java.awt.Image;

/**
 *
 * @author krr428
 */
public abstract class ArtificialIntelligence extends Tile implements OnMovementTile, TimedObject
{

    public ArtificialIntelligence(Image i)
    {
        super(i);
    }
    
    public ArtificialIntelligence(Image i, int x, int y, String layer)
    {
        super(i);
        this.xloc = x;
        this.yloc = y;
        this.layerName = layer;
    }
           
    protected int xloc;
    protected int yloc;
    protected String layerName;
    
    public String getLayerName()
    {
        return layerName;
    }

    public int getXloc()
    {
        return xloc;
    }

    public void setXloc(int xloc)
    {
        this.xloc = xloc;
    }

    public int getYloc()
    {
        return yloc;
    }

    public void setYloc(int yloc)
    {
        this.yloc = yloc;
    }
    
    public void setLayerName(String name)
    {
        this.layerName = name;
    }

    @Override
    public void movedOn(ArtificialIntelligence ai)
    {
    }

    
    
}
