/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen.laygen;

import ancienttiles.Layer;
import ancienttiles.Tile;
import ancienttiles.tiles.MainTileFactory;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author krr428
 */
public class LargePictureGen extends LayerGenerator
{

    protected final static int TILE_WIDTH = 32;

    public LargePictureGen(int _width, int _height)
    {
        super(_width, _height);
    }

    public Layer generateLayer(boolean tiled, String...params)
    {
        if (params.length == 0)
        {
            throw new IllegalArgumentException("Not enough parameters. "
                    + "You must specify a filename as the first argument.");
        }

        //Scale the image up to the size we will need.
        BufferedImage ii = null;
        Layer result = null;
        try
        {
            ii = ImageIO.read(MainTileFactory.class.getResource(params[0]));
           result = generateLayer(ii, tiled, params);
            
        } catch (IOException i)
        {
            System.out.println("Error, could not load image.");
            result = generateLayer(Color.WHITE);
        }
        
        return result;
        
    }
    @Override
    public Layer generateLayer(String... params)
    {
        return generateLayer(false, params);
    }
    public Layer generateLayer(Color color, String...params)
    {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(color);
        g2d.drawLine(0, 0, 1, 1);
        return generateLayer(bi, params);
    }
    
    public Layer generateLayer(BufferedImage bufimg, boolean tiled, String...params)
    {
        if (!tiled)
        {
            return generateLayer(bufimg, params);
        }
        
        BufferedImage croppedTiled = new BufferedImage(width * TILE_WIDTH, height * TILE_WIDTH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = croppedTiled.createGraphics();
        for (int x = 0; x < croppedTiled.getWidth(); x += bufimg.getWidth())
        {
            for (int y = 0; y < croppedTiled.getHeight(); y += bufimg.getHeight())
            {
                g2d.drawImage(bufimg, x, y, null);
            }
        }
        
        return generateLayer(croppedTiled, params);
    }
    
    public Layer generateLayer(BufferedImage bufimg, String...params)
    {
        Layer backdropLayer = new Layer(width, height);
        ImageTiler it = new ImageTiler(bufimg);
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    Tile t = new Tile(it.getImageAt(x, y));
                    for (int p = 0; p < params.length; p++)
                    {
                        t.addAttribute(params[p]);
                    }
                    t.setRGBIndex(x);
                    backdropLayer.setTileAt(x, y, t);
                }
            }

            return backdropLayer;
    }

    protected class ImageTiler
    {

        private BufferedImage originalImg = null;
        private BufferedImage scaledImg = null;
        //private Image[][] cutImages = null;

        public ImageTiler(BufferedImage i)
        {
            originalImg = i; 
            System.out.println(width * TILE_WIDTH);
            BufferedImage bi = new BufferedImage(width * TILE_WIDTH, height * TILE_WIDTH, BufferedImage.TYPE_4BYTE_ABGR);
            
            Graphics2D bi2d = bi.createGraphics();
            bi2d.drawImage(originalImg, 0, 0, width * TILE_WIDTH, height * TILE_WIDTH, null);
            scaledImg = bi;

            //showTestImage(bi);
        }

        public Image getImageAt(int x, int y)
        {
            Image i = scaledImg.getSubimage(x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
            //showTestImage(i);
            return i;
        }
    }
}
