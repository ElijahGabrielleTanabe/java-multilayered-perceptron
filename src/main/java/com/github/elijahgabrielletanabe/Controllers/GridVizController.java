package com.github.elijahgabrielletanabe.Controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.elijahgabrielletanabe.App;
import com.github.elijahgabrielletanabe.Model.Matrix;
import com.github.elijahgabrielletanabe.Model.NeuralNetwork;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
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

    @FXML private Label learningRateLabel;
    @FXML private Label hiddenNodesLabel;
    @FXML private Label iterationsLabel;

    private Stage nodeStage;
    private NodeVizController nodeController;

    private final Matrix[] inputs;
    private final Matrix[] targets;
    private final Object nnLock;
    private ExecutorService executor;
    private WritableImage canvasImage;
    private int[] intArgbBuffer;

    private NeuralNetwork nn;
    private AnimationTimer timer;
    private boolean playing;
    private int totalIterations;
    private double resolution;
    private double speed;

    public GridVizController()
    {
        this.playing = false;
        this.totalIterations = 0;
        
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

        this.nnLock = new Object();
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        //#Initialize Values
        GraphicsContext gc = this.XORGrid.getGraphicsContext2D();
        gc.setFill(Color.web("#1e1e1e")); // dark gray background
        gc.fillRect(0, 0, this.XORGrid.getWidth(), this.XORGrid.getHeight());

        this.speed = this.speedSlider.getMax() + 1 - this.speedSlider.getValue();
        this.resolution = this.resolutionSlider.getValue();

        this.nn = new NeuralNetwork(2, (int) this.hiddenNodesSlider.getValue(), 1);
        this.nn.setLearningRate(learningRateSlider.getValue());

        //Set Label texts
        updateLabels();

        //# Main Animation loop
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

                    executor.submit(() -> {
                        train();

                        Platform.runLater(() -> updateView(gc));
                    });

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
        this.totalIterations = 0;
        this.playing = true;

        //Set texts
        this.pausePlayButton.setText("Pause");
        updateLabels();

        //!!FIX!!
        PauseTransition delay = new PauseTransition(Duration.millis(150));
        delay.setOnFinished(e -> {
            if (this.nodeStage != null)
            {
                try {
                    this.nodeStage.close();
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

        nodeScene.getStylesheets().add(App.getFileByString("NodeViz.css", "css").toExternalForm());
        this.nodeController.setNeuralNetwork(this.nn);
        this.nodeController.setStage(nodeStage);
        this.nodeStage.setScene(nodeScene);
        this.nodeStage.setResizable(false);
        this.nodeStage.setOnHidden(e -> cleanUpNodeView());
        this.nodeStage.show();
        this.nodeViewButton.setDisable(true);
    }

    public void cleanUpNodeView()
    {
        System.out.println("Cleaning Node View!");

        if (this.nodeController != null)
        {
            this.nodeController.cleanUp();
            this.nodeController = null;
        }
        
        if (this.nodeStage != null) 
        { 
            this.nodeStage.close();
            this.nodeStage = null;
        }
        
        this.nodeViewButton.setDisable(false);
    }

    public void cleanUp()
    {
        this.timer.stop();
        cleanUpNodeView();
        this.executor.shutdownNow();
        Platform.exit();
    }

    //# Train on worker thread
    private void train()
    {
        synchronized (this.nnLock)
        {
            Random rand = new Random();
            int iters = 1000;

            for (int i = 0; i < iters; i++)
            {
                int randomIndex = rand.nextInt(4);

                this.nn.train(this.inputs[randomIndex], this.targets[randomIndex]);
                this.totalIterations++;
                Platform.runLater(() -> this.iterationsLabel.setText("iterations=" + this.totalIterations));
            }
        } 
    }

    private void updateView(GraphicsContext gc)
    {
        synchronized (this.nnLock)
        {
            double width = this.XORGrid.getWidth() / this.resolution;
            double height = this.XORGrid.getHeight() / this.resolution;
            int intResolution = (int) this.resolution;

            if (this.canvasImage == null || this.canvasImage.getWidth() != this.resolution || this.canvasImage.getHeight() != this.resolution)
            {
                this.canvasImage = new WritableImage(intResolution, intResolution);
                this.intArgbBuffer = new int[intResolution * intResolution];
            }

            PixelWriter pw = this.canvasImage.getPixelWriter();
            PixelFormat<IntBuffer> format = PixelFormat.getIntArgbInstance();

            Matrix input = new Matrix(new double[]{0, 0});

            for (int py = 0; py < this.resolution; py++)
            {
                double y = App.map((double) py, 0, this.resolution, 0, 1);

                for (int px = 0; px < this.resolution; px++)
                {
                    double x = App.map((double) px, 0, this.resolution, 0, 1);

                    input.set(0, 0, x);
                    input.set(1, 0, y);

                    Matrix output = nn.feedForward(input);
                    double result = output.get(0, 0);
                    int hexColour = (int) (result * 255);

                    int rgbInt = (255 << 24) | (hexColour << 16) | (hexColour << 8) | hexColour;
                    this.intArgbBuffer[py * intResolution + px] = rgbInt;
                }
            }

            pw.setPixels(0, 0, intResolution, intResolution, format, this.intArgbBuffer, 0, intResolution);

            gc.drawImage(this.canvasImage, 0, 0, this.XORGrid.getWidth(), this.XORGrid.getHeight());

            Platform.runLater(() -> {
                synchronized (this.nnLock) {
                    if (this.nodeController != null) {
                        this.nodeController.updateWeightLines();
                    }
                }
            });
        }
    }

    private void updateLabels()
    {
        this.learningRateLabel.setText("learning_rate=" + this.learningRateSlider.getValue());
        this.hiddenNodesLabel.setText("hidden_nodes=" + (int) this.hiddenNodesSlider.getValue());
        this.iterationsLabel.setText("iterations=0");
    }

    public double getSpeed() { return this.speed; }
}   
