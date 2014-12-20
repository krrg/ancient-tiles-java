/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen;

import ancienttiles.Layer;
import ancienttiles.LayerStack;
import ancienttiles.tiles.mapgen.laygen.FillLayerGen;
import ancienttiles.tiles.mapgen.laygen.LargePictureGen;
import ancienttiles.tiles.mapgen.laygen.RasterToLayer;

/**
 *
 * @author krr428
 */
public class TFloatMap1 extends MapFactory
{

    public TFloatMap1(int width_, int height_)
    {
        super(width_, height_);
    }

    @Override
    public LayerStack generateMap()
    {
        LayerStack ls = new LayerStack(width, height);
        
        Layer backgroundLayer = new LargePictureGen(width, height).generateLayer("background2.png");
        Layer tmp_platformLayer = new FillLayerGen(width - 2, height - 2).generateLayer("black_tp_sp.png", "floor");
        Layer platformLayer = new Layer(width, height);
        platformLayer.pasteLayer(1, 1, tmp_platformLayer);        
//        Layer tmpWallLayer = new BorderLayerGen(width - 2, height - 2).generateLayer("walls/wall3.PNG", "wall");
        RasterToLayer rtl = new RasterToLayer(width - 2, height - 2);
        rtl.maprgb(0xFF000000, "transparent_blue_overlay20p.png", "overlay");
        Layer tmpWallLayer = rtl.generateLayer("test_circle.png");
        Layer wallLayer = new Layer(width, height);
        wallLayer.pasteLayer(1, 1, tmpWallLayer);
        
        ls.addLayer("00 Base", backgroundLayer);
        ls.addLayer("01 Floor", platformLayer);
        ls.addLayer("02 Wall", wallLayer);        
        
        return ls;
    }
    
}
