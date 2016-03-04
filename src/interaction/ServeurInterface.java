
package interaction;

import exception.NomExistantException;
import exception.PartiePleineException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import model.BoatPosition;
import model.Grille;


public interface ServeurInterface extends Remote {
    
    public abstract Grille verifierInscription(String name, BoatPosition position) throws RemoteException, PartiePleineException, NomExistantException;
    public abstract void setClient(String name, Grille grille) throws RemoteException;
    
}
