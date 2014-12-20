/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ancienttiles.math;

/**
 *
 * @author krr428
 */
public class Matrix
{
    private double[][] values = null;

    public Matrix(int m_rows, int n_cols)
    {
        values = new double[m_rows][n_cols];
    }

    public Matrix(double[][] values)
    {
        this.values = values;
    }

    public void set(int i, int j, double value)
    {
        values[i][j] = value;
    }

    public double get(int i, int j)
    {
        return values[i][j];
    }

    public int getM()
    {
        return values.length;
    }

    public int getN()
    {
        return values[0].length;
    }

    public Matrix Add(Matrix other)
    {
        return null;
    }

    public Matrix Sub(Matrix other)
    {
        return null;
    }



    public Matrix Mult(Matrix other)
    {
        if (this.getN() != other.getM())
        {
            throw new MatrixComputationException();
        }              
        
        if (this.getN() != other.getM())
        {
            throw new MatrixComputationException();
        }
        
        Matrix result = new Matrix(this.getM(), other.getN());
        for (int i = 0; i < this.getM(); i++)
        {
            for (int j = 0; j < other.getN(); j++)
            {
                double sum = 0;
                for (int k = 0; k < this.getN(); k++)
                {
                   sum += this.get(i, k) * other.get(k, j);
                }
                result.set(i, j, sum);
            }
        }
        
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();       

        for (int i = 0; i < getM(); i++)
        {
            sb.append("[");
            for (int j = 0; j < getN(); j++)
            {
                sb.append(get(i, j));
                sb.append("\t");
            }
            sb.append("]\n");
        }
        
        return sb.toString();
    }



    public class MatrixComputationException extends ArithmeticException
    {

    }

//    public static void main(String [] args)
//    {
//        double [][] ma1 = {{1, 2, 3},{3, 4, 5}, {5, 6, 7}};
//        double [][] ma2 = {{1, 3, 5},{0, -4,1}, {0, -1, 3}};
//
//        Matrix m1 = new Matrix(ma1);
//        System.out.println("Matrix 1 is a m = " + m1.getM() + " by n = " + m1.getN() );
//        System.out.println(m1.toString());
//        Matrix m2 = new Matrix(ma2);        
//        System.out.println("Matrix 2 is a m = " + m2.getM() + " by n = " + m2.getN() );
//        System.out.println(m2.toString());
//
//        Matrix result = m1.RightMult(m2);
//        System.out.println("Matrix Result is a m = " + result.getM() + " by n = " + result.getN() );
//        System.out.println(result.toString());
//    }
}
