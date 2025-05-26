package com.github.elijahgabrielletanabe.Controllers;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import com.github.elijahgabrielletanabe.Model.Matrix;
import com.github.elijahgabrielletanabe.Model.NeuralNetwork;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GridVizController implements Initializable 
{
    @FXML private Canvas XORGrid;

    private final NeuralNetwork nn;
    private final Matrix[] inputs;
    private final Matrix[] targets;

    public GridVizController()
    {
        this.nn = new NeuralNetwork(2, 6, 1);

        this.inputs = new Matrix[]{
            new Matrix(new double[]{0, 1}),
            new Matrix(new double[]{1, 0}),
            new Matrix(new double[]{0, 0}),
            new Matrix(new double[]{1, 1})
        };

        this.targets = new Matrix[]{
            new Matrix(new double[]{1}),
            new Matrix(new double[]{1}),
            new Matrix(new double[]{0}),
            new Matrix(new double[]{0})
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        GraphicsContext gc = this.XORGrid.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            private long last = 0;
            private float duration = 16.67F; //Milliseconds
            @Override
            public void handle(long now)
            {
                if (now - last >= duration * 1000000) //Nanoseconds
                {
                    train();
                    fillGrid(gc);
                    last = now;
                }
            }
        };

        timer.start();
    }

    private void train()
    {
        int iters = 5000;

        for (int i = 0; i < iters; i++)
        {
            Random rand = new Random();
            int randomIndex = rand.nextInt(4);

            this.nn.train(this.inputs[randomIndex], this.targets[randomIndex]);
        }
    }

    private void fillGrid(GraphicsContext gc)
    {
        double resolution = 150;
        double gridSize = this.XORGrid.getHeight();
        double widthAndHeight = gridSize / resolution;

        for (double i = 0; i < resolution; i++)
        {
            for (double j = 0; j < resolution; j++)
            {
                double x = map(widthAndHeight * j, 0, gridSize, 0, 1);
                double y = map(widthAndHeight * i, 0, gridSize, 0, 1);

                Matrix output = nn.feedForward(new Matrix(new double[]{x, y}));
                double result = output.getMatrix()[0][0];

                gc.setFill(new Color(result, result, result, 1.0));
                gc.fillRect(widthAndHeight * j, widthAndHeight * i, widthAndHeight, widthAndHeight);
            }
        }
    }

    private static double map(double value, double minA, double maxA, double minB, double maxB) 
    {
        return (1 - ((value - minA) / (maxA - minA))) * minB + ((value - minA) / (maxA - minA)) * maxB;
    }
}   
