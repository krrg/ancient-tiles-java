/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles;

import ancienttiles.tiles.MainTileFactory;
import ancienttiles.tiles.ai.ArtificialIntelligence;
import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author krr428
 */
public class ToggleTile extends Tile implements RestrictedMovementTile
{

    private boolean active = false;
    
    public ToggleTile(Image i)
    {
        super(i);        
    }

    @Override
    public void addMetadata(String key, String value)
    {
        super.addMetadata(key, value);
        if (key.trim().equals("%state") && value.trim().equals("on"))
        {
            setActive(true);
        }
        else
        {
            setActive(false);
        }
    }

    protected void setActive(boolean on)
    {
        if (on)
        {
            active = true;
        }
        else
        {
            active = false;
        }
        updatePicture();
    }

    public void toggle()
    {
        setActive(!active);
    }

    private void updatePicture()
    {
        if (getMetadata().containsKey("%imgloc"))
        {
            String imgLoc = getMetadata().get("%imgloc");

            try
            {
                if (active)
                {
                    imgLoc = imgLoc.replace("OFF", "ON").trim();
                    URL url = new URL(imgLoc);
                    this.setImage(MainTileFactory.getInstance().getImage(url));
                }
                else
                {
                    imgLoc = imgLoc.replace("ON", "OFF").trim();
                    URL url = new URL(imgLoc);
                    this.setImage(MainTileFactory.getInstance().getImage(url));
                }
            }
            catch (MalformedURLException me)
            {
                if (active)
                {
                    this.setImage(MainTileFactory.getInstance().getImage("toggle wall on"));
                }
                else
                {
                    this.setImage(MainTileFactory.getInstance().getImage("toggle wall off"));
                }
            }
        }

    }

    @Override
    public boolean allowMoveFrom(HumanIntelligence hi)
    {
        return !active;
    }

    @Override
    public boolean allowMoveFrom(ArtificialIntelligence ai)
    {
        return !active;
    }
}
