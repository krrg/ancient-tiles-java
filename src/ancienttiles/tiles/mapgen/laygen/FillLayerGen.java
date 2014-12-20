/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen.laygen;

import ancienttiles.Layer;
import ancienttiles.tiles.MainTileFactory;
import java.util.Arrays;

/**
 *
 * @author krr428
 */
public class FillLayerGen extends LayerGenerator
{
    public static final String DEFAULT_BASE_TILE = "sand";
    public FillLayerGen(int _width, int _height)
    {
        super(_width, _height);
    }

    @Override    
    public Layer generateLayer(String...params)
    {
        if (params.length < 1)
        {
            params = new String[1];
            params[0] = DEFAULT_BASE_TILE;
        }
        
        String tileName = params[0];
        String[] tileAttributes = new String[0];
        if (params.length > 1)
        {
            tileAttributes = Arrays.copyOfRange(params, 1, params.length);
        }        
        
        Layer baseLayer = new Layer(width, height);
        
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                baseLayer.setTileAt(i, j, 
                        MainTileFactory.getInstance().constructTile(tileName, tileAttributes));
            }
        }        
        
        return baseLayer;
    }
    
}
