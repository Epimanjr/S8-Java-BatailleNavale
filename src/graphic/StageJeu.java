/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic;

import interaction.Client;
import interaction.ClientGraphique;
import interaction.ServeurInterface;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Position;

/**
 *
 * @author Maxime
 */
public class StageJeu extends Stage {

    public boolean enAttente = true;
    // Attributs nécessaire à la fenêtre
    public static Scene scene;
    private final TextArea textarea = new TextArea();

    public static int margeX = 10, margeY = 10;
    public static int espacement = 40;
    public static int tailleChat = 200;

    public Position positionCourante = null;

    private MainGroup mg = new MainGroup();

    public StageJeu() {
        initComponents();
        this.setTitle("Fenêtre de jeu");

    }

    public void tracer(boolean touche) {
        this.mg.tracer(touche, positionCourante);
    }

    public void ajouteTextToChat(String text) {
        this.textarea.appendText(text);
    }

    private void initComponents() {

        scene = new Scene(mg, 2 * margeX + 11 * espacement, 4 * margeY + 11 * espacement + tailleChat);
        // Ajout de l'action du click
        mg.actionClick();

        this.setScene(scene);
    }

    class MainGroup extends Parent {

        ArrayList<Position> listePositions = new ArrayList<>();
        
        MainGroup() {
            // Tracement du plateau
            tracerPlateau();
            // Init chat
            initChat();
        }

        private void initChat() {

            textarea.setPrefSize(11 * espacement, tailleChat);
            textarea.setTranslateX(margeX);
            textarea.setTranslateY(3 * margeY + 11 * espacement);
            textarea.setText("*** CHATBOX V1.0 ***\n");
            this.getChildren().add(textarea);
        }

        public void tracer(boolean touche, Position position) {
            String text = (touche) ? "X" : "O";
            int sizeFont = ((int) (espacement * 0.8));
            double posX = margeX + (position.x + 1) * espacement + 0.3 * espacement;
            double posY = margeY + (position.y + 1) * espacement + 0.8 * espacement;
            Text chiffre = new Text(posX, posY, text);
            chiffre.setFont(new Font(sizeFont));
            chiffre.setFill(Color.web("#0000ff"));

            Service<Void> service = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            ajouterChiffre(chiffre);

                            return null;
                        }
                    };
                }
            };
            service.start();

        }

        private void ajouterChiffre(Text chiffre) {

            this.requestLayout();
            Timeline timeline1 = new Timeline();
            timeline1.getKeyFrames().add(new KeyFrame(Duration.millis(1), (ActionEvent actionEvent) -> {
                this.getChildren().add(chiffre);
            }));
            timeline1.setCycleCount(1);
            SequentialTransition animation = new SequentialTransition();
            animation.getChildren().addAll(timeline1);
            animation.play();
        }

        public final void tracerPlateau() {
            // Tracement des lignes verticales
            for (int x = 0; x <= 11; x++) {
                Line line = new Line(margeX + x * espacement, margeY, margeX + x * espacement, margeY + 11 * espacement);
                this.getChildren().add(line);
            }
            // Tracement des lignes horizontales
            for (int y = 0; y <= 11; y++) {
                Line line = new Line(margeX, margeY + y * espacement, margeX + 11 * espacement, margeY + y * espacement);
                this.getChildren().add(line);
            }
            // Tracement des chiffres en ligne
            int sizeFont = ((int) (espacement * 0.8));
            for (int i = 1; i <= 10; i++) {
                double posX = ((i < 10) ? margeX + espacement * i + espacement * 0.3 : margeX + espacement * i + espacement * 0.1);
                Text chiffre = new Text(posX, margeY + espacement * 0.8, "" + i);
                chiffre.setFont(new Font(sizeFont));
                chiffre.setFill(Color.web("#0000ff"));
                this.getChildren().add(chiffre);
            }
            // Tracement des chiffres en colonnes
            for (int i = 1; i <= 10; i++) {
                double posX = ((i < 10) ? margeX + espacement * 0.3 : margeX + espacement * 0.1);
                Text chiffre = new Text(posX, margeY + (i) * espacement + espacement * 0.8, "" + i);
                chiffre.setFont(new Font(sizeFont));
                chiffre.setFill(Color.web("#0000ff"));
                this.getChildren().add(chiffre);
            }
        }

        public void actionClick() {
            scene.setOnMouseClicked((MouseEvent event) -> {
                if (enAttente) {
                    ajouteTextToChat("Patience, ce n'est pas encore ton tour\n");
                } else //System.out.println("Click sur le point {" + event.getX() + ";" + event.getY() + "}");
                {
                    if (event.getX() < margeX + espacement || event.getX() > margeX + 11 * espacement || event.getY() < margeY + espacement || event.getY() > margeY + espacement * 11) {
                        System.err.println("Click en dehors du plateau de jeu");
                    } else {
                        int x = (int) ((event.getX() - margeX) / espacement);
                        int y = (int) ((event.getY() - margeY) / espacement);
                        Position pos = new Position(x, y);
                        if (listePositions.contains(pos)) {
                            ajouteTextToChat("Attention: tu as déjà cliqué ici.\n");
                        } else {
                            listePositions.add(pos);
                            actionApresClickCorrect(x - 1, y - 1);
                        }
                    }
                }
            });
        }

        private void actionApresClickCorrect(int x, int y) {
            try {
                enAttente = true;

                //System.out.println(x + ";" + y);
                // Contact avec le serveur
                Registry reg = LocateRegistry.getRegistry(3212);
                ServeurInterface serveur = (ServeurInterface) reg.lookup("Serv");
                positionCourante = new Position(x, y);
                serveur.sendPosition(positionCourante);
            } catch (RemoteException | NotBoundException ex) {
                Logger.getLogger(StageJeu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
