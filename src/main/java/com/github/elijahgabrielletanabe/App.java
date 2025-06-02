package com.github.elijahgabrielletanabe;

import java.io.IOException;
import java.net.URL;

import com.github.elijahgabrielletanabe.Controllers.GridVizController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        FXMLLoader fxmlLoader = new FXMLLoader(getFileByString("GridViz.fxml", "fxml"));
        scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getFileByString("GridViz.css", "css").toExternalForm());
        this.controller = fxmlLoader.getController();
        //# Custom Menu Bar
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnHidden(e -> this.controller.cleanUp());
        stage.show();
    }

    public static double map(double value, double minA, double maxA, double minB, double maxB) 
    {
        return (1 - ((value - minA) / (maxA - minA))) * minB + ((value - minA) / (maxA - minA)) * maxB;
    }

    public static URL getFileByString(String path, String folder)
    {
        return App.class.getResource(folder + "/" + path);
    }
}