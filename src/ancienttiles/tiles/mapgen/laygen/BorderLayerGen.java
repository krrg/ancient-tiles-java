/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.mapgen.laygen;

import ancienttiles.Layer;
import ancienttiles.Tile;
import ancienttiles.tiles.MainTileFactory;

/**
 *
 * @author krr428
 */
public class BorderLayerGen extends LayerGenerator
{

    public BorderLayerGen(int _width, int _height)
    {
        super(_width, _height);
    }

    @Override
    public Layer generateLayer(String... params)
    {
        if (params.length == 0)
        {
            params = new String[1];
            params[0] = "wall";
        }

        Layer l = new Layer(width, height);

        for (int x = 0; x < width; x++)
        {
            Tile t = MainTileFactory.getInstance().constructTile(params[0]);
            Tile t2 = MainTileFactory.getInstance().constructTile(params[0]);
            for (int p = 1; p < params.length; p++)
            {
                t.addAttribute(params[p]);
                t2.addAttribute(params[p]);
            }

            l.setTileAt(x, 0, t);
            l.setTileAt(x, height - 1, t2);

        }
        for (int y = 0; y < width; y++)
        {
            Tile t = MainTileFactory.getInstance().constructTile(params[0]);
            Tile t2 = MainTileFactory.getInstance().constructTile(params[0]);
            for (int p = 1; p < params.length; p++)
            {
                t.addAttribute(params[p]);
                t2.addAttribute(params[p]);
            }

            l.setTileAt(0, y, t);
            l.setTileAt(width - 1, y, t2);

        }
        
        return l;

    }
}
