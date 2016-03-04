package interaction;

import exception.NomExistantException;
import exception.PartiePleineException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BoatPosition;
import model.Grille;

public class Client implements Serializable {

    /**
     * Nom du client.
     */
    private String name;


    /**
     * Créer un client à partir de son nom.
     *
     * @param name Nom du client.
     * @throws RemoteException
     */
    public Client(String name) {
        this.name = name;
    }

    /**
     * Affiche la grille du côté client.
     *
     * @param g La grille
     */
    public void afficherSaGrille(Grille g) {
        System.out.println("Voici votre grille : \n" + g);
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
                Client cli = new Client(name);
                serveur.setClient(cli, position);

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Client other = (Client) obj;
        return Objects.equals(this.name, other.name);
    }
    
    

    @Override
    public String toString() {
        return this.name;
    }

    
    
}
