/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Patricia Rocha de Toro, Elisa Calhau de Castro, Ricardo Ribeiro Gudwin
 *****************************************************************************/

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

