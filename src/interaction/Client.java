package interaction;

import exception.NomExistantException;
import exception.PartiePleineException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BoatPosition;
import model.Grille;

public class Client extends UnicastRemoteObject {

    public Client() throws RemoteException {
    }

    public static void main(String args[]) {
        try {
            Registry reg = LocateRegistry.getRegistry(3212);
            ServeurInterface serveur = (ServeurInterface) reg.lookup("Serv");

            // Demandes à l'utilisateur du nom et de la position de sont bateau
            String name = Interaction.askForName();
            BoatPosition position = Interaction.askForPosition();

            try {
                // Demande d'ajout auprès du serveur
                serveur.setClient(name, position);

                // TODO
                System.out.println("Inscription réalisée avec succès.");
            } catch (PartiePleineException ex) {
                System.err.println("Erreur: partie déjà pleine.");
            } catch (NomExistantException ex) {
                System.err.println("Erreur: votre nom est déjà utilisé.");
            }

        } catch (NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
