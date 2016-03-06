package model;

import java.io.Serializable;

public class Grille implements Serializable {

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
     * Placement des B dans les cases correspondantes.
     *
     * @param position Position du bateau
     */
    private void setBoat(BoatPosition position) {
        // TODO placer le bateau
        int x = position.getPosX();
        int y = position.getPoxY();
        for (int i = 0; i < BoatPosition.BOAT_LENGTH; i++) {
            this.grille[x][y] = "B";
            if (position.getOrientation().toUpperCase().equals("H")) {
                y++;
            }
            if (position.getOrientation().toUpperCase().equals("V")) {
                x++;
            }
        }
    }

    @Override
    public String toString() {

        String s = "|   |";
        for (int i = 1; i <= 10; i++) {
            s += " " + i + " |";
        }
        s += "\n";
        s += "---------------------------------------------\n";
        for (int i = 1; i <= 10; i++) {
            s += "| " + i + " |";
            for (int j = 1; j <= 10; j++) {
                s += " " + this.grille[i - 1][j - 1] + " |";
            }
            s += "\n";
            s += "---------------------------------------------\n";
        }
        return s;
    }
    
    public String afficherPourAdversaire() {
        String s = "|   |";
        for (int i = 1; i <= 10; i++) {
            s += " " + i + " |";
        }
        s += "\n";
        s += "---------------------------------------------\n";
        for (int i = 1; i <= 10; i++) {
            s += "| " + i + " |";
            for (int j = 1; j <= 10; j++) {
                String affiche = (this.grille[i - 1][j - 1].equals("B")) ? "." : this.grille[i - 1][j - 1];
                s += " " + affiche + " |";
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

    /**
     * Test de la victoire.
     *
     * @return boolean
     */
    public boolean testVictoire() {
        for (int i = 0; i < Grille.TAILLE_GRILLE; i++) {
            for (int j = 0; j < Grille.TAILLE_GRILLE; j++) {
                if (this.grille[i][j].equals("B")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Impact d'un tir sur une grille.
     *
     * @param pos Position ciblée
     * @return vrai si bateau touché
     */
    public boolean impact(Position pos) {
        String contenu =  this.grille[pos.x][pos.y];
        if(contenu.equals("B")) {
            this.grille[pos.x][pos.y] = "X";
            return true;
        } else {
            this.grille[pos.x][pos.y] = "0";
            return false;
        }
    }
    
    public String getValueOfPosition(int x, int y) {
        return this.grille[x][y];
    }
}
