package com.github.elijahgabrielletanabe.Model;

import java.util.Arrays;
import java.util.Random;

public class Matrix
{
    public interface Func { double apply(double val); }

    private double[][] matrix;

    public Matrix(int rows, int columns)
    {
        this.matrix = new double[rows][columns];

        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[i].length; j++)
            {
                this.matrix[i][j] = 0;
            }
        }
    }

    public Matrix(double[][] matrix)
    {
        this.matrix = matrix;
    }

    public Matrix(double[] matrix)
    {
        this.matrix = new double[matrix.length][1];

        for (int i = 0; i < matrix.length; i++)
        {
            this.matrix[i][0] = matrix[i];
        }
    }

    public void randomize()
    {
        Random rand = new Random();

        this.map(x -> rand.nextDouble() * 2 - 1);
    }

    public void add(double n)
    {
        for (int i = 0; i < this.matrix.length; i++)
        {
            for (int j = 0; j < this.matrix[i].length; j++)
            {
                matrix[i][j] += n;
            }
        }
    }

    public void add(Matrix m)
    {
        double[][] matrix1 = m.getMatrix();

        if (matrix1.length != this.matrix.length || matrix1[0].length != this.matrix[0].length)
        {
            throw new IllegalArgumentException("Incompatable matrix size: (" + matrix1.length + "x" + matrix1[0].length
            + ") + (" + this.matrix.length + "x" + this.matrix[0].length + ")");
        }

        for (int i = 0; i < this.matrix.length; i++)
        {
            for (int j = 0; j < this.matrix[0].length; j++)
            {
                this.matrix[i][j] += matrix1[i][j];
            }
        }
    }

    public void subtract(Matrix m1)
    {
        double[][] matrix1 = m1.getMatrix();

        if (matrix1.length != this.matrix.length || matrix1[0].length != this.matrix[0].length)
        {
            throw new IllegalArgumentException("Incompatable matrix size: (" + matrix1.length + "x" + matrix1[0].length
            + ") + (" + this.matrix.length + "x" + this.matrix[0].length + ")");
        }

        for (int i = 0; i < this.matrix.length; i++)
        {
            for (int j = 0; j < this.matrix[0].length; j++)
            {
                this.matrix[i][j] -= matrix1[i][j];
            }
        }
    }

    public static Matrix subtract(Matrix m1, Matrix m2)
    {
        double[][] matrix1 = m1.getMatrix();
        double[][] matrix2 = m2.getMatrix();
        double[][] newMatrix = new double[m1.getMatrix().length][m1.getMatrix()[0].length];

        if (matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length)
        {
            throw new IllegalArgumentException("Incompatable matrix size: (" + matrix1.length + "x" + matrix1[0].length
            + ") - (" + matrix2.length + "x" + matrix2[0].length + ")");
        }

        for (int i = 0; i < newMatrix.length; i++)
        {
            for (int j = 0; j < newMatrix[i].length; j++)
            {
                newMatrix[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }

        return new Matrix(newMatrix);
    }

    // Hadamard Product
    public void multiply(Matrix m)
    {
        double[][] matrix1 = m.getMatrix();

        if (matrix1.length != this.matrix.length || matrix1[0].length != this.matrix[0].length)
        {
            throw new IllegalArgumentException("Incompatable matrix size: (" + matrix1.length + "x" + matrix1[0].length
            + ") - (" + this.matrix.length + "x" + this.matrix[0].length + ")");
        }

        for (int i = 0; i < matrix1.length; i++)
        {
            for (int j = 0; j < matrix1[i].length; j++)
            {
                this.matrix[i][j] *= matrix1[i][j];
            }
        }
    }

    public void scalerMultiply(double n)
    {
        for (int i = 0; i < this.matrix.length; i++)
        {
            for (int j = 0; j < this.matrix[i].length; j++)
            {
                this.matrix[i][j] *= n;
            }
        }
    }

    public static Matrix matrixMultiply(Matrix m1, Matrix m2)
    {
        double[][] matrix1 = m1.getMatrix();
        double[][] matrix2 = m2.getMatrix();

        if (matrix1[0].length != matrix2.length)
        {
            throw new IllegalArgumentException("Incompatable matrix size: (" + matrix1.length + "x" + matrix1[0].length
            + ") * (" + matrix2.length + "x" + matrix2[0].length + ")");
        }

        double[][] newMatrix = new double[matrix1.length][matrix2[0].length];

        for (int i = 0; i < matrix1.length; i++)
        {
            for (int j = 0; j < matrix2[0].length; j++)
            {
                for (int k = 0; k < matrix1[i].length; k++)
                {
                    newMatrix[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return new Matrix(newMatrix);
    }

    public void transpose()
    {
        double[][] newMatrix = new double[this.matrix[0].length][this.matrix.length];

        for (int i = 0; i < this.matrix.length; i++)
        {
            for (int j = 0; j < this.matrix[0].length; j++)
            {
                newMatrix[j][i] = this.matrix[i][j];
            }
        }

        setMatrix(newMatrix);
    }

    public static Matrix transpose(Matrix matrix)
    {
        double[][] curMatrix = matrix.getMatrix();

        double[][] newMatrix = new double[curMatrix[0].length][curMatrix.length];

        for (int i = 0; i < curMatrix.length; i++)
        {
            for (int j = 0; j < curMatrix[0].length; j++)
            {
                newMatrix[j][i] = curMatrix[i][j];
            }
        }

        return new Matrix(newMatrix);
    }

    public void map(Func map)
    {
        for (int i = 0; i < this.matrix.length; i++) 
        {
            for (int j = 0; j < this.matrix[0].length; j++) 
            {
                double val = this.matrix[i][j];
                this.matrix[i][j] = map.apply(val);
            }
        }
    }

    public static Matrix map(Matrix m, Func map)
    {
        double[][] curMatrix = m.getMatrix();

        Matrix newMatrix = new Matrix(curMatrix.length, curMatrix[0].length);

        for (int i = 0; i < curMatrix.length; i++) 
        {
            for (int j = 0; j < curMatrix[i].length; j++) 
            {
                double val = curMatrix[i][j];
                newMatrix.getMatrix()[i][j] = map.apply(val);
            }
        }

        return newMatrix;
    }

    public double[][] getMatrix() { return this.matrix; }
    public double get(int i, int j) { return this.matrix[i][j]; }
    public double getMinValue()
    {
        Double min = Double.MAX_VALUE;

        for (int i = 0; i < this.matrix.length; i++)
        {
            for (int j = 0; j < this.matrix[i].length; j++)
            {
                if (this.matrix[i][j] < min) { min = this.matrix[i][j]; }
            }
        }

        return min;
    }
    public double getMaxValue()
    {
        Double max = Double.MIN_VALUE;

        for (int i = 0; i < this.matrix.length; i++)
        {
            for (int j = 0; j < this.matrix[i].length; j++)
            {
                if (this.matrix[i][j] > max) { max = this.matrix[i][j]; }
            }
        }

        return max;
    }

    public void setMatrix(Matrix matrix) { this.matrix = matrix.getMatrix(); }
    public void setMatrix(double[][] matrix) { this.matrix = matrix; }
    public void set(int i, int j, double value) { this.matrix[i][j] = value; }

    @Override
    public String toString()
    {
        return Arrays.deepToString(this.matrix).replace("], ", "]\n") + "\n";
    }
}