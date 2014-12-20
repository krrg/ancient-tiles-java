/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen;

import ancienttiles.Layer;
import ancienttiles.LayerStack;
import ancienttiles.tiles.mapgen.laygen.LargePictureGen;
import ancienttiles.tiles.mapgen.laygen.RasterToLayer;
import java.awt.Color;

/**
 *
 * @author krr428
 */
public class TFloatMap2 extends MapFactory
{

    public TFloatMap2(int width_, int height_)
    {
        super(width_, height_);
    }

    public Layer getWalls()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);
        rtl.maprgb(0xFF0000FF, "walls/wall3.PNG", "wall");
        rtl.maprgb(0xFF000000, "mwall d", "wall");
        return rtl.generateLayer("maps/tmap1.png");
    }

    public Layer getFloor()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);
        rtl.maprgb(0xFF00FFFF, "mayan/mfloor2_80.png", "floor");
        rtl.maprgb(0xFFff8000, "mayan/mfloor.png", "floor");

        return rtl.generateLayer("maps/tmap1.png");
    }

    public Layer getUAObjs()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);
        //rtl.maprgb(0xFFFF0000, "anball_red1.gif", "uaobj");
        rtl.maprgb(0xFF990000, "key r", "redkey", "uaobj");
        rtl.maprgb(0xFFFF0000, "lock r", "redlock");
        rtl.maprgb(0xFF009900, "key g", "greenkey", "uaobj");
        rtl.maprgb(0xFF00FF00, "lock g", "greenlock");
        rtl.maprgb(0xFF000099, "key b", "bluekey", "uaobj");
        rtl.maprgb(0xFF0000FF, "lock b", "bluelock");
        rtl.maprgb(0xFF999900, "key y", "yellowkey", "uaobj");
        rtl.maprgb(0xFFFFFF00, "lock y", "yellowlock");        
        return rtl.generateLayer("maps/tmap1_pu.png");
    }

    public Layer getAI()
    {
        RasterToLayer rtl = new RasterToLayer(width, height);
                
        //rtl.maprgb(0xFFddFF00, "ai sglowtile", "ai sglowtile");
        rtl.maprgb(0xFFFFbb00, "ai jaguar", "ai jaguar");
        
        return rtl.generateLayer("maps/tmap1_ai.png");
    }

    @Override
    public LayerStack generateMap()
    {
        
        Layer baseLayer_0 = new LargePictureGen(width, height).generateLayer(Color.white);
        Layer baseLayer_1 = new LargePictureGen(width, height).generateLayer(true, "bg_cave1.png");
        Layer floorLayer = getFloor();
        Layer wallLayer = getWalls();
        Layer objLayer = getUAObjs();        
        Layer aiLayer = getAI();

        LayerStack ls = new LayerStack(width, height);
        ls.addLayer("!00 Base", baseLayer_0);
        ls.addLayer("00 Base", baseLayer_1);
        
        ls.addLayer("01 Floor", floorLayer);
        ls.addLayer("02 Walls", wallLayer);
        ls.addLayer("03 Powerups", objLayer);
        ls.addLayer("04 AI", aiLayer);
        return ls;
    }

}
