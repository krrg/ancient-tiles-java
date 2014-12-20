/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.tiles.ai;

import ancienttiles.AbstractGameManager;
import ancienttiles.math.Matrix;
import java.awt.Image;

/**
 *
 * @author krr428
 */
public abstract class RotationAI extends MovingHostileAI
{
    private double moveX = 0;
    private double moveY = 0;    
    private Matrix rotationMatrix;

    public RotationAI(Image i)
    {
        super(i);
    }

    public RotationAI(Image i, int x, int y, String layer)
    {
        super(i, x, y, layer);
    }

    protected abstract double[][] getRotationMatrix();
    protected abstract Image getImage(double x1, double y1, double x2, double y2);
    
    private void initMove()
    {
        //-pi halves
        rotationMatrix = new Matrix(getRotationMatrix());

        if (!getMetadata().containsKey("%start"))
        {
            this.setImage(this.getImage(0, 0, moveX, moveY));
            return;
        }
        
        String startPosition = getMetadata().get("%start").trim().toLowerCase();
        
        System.out.println("Start Position listed as: " + startPosition);
        
        if (startPosition.startsWith("d"))
        {
            moveX = 0;
            moveY = 1;
        }
        else if (startPosition.startsWith("u"))
        {
            moveX = 0;
            moveY = -1;
        }
        else if (startPosition.startsWith("l"))
        {
            moveX = -1;
            moveY = 0;
        }
        else if (startPosition.startsWith("r"))
        {
            moveX = 1;
            moveY = 0;
        }
        
        getMetadata().remove("%start");
        this.setImage(this.getImage(0, 0, moveX, moveY));
    }

    @Override
    public void init(AbstractGameManager gm)
    {
        super.init(gm); 
        initMove();
    }
    
    @Override
    protected int getMoveX()
    {
        return (int)moveX;
    }

    @Override
    protected int getMoveY()
    {
        return (int)moveY;
    }

    @Override
    protected void turn()
    {        
        double oldMoveX = moveX;
        double oldMoveY = moveY;
        
        Matrix moveVector = new Matrix(new double[][] {{moveX},{moveY}});
        Matrix newMoveVector = rotationMatrix.Mult(moveVector);
        moveX = newMoveVector.get(0, 0);
        moveY = newMoveVector.get(1, 0);
        
        this.setImage(this.getImage(oldMoveX, oldMoveY, moveX, moveY)); //Get a change of image.
        
    }


}
