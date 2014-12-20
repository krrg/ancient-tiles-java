/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.tiles.ai.ArtificialIntelligence;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;

/**
 *
 * @author krr428
 */
public abstract class Button extends ArtificialIntelligence implements OnMovementTile, ActionListener, CustomRenderingTile
{
    /**
     * Creates a new button with the specified image map.
     * @param i -- The 32x64 image.  The left one is the default, the right one is the pushed image.
     */
    public Button(Image i)
    {        
        super(i);
    }

    public Button(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }    

    @Override
    public void init(AbstractGameManager gm)
    {
    }

    @Override
    public void kill()
    {
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
    }

    @Override
    public void Render(JComponent onto, Graphics g, int u32, int v32)
    {
    }
    
    
}
