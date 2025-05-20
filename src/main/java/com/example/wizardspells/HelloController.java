package com.example.wizardspells;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.text.Font;
import javafx.scene.shape.Path;
import java.util.Stack;


public class HelloController {
    @FXML private RadioButton mainLevee;
    @FXML private RadioButton ligne;
    @FXML private Canvas canvas;
    @FXML private Slider precisionSlider;

    private GraphicsContext gc;
    private double lastX, lastY;
    private boolean dessiner = false;
    private Path path = new Path();
    public Stack<Point2D> pointsPol = new Stack<Point2D>();

    public void initialize() {
        gc = canvas.getGraphicsContext2D();
        gc.setFont(new Font("Arial", 26));
        gc.strokeText("Bienvenue dans Wizard Spells !\nMaintenez le click de la souris pour dessiner",100 ,100);

        // Focus clavier
        canvas.setFocusTraversable(true);

        // Groupe de radio button
        ToggleGroup tg = new ToggleGroup();
        mainLevee.setToggleGroup(tg);
        ligne.setToggleGroup(tg);
        mainLevee.setSelected(true);

        // Clear le dessin
        canvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
        });

        // Dessine tant qu'on appuie sur la souris
        canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                lastX = mouseEvent.getX();
                lastY = mouseEvent.getY();
                dessiner = true;
            }
        });

        // Effet quand la souris se déplace
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Color c = Color.BLACK;
                gc.setStroke(c);
                gc.setLineWidth(2);

                gc.beginPath();

                if (dessiner) {
                    if (mainLevee.isSelected()) {
                        double x = mouseEvent.getX();
                        double y = mouseEvent.getY();
                        gc.strokeLine(lastX, lastY, x, y);
                        //TODO alimenter la stack de point, en prenant l'ancienne position de la souris,
                        pointsPol.push(new Point2D(lastX,lastY));
                        lastX = x;
                        lastY = y;
                        //Possible que le dernier point ne soit pas enregistré, mais on s'en fout, car ça va tomber dans la plage d'erreur. Donc askip ça marche



                    } else if (ligne.isSelected()) {
                        path.getElements().add(new MoveTo(lastX, lastY));

                        canvas.setOnMouseReleased(event -> {
                            double x = mouseEvent.getX();
                            double y = mouseEvent.getY();
                            path.getElements().add(new LineTo(x, y));
                            gc.strokeLine(lastX, lastY, x, y);
                            canvas.requestFocus();
                            //TODO alimenter la stack de point, en prenant la position de départ et la position de fin
                            //Le point last, le poient event.get, on trace a la main les points intermediaires
                            ajouterPointIntermediaire(lastX, lastY, x, y);
                        });
                    }
                }
            }
        });

        // Quand la souris est relâchée, on ne dessine plus
        canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                dessiner = false;
                canvas.requestFocus();
            }
        });
    }

    //TODO mais possible on s'en branle
    void ajouterPointIntermediaire(double lastX, double lastY, double actX, double actY){
        //ajouter le point de départ a la stack
        pointsPol.push(new Point2D(lastX,lastY));

        //trouver les points intermediaires ou pas

        //on ajoute le point d'arrivée a la stack
        pointsPol.push(new Point2D(actX,actY));
    }

    Stack<Point2D> lisserStackPoint(Stack<Point2D> stack){
        //réduit le nombre de point en épurant les points trop proche

        //ajout du premier point dans la nouvelle stack
        Stack<Point2D> retour = new Stack<Point2D>();
        Point2D pointReference = stack.pop();
        retour.push(pointReference);

        Point2D pointCourant;
        while(!stack.isEmpty()){
            pointCourant = stack.pop();
            //si la différence est supérieure a x (precision choisie par l'utilisateur avec un slider), ajoute le point courant dans la stack a retourner, sinon change le point courant
            //TODO faire un curseur pour choisir l'approximation
            if(Math.abs(pointCourant.getX()-pointReference.getX())>precisionSlider.getValue() && Math.abs(pointCourant.getY()-pointReference.getY())>precisionSlider.getValue()){
                //ajoute le point courant dans la stack a retourner
                retour.push(pointCourant);
                //change le point de référence
                pointReference = pointCourant;
            }
            retour.push(pointCourant);
        }

        return retour;
    }
}

