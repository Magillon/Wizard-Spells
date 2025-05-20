package com.example.wizardspells;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //charge le layout fxml
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),1024,600);

        //container
        Group group = new Group();

        //TODO //la taille de la zone est fixé dans le layout.fxml -> trouver comment les links
        //set l'outil de dessin
        final Canvas canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        group.getChildren().add(canvas);

        //affiche l'outil et la page
        stage.setTitle("Wizard Spells");
        stage.setScene(scene);
        stage.show();
    }

    //démarre l'application'
    public static void main(String[] args) {
        launch();
    }
}