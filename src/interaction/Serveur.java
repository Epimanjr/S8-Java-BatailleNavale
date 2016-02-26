
package interaction;

import exception.NomExistantException;
import exception.PartiePleineException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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
        if(clients.size() >= 2) {
            throw new PartiePleineException();
        }
        // Nom déjà pris
        if(clients.containsKey(name)) {
            throw new NomExistantException();
        }
        // Création de la grille
        Grille grille = new Grille(position);
        // OK, on ajoute le client
        clients.put(name, grille);
    }
    
    public static void main(String args[]) {
        try {
            // Création du rmiregistry
            LocateRegistry.createRegistry(2500);
            
            // Définition du security manager et des propriétés
            System.setSecurityManager(new SecurityManager());
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            
            // Création du serveur
            Serveur serveur = new Serveur();
            
            // Ajout du serveur dans le réseau via RMI
            Naming.rebind("rmi://127.0.0.1:2500/", serveur);
            
            System.out.println("Bataille Navale : Serveur --> OK");
            
            // TODO 
            
        } catch (RemoteException ex) {
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Serveur.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
