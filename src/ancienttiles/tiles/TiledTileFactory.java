/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author krr428
 */
public class TiledTileFactory
{
    private static TiledTileFactory instance = null;
    private final static int TILE_WIDTH = 32;
    
    public static TiledTileFactory getInstance()
    {
        if (instance == null)
        {
            instance = new TiledTileFactory();
        }
        return instance;
    }
    
    private Map<String, String> tiledImgs = null;
    private Map<URL, BufferedImage> cache = null;
    
    public TiledTileFactory()
    {
        tiledImgs = new HashMap<String, String>();
        cache = new HashMap<URL, BufferedImage>();
        initTiledImages();
    }
    
    private void initTiledImages()
    {
        tiledImgs.put("dirt", "mayan/dirt_path.png");
//        tiledImgs.put("dirt2", "dirt_trans_map2.png");
        tiledImgs.put("lswall", "water.gif");
        tiledImgs.put("grass", "mayan/grass_tmap.png");
        tiledImgs.put("jungle tmap1", "mayan/jungle/forest_tilemap1.png");
        tiledImgs.put("jungle tmap2", "mayan/jungle/forest_tilemap2.gif");
        tiledImgs.put("jungle tmap2b", "mayan/jungle/forest_tilemap2.png");
        tiledImgs.put("monsterblock", "monster_block.png");
    }
    
    public boolean containsImageFor(String name)
    {
        return tiledImgs.containsKey(name);
    }
    
    private void cacheImage(URL imgloc)
    {
        if (! cache.containsKey(imgloc))
        {
            try
            {                
                BufferedImage bi = ImageIO.read(imgloc);                
                cache.put(imgloc, bi);
            }
            catch (IOException e)
            {
                System.out.println("Error, could not load image for " + imgloc);
                System.out.println(e.getLocalizedMessage());
            }            
        }
        
    }
    
    public Image getImageFor(String name, int x, int y)
    {
//        if (!tiledImgs.containsKey(name))
//        {
//            throw new NoSuchElementException();
//        }
        
        x = Math.abs(x);
        y = Math.abs(y);
       
        String imgloc = tiledImgs.get(name);
        URL imgurl = getClass().getResource(imgloc);
        cacheImage(imgurl);        
        BufferedImage bi = cache.get(imgurl);
        
        
        x = x % (bi.getWidth() / TILE_WIDTH);
        y = y % (bi.getHeight() / TILE_WIDTH);
        
        //BufferedImage bi2 = new BufferedImage(TILE_WIDTH, TILE_WIDTH, BufferedImage.TYPE_INT_ARGB);
        BufferedImage bi2 = bi.getSubimage(x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
        
        
        
        if (name.endsWith("wall"))
        {
            showTestImage(bi2);
        }
        
        return bi2;
    }
    
    private void showTestImage(Image i)
    {
        ImageIcon ii = new ImageIcon(i);
        JOptionPane.showMessageDialog(null, "Test", "test", 0, ii);
    }
}
