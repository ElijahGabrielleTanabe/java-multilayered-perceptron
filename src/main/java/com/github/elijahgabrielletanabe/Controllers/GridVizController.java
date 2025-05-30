package com.github.elijahgabrielletanabe.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import com.github.elijahgabrielletanabe.App;
import com.github.elijahgabrielletanabe.Model.Matrix;
import com.github.elijahgabrielletanabe.Model.NeuralNetwork;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GridVizController implements Initializable 
{
    @FXML private Canvas XORGrid;
    
    @FXML private Slider speedSlider;
    @FXML private Slider learningRateSlider;
    @FXML private Slider hiddenNodesSlider;
    @FXML private Slider resolutionSlider; //Queue
    
    @FXML private Button nodeViewButton;
    @FXML private Button pausePlayButton;
    @FXML private Button restartButton;

    private Stage nodeStage;
    private NodeVizController nodeController;

    private final Matrix[] inputs;
    private final Matrix[] targets;

    private NeuralNetwork nn;
    private AnimationTimer timer;
    private boolean playing;
    private double resolution;
    private double speed;

    public GridVizController()
    {
        this.playing = false;
        
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
        //#Initialize Values
        GraphicsContext gc = this.XORGrid.getGraphicsContext2D();
        this.speed = this.speedSlider.getMax() + 1 - this.speedSlider.getValue();
        this.resolution = this.resolutionSlider.getValue();
        this.nn = new NeuralNetwork(2, (int) this.hiddenNodesSlider.getValue(), 1);
        this.nn.setLearningRate(learningRateSlider.getValue());

        //# Animation loop
        this.timer = new AnimationTimer() {
            private long last = 0;
            //# Take from speed slider
            private double duration = 16.67; //Milliseconds      
            @Override
            public void handle(long now)
            {
                if (now - last >= duration * 1000000) //Nanoseconds
                {
                    this.duration = getSpeed();
                    train();
                    updateView(gc);

                    last = now;
                }
            }
        };
    }

    @FXML
    private void pausePlayPressed(MouseEvent event) 
    {
        this.playing = !this.playing;

        if (!this.playing)
        {
            this.timer.stop();
            this.pausePlayButton.setText("Play");
        }
        else if (this.playing)
        {
            this.timer.start();
            this.pausePlayButton.setText("Pause");
        }
    }

    @FXML
    private void restartPressed(MouseEvent event) 
    {
        this.timer.stop();
        //# New NeuralNetwork object
        double learningRate = this.learningRateSlider.getValue();
        int hiddenNodes = (int) this.hiddenNodesSlider.getValue();

        this.nn = new NeuralNetwork(2, hiddenNodes, 1);
        this.nn.setLearningRate(learningRate);
        this.resolution = this.resolutionSlider.getValue();
        this.playing = true;
        this.pausePlayButton.setText("Pause");

        PauseTransition delay = new PauseTransition(Duration.millis(150));
        delay.setOnFinished(e -> {
            if (this.nodeStage != null)
            {
                try {
                    this.nodeStage.hide();
                    createNodeView();
                } catch (IOException ex) {
                    System.out.println("Unable to load: NodeViz.fxml");
                }
            }
        this.timer.start();
        });

        delay.play();
    }

    @FXML
    private void nodeViewPressed(MouseEvent event)
    {
        try {
            createNodeView();
        } catch (IOException e) {
            System.out.println("Unable to load: NodeViz.fxml");
        }
    }

    @FXML
    private void speedSliderChanged(MouseEvent event) 
    {
        this.speed = this.speedSlider.getMax() + 1 - this.speedSlider.getValue();
    }

    private void createNodeView() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/NodeViz.fxml"));
        Parent root = loader.load();
        this.nodeController = loader.getController();
        this.nodeStage = new Stage();
        Scene nodeScene = new Scene(root);

        this.nodeController.setNeuralNetwork(this.nn);
        this.nodeController.setStage(nodeStage);
        this.nodeStage.setScene(nodeScene);
        this.nodeStage.setResizable(false);
        this.nodeStage.setOnHidden(e -> cleanUpNodeView());
        this.nodeStage.show();
    }

    private void cleanUpNodeView()
    {
        System.out.println("Cleaning Node View!");
        this.nodeController = null;
        this.nodeStage = null;
    }

    //# Train on worker thread
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

    private void updateView(GraphicsContext gc)
    {
        double gridSize = this.XORGrid.getHeight();
        double widthAndHeight = gridSize / this.resolution;

        for (double i = 0; i < this.resolution; i++)
        {
            for (double j = 0; j < this.resolution; j++)
            {
                double x = App.map(widthAndHeight * j, 0, gridSize, 0, 1);
                double y = App.map(widthAndHeight * i, 0, gridSize, 0, 1);

                Matrix output = nn.feedForward(new Matrix(new double[]{x, y}));
                double result = output.getMatrix()[0][0];

                gc.setFill(new Color(result, result, result, 1.0));
                gc.fillRect(widthAndHeight * j, widthAndHeight * i, widthAndHeight, widthAndHeight);
            }
        }

        PauseTransition delay = new PauseTransition(Duration.millis(100));
        delay.setOnFinished(e -> {if (this.nodeController != null) { this.nodeController.updateWeightLines(); }});
        delay.play();
    }

    public double getSpeed() { return this.speed; }
}   
