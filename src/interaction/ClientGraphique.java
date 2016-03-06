/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interaction;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import model.Position;

/**
 *
 * @author Maxime
 */
public class ClientGraphique extends UnicastRemoteObject implements ClientInterface, Serializable {

    
    public ClientGraphique() throws RemoteException {
    }

    
    @Override
    public void recevoirMessage(String message) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Position jouer() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) {
        
    }
    

}
