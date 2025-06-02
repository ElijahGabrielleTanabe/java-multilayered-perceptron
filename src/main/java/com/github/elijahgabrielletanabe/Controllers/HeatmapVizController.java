package com.github.elijahgabrielletanabe.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.github.elijahgabrielletanabe.App;
import com.github.elijahgabrielletanabe.Model.Matrix;
import com.github.elijahgabrielletanabe.Model.NeuralNetwork;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HeatmapVizController implements Initializable
{
    @FXML private Canvas heatMapCanvas;
    @FXML private LineChart lineChart;
    @FXML private Rectangle legend;

    private NeuralNetwork nn;
    private int node;
    private double resolution;

    public HeatmapVizController()
    {
        this.resolution = 200;
    }

    public void initialize(URL location, ResourceBundle resources)
    {   
    }

    public void createHeatMap()
    {
        this.legend.getStyleClass().add("legend");
        GraphicsContext gc = this.heatMapCanvas.getGraphicsContext2D();
        double gridSize = this.heatMapCanvas.getHeight();
        double widthAndHeight = gridSize / this.resolution;

        for (double i = 0; i < this.resolution; i++)
        {
            for (double j = 0; j < this.resolution; j++)
            {
                double x = App.map(widthAndHeight * j, 0, gridSize, 0, 1);
                double y = App.map(widthAndHeight * i, 0, gridSize, 0, 1);

                Matrix output = nn.feedForwardOnNode(new Matrix(new double[]{x, y}), this.node);
                double result = output.getMatrix()[0][0];
                Color colour;

                if (result > 0.5) {
                    double colourValue = App.map(result, 0.5,  1, 0, 1);
                    colour = new Color(0, 0, colourValue, 1.0);
                } else if (result < 0.5) {
                    double colourValue = App.map(result, 0.5,  0, 0, 1);
                    colour = new Color(colourValue, 0, 0, 1.0);
                } else {
                    colour = new Color(0, 0, 0, 1.0);
                }

                gc.setFill(colour);
                gc.fillRect(Math.round(widthAndHeight * j), Math.round(widthAndHeight * i), Math.ceil(widthAndHeight), Math.ceil(widthAndHeight));
            }
        }
    }

    public void setNeuralNetwork(NeuralNetwork nn) { this.nn = nn; }
    public void setNode(int node) { this.node = node; }
}
