package interaction;

import exception.NomExistantException;
import exception.PartiePleineException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BoatPosition;
import model.Grille;

public class Serveur extends UnicastRemoteObject implements ServeurInterface {

    /**
     * Contient toutes les grilles nécessaires au jeu.
     */
    private final ArrayList<Client> clients = new ArrayList<>();

    public Serveur() throws RemoteException {
    }

    @Override
    public Grille verifierInscription(String name, BoatPosition position) throws RemoteException, PartiePleineException, NomExistantException {
        // Déjà deux joueurs
        if (clients.size() >= 2) {
            throw new PartiePleineException();
        }
        // Nom déjà pris
        if (clients.contains(new Client(name, null))) {
            throw new NomExistantException();
        }
        // Création de la grille
        Grille grille = new Grille(position);
        return grille;
    }

    @Override
    public void setClient(String name, Grille grille) throws RemoteException {
        // OK, on ajoute le client
        clients.add(new Client(name, grille));
        System.out.println("Nouveau client : " + name + "\n" + grille);
        // Notifications
        notifierClients();
        // Lancement du jeu si 2 joueurs
        if (clients.size() == 2) {
            lancerPartie();
        }
    }

    /**
     * Lancement de la partie.
     *
     * @throws RemoteException
     */
    private void lancerPartie() throws RemoteException {

    }

    /**
     * Notifie le ou les clients après une inscription
     */
    private void notifierClients() throws RemoteException {
        try {
            Registry reg = LocateRegistry.getRegistry(3212);

            String message = "Votre inscription a été acceptée par le serveur.\n";
            ClientInterface joueur1 = (ClientInterface) reg.lookup("Client_" + clients.get(0).getName());
            switch (clients.size()) {
                // Cas où pas de joueur avant
                case 1:
                    // Message pour le joueur 1
                    message += "Votre grille : \n" + clients.get(0).getGrille() + "En attente d'un adversaire ...";
                    joueur1.recevoirMessage(message);
                    break;
                // Cas où il rejoint un joueur
                case 2:
                    // Message pour le joueur 2
                    message += "Votre grille : \n" + clients.get(1).getGrille() + "Vous avez rejoint la partie de " + clients.get(0).getName() + "\n";
                    ClientInterface joueur2 = (ClientInterface) reg.lookup("Client_" + clients.get(1).getName());
                    joueur2.recevoirMessage(message);
                    // Message pour le joueur 1
                    String message1 = "trouvé !\nVous allez affronter " + clients.get(1).getName();
                    joueur1.recevoirMessage(message1);
                    break;
                default:
                    System.err.println("Erreur: nombre de joueurs incorrect.");
                    break;
            }
        } catch (AccessException | NotBoundException ex) {
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        try {
            Registry reg = LocateRegistry.createRegistry(3212);
            reg.rebind("Serv", (ServeurInterface) new Serveur());
            System.out.println("Serveur OK");
        } catch (RemoteException ex) {
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
