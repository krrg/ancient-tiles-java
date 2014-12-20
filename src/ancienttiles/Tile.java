/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import java.awt.Image;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author krr428
 */
public class Tile implements Serializable
{
    private transient Image visual_rep = null;    
    private Map<String,String> metadata = null;
    private String primaryAttribute = null;
    private int rgb_index = 0;
    
    public Tile(Image i)
    {
        visual_rep = i;
        metadata = new HashMap<String, String>();
    }
    
    public int getRGBIndex()
    {
        return rgb_index;
    }
    
    public void setRGBIndex(int index)
    {
        rgb_index = index;
    }
    
    public Map<String, String> getMetadata()
    {
        return metadata;
    }
    
    public void addMetadata(String key, String value)
    {        
        metadata.put(key, value);
    }
    
    protected Set<String> getAttributes()
    {
        return metadata.keySet();
    }
    
    protected void setImage(Image i)
    {
        this.visual_rep = i;
    }
    
    public Image getImage()
    {
        return visual_rep;
    }
    
    public boolean hasAttribute(String attribute)
    {
        return metadata.keySet().contains(attribute);
    }
    
    public void addAttribute(String attribute)
    {
        if (primaryAttribute == null)
        {
            primaryAttribute = attribute;
        }
        
        if (attribute.trim().startsWith("%"))
        {
            Scanner sc = new Scanner(attribute.trim());
            sc.useDelimiter("=");
            String key = sc.next();
            String value = sc.next();
            metadata.put(key, value);
        }
        else
        {
            metadata.put(attribute, attribute);
        }        
    }
    
    public String getPrimaryAttribute()
    {
        return primaryAttribute;
    }

    

    
    
    
}
