/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen;

import ancienttiles.Layer;
import ancienttiles.LayerStack;
import ancienttiles.tiles.mapgen.laygen.LargePictureGen;
import ancienttiles.tiles.mapgen.laygen.RasterToLayer;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Scanner;

/**
 *
 * @author krr428
 */
public class ZTFloatMap3 extends ZipFloatMap
{

    public ZTFloatMap3()
    {
        super();
    }

    public ZTFloatMap3(int width_, int height_)
    {
        super(width_, height_);
    }

    private Layer getBackFill()
    {
        LargePictureGen lpg = new LargePictureGen(width, height);
        return lpg.generateLayer(Color.WHITE);
    }

    private Layer getBackground()
    {
        LargePictureGen lpg = new LargePictureGen(width, height);
        
        if (! getLayerImages().containsKey("background"))
        {
            return lpg.generateLayer(Color.white);
        }
        else
        {
            BufferedImage bgImg = getLayerImages().get("background");
            return lpg.generateLayer(bgImg, true);
                    
        }
        
    }

    private Layer getFloor()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);

//        rtl.maprgb(0xFFffffff, "lvfloor", "floor");
//        rtl.maprgb(0xFFff00ff, "mayan/mfloor3_75.png", "floor");
//        rtl.maprgb(0xFFffff00, "lvfloor", "floor");
        rtl.maprgb(0xFFffffff, "mayan/mfloor2_80.png", "floor");
        rtl.maprgb(0xFFff00ff, "mayan/mfloor3_75.png", "floor");
        rtl.maprgb(0xFFffff00, "mayan/mfloor80.png", "floor");
        rtl.maprgb(0xffffffaa, "dirt", "floor");
        rtl.maprgb(0xFFaaffaa, "grass", "floor");
        
//          rtl.maprgb(0xFFffffff, "dirt", "floor");
//          rtl.maprgb(0xFFffff00, "dirt2", "floor");
//        rtl.maprgb(0xFFffffff, "dirt_trans.png", "floor");
//        

        BufferedImage floorImg = getLayerImages().get("floor");
        return rtl.generateLayer(floorImg);

    }

    private Layer getWalls()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);
//        rtl.maprgb(0xFF000000, "lswall", "wall");
        rtl.maprgb(0xFF000000, "mwall d", "wall");
        rtl.maprgb(0xFF0000FF, "walls/wall3.PNG", "wall");
        rtl.maprgb(0xFF003333, "glyphs/dglyph.png", "wall");
        rtl.maprgb(0xFF00aaaa, "glyphs/dglyph.png", "wall"); //LEGACY
        
        rtl.maprgb(0xFF335500, "toggle wall ON", "toggle wall", "%state=on");
        rtl.maprgb(0xFF339900, "toggle wall OFF", "toggle wall", "%state=off");

        return rtl.generateLayer(getLayerImages().get("walls"));
    }

    private Layer getUAObjects()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);
        executeConfigFile("attributes.txt", rtl);
        
        //rtl.maprgb(0xFFFF0000, "anball_red1.gif", "uaobj");
        rtl.maprgb(0xFF990000, "key r", "redkey", "uaobj");
        rtl.maprgb(0xFFFF0000, "lock r", "redlock");
        rtl.maprgb(0xFF009900, "key g", "greenkey", "uaobj");
        rtl.maprgb(0xFF00FF00, "lock g", "greenlock");
        rtl.maprgb(0xFF000099, "key b", "bluekey", "uaobj");
        rtl.maprgb(0xFF0000FF, "lock b", "bluelock");
        rtl.maprgb(0xFF999900, "key y", "yellowkey", "uaobj");
        rtl.maprgb(0xFFFFFF00, "lock y", "yellowlock");

        rtl.maprgb(0xFFbbFF00, "toggle btn", "toggle btn");
        rtl.maprgb(0xFF335500, "toggle wall ON", "toggle wall", "%state=on");
        rtl.maprgb(0xFF339900, "toggle wall OFF", "toggle wall", "%state=off");

        rtl.maprgb(0xFF770077, "helptile", "helptile");
        rtl.maprgb(0xFF552200, "barkcodex", "uaobj", "barkcodex");

        return rtl.generateLayer(getLayerImages().get("uaobjs"));
    }

    private Layer getAIHoverLayer()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);

        rtl.maprgb(0xFFff6600, "ai hover", "ai hoverH", "ai", "floor");
        rtl.maprgb(0xFFaa5400, "ai hover", "ai hoverV", "ai", "floor");
        rtl.maprgb(0xFFff9977, "ai hover", "ai hoverP", "ai", "floor");

        return rtl.generateLayer(getLayerImages().get("ai"));
    }

    private Layer getAILayer()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);

        rtl.maprgb(0xFFffbb00, "ai jaguar", "ai jaguarX", "ai");
        rtl.maprgb(0xFFff00bb, "ai jaguar", "ai jaguarY", "ai");

        rtl.maprgb(0xFF88ff88, "ai parrot", "ParrotCW", "ai", "%start=Left");
        rtl.maprgb(0xFF99ff88, "ai parrot", "ParrotCW", "ai", "%start=Up");
        
        
        rtl.maprgb(0xFF88ff99, "ai parrot", "ParrotCW", "ai", "%start=Right");
        rtl.maprgb(0xFF99ff99, "ai parrot", "ParrotCW", "ai", "%start=Down");
        rtl.maprgb(0xFF33ffcc, "ai parrot", "ParrotCCW", "ai", "%start=Left");
        rtl.maprgb(0xFF33ffbb, "ai parrot", "ParrotCCW", "ai", "%start=Right");
        rtl.maprgb(0xFF22ffcc, "ai parrot", "ParrotCCW", "ai", "%start=Up");
        rtl.maprgb(0xFF22ffbb, "ai parrot", "ParrotCCW", "ai", "%start=Down");

        
        


        rtl.maprgb(0xFF5b2b00, "ai monkey", "ai monkey", "ai");

        return rtl.generateLayer(getLayerImages().get("ai"));
    }

    private void executeHIStartsConfig(String line, RasterToLayer rtl)
    {
        Scanner lineScan = new Scanner(line);
        lineScan.next(); //Get rid of the "Next" token.
        int color;
        if (lineScan.hasNext())
        {
            String colorString = lineScan.next();
            if (colorString.equals("Default"))
            {
                //This will actually be a long, but should filter down to an int.
                //For some reason, Integer.parseInt() does not want to work here.
                colorString = "0xFF008888";
            }
            color = Long.decode(colorString).intValue();
        }
        else
        {
            System.out.println("Error in configuration file!\nMissing color string.");
            return;
        }
        if (lineScan.hasNext())
        {
            String urlString = lineScan.next();
            if (line.startsWith("Next"))
            {
                rtl.maprgb(color, "blue_block1.png", "HI ExitSquare", "%exit=" + urlString);
            }
            else if (line.startsWith("Jump"))
            {
                rtl.maprgb(color, "blue_block1.png", "HI JumpSquare", "%exit=" + urlString);
            }            

        }
        else
        {
            System.out.println("Error in configuration file!\nMissing file string.");

        }
    }

    private void executeConfigAttrLine(String line, RasterToLayer rtl)
    {
        Scanner lineScan = new Scanner(line);
        lineScan.next(); //Get rid of 'Attr' token.
        //This is not very error proof.        
        int x = lineScan.nextInt();
        int y = lineScan.nextInt();
        String str = lineScan.nextLine();

        rtl.mapattribute(x, y, str);

    }

    private void executeConfigLine(String line, RasterToLayer rtl)
    {
        if (line.startsWith("Next") || line.startsWith("Jump"))
        {
            executeHIStartsConfig(line, rtl);
        }
        else if (line.startsWith("Attr"))
        {
            executeConfigAttrLine(line, rtl);
        }
    }

    private void executeConfigFile(String internalFileName, RasterToLayer rtl)
    {
        if (getMetaData().containsKey(internalFileName))
        {
            String configuration = getMetaData().get(internalFileName).toString();
            Scanner sc = new Scanner(configuration);
//      Format of Config file:  
//      Next Default (0xFF008888) basic_map.zip
//      Next 0xFFdddddd jaguar_map.zip        
            while (sc.hasNextLine())
            {
                executeConfigLine(sc.nextLine(), rtl);
            }
        }
        else
        {
            System.out.println("No " + internalFileName + " found in archive.  Perhaps it was not necessary or is missing...");            
        }


    }

    private Layer getHIStarts()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);

        rtl.maprgb(0xFF00FF00, "transparent.png", "HI MainStart");
        rtl.maprgb(0xFF00FFFF, "transparent.png", "HI CheckStart");
        rtl.maprgb(0xFF008888, "blue_block1.png", "HI ExitSquare");
        rtl.maprgb(0xFFff00bb, "mayan/temple_nocolor.png", "HI EnterChildSquare");
        
        executeConfigFile("config.txt", rtl);

        return rtl.generateLayer(getLayerImages().get("histarts"));
    }

    private void autodetectSize()
    {
        if (width == -1)
        {
            //Then look at the floor, and use that as the width and height.
            width = getLayerImages().get("floor").getWidth();
        }
        if (height == -1)
        {
            height = getLayerImages().get("floor").getHeight();
        }
    }

    private Layer getAIPaths()
    {
        if (!getLayerImages().containsKey("aipaths"))
        {
            return null;
        }

        RasterToLayer rtl = new RasterToLayer(width, height);

        rtl.maprgb(0xFFffeebb, "transparent_blue_overlay20p.png", "path");

        return rtl.generateLayer(getLayerImages().get("aipaths"));
    }

    @Override
    public LayerStack generateMap()
    {
        System.gc();
        /*
         A typical map has several layers, as listed here:
         00a A colored background, to ensure that the map always has a background.
         00b In most cases a picture to serve as a background.

         01a Floor Layer
         02a Wall Layer
         03a User Object Layer
         04a AI Layer
         */

        MapGenForm mgf = new MapGenForm();
        mgf.setLocationRelativeTo(null);
        mgf.loadProgress.setString("Loading map...");
        mgf.loadProgress.setStringPainted(true);
        mgf.setAlwaysOnTop(true);
        //mgf.setVisible(true);        
        autodetectSize();


        LayerStack ls = new LayerStack(width, height);

        ls.addLayer("00a Background", getBackFill());
        ls.addLayer("00b Background", getBackground());
        mgf.loadProgress.setValue(10);
        Layer aipath = getAIPaths();
        if (aipath != null)
        {
            ls.addLayer("00c Paths", aipath);
        }
        mgf.loadProgress.setValue(20);
        ls.addLayer("01a Floor", getFloor());
        mgf.loadProgress.setValue(30);
        ls.addLayer("02a Walls", getWalls());
        mgf.loadProgress.setValue(40);
        ls.addLayer("03a UAObjects", getUAObjects());
        mgf.loadProgress.setValue(60);
        ls.addLayer("04a AI", getAILayer());
        ls.addLayer("04b HoverAI", getAIHoverLayer());
        mgf.loadProgress.setValue(80);
        ls.addLayer("HumInt", getHIStarts());
        mgf.loadProgress.setValue(90);
        ls.setOrigin(getOrigin());
        mgf.loadProgress.setValue(100);

        mgf.setVisible(false);
        return ls;
    }
}
