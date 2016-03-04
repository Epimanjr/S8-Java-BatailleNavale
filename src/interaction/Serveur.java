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
import model.Position;

public class Serveur extends UnicastRemoteObject implements ServeurInterface {

    /**
     * Contient toutes les grilles nécessaires au jeu.
     */
    private final ArrayList<Client> clients = new ArrayList<>();

    private int numeroTour;

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
        boolean victoire = false;
        numeroTour = 0;
        String nameJoueurQuiDoitJouer="";
        try {
            Registry reg = LocateRegistry.getRegistry(3212);
            while (!victoire) {
                // Demande à un joueur de jouer
                nameJoueurQuiDoitJouer = clients.get(numeroTour % 2).getName();
                ClientInterface joueur = (ClientInterface) reg.lookup("Client_" + nameJoueurQuiDoitJouer);
                Position pos = joueur.jouer();
                // Impacter grille
                Grille grilleImpactee = clients.get((numeroTour+1)%2).getGrille();
                boolean touche = impacterGrille(pos, grilleImpactee);
                // Notifier joueurs
                notifierJoueurs(touche, numeroTour % 2, reg, joueur);
                // Test victoire
                victoire = grilleImpactee.testVictoire();
                numeroTour++;
            }
            // Fin de la partie
            System.out.println("Fin de la partie");
            System.out.println("Vainqueur : " +nameJoueurQuiDoitJouer);
        } catch (NotBoundException | AccessException ex) {
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Impact sur la grille après un jeu.
     *
     * @param pos
     * @param grille
     * @throws RemoteException
     */
    private boolean impacterGrille(Position pos, Grille grille) throws RemoteException {
        // Impact sur la grille
        return grille.impact(pos);
    }
    
    
    private void notifierJoueurs(boolean touche, int indiceJoueur1, Registry reg, ClientInterface joueur) throws RemoteException {
        try {
            int indiceJoueur2 = (indiceJoueur1+1)%2;
            ClientInterface joueur2 = (ClientInterface) reg.lookup("Client_" + clients.get(indiceJoueur2).getName());
            // Affichage grilles
            Grille grille = clients.get(indiceJoueur2).getGrille();
            String messageJoueur1 = grille.afficherPourAdversaire();
            String messageJoueur2 = grille.toString();
            // Affichage message personnalisé
            if(touche) {
                messageJoueur1 += "Bien joué, vous avez touché un bateau de " + clients.get(indiceJoueur2).getName();
                messageJoueur2 += "PLATCH: " + clients.get(indiceJoueur1).getName() + " a touché un de vos bateaux.";
            } else {
                messageJoueur1 += "Raté, tant pis pour la baleine.";
                messageJoueur2 += "Sauvé, il ne sait pas viser.";
            }
            // Envoie des messages
            joueur.recevoirMessage(messageJoueur1);
            joueur2.recevoirMessage(messageJoueur2);
        } catch (NotBoundException | AccessException ex) {
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        }
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
