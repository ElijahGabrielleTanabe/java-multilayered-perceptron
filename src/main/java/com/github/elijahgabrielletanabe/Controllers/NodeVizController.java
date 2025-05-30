package com.github.elijahgabrielletanabe.Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.github.elijahgabrielletanabe.App;
import com.github.elijahgabrielletanabe.Model.NeuralNetwork;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NodeVizController implements Initializable
{
    @FXML private HBox hbox;
    @FXML private AnchorPane anchorPaneRoot;

    private NeuralNetwork nn;
    private Stage stage;
    //For each weight, there is a corresponding line
    private final ArrayList<ArrayList<Line>> weightLines;

    public NodeVizController()
    {
        this.weightLines = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Platform.runLater(() -> {
            double vboxWidth = this.hbox.getWidth() / 3;

            //# Make Input nodes VBox
            VBox inputNodes = createNodeVBox(this.nn.getWeightsIH().getMatrix()[0].length, vboxWidth);

            //# Make Hidden nodes VBox
            VBox hiddenNodes = createNodeVBox(this.nn.getWeightsIH().getMatrix().length, vboxWidth);

            //# Make Output nodes VBox
            VBox outputNodes = createNodeVBox(this.nn.getWeightsHO().getMatrix().length, vboxWidth);

            this.hbox.getChildren().addAll(inputNodes, hiddenNodes, outputNodes);
        });
    }

    private VBox createNodeVBox(int nodes, double vboxWidth)
    {
        VBox vbox = new VBox(60);
        vbox.setMinWidth(vboxWidth);
        vbox.setMaxWidth(Double.MAX_VALUE);
        vbox.setPrefWidth(vboxWidth);
        vbox.setAlignment(Pos.CENTER);

        createCircles(vbox, nodes);
        
        return vbox;
    }

    private void createCircles(VBox container, int num)
    {
        for (int i = 0; i < num; i++)
        {
            Circle circle = new Circle(24);
            container.getChildren().add(circle);
        }
    }

    private void createWeightLines()
    {
        ObservableList<Node> vboxList = this.hbox.getChildren();

        for (int i = 1; i < vboxList.size(); i++)
        {
            if (!(vboxList.get(i - 1) instanceof VBox) || !(vboxList.get(i) instanceof VBox)) 
            { 
                throw new IllegalArgumentException("Unexpected nodes: " + vboxList.get(i - 1) + " | " + vboxList.get(i)); 
            }

            VBox v1 = (VBox) vboxList.get(i - 1);
            VBox v2 = (VBox) vboxList.get(i);

            ObservableList<Node> circleList1 = v1.getChildren();
            ObservableList<Node> circleList2 = v2.getChildren();

            this.weightLines.add(new ArrayList<>());

            for (int j = 0; j < circleList2.size(); j++)
            {
                for (int k = 0; k < circleList1.size(); k++)
                {
                    if (!(circleList1.get(k) instanceof Circle) || !(circleList2.get(j) instanceof Circle)) 
                    {
                        throw new IllegalArgumentException("Unexpected nodes: " + circleList1.get(k) + " | " + circleList2.get(j)); 
                    }

                    Circle to = (Circle) circleList1.get(k);
                    Circle from = (Circle) circleList2.get(j);

                    Bounds p1 = this.anchorPaneRoot.sceneToLocal(to.localToScene(to.getBoundsInLocal()));
                    Bounds p2 = this.anchorPaneRoot.sceneToLocal(from.localToScene(from.getBoundsInLocal()));

                    System.out.println("p1: x = " + p1.getCenterX() + ", y = " + p1.getCenterY());
                    System.out.println("p2: x = " + p2.getCenterX() + ", y = " + p2.getCenterY());

                    Line line = new Line(p1.getCenterX(), p1.getCenterY(), p2.getCenterX(), p2.getCenterY());
                    line.setStrokeWidth(2);
                    this.weightLines.get(i - 1).add(line);
                }
            }
        }

        for (ArrayList<Line> lines : this.weightLines)
        {
            this.anchorPaneRoot.getChildren().addAll(lines);
        }

        updateWeightLines();
    }
    
    public void updateWeightLines()
    {
        System.out.println("Updating Weight Lines!");
        //Extremely hardcoded due to there being only two weight matrices
        double[][] weightsIH = this.nn.getWeightsIH().getMatrix();
        double[][] weightsHO = this.nn.getWeightsHO().getMatrix();

        if (this.weightLines.isEmpty()) { return; }

        if ((weightsIH.length * weightsIH[0].length) != this.weightLines.get(0).size())
        {
            throw new IllegalArgumentException("Not the same size: " + weightsIH.length + " vs " + this.weightLines.get(0).size());
        }

        int index = 0;
        //WeightsIH
        for (int i = 0; i < weightsIH.length; i++)
        {
            for (int j = 0; j < weightsIH[i].length; j++)
            {
                //Weights range -1 - 1
                double weight = App.map(weightsIH[i][j], this.nn.getWeightsIH().getMinValue(), this.nn.getWeightsIH().getMaxValue(), -1, 1);
                Color color = null;

                if (weight > 0) {
                    color = new Color(0, 0, weight, 1.0);
                } else if (weight < 0) {
                    color = new Color(-weight, 0, 0, 1.0);
                } else {
                    color = new Color(0, 0, 0, 1.0);
                }

                this.weightLines.get(0).get(index).setStroke(color);
                index++;
            }
        }

        if ((weightsHO.length * weightsHO[0].length) != this.weightLines.get(1).size())
        {
            throw new IllegalArgumentException("Not the same size: " + weightsHO.length + " vs " + this.weightLines.get(1).size());
        }

        index = 0;
        //WeightOH
        for (int i = 0; i < weightsHO.length; i++)
        {
            for (int j = 0; j < weightsHO[i].length; j++)
            {
                //Weights range -1 - 1
                double weight = weightsHO[i][j];
                double normalized = App.map(weight, this.nn.getWeightsHO().getMinValue(), this.nn.getWeightsHO().getMaxValue(), -1, 1);
                Color color = null;

                if (normalized > 0) {
                    color = new Color(0, 0, normalized, 1.0);
                } else if (normalized < 0) {
                    color = new Color(normalized * -1, 0, 0, 1.0);
                } else {
                    color = new Color(0, 0, 0, 1.0);
                }

                this.weightLines.get(1).get(index).setStroke(color);
                index++;
            }
        }
    }

    public void setNeuralNetwork(NeuralNetwork nn) { this.nn = nn; }
    public void setStage(Stage stage) 
    { 
        this.stage = stage;

        this.stage.setOnShown(e -> {
            System.out.println("Stage set");
            PauseTransition delay = new PauseTransition(Duration.millis(50));
            delay.setOnFinished(ev -> createWeightLines());
            delay.play();
        });
    }
}