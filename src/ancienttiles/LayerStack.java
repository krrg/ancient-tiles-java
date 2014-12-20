/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.tiles.ai.TimedObject;
import ancienttiles.tiles.mapgen.laygen.RasterToLayer;
import java.awt.Point;
import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author krr428
 */
public class LayerStack implements Serializable
{

    private int size_width;
    private int size_height;
    private Map<String, Layer> layers = null;
    private URL origin;
    private LayerStack parentMap = null;
    
    
    public LayerStack(int width, int height)
    {
        this.size_height = height;
        this.size_width = width;
        this.layers = new TreeMap<String, Layer>();
    }
    
    public LayerStack(LayerStack oldCopy)
    {
        this(oldCopy.getWidth(), oldCopy.getHeight());
        for (String layerName: oldCopy.getLayers())
        {
            this.addLayer(layerName, new Layer(size_width, size_height));
            this.getLayer(layerName).pasteLayer(0, 0, oldCopy.getLayer(layerName));
        }
    }
    
    
    
    public void setParent(LayerStack parent)
    {
        this.parentMap = parent;
    }
    
    public LayerStack getParent()
    {
        return this.parentMap;
    }
    
    public void setOrigin(URL url)
    {
        this.origin = url;
    }
    
    public URL getOrigin()
    {
        return origin;
    }    
    
    public Point getPreferredHIStart()
    {        
        for (int i = 0; i < getWidth(); i++)
        {
            for (int j = 0; j < getHeight(); j++)
            {
                if (getLayer("HumInt").getTileAt(i, j) != null
                        && getLayer("HumInt").getTileAt(i, j).hasAttribute("HI MainStart"))
                {
                    return new Point(i, j);
                }
            }
        }
        return new Point (0, 0); //Since the user did not provide a starting point, we just have to guess.
    }
    
    public Set<TimedObject> getTimedObjects()
    {
        Set<TimedObject> timedObjs = new HashSet<TimedObject>();
        for (String layerName: getLayers())
        {
            timedObjs.addAll(getLayer(layerName).getTimedObjects());
        }
        
        return timedObjs;
    }
    
    public int getWidth()
    {
        return size_width;
    }
    
    public int getHeight()
    {
        return size_height;
    }

    public final boolean addLayer(String name, Layer l)
    {
        if (l.getHeight() != size_height || l.getWidth() != size_width)
        {
            System.out.println("WARNING: Attempted to add layer, but does not fit dimensions.");
            return false;
        }
        layers.put(name, l);
        
        return true;
    }
    
    public final Layer getLayer(String s)
    {
        if (! layers.containsKey(s))
        {
            addLayer(s, new Layer(size_width, size_height));
        }
        return layers.get(s);
    }
    
    public Iterable<String> getLayers()
    {
        return layers.keySet();
    }
}
