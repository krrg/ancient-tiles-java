/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import java.awt.Image;

/**
 *
 * @author krr428
 */
public class ParrotCCW extends Parrot
{

    public ParrotCCW(Image i)
    {
        super(i);
    }

    public ParrotCCW(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    @Override
    protected double[][] getRotationMatrix()
    {
        return new double[][] {{0, 1},{-1,0}};
    }
    
}
