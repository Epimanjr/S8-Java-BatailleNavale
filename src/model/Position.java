/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Maxime
 */
public class Position implements Serializable {
    public int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    
}
