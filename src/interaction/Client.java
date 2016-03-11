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
import model.Position;

public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {

    /**
     * Nom du client.
     */
    private String name;
    
    /**
     * Grille du client.
     */
    public Grille grille;


    /**
     * Créer un client à partir de son nom.
     *
     * @param name Nom du client
     * @param grille Grille du client
     * @throws RemoteException
     */
    public Client(String name, Grille grille) throws RemoteException{
        this.name = name;
        this.grille = grille;
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
            // Récupère le serveur
            Registry reg = LocateRegistry.getRegistry(3212);
            ServeurInterface serveur = (ServeurInterface) reg.lookup("Serv");

            // Demande à l'utilisateur du nom et de la position de sont bateau
            String name = Interaction.askForName();
            BoatPosition position = Interaction.askForPosition();

            try {
                // Vérifier l'inscription
                serveur.verifierInscription(name, position);
                Grille g = new Grille(position);
                Client client = new Client(name, g);
                // OK
                reg.rebind("Client_"+name, (ClientInterface)client);
                // Demande d'ajout auprès du serveur
                serveur.setClient(name, g);
            } catch (PartiePleineException ex) {
                System.err.println("Erreur: partie déjà pleine.");
            } catch (NomExistantException ex) {
                System.err.println("Erreur: votre nom est déjà utilisé.");
            }

        } catch (NotBoundException | RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Grille getGrille() {
        return grille;
    }

    public void setGrille(Grille grille) {
        this.grille = grille;
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

    @Override
    public void recevoirMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    @Override
    public void jouer() throws RemoteException {
        System.out.print("A toi de jouer ! \nX=");
        int x = Interaction.demanderEntierEntreIntervalle(1, 10);
        System.out.print("Y=");
        int y = Interaction.demanderEntierEntreIntervalle(1, 10);
        //return new Position(x-1, y-1);
        // TODO send position to server
    }

    @Override
    public boolean modeGraphique() throws RemoteException {
        return false;
    }

    @Override
    public void afficherResultat(boolean touche) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
