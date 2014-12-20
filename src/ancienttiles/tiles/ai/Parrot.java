/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.RestrictedMovementTile;
import ancienttiles.Tile;
import ancienttiles.tiles.MainTileFactory;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 *
 * @author krr428
 */
public abstract class Parrot extends RotationAI
{

    private static Map<List<Integer>, Map<List<Integer>, Image>> rotationalImages = null;

    public Parrot(Image i)
    {
        super(i);
        initImages();
    }

    public Parrot(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
        initImages();
    }
    private static Map<String, Image> parrotImageCache = null;

    private static Map<String, Image> getImages()
    {
        if (parrotImageCache != null)
        {
            return parrotImageCache;
        }
        parrotImageCache = new HashMap<String, Image>();
        try
        {
            BufferedImage HDL = ImageIO.read(MainTileFactory.class.getResource("mayan/parrot1_HDL.png"));
            BufferedImage HDR = ImageIO.read(MainTileFactory.class.getResource("mayan/parrot1_HDR.png"));
            BufferedImage HUL = ImageIO.read(MainTileFactory.class.getResource("mayan/parrot1_HUL.png"));
            BufferedImage HUR = ImageIO.read(MainTileFactory.class.getResource("mayan/parrot1_HUR.png"));

            BufferedImage VDL = ImageIO.read(MainTileFactory.class.getResource("mayan/parrot1_VDL.png"));
            BufferedImage VDR = ImageIO.read(MainTileFactory.class.getResource("mayan/parrot1_VDR.png"));
            BufferedImage VUL = ImageIO.read(MainTileFactory.class.getResource("mayan/parrot1_VUL.png"));
            BufferedImage VUR = ImageIO.read(MainTileFactory.class.getResource("mayan/parrot1_VUR.png"));

            parrotImageCache.put("HDL", HDL);
            parrotImageCache.put("HDR", HDR);
            parrotImageCache.put("HUL", HUL);
            parrotImageCache.put("HUR", HUR);

            parrotImageCache.put("VDL", VDL);
            parrotImageCache.put("VDR", VDR);
            parrotImageCache.put("VUL", VUL);
            parrotImageCache.put("VUR", VUR);

            return parrotImageCache;
        }
        catch (IOException e)
        {
            System.out.println("Unable to load parrot image resources.");
            System.out.println(e.getLocalizedMessage());
            return null;
        }

    }

    private void initImages()
    {
        if (rotationalImages != null)
        {
            return;
        }
        rotationalImages = new HashMap<List<Integer>, Map<List<Integer>, Image>>();
        List<Integer> moveRight = Arrays.asList(1, 0);
        List<Integer> moveLeft = Arrays.asList(-1, 0);
        List<Integer> moveDown = Arrays.asList(0, 1);
        List<Integer> moveUp = Arrays.asList(0, -1);
        List<Integer> noMove = Arrays.asList(0, 0);

        rotationalImages.put(moveRight, new HashMap<List<Integer>, Image>());
        rotationalImages.put(moveLeft, new HashMap<List<Integer>, Image>());
        rotationalImages.put(moveUp, new HashMap<List<Integer>, Image>());
        rotationalImages.put(moveDown, new HashMap<List<Integer>, Image>());
        rotationalImages.put(noMove, new HashMap<List<Integer>, Image>());

        Map<String, Image> imgMap = getImages();

        rotationalImages.get(moveRight).put(moveDown, imgMap.get("VDR"));
        rotationalImages.get(moveRight).put(moveUp, imgMap.get("VUR"));
        rotationalImages.get(moveLeft).put(moveDown, imgMap.get("VDL"));
        rotationalImages.get(moveLeft).put(moveUp, imgMap.get("VUL"));
        rotationalImages.get(moveDown).put(moveRight, imgMap.get("HDR"));
        rotationalImages.get(moveDown).put(moveLeft, imgMap.get("HDL"));
        rotationalImages.get(moveUp).put(moveRight, imgMap.get("HUR"));
        rotationalImages.get(moveUp).put(moveLeft, imgMap.get("HUL"));

        rotationalImages.get(noMove).put(moveUp, imgMap.get("VUL"));
        rotationalImages.get(noMove).put(moveDown, imgMap.get("VDL"));
        rotationalImages.get(noMove).put(moveRight, imgMap.get("HDR"));
        rotationalImages.get(noMove).put(moveLeft, imgMap.get("HDL"));
        rotationalImages.get(noMove).put(noMove, imgMap.get("HUR"));


    }

    @Override
    protected Image getImage(double x1, double y1, double x2, double y2)
    {
        List<Integer> oldDir = Arrays.asList((int) x1, (int) y1);
        List<Integer> newDir = Arrays.asList((int) x2, (int) y2);
        //System.out.println("RotationalImages = " + rotationalImages);
        Image img = rotationalImages.get(oldDir).get(newDir);
        return img;
    }

    @Override
    protected String getHIKilledMsg()
    {
        return "Oh no! You've been eaten by a large carnivorous bird!";
    }

    @Override
    protected boolean canMove(int x, int y)
    {
        if (x < 0 || y < 0 || x >= gameManager.getCurrentMap().getWidth()
                || y >= gameManager.getCurrentMap().getHeight())
        {
            return false;
        }
        for (Tile t : gameManager.getMapManager().getCurTiles(x, y))
        {
            if (t.hasAttribute("wall"))
            {
                return false;
            }
            else if (t instanceof RestrictedMovementTile)
            {
                boolean allowed = ((RestrictedMovementTile) t).allowMoveFrom(this);
                if (!allowed)
                {
                    return false;
                }
            }
        }
        Tile t = gameManager.getCurrentMap().getLayer(getLayerName()).getTileAt(x, y);
        if (t != null && t.hasAttribute("ai"))
        {
            return false;
        }

        return true; //Doesn't matter if there is a floor or not.
    }

    @Override
    protected int getTiming()
    {
        if (this.getMetadata().containsKey("%speed"))
        {
            try
            {
                String speedStr = this.getMetadata().get("%speed");
                return Integer.parseInt(speedStr);
            }
            catch (NumberFormatException n)
            {
                return super.getTiming();
            }

        }
        else
        {
            return super.getTiming();
        }
    }
}
