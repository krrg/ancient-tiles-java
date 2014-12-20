/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.tiles.ai.TimedObject;
import ancienttiles.tiles.mapgen.laygen.RasterToLayer;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author krr428
 */
public class Layer implements Externalizable
{
    //Represents a single map layer.

    private Map<Integer, Map<Integer, Tile>> map = null;
    private Set<TimedObject> ais = null;
    private RasterToLayer sourceRTL = null;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeObject(map);        
        out.writeObject(sourceRTL);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        Map<Integer, Map<Integer, Tile>> tempMap = (Map<Integer, Map<Integer, Tile>>)in.readObject();        
        List<Integer> rgbvals = new ArrayList();
        
        for (int row: tempMap.keySet())
        {
            for (int col: tempMap.get(row).keySet())
            {
                int rgb = tempMap.get(row).get(col).getRGBIndex();
                rgbvals.add(rgb);
                
                this.setTileAt(row, col, tempMap.get(row).get(col));
            }
        }
        
        sourceRTL = (RasterToLayer)in.readObject();
              
        Layer imageLayer = sourceRTL.loadFromRaster(rgbvals);
        
        copyImagesFrom(imageLayer);
    }    
    
    private void copyImagesFrom(Layer duplicate)
    {
        for (int row: duplicate.map.keySet())
        {
            for (int col: duplicate.map.get(row).keySet())
            {
                if (getTileAt(row, col) != null)
                {
                    getTileAt(row, col).setImage(duplicate.getTileAt(row, col).getImage());
                }
            }
        }
    }
    
    public void setRTL(RasterToLayer rtl)
    {
        this.sourceRTL = rtl;
    }
    
    public Layer()
    {
        ais = new HashSet<TimedObject>();
        map = new HashMap<Integer, Map<Integer, Tile>>();
    }
    
    public Layer(int width, int height)
    {
        ais = new HashSet<TimedObject>();
        map = new HashMap<Integer, Map<Integer, Tile>>();
        for (int i = 0; i < width; i++)
        {
            map.put(i, new HashMap<Integer, Tile>());
            for (int j = 0; j < height; j++)
            {
                map.get(i).put(j, null);
                //System.out.println("Initialization at: " + i + ", " + j);
            }
        }
    }

    public Set<TimedObject> getTimedObjects()
    {
        return ais;
    }
    
    public void pasteLayer(int offsetX, int offsetY, Layer l)
    {
        offsetX = Math.abs(offsetX);
        offsetY = Math.abs(offsetY);

        for (int i = 0; i < l.getWidth(); i++)
        {
            if (i + offsetX < this.getWidth())
            {
                for (int j = 0; j < l.getHeight(); j++)
                {
                    if (j + offsetY < this.getHeight())
                    {
                        this.setTileAt(i + offsetX, j + offsetY, l.getTileAt(i, j));
                    }
                }
            }

        }
    }

    public void setTileAt(int x, int y, Tile t)
    {
        if (getTileAt(x, y) instanceof TimedObject)
        {
            ais.remove((TimedObject)map.get(x).get(y));
        }
        
        if (map.get(x) == null)
        {
            map.put(x, new HashMap<Integer, Tile>());
        }
        map.get(x).put(y, t);
        
        if (t instanceof TimedObject)
        {
            ais.add((TimedObject)t);
        }
    }

    public Tile getTileAt(int x, int y)
    {
        //System.out.println("Getting at: " + x + " " + y);
        if (map.get(x) == null)
        {
            return null;
            
        }        
        return map.get(x).get(y);
    }

    public int getHeight()
    {
        if (map.containsKey(0))
        {
            return map.get(0).size();
        }
        else
        {
            return 0;
        }
    }

    public int getWidth()
    {
        return map.size();
    }
}
