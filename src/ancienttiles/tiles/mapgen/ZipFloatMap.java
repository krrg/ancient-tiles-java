/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ancienttiles.tiles.mapgen;

import ancienttiles.LayerStack;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author krr428
 */
public abstract class ZipFloatMap extends MapFactory
{
    private Map<String, BufferedImage> mapLayers;
    private Map<String, Object> metaData;
    private URL origin = null;

    public ZipFloatMap()
    {
        this(-1, -1);
    }

    public ZipFloatMap(int width_, int height_)
    {
        super(width_, height_);
        mapLayers = new HashMap<String, BufferedImage>();
        metaData = new HashMap<String, Object>();
    }

    protected Map<String, BufferedImage> getLayerImages()
    {
        return mapLayers;
    }

    protected Map<String, Object> getMetaData()
    {
        return metaData;
    }

    protected URL getOrigin()
    {
        return origin;
    }

    protected MapGenForm initMapGenForm()
    {
        MapGenForm mgf = new MapGenForm();
        mgf.setLocationRelativeTo(null);
        mgf.loadProgress.setString("Loading map data archive...");
        mgf.loadProgress.setStringPainted(true);
        mgf.setAlwaysOnTop(true);
        mgf.setVisible(true);
        return mgf;
    }

    private void deceptiveUpdateMGF(MapGenForm mgf)
    {
        int value = mgf.loadProgress.getValue();
        if (value == 0)
        {
            value = 30;
        }
        else
        {
            value += (100 - value) / 25.0;
        }
        System.out.println("Value: " + value);
        mgf.loadProgress.setValue(value);
    }

    public LayerStack generateMap(URL fileName)
    {
        MapGenForm mgf = initMapGenForm();
        mgf.setVisible(false);

        this.origin = fileName;
        InputStream fStream = null;
        try
        {
            fStream = fileName.openStream();
            ZipInputStream zis = new ZipInputStream(fStream);
            mapLayers = new HashMap<String, BufferedImage>();

            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry())
            {
                deceptiveUpdateMGF(mgf);
                String zipEntName = ze.getName().toLowerCase();
                if (zipEntName.endsWith(".png") ||
                        zipEntName.endsWith(".jpg") ||
                        zipEntName.endsWith(".gif"))
                {
                    mgf.loadProgress.setString("Loading: " + zipEntName);
                    BufferedImage bi = ImageIO.read(zis);
                    zipEntName = zipEntName.substring(0, zipEntName.length() - 4);

                    mapLayers.put(zipEntName, bi);
                }
                else if (zipEntName.endsWith(".txt") || zipEntName.endsWith(".xml"))
                {
                    //Then read in the file and store it as a big string in the map.
                    Scanner sc = new Scanner(zis);
                    StringBuilder sb = new StringBuilder();
                    while (sc.hasNextLine())
                    {
                        sb.append(sc.nextLine());
                        sb.append("\n");
                    }
                    metaData.put(zipEntName, sb.toString());
                }

            }
            zis.close();
            mgf.setVisible(false);
            return generateMap();

        }
        catch (IOException i)
        {
            System.err.println(i.getLocalizedMessage());
            return null;
        }
        finally
        {
            mgf.setVisible(false);
            mgf.dispose();
        }


    }



}
