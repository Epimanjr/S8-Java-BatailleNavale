package interaction;

import java.rmi.Remote;
import java.rmi.RemoteException;
import model.Grille;

public interface ClientInterface extends Remote {

    /**
     * Méthode appelée par le serveur pour confirmer l'inscription du client.
     *
     * @param grille Grille du joueur
     * @throws RemoteException
     */
    public void recevoirConfirmationInscription(Grille grille) throws RemoteException;
}
