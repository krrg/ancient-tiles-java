/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import java.awt.Graphics;
import javax.swing.JComponent;

/**
 *
 * @author krr428
 */
public interface CustomRenderingTile
{
    /**
     * Render the tile.  
     * The tile in most cases will render itself at the coordinate u32, v32, and take up 
     * at most 32 by 32 pixels.  However, there are instances that this may not be true.
     * @param g -- The Graphics object on which to render.
     * @param u32 -- The horizontal offset, multiplied by 32 pixels.
     * @param v32 -- The vertical offset, multiplied by 32 pixels.
     */
    public void Render(JComponent onto, Graphics g, int u32, int v32);
}
