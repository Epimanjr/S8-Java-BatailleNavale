package interaction;

import java.util.InputMismatchException;
import java.util.Scanner;
import model.BoatPosition;

public class Interaction {

    /**
     * Scanner utilisé dans toutes les interactions.
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Méthode qui demande le nom de l'utilisateur.
     *
     * @return Nom valide
     */
    public static String askForName() {
        do {
            System.out.print("Entrer votre nom : (30 caractères max) ");
            String name = SCANNER.nextLine();
            if (name.length() <= 30) {
                return name;
            } else {
                System.err.println("Erreur: veuillez entrer un nom valide.");
            }
        } while (true);
    }

    /**
     * Demande la position à un utilisateur.
     *
     * @return BoatPosition
     */
    public static BoatPosition askForPosition() {
        // First step : orientation
        String orientation;
        do {
            System.out.println("Orientation (H/V) : ");
            orientation = SCANNER.nextLine();
        } while (orientation.equals("H") || orientation.equals("V") || orientation.equals("h") || orientation.equals("v"));
        // Positions X & Y
        System.out.println("Position X : ");
        int posX = demanderEntierEntreIntervalle(1, 10);
        int posY = demanderEntierEntreIntervalle(1, 10);
        return new BoatPosition(orientation, posX - 1, posY - 1);
    }

    /**
     * Demande au clavier un nombre entier valide dans un certain intervalle.
     *
     * @param min Min
     * @param max Max
     * @return Nombre entier valide
     */
    public static int demanderEntierEntreIntervalle(int min, int max) {
        while (true) {
            int res = demanderEntier();
            if (res < min || res > max) {
                System.err.println("Erreur: veuillez entrer un nombre entre " + min + " et " + max);
            } else {
                return res;
            }
        }
    }

    /**
     * Demande au clavier un nombre entier.
     *
     * @return Entier valide
     */
    public static int demanderEntier() {
        while (true) {
            try {
                int res = SCANNER.nextInt();
                SCANNER.nextLine();
                return res;
            } catch (InputMismatchException e) {
                System.err.println("Erreur: veuillez entrer un nombre entier valide.");
                SCANNER.nextLine();
            }
        }
    }
}
