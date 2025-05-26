package com.github.elijahgabrielletanabe.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import com.github.elijahgabrielletanabe.App;
import com.github.elijahgabrielletanabe.Model.Matrix;
import com.github.elijahgabrielletanabe.Model.NeuralNetwork;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class GridVizController implements Initializable 
{
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        NeuralNetwork nn = new NeuralNetwork(2, 2, 1);

        Matrix[] inputs = {
            new Matrix(new double[]{0, 1}),
            new Matrix(new double[]{1, 0}),
            new Matrix(new double[]{0, 0}),
            new Matrix(new double[]{1, 1})
        };

        Matrix[] targets = {
            new Matrix(new double[]{1}),
            new Matrix(new double[]{1}),
            new Matrix(new double[]{0}),
            new Matrix(new double[]{0})
        };

        for (int j = 0; j < 100000; j++)
        {
            Random rand = new Random();
            int i = rand.nextInt(4);

            nn.train(inputs[i], targets[i]);
        }

        System.out.println(nn.feedForward(new Matrix(new double[]{0, 1})));
        System.out.println(nn.feedForward(new Matrix(new double[]{1, 0})));
        System.out.println(nn.feedForward(new Matrix(new double[]{0, 0})));
        System.out.println(nn.feedForward(new Matrix(new double[]{1, 1})));
    }

    @FXML
    private void switchToSecondary() throws IOException 
    {
        App.setRoot("secondary");
    }
}
