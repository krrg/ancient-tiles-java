/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.griddisplay.TileDisplay.MessageCallback;
import ancienttiles.tiles.ai.ArtificialIntelligence;
import ancienttiles.tiles.ai.MovingHostileAI;
import java.awt.Graphics;
import java.awt.Image;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;

/**
 *
 * @author krr428
 */
public class GlowGlyphTile extends ArtificialIntelligence implements RestrictedMovementTile, CustomRenderingTile
{

    private AbstractGameManager gameManager = null;
    private String ourLayer = null;
    private Set<MessageCallback> callbacks = null;
    private TileRenderer tileRenderingService = null;


    public GlowGlyphTile(Image i)
    {
        super(i);        
    }

    public GlowGlyphTile(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    @Override
    public void Render(JComponent onto, Graphics g, int u32, int v32)
    {
        if (tileRenderingService == null)
        {
            tileRenderingService = new TileRenderer(this);
        }
        tileRenderingService.Render(onto, g, u32, v32);
    }

    @Override
    public void movedOn(HumanIntelligence hi)
    {
    }

    @Override
    public void init(AbstractGameManager gm)
    {
        this.gameManager = gm;
        ourLayer = discoverLayer();
        callbacks = new HashSet<MessageCallback>();
    }

    private String discoverLayer()
    {
        for (String layer: gameManager.getCurrentMap().getLayers())
        {
            Tile t = gameManager.getCurrentMap().getLayer(layer).getTileAt(xloc, yloc);
            if (t == this)
            {
                return layer;
            }
        }

        return null;
    }

    @Override
    public void kill()
    {
        for (MessageCallback mc : callbacks)
        {
            mc.remove();
        }
    }

    @Override
    public boolean allowMoveFrom(HumanIntelligence hi)
    {
        for (int i = 0; i < gameManager.getCurrentMap().getWidth(); i++)
        {
            for (int j = 0; j < gameManager.getCurrentMap().getHeight(); j++)
            {
                Tile t = gameManager.getCurrentMap().getLayer(ourLayer).getTileAt(i, j);
                if (t != null && t.hasAttribute("barkcodex"))
                {
                    displayMessage();
                    return false;
                }
            }
        }

        return true;
    }

    private void displayMessage()
    {
//        MessageCallback mc = gameManager.getViewManager().getView().displayMessage("You need to collect all of the bark scrolls!", null);
//        callbacks.add(mc);
        gameManager.getViewManager().getView().displayMessageTooltip("Blocked!", xloc, yloc);

    }

    @Override
    public boolean allowMoveFrom(ArtificialIntelligence ai)
    {
        if (ai instanceof MovingHostileAI)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
