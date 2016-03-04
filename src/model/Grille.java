package model;

import java.io.Serializable;

public class Grille  implements Serializable{

    /**
     * Taille de la grille.
     */
    public static final int TAILLE_GRILLE = 10;

    /**
     * Modélisation de la grille par un tableau à deux dimensions. String vide =
     * Rien "0" = Case vide attaqué "B" = Présence d'un bateau "X" = Une partie
     * du bateau touché
     */
    private final String[][] grille = new String[TAILLE_GRILLE][TAILLE_GRILLE];

    /**
     * Constructeur d'une grille : initialise toutes les cases avec une chaîne
     * vide.
     */
    public Grille() {
        init();
    }

    public Grille(BoatPosition position) {
        init();
        setBoat(position);
    }

    /**
     * Placement des X dans les cases correspondantes.
     *
     * @param position Position du bateau
     */
    private void setBoat(BoatPosition position) {
        // TODO placer le bateau
        int x = position.getPosX();
        int y = position.getPoxY();
        for(int i=0;i<BoatPosition.BOAT_LENGTH;i++) {
            this.grille[x][y] = "X";
            if(position.getOrientation().toUpperCase().equals("H")) {
                y++;
            }
            if(position.getOrientation().toUpperCase().equals("V")) {
                x++;
            }
        }
    }
    
    @Override
    public String toString() {
        
        String s = "|   |";
        for(int i=1;i<=10;i++) {
            s += " " + i + " |";
        }
        s += "\n";
        s += "---------------------------------------------\n";
        for(int i=1;i<=10;i++) {
            s += "| " + i + " |";
            for(int j=1;j<=10;j++) {
                s += " " + this.grille[i-1][j-1] + " |";
            }
            s += "\n";
            s += "---------------------------------------------\n";
        }
        return s;
    }

    /**
     * Initialisation de la grille.
     */
    private void init() {
        for (int i = 0; i < TAILLE_GRILLE; i++) {
            for (int j = 0; j < TAILLE_GRILLE; j++) {
                grille[i][j] = ".";
            }
        }
    }
    
    public static void main(String[] args) {
        Grille g = new Grille();
        System.out.println(g);
    }
}
