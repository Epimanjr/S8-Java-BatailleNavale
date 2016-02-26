
package model;


public class BoatPosition {
    
    /**
     * Orientation du bateau (H ou V).
     */
    private final String orientation;
    
    /**
     * Position de la première case du bateau.
     */
    private final int posX, poxY;

    /**
     * Créer un bateau avec une certaine orientation et la bonne position.
     * @param orientation H/V
     * @param posX Integer
     * @param poxY Integer
     */
    public BoatPosition(String orientation, int posX, int poxY) {
        this.orientation = orientation;
        this.posX = posX;
        this.poxY = poxY;
    }
    
    /**
     * Vérifie si la position est correcte.
     * @return 
     */
    public boolean verifier() {
        // TODO : Algo de vérification d'une position
        return false;
    }
}
