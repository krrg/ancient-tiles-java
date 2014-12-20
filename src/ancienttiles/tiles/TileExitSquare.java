/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles;

import ancienttiles.CustomRenderingTile;
import ancienttiles.HumanIntelligence;
import ancienttiles.TileRenderer;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JComponent;

/**
 *
 * @author krr428
 */
public class TileExitSquare extends TileJumpSquare implements CustomRenderingTile
{
    private TileRenderer arrowsRenderer = null;
    
    @Override
    public void Render(JComponent onto, Graphics g, int u32, int v32)
    {
        if (arrowsRenderer == null)
        {
            arrowsRenderer = new TileRenderer(this);
        }
        arrowsRenderer.Render(onto, g, u32, v32);
    }
    

    public TileExitSquare(Image i)
    {
        super(i);
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {
        hi.getInventoryMgr().clearInventory(); //resets the inventory.
        super.movedOn(hi); //To change body of generated methods, choose Tools | Templates.
    }

    
}
