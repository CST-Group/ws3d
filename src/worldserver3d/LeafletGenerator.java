/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package worldserver3d;

import java.util.Random;
import model.Leaflet;
import util.Constants;

/**
 *
 * @author eccastro
 */
public class LeafletGenerator {

    private Leaflet leaflet = null;

    public LeafletGenerator(){

        leaflet = new Leaflet(generateItems());

    }
    public LeafletGenerator(Long owner){

        leaflet = new Leaflet(generateItems(), owner);

    }

    public Leaflet getLeaflet(){
        return leaflet;
    }

    private int[] generateItems(){
        int[] items = new int[Constants.LEAFLET_ITEMS_NUMBER]; //currently set to 3
        Random rd = new Random();

        for (int i = 0; i< Constants.LEAFLET_ITEMS_NUMBER; i++){
              items[i] = rd.nextInt(Constants.MAX_NUMBER_OF_COLORS);
         }
        return items;
    }
}

