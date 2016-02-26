
package interaction;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.BoatPosition;


public class Client extends UnicastRemoteObject {

    public Client() throws RemoteException {
    }
    
    public static void main(String args[]) {
        try {
            // Définition du security manager
            System.setSecurityManager(new SecurityManager());
            
            // Récupération du serveur via RMI
            ServeurInterface serveur = (ServeurInterface)Naming.lookup("rmi://127.0.0.1/");
            
            // Demandes à l'utilisateur du nom et de la position de sont bateau
            String name = Interaction.askForName();
            BoatPosition position = Interaction.askForPosition();
            
            // Demande d'ajout auprès du serveur
            serveur.setClient(name, position);
            
            // TODO
            
        } catch (NotBoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
