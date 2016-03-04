
package model;

import java.io.Serializable;


public class BoatPosition implements Serializable{
    
    /**
     * Taille par défaut d'un bateau.
     */
    public static final int BOAT_LENGTH = 4;
    
    /**
     * Orientation du bateau (H ou V).
     */
    private final String orientation;
    
    /**
     * Position de la première case du bateau.
     */
    private final int posX, posY;

    /**
     * Créer un bateau avec une certaine orientation et la bonne position.
     * @param orientation H/V
     * @param posX Integer
     * @param poxY Integer
     */
    public BoatPosition(String orientation, int posX, int posY) {
        this.orientation = orientation;
        this.posX = posX;
        this.posY = posY;
    }
    
    /**
     * Vérifie si la position est correcte.
     * @return 
     */
    public boolean verifier() {
        // TODO : Algo de vérification d'une position
        if(((this.posX + BOAT_LENGTH > Grille.TAILLE_GRILLE)&&(this.orientation=="V")) && ((this.posY + BOAT_LENGTH > Grille.TAILLE_GRILLE)&&(this.orientation=="H"))) {
            return false;
        }
        return true;
    }

    public int getPosX() {
        return posX;
    }

    public int getPoxY() {
        return posY;
    }

    public String getOrientation() {
        return orientation;
    }
    
}
