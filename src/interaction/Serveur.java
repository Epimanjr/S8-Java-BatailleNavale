package interaction;

import exception.NomExistantException;
import exception.PartiePleineException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BoatPosition;
import model.Grille;

public class Serveur extends UnicastRemoteObject implements ServeurInterface {

    /**
     * Contient toutes les grilles nécessaires au jeu.
     */
    private final HashMap<String, Grille> clients = new HashMap<>();
    
    public Serveur() throws RemoteException {
    }
    
    @Override
    public void setClient(String name, BoatPosition position) throws RemoteException, PartiePleineException, NomExistantException {
        // Déjà deux joueurs
        if (clients.size() >= 2) {
            throw new PartiePleineException();
        }
        // Nom déjà pris
        if (clients.containsKey(name)) {
            throw new NomExistantException();
        }
        // Création de la grille
        Grille grille = new Grille(position);
        // OK, on ajoute le client
        clients.put(name, grille);
        System.out.println("Nouveau client : " + name + "\n" + grille);
    }
    
    public static void main(String args[]) {
        try {
            Registry reg = LocateRegistry.createRegistry(3212);
            reg.rebind("Serv", (ServeurInterface)new Serveur());
            System.out.println("Serveur OK");
        } catch (RemoteException ex) {
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
