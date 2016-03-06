/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic;

import interaction.ServeurInterface;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
    
    public StageJeu() {
        initComponents();
        this.setTitle("Fenêtre de jeu");
        
    }
    
    public void ajouteTextToChat(String text) {
        this.textarea.appendText(text);
    }
    
    private void initComponents() {
        MainGroup mg = new MainGroup();
        scene = new Scene(mg, 2 * margeX + 11 * espacement, 4 * margeY + 11 * espacement + tailleChat);
        // Ajout de l'action du click
        mg.actionClick();
        
        this.setScene(scene);
    }
    
    class MainGroup extends Parent {
        
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
        
        private void tracerPlateau() {
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
                    ajouteTextToChat("Patience, ce n'est pas encore ton tour");
                } else //System.out.println("Click sur le point {" + event.getX() + ";" + event.getY() + "}");
                 if (event.getX() < margeX + espacement || event.getX() > margeX + 11 * espacement || event.getY() < margeY + espacement || event.getY() > margeY + espacement * 11) {
                        System.err.println("Click en dehors du plateau de jeu");
                    } else {
                        actionApresClickCorrect(event.getX(), event.getY());
                    }
            });
        }
        
        private void actionApresClickCorrect(double xEvent, double yEvent) {
            try {
                enAttente = true;
                int x = (int) ((xEvent - margeX) / espacement);
                int y = (int) ((yEvent - margeY) / espacement);
                //System.out.println(x + ";" + y);
                // Contact avec le serveur
                Registry reg = LocateRegistry.getRegistry(3212);
                ServeurInterface serveur = (ServeurInterface) reg.lookup("Serv");
                serveur.sendPosition(new Position(x-1, y-1));
            } catch (RemoteException | NotBoundException ex) {
                Logger.getLogger(StageJeu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
