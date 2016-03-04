
package interaction;

import exception.NomExistantException;
import exception.PartiePleineException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import model.BoatPosition;


public interface ServeurInterface extends Remote {
    
    public abstract void setClient(Client client, BoatPosition position) throws RemoteException, PartiePleineException, NomExistantException;
    
}
