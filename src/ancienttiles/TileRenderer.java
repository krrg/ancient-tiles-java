/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import java.awt.Graphics;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 *
 * @author krr428
 */
public class TileRenderer implements CustomRenderingTile
{
    private ImageIcon icon = null;
    private Tile renderTile;
    
    public TileRenderer(Tile t)
    {
        this.renderTile = t;
        initImageIcon();
    }
    
    private void initImageIcon()
    {
        //First, get the image location, from the attributes.
        String imgloc = renderTile.getMetadata().get("%imgloc");
        try
        {
            icon = new ImageIcon(new URL(imgloc));
        }
        catch (MalformedURLException me)
        {
            icon = null;
        }                
    }
    
    @Override
    public void Render(JComponent onto, Graphics g, int u32, int v32)
    {
        if (icon == null)
        {
            icon = new ImageIcon(renderTile.getImage());
        }
        
        icon.paintIcon(onto, g, u32, v32);
    }
    
}
