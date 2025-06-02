package com.github.elijahgabrielletanabe.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.github.elijahgabrielletanabe.App;
import com.github.elijahgabrielletanabe.Model.NeuralNetwork;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NodeVizController implements Initializable
{
    @FXML private HBox hbox;
    @FXML private AnchorPane anchorPaneRoot;
    @FXML private Rectangle legend;

    private NeuralNetwork nn;
    private Stage stage;
    //For each weight, there is a corresponding line
    private final ArrayList<ArrayList<Line>> weightLines;
    private final HashMap<Circle, Integer> circleToNode;

    private HeatmapVizController heatMapController;
    private Stage heatMapStage;

    public NodeVizController()
    {
        this.weightLines = new ArrayList<>();
        this.circleToNode = new HashMap<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        this.legend.getStyleClass().add("legend");

        Platform.runLater(() -> {
            double vboxWidth = this.hbox.getWidth() / 3;

            //# Make Input nodes VBox
            VBox inputNodes = createNodeVBox(this.nn.getWeightsIH().getMatrix()[0].length, vboxWidth, "Input Layer");

            //# Make Hidden nodes VBox
            VBox hiddenNodes = createNodeVBox(this.nn.getWeightsIH().getMatrix().length, vboxWidth, "Hidden Layer");
            //# Map circle to corrusponding node
            VBox hiddenNodeVBox = (VBox) hiddenNodes.getChildren().get(1);
            ObservableList<Node> hiddenNodesList = hiddenNodeVBox.getChildren();

            for (int i = 0; i < hiddenNodesList.size(); i++)
            {
                Circle node = (Circle) hiddenNodesList.get(i);

                node.setOnMouseClicked((MouseEvent event) -> {
                    Circle circle = (Circle) event.getSource();
                    createHeatMap(circle);
                });

                node.getStyleClass().add("hidden-node");
                this.circleToNode.put(node, i);
            }

            //# Make Output nodes VBox
            VBox outputNodes = createNodeVBox(this.nn.getWeightsHO().getMatrix().length, vboxWidth, "Output Layer");

            this.hbox.getChildren().addAll(inputNodes, hiddenNodes, outputNodes);
        });
    }

    private VBox createNodeVBox(int nodes, double vboxWidth, String name)
    {
        VBox parent = new VBox();
        Label label = new Label(name);
        label.getStyleClass().add("node-layer-label");

        VBox nodeVbox = new VBox(60);
        //nodeVbox.setMinWidth(vboxWidth);
        //nodeVbox.setMaxWidth(Double.MAX_VALUE);
        nodeVbox.setPrefWidth(vboxWidth);
        nodeVbox.setAlignment(Pos.CENTER);

        createCircles(nodeVbox, nodes);

        parent.getChildren().addAll(label, nodeVbox);
        parent.setPrefWidth(vboxWidth);
        parent.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(nodeVbox, Priority.ALWAYS);
        
        return parent;
    }

    private void createCircles(VBox container, int num)
    {
        for (int i = 0; i < num; i++)
        {
            Circle circle = new Circle(24);
            circle.getStyleClass().add("node");
            container.getChildren().add(circle);
        }
    }

    private void createHeatMap(Circle node)
    {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/HeatmapViz.fxml"));
            Parent root = loader.load();
            this.heatMapController = loader.getController();
            this.heatMapStage = new Stage();
            Scene heatMapScene = new Scene(root);

            heatMapScene.getStylesheets().add(App.getFileByString("HeatmapViz.css", "css").toExternalForm());
            this.heatMapController.setNeuralNetwork(this.nn);
            this.heatMapController.setNode(this.circleToNode.get(node));
            this.heatMapController.createHeatMap();
            this.heatMapStage.setResizable(false);
            this.heatMapStage.setScene(heatMapScene);
            this.heatMapStage.show();
        } catch (IOException e) {
            System.out.println("Could not find HeatmapViz.fxml");
        }
    }

    private void createWeightLines()
    {
        ArrayList<VBox> vboxList = new ArrayList<>();
        ObservableList<Node> parentHboxList = this.hbox.getChildren();
        
        for (int i = 0; i < parentHboxList.size(); i++)
        {
            if (!(parentHboxList.get(i) instanceof VBox)) { throw new IllegalArgumentException("Unexpected node: " + parentHboxList.get(i)); }

            VBox parentVBox = (VBox) parentHboxList.get(i);
            ObservableList<Node> parentVboxList = parentVBox.getChildren();

            if (!(parentVboxList.get(1) instanceof VBox)) { throw new IllegalArgumentException("Unexpected node: " + parentVboxList.get(1)); }

            VBox vbox = (VBox) parentVboxList.get(1);
            vboxList.add(vbox);
        }

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

                    Line line = new Line(p1.getCenterX(), p1.getCenterY(), p2.getCenterX(), p2.getCenterY());
                    line.setStrokeWidth(2.1);

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
        //Extremely hardcoded due to NeuralNetwork's design
        double[][][] weightsList = {
            this.nn.getWeightsIH().getMatrix(),
            this.nn.getWeightsHO().getMatrix()
        };

        if (this.weightLines.isEmpty()) { return; }

        for (int k = 0; k < weightsList.length; k++)
        {
            int index = 0;
            double[][] weightMatrix = weightsList[k];

            if ((weightMatrix.length * weightMatrix[0].length) != this.weightLines.get(k).size())
            {
                throw new IllegalArgumentException("Not the same size: " + weightMatrix.length * weightMatrix[0].length + " vs " + this.weightLines.get(0).size());
            }

            for (int i = 0; i < weightMatrix.length; i++)
            {
                for (int j = 0; j < weightMatrix[i].length; j++)
                {
                    //Normalized range -1 - 1
                    double weight = weightMatrix[i][j];
                    double normalized = App.map(weight, this.nn.getWeightsIH().getMinValue(), this.nn.getWeightsIH().getMaxValue(), -1, 1);
                    normalized = Math.max(-1, Math.min(1, normalized));
                    Color color = null;
                    double stroke = 0;

                    if (normalized > 0) {
                        color = new Color(0, 0, normalized, 1.0);
                        stroke = App.map(normalized, 0, 1, 0.5, 2);
                    } else if (normalized < 0) {
                        color = new Color(-normalized, 0, 0, 1.0);
                        stroke = App.map(normalized, -1, 0, 2,0.5);
                    } else {
                        color = new Color(0, 0, 0, 1.0);
                        stroke = 1;
                    }

                    this.weightLines.get(k).get(index).setEffect(new DropShadow(5, color));
                    this.weightLines.get(k).get(index).setStroke(color);
                    this.weightLines.get(k).get(index).setStrokeWidth(stroke);
                    index++;
                }
            }
        }
    }

    public void cleanUp()
    {
        if (this.heatMapStage != null) 
        {
            this.heatMapStage.close();
            this.heatMapStage = null;
        }
        if (this.heatMapController != null) { this.heatMapController = null; }
    }

    public void setNeuralNetwork(NeuralNetwork nn) { this.nn = nn; }
    public void setStage(Stage stage)
    { 
        this.stage = stage;

        this.stage.setOnShown(e -> {
            System.out.println("Stage set");
            PauseTransition delay = new PauseTransition(Duration.millis(100));
            delay.setOnFinished(ev -> Platform.runLater(() -> createWeightLines()));
            delay.play();
        });
    }
}