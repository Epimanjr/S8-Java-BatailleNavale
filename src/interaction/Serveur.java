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

    /**
     * Contient les objets clients distants.
     */
    private ArrayList<ClientInterface> clientsRemote = new ArrayList<>();

    private int numeroTour;

    private final Registry reg;
    

    public Serveur() throws RemoteException {
        reg = LocateRegistry.getRegistry(3212);
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
    
    @Override
    public void sendPosition(Position position) throws RemoteException {
        // Impacter grille
        Grille grilleImpactee = clients.get((numeroTour + 1) % 2).getGrille();
        boolean touche = impacterGrille(position, grilleImpactee);
        // Notifier joueurs
        notifierJoueurs(touche, numeroTour);
        // Test victoire
        if(grilleImpactee.testVictoire()) {
            finPartie();
        } else {
            numeroTour++;
            jouerUnTour(numeroTour);
        }
    }
    
    private void finPartie() throws RemoteException {
        ClientInterface vainqueur = this.clientsRemote.get((numeroTour - 1) % 2);
        ClientInterface perdant = this.clientsRemote.get((numeroTour) % 2);
        vainqueur.recevoirMessage("Bravo, tu as gagné la partie.\n");
        perdant.recevoirMessage("Tu as perdu la partie.\n");
        // Fin de la partie
        System.out.println("Fin de la partie");
    }

    /**
     * Lancement de la partie.
     *
     * @throws RemoteException
     */
    private void lancerPartie() throws RemoteException {
        boolean victoire = false;
        numeroTour = 0;
        initRemote();
        jouerUnTour(numeroTour);
        
    }

    private void jouerUnTour(int numeroTour) throws RemoteException {
        // Demande à un joueur de jouer
        this.clientsRemote.get(numeroTour % 2).jouer();
        
    }

    /**
     * Méthode qui va récupérer les interfaces clients nécessaires.
     *
     * @throws RemoteException
     */
    private void initRemote() throws RemoteException {
        try {
            for (Client client : this.clients) {
                this.clientsRemote.add((ClientInterface) reg.lookup("Client_" + client.getName()));
            }
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Erreur: echec de la récupération des clients distants.");
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

    private void notifierJoueurs(boolean touche, int numeroTour) throws RemoteException {
        try {
            // Indices
            int indiceJoueur1 = numeroTour % 2;
            int indiceJoueur2 = (numeroTour + 1) % 2;
            // Noms
            String nomJoueur1 = this.clients.get(indiceJoueur1).getName();
            String nomJoueur2 = this.clients.get(indiceJoueur2).getName();
            // Affichage grilles
            Grille grille = clients.get(indiceJoueur2).getGrille();
            
            String messageJoueur1 = (this.clientsRemote.get(indiceJoueur1).modeGraphique()) ? "" : grille.afficherPourAdversaire();
            String messageJoueur2 = (this.clientsRemote.get(indiceJoueur2).modeGraphique()) ? "" : grille.toString();
            // Affichage message personnalisé
            if (touche) {
                messageJoueur1 += "Bien joué, vous avez touché un bateau de " + nomJoueur2 + "\n";
                messageJoueur2 += "PLATCH: " + nomJoueur1 + " a touché un de vos bateaux.\n";
            } else {
                messageJoueur1 += "Raté, tant pis pour la baleine.\n";
                messageJoueur2 += "Sauvé, il ne sait pas viser.\n";
            }
            // Envoie des messages
            this.clientsRemote.get(indiceJoueur1).afficherResultat(touche);
            this.clientsRemote.get(indiceJoueur1).recevoirMessage(messageJoueur1);
            this.clientsRemote.get(indiceJoueur2).recevoirMessage(messageJoueur2);
        } catch (AccessException ex) {
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Notifie le ou les clients après une inscription
     */
    private void notifierClients() throws RemoteException {
        try {
            String message = "Votre inscription a été acceptée par le serveur.\n";
            ClientInterface joueur1 = (ClientInterface) reg.lookup("Client_" + clients.get(0).getName());
            switch (clients.size()) {
                // Cas où pas de joueur avant
                case 1:
                    // Message pour le joueur 1

                    if (!joueur1.modeGraphique()) {
                        message += "Votre grille : \n" + clients.get(0).getGrille();
                    }
                    message += "En attente d'un adversaire ...\n";
                    joueur1.recevoirMessage(message);
                    break;
                // Cas où il rejoint un joueur
                case 2:
                    // Message pour le joueur 2
                    ClientInterface joueur2 = (ClientInterface) reg.lookup("Client_" + clients.get(1).getName());
                    if (!joueur2.modeGraphique()) {
                        message += "Votre grille : \n" + clients.get(1).getGrille();
                    }
                    message += "Vous avez rejoint la partie de " + clients.get(0).getName() + "\n";

                    joueur2.recevoirMessage(message);
                    // Message pour le joueur 1
                    String message1 = "trouvé !\nVous allez affronter " + clients.get(1).getName() + "\n";
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
