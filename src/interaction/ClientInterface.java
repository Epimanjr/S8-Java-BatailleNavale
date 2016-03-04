package interaction;

import java.rmi.Remote;
import java.rmi.RemoteException;
import model.Grille;

public interface ClientInterface extends Remote {

    /**
     * Méthode appelée par le serveur pour envoyer un message au client.
     *
     * @param message message
     * @throws RemoteException
     */
    public void recevoirMessage(String message) throws RemoteException;
}
