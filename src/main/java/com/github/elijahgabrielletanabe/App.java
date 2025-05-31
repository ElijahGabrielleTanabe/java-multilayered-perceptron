package com.github.elijahgabrielletanabe;

import java.io.IOException;

import com.github.elijahgabrielletanabe.Controllers.GridVizController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private GridVizController controller;

    public App()
    {
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("fxml/GridViz.fxml"));
        scene = new Scene(fxmlLoader.load());
        this.controller = fxmlLoader.getController();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnHidden(e -> this.controller.cleanUpNodeView());
        stage.show();
    }

    public static double map(double value, double minA, double maxA, double minB, double maxB) 
    {
        return (1 - ((value - minA) / (maxA - minA))) * minB + ((value - minA) / (maxA - minA)) * maxB;
    }
}