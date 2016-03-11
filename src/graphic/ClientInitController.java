/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic;

import exception.NomExistantException;
import exception.PartiePleineException;
import interaction.Client;
import interaction.ClientGraphique;
import interaction.ClientInterface;
import interaction.ServeurInterface;
import java.net.URL;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import model.BoatPosition;
import model.Grille;

/**
 * FXML Controller class
 *
 * @author Maxime
 */
public class ClientInitController implements Initializable {

    @FXML
    TextField saisieNom;
    @FXML
    RadioButton posVertical;
    @FXML
    RadioButton posHorizontal;
    
    @FXML
    Slider sliderX;
    @FXML
    Slider sliderY;
    
    @FXML
    Label label;

    private final ToggleGroup tg = new ToggleGroup();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        // Groupe de RadioButton
        posVertical.setToggleGroup(tg);
        posHorizontal.setToggleGroup(tg);
        // Action slider
        sliderX.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mettreAJourLabel();
            }
        });
        sliderY.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mettreAJourLabel();
            }
        });
    }
    
    private void mettreAJourLabel() {
        int x = (int)sliderX.getValue();
        int y = (int)sliderY.getValue();
        label.setText("("+x+";"+y+")");
    }

    @FXML
    private void validerInscription(ActionEvent event) {
        try {
            // Récupère le serveur
            Registry reg = LocateRegistry.getRegistry(3212);
            ServeurInterface serveur = (ServeurInterface) reg.lookup("Serv");

            ClientGraphique.stagejeu = new StageJeu();
            try {
                // Récupération variables
                String name = saisieNom.getText();
                String orientation = (tg.getSelectedToggle().toString().endsWith("'Horizontal'")) ? "H" : "V";
                BoatPosition position = new BoatPosition(orientation, (int)sliderX.getValue()-1,(int)sliderY.getValue()-1);
                // Vérifier l'inscription
                Grille grille = serveur.verifierInscription(name, position);
                ClientGraphique client = new ClientGraphique(name, grille);
                // OK
                reg.rebind("Client_" + name, (ClientInterface) client);
                // Demande d'ajout auprès du serveur
                serveur.setClient(name, grille);
                //Affichage de la fenêtre de jeu
                ClientInitStage.stage.close();
                ClientGraphique.stagejeu.show();
            } catch (PartiePleineException ex) {
                System.err.println("Erreur: partie déjà pleine.");
            } catch (NomExistantException ex) {
                System.err.println("Erreur: votre nom est déjà utilisé.");
            }

        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(ClientInitController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
