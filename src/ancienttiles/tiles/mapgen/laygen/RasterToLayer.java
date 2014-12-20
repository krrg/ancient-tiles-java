/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen.laygen;

import ancienttiles.Layer;
import ancienttiles.Tile;
import ancienttiles.tiles.MainTileFactory;
import ancienttiles.tiles.ai.ArtificialIntelligence;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author krr428
 */
public class RasterToLayer extends LayerGenerator implements Serializable
{

    private Map<Integer, String[]> rgb2tile = null; //Raster conversion map.       
    private List<AttributeEnvelope> attributesToAssign = null;
    private String layerName = null;

    public RasterToLayer(int _width, int _height, String layerName)
    {
        super(_width, _height);
        rgb2tile = new HashMap<Integer, String[]>();
        attributesToAssign = new ArrayList<AttributeEnvelope>();
        this.layerName = layerName;

        //maprgb(0xFF000000, "wall3.png", "wall");
        //maprgb(0xFF)
    }
    
    public RasterToLayer(int _width, int _height)
    {
        this(_width, _height, null);
    }

    private class AttributeEnvelope implements Serializable
    {
        private int x;
        private int y;       
        private String attr;

        public AttributeEnvelope(int x, int y, String attr)
        {
            this.x = x;
            this.y = y;
            this.attr = attr;
        }
        
    }
    
    public void mapattribute(int x, int y, String attribute)
    {
        attributesToAssign.add(new AttributeEnvelope(x, y,attribute));
    }
    
    public void maprgb(int rgb, String tilename, String... attrs)
    {
        String[] params = new String[attrs.length + 1];
        params[0] = tilename;
        for (int i = 1; i < params.length; i++)
        {
            params[i] = attrs[i - 1];
        }

        rgb2tile.put(rgb, params);
    }

    public Layer generateLayer(BufferedImage gameLayerImage, String... params)
    {
        BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi2.createGraphics();
        g2d.drawImage(gameLayerImage, 0, 0, bi2.getWidth(), bi2.getHeight(), null);
//            g2d.setColor(Color.black);
//            g2d.drawRect(0, 0, width, height);
//        showTestImage(bi2);
        

        PixelGrabber pg = new PixelGrabber(bi2, 0, 0, bi2.getWidth(), bi2.getHeight(), true);
        try
        {
            pg.grabPixels();
            Layer resultLayer = loadFromRaster((int[]) pg.getPixels());
            
            for (AttributeEnvelope ae : attributesToAssign)
            {
                if (resultLayer.getTileAt(ae.x, ae.y) != null)
                {
                    resultLayer.getTileAt(ae.x, ae.y).addAttribute(ae.attr);
                }
            }
            
            resultLayer.setRTL(this);
            
            return resultLayer;
        }
        catch (InterruptedException e)
        {
            System.out.println("Unable to load from buffered image.");
            return new Layer(width, height);
        }

    }

    public Layer generateLayer(InputStream imageInput, String... params)
    {
        try
        {
            BufferedImage bi = ImageIO.read(imageInput);
            //showTestImage(bi);
            return generateLayer(bi, params);
        }
        catch (IOException i)
        {
            System.out.println("Unable to load from input stream.");
            return new Layer(width, height);
        }
    }

    @Override
    public Layer generateLayer(String... params)
    {
        try
        {
            InputStream mapFileIn = MainTileFactory.class.getResourceAsStream(params[0]);
            Layer generatedLayer = generateLayer(mapFileIn, params);
            mapFileIn.close();
            return generatedLayer;
        }
        catch (IOException i)
        {
            System.out.println("Error in layer generation: " + i.getLocalizedMessage());
            return new Layer(width, height);
        }
        
        
//        try
//        {
//            loadBuiltInConversions();
//            BufferedImage bi = ImageIO.read(MainTileFactory.class.getResource(params[0]));
//            BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2d = bi2.createGraphics();
//            g2d.drawImage(bi, 0, 0, bi2.getWidth(), bi2.getHeight(), null);
////            g2d.setColor(Color.black);
////            g2d.drawRect(0, 0, width, height);
//            //showTestImage(bi2);
//
//            PixelGrabber pg = new PixelGrabber(bi2, 0, 0, bi2.getWidth(), bi2.getHeight(), true);
//            pg.grabPixels();
//
//            return loadFromRaster((int[]) pg.getPixels());
//        }
//        catch (IOException e)
//        {
//            System.out.println("Warning, unable to load file.\n" + e.getLocalizedMessage());
//        }
//        catch (InterruptedException ie)
//        {
//            System.out.println("Warning, unable to load file, interrupted. " + ie.getMessage());
//        }
//
//        return new Layer(width, height);
    }

    private void showTestImage(Image i)
    {
        ImageIcon ii = new ImageIcon(i);
        JOptionPane.showMessageDialog(null, "Test", "test", 0, ii);
    }
    
    public Layer loadFromRaster(List<Integer> pixel_data)
    {
        Layer l = new Layer(width, height);
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                int pixdat = pixel_data.get(y * width + x);
                
                if (pixdat != 0 && rgb2tile.containsKey(pixdat) == false)
                {
                    System.out.println("Could not identify match for: " + pixdat);
                }
                
                if (rgb2tile.containsKey(pixdat))
                {                    
                    String[] params = rgb2tile.get(pixdat);
                    String tileName = params[0];
                    String[] attr = Arrays.copyOfRange(params, 1, params.length);
                    //Tile t = MainTileFactory.getInstance().constructTile(tileName, attr);
                    Tile t = MainTileFactory.getInstance().getTile(tileName, x, y, attr);
                    if (t instanceof ArtificialIntelligence)
                    {
                        ((ArtificialIntelligence) t).setXloc(x);
                        ((ArtificialIntelligence) t).setYloc(y);
                        ((ArtificialIntelligence) t).setLayerName(layerName);
                    }
                    
                    //For serializing purposes, archive the pixel data so we can recreate this later.
                    t.setRGBIndex(pixdat);
                    l.setTileAt(x, y, t);
                }
            }
        }
//        for (int i : pixel_data)
//        {
//            int r = i - 0xFFFF0000;
//            int g = i - 0xFF00FF00;
//            int b = i - 0xFF0000FF;
//            
//            
//            System.out.println("R:" + r + " G:" + g + " B:" + b);           
//            
//        }

        return l;
    }

    public Layer loadFromRaster(int[] pixel_data)
    {
        List<Integer> pixels = new ArrayList<Integer>();
        for (int i = 0; i < pixel_data.length; i++)
        {
            pixels.add(pixel_data[i]);
        }
        
        return loadFromRaster(pixels);
    }

}
