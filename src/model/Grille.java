
package model;


public class Grille {
    
    /**
     * Taille de la grille.
     */
    public static final int TAILLE_GRILLE = 10;
    
    /**
     * Modélisation de la grille par un tableau à deux dimensions.
     * String vide = Rien
     * "0" = Case vide attaqué
     * "B" = Présence d'un bateau
     * "X" = Une partie du bateau touché
     */
    private final String[][] grille = new String[TAILLE_GRILLE][TAILLE_GRILLE];

    /**
     * Constructeur d'une grille : initialise toutes les cases avec une chaîne vide.
     */
    public Grille() {
        for(int i=0;i<TAILLE_GRILLE;i++) {
            for(int j=0;j<TAILLE_GRILLE;j++) {
                grille[i][j] = "";
            }
        }
    }
    
    
}
