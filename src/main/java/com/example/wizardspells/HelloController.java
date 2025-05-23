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

import java.util.ArrayList;
import java.util.List;
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
            // TODO : régler le problème du dernier point enregistré lors du passage du mode trait à main levé
            @Override
            public void handle(MouseEvent mouseEvent) {
                dessiner = false;
                canvas.requestFocus();

                List<Point2D> pointsPolTraites=douglasPeucker(lisserStackPoint(pointsPol), precisionSlider.getValue()*50);
                System.out.println("oui");
                reconnaissanceBasique(pointsPolTraites);
                System.out.println("non");


                /*Affichage de la pile de points a la in du tracage
                System.out.println(precisionSlider.getValue());
                while (!(pointsPol.empty())) {
                    Point2D pointAffiche=pointsPol.pop();
                    System.out.print("X : ");
                    System.out.print(pointAffiche.getX());
                    System.out.print(" Y : ");
                    System.out.println(pointAffiche.getY());

                } System.out.println("\n");*/

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

    List<Point2D> lisserStackPoint(Stack<Point2D> stack){
        //réduit le nombre de point en épurant les points trop proche

        //ajout du premier point dans la nouvelle stack
        List<Point2D> retour = new ArrayList<Point2D>();
        Point2D pointReference = stack.pop();
        retour.add(pointReference);

        Point2D pointCourant;
        while(!stack.isEmpty()){
            pointCourant = stack.pop();
            //si la différence est supérieure a x (precision choisie par l'utilisateur avec un slider), ajoute le point courant dans la stack a retourner, sinon change le point courant
            //TODO faire un curseur pour choisir l'approximation
            if(Math.abs(pointCourant.getX()-pointReference.getX())>precisionSlider.getValue() && Math.abs(pointCourant.getY()-pointReference.getY())>precisionSlider.getValue()){
                //ajoute le point courant dans la stack a retourner
                retour.add(pointCourant);
                //change le point de référence
                pointReference = pointCourant;
            }
            retour.add(pointCourant);
        }

        return retour;
    }


    public static List<Point2D> douglasPeucker(List<Point2D> points, double epsilon) {
        if (points.size()<3) {;
            return points; }

        double maxDist = 0;
        int index= 0;
        Point2D start = points.get(0);
        Point2D end = points.get(points.size()-1);

        for (int i=1; i<points.size();i++) {
            double dist = perpendicularDistance(points.get(i), start, end);
            if (dist > maxDist) {
                index=1;
                maxDist=dist;
            }
        }
        if (maxDist > epsilon) {
            List<Point2D> result1=douglasPeucker(points.subList(0,index+1),epsilon);
            List<Point2D> result2=douglasPeucker(points.subList(index, points.size()),epsilon);

            List<Point2D> finalResult = new ArrayList<>(result1);
            finalResult.remove(finalResult.size()-1); //évite les doublons
            finalResult.addAll(result2);
            return finalResult;}

        else {
            return List.of(start,end);
        }
    }











    private static double perpendicularDistance(Point2D pt, Point2D lineStart, Point2D lineEnd) {
        double dx = lineEnd.getX() - lineStart.getX();
        double dy = lineEnd.getY() - lineStart.getY();

        if (dx == 0 && dy == 0) {
            return pt.distance(lineStart);
        }

        double num=Math.abs(dy*pt.getX() - dx*pt.getY() + lineEnd.getX()*lineStart.getY() - lineEnd.getY()*lineStart.getX());
        double denum=Math.sqrt(dx*dx+dy*dy);
        return num/denum;

    }

    public void reconnaissanceBasique(List<Point2D> points) {
        for (Point2D p : points) {
            //TODO faire la reconnaissance (dire si c'est un carre, triangle...)
            System.out.print("X : ");
            System.out.print(p.getX());
            System.out.print(" Y : ");
            System.out.println(p.getY());

        }
    }
}

