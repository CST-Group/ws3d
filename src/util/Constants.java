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
package util;

import com.jme.renderer.ColorRGBA;
import java.awt.Color;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author eccastro
 */
public class Constants {

    public static final int PORT = 4011;
    public static final String colorRED = "Red";
    public static final String colorGREEN = "Green";
    public static final String colorBLUE = "Blue";
    public static final String colorYELLOW = "Yellow";
    public static final String colorMAGENTA = "Magenta";
    public static final String colorWHITE = "White";
    public static final String colorDARKGRAY_SPOILED = "DarkGray_Spoiled";
    public static final String colorORANGE = "Orange";    
    public static final String PFOOD = "PerishableFood";
    public static final String NPFOOD = "NonPerishableFood";
    public static final int categoryCREATURE = 0;
    public static final int categoryBRICK = 1;
    public static final int categoryFOOD = 2;
    public static final int categoryPFOOD = 21;
    public static final int categoryNPFOOD = 22;
    public static final int categoryJEWEL = 3;
    public static final int categoryDeliverySPOT = 4;
    public static final int categoryCAGE = 5;
    public static final double[] deliverySpotCoords = {0, 0};
    public static final double M_PI = Math.PI;

    public static final double PITCH_INEXISTENT = -11111;
            
    //Affordances:
    public static final int Affordance__VIEWABLE = 30;
    public static final int Affordance__HIDEABLE = 31;
    public static final int Affordance__UNHIDEABLE = 32;
    public static final int Affordance__GRASPABLE = 33;
    public static final int Affordance__EATABLE = 34;
    public static final int Affordance__PUTINBAGABLE = 35;//sth that can be put in a bag
    public static final int Affordance__OPENABLE = 36; //sth that can be opened (eg. a cage)
    public static final int Affordance__CLOSEABLE = 37;//sth than be closed (eg. a cage)
    public static final int Affordance__INSERTABLE = 38;//sth than can contain another thing (which had been inserted into this sth)[eg. a container]
    public static final int Affordance__REMOVEFROMABLE = 39;//sth from whose inside another thing is removed (eg. a container)

    //Error code: used in the WS3DProxy. Do not change it.
    public static final String ERROR_CODE = "@@@";

    //Prefix for all Thing's ID. It is part of the alphanumeric ID in the system.
    public static final String CREATURE_PREFIX = "Creature_";
    public static final String CRYSTAL_PREFIX = "Jewel_";
    public static final String PFOOD_PREFIX = "PFood_";
    public static final String NPFOOD_PREFIX = "NPFood_";
    public static final String BRICK_PREFIX = "Brick_";
    public static final String CAGE_PREFIX = "Cage_";
    public static final String DELIVERY_SPOT_PREFIX = "DeliverySpot_";

    // State of a cage: (Note: mnj --> apple-nut-jewel)
    ///////Never use "-1" for new states. It is used to notify errors 
    /////// (see class CageFSM).
    public static final int EMPTY_OPENED = 0; //"Container_empty_opened";
    public static final int EMPTY_CLOSED = 1; //"Container_empty_closed";
    public static final int FULL_CLOSED_APPLE = 2; // "Container_full_closed_apple";
    public static final int FULL_OPENED_APPLE = 3; //"Container_full_opened_apple";
    public static final int FULL_CLOSED_NUT = 4; //"Container_full_closed_nut";
    public static final int FULL_OPENED_NUT = 5;//"Container_full_opened_nut";
    public static final int FULL_OPENED_JEWEL = 6; //"Container_full_opened_jewel";
    public static final int FULL_CLOSED_JEWEL = 7; //"Container_full_closed_jewel";
    public static final int FULL_OPENED_MNJ = 8; //"Container_full_opened_mnj";
    public static final int FULL_CLOSED_MNJ = 9; //"Container_full_closed_mnj";
    public static final int FULL_OPENED_MN = 10; //"Container_full_opened_mn";
    public static final int FULL_CLOSED_MN = 11; //"Container_full_closed_mn";
    public static final int FULL_OPENED_MJ = 12; //"Container_full_opened_mj";
    public static final int FULL_CLOSED_MJ = 13; //"Container_full_closed_mj";
    public static final int FULL_OPENED_NJ = 14; //"Container_full_opened_nj";
    public static final int FULL_CLOSED_NJ = 15; //"Container_full_closed_nj";

    //Valid period for Perishable food:
    public static final int VALID_PERIOD_SECS = 600;

    //Spurious value for angular velocity:
    public static final double WNULL = 1001;
    public static final double CREATURE_SIZE = 40.0;
    public static final double FOOD_SIZE = 12.0;
    public static final double CRYSTAL_SIZE = 12.0;
    public static final double CREATURE_MAX_FUEL = 1000.0;
    public static final double CREATURE_MAX_SEROTONIN = 100.0;
    public static final double CREATURE_MAX_ENDORPHINE = 100.0;
    //how much energy is decrement at each timer cycle:
    public static final double CREATURE_FUEL_DEC = CREATURE_MAX_FUEL * 0.05;
    public static final double CREATURE_SEROTONIN_DEC = CREATURE_MAX_SEROTONIN * 0.20;
    public static final double CREATURE_ENDORPHINE_DEC = CREATURE_MAX_ENDORPHINE * 0.20;

    //food energy/calories:
    public static final double PFOOD_ENERGY = Constants.CREATURE_MAX_FUEL * 0.30;
    public static final double NPFOOD_ENERGY = Constants.CREATURE_MAX_FUEL * 0.15;
    
    

    public final static int MAX_NUMBER_OF_LEAFLETS = 3;
    public final static int LEAFLET_ITEMS_NUMBER = 3;
    public final static int MAX_NUMBER_OF_COLORS = 6; //see EditJewelFrame
    public static final String[] arrayOfColors = {Constants.colorRED, Constants.colorGREEN, Constants.colorBLUE, Constants.colorYELLOW, Constants.colorMAGENTA, Constants.colorWHITE, Constants.colorDARKGRAY_SPOILED, Constants.colorORANGE};
    public static final double M_PI_2 = Math.PI / 2.0;
    public static final int NUMBER_CAMERAS = 2;
    private static final HashMap<String, ColorRGBA> colorMap = new HashMap();

    public static final int HARDNESS_DEFAULT = 1; //solid
    public static final int ENERGY_DEFAULT = 0; //not a food
    public static final int OCCLUDED_DEFAULT = 0; //it is visible;not occluded

    /**
     * Tokens to parser Attribute
     */
    public static final String TOKEN_NAME_ID = "NAMEID=";
    public static final String TOKEN_INDEX = "INDEX=";
    public static final String TOKEN_COLOR = "COLOR=";
    public static final String TOKEN_SPEED = "SPEED=";
    public static final String TOKEN_WHEEL = "WHEEL=";//to be deleted!!!
    public static final String TOKEN_WHEEL_R = "WHEEL_R=";
    public static final String TOKEN_WHEEL_L = "WHEEL_L=";
    public static final String TOKEN_THING_PITCH = "THING_PITCH=";
    public static final String TOKEN_CREATURE_ENERGY = "CREATURE_ENERGY=";
    public static final String TOKEN_SEROTONIN = "SEROTONIN=";
    public static final String TOKEN_ENDORPHINE = "ENDORPHINE=";
    public static final String TOKEN_ACTION = "ACTION";
    public static final String TOKEN_SCORE = "SCORE=";
    public static final String TOKEN_X1 = "X1=";
    public static final String TOKEN_X2 = "X2=";
    public static final String TOKEN_Y1 = "Y1=";
    public static final String TOKEN_Y2 = "Y2=";
    public static final String TOKEN_CENTER_OF_MASS_X = "COM_X=";
    public static final String TOKEN_CENTER_OF_MASS_Y = "COM_Y=";
    public static final String TOKEN_HAS_LEAFLET = "HASLEAFLET=";
    public static final String TOKEN_HAS_COLLIDED = "HASCOLLIDED=";
    public static final String TOKEN_CATEGORY = "CATEGORY=";
    public static final String TOKEN_OCCLUDED = "OCCLUDED=";
    public static final String TOKEN_THING_ENERGY = "THING_ENERGY=";
    public static final String TOKEN_HARDNESS = "HARDNESS=";
    public static final String TOKEN_SHININESS = "SHININESS=";
    public static final String TOKEN_CREATURE_X = "CREATURE_X=";//center of mass
    public static final String TOKEN_CREATURE_Y = "CREATURE_Y=";// ""
    public static final String TOKEN_CREATURE_X1 = "CREATURE_X1=";
    public static final String TOKEN_CREATURE_X2 = "CREATURE_X2=";
    public static final String TOKEN_CREATURE_Y1 = "CREATURE_Y1=";
    public static final String TOKEN_CREATURE_Y2 = "CREATURE_Y2=";
    public static final String TOKEN_BAG_TOTAL_FOOD = "BAG_TOTAL_FOOD=";
    public static final String TOKEN_BAG_TOTAL_CRYSTALS = "BAG_TOTAL_CRYSTALS=";
    public static final String TOKEN_BAG_TOTAL_PFOOD = "BAG_TOTAL_PFOOD=";
    public static final String TOKEN_BAG_TOTAL_NPFOOD = "BAG_TOTAL_NPFOOD=";
    public static final String TOKEN_BAG_CRYSTAL_RED = "TOKEN_BAG_CRYSTAL_RED=";
    public static final String TOKEN_BAG_CRYSTAL_GREEN = "TOKEN_BAG_CRYSTAL_GREEN=";
    public static final String TOKEN_BAG_CRYSTAL_BLUE = "TOKEN_BAG_CRYSTAL_BLUE=";
    public static final String TOKEN_BAG_CRYSTAL_YELLOW = "TOKEN_BAG_CRYSTAL_YELLOW=";
    public static final String TOKEN_BAG_CRYSTAL_MAGENTA = "TOKEN_BAG_CRYSTAL_MAGENTA=";
    public static final String TOKEN_BAG_CRYSTAL_WHITE = "TOKEN_BAG_CRYSTAL_WHITE=";
    public static final String TOKEN_THING_DATA = "THING_DATA=";
    public static final String TOKEN_SELF_DATA = "SELF_DATA=";

    
    public static final String ACTION_NAME_SEE = "Action_SEE";
    public static final String ACTION_NAME_HIDE = "Action_HIDE";
    public static final String ACTION_NAME_UNHIDE = "Action_UNHIDE";
    public static final String ACTION_NAME_PUTINTOBAG = "Action_PUT_IN_BAG";
    public static final String ACTION_NAME_EAT = "Action_EAT";
    public static final String ACTION_NAME_MOVE = "Action_MOVE";
    public static final String ACTION_NAME_CARRY = "Action_CARRY";
    public static final String ACTION_NAME_DROP = "Action_DROP";
    public static final String ACTION_NAME_DELIVER = "Action_DELIVER";
    
    
    //After a contact action is completed, the Thing location is updated:    
    public static final double AFTER_EAT_COORDS = -25;
    public static final double AFTER_PUT_IN_BAG_COORDS = -26;
    public static final double AFTER_DELIVERY_COORDS = -27;
    public static final double AFTER_HIDE_COORDS = -28;

    private static void initColorHashMap() {
        HashMap<String, ColorRGBA> map = new HashMap();
        for (String c : arrayOfColors) {
            colorMap.put(c, translateIntoColor(c));
        }
    }
    ////////Poisson distribution
    //the average rate of generation of each kind of crystal:
    public static final double redLAMBDA = 2.2;
    public static final double greenLAMBDA = 2;
    public static final double blueLAMBDA = 2.5;
    public static final double yellowLAMBDA = 1.7;
    public static final double magentaLAMBDA = 1.5;
    public static final double whiteLAMBDA = 1;

    public static final double pFoodLAMBDA = 2.2;
    public static final double npFoodLAMBDA = 1.1;

    /**
     * Knuth's algorithm to generate random Poisson distributed numbers
     *
     * @param lambda average rate of success in a Poisson distribution
     * @return random number
     */
    public static int getPoissonRandomNumber(double lambda) {
        int k = 1;
        double p = 1.0;
        Random rd = new Random();

        do {
            k += 1;
            p *= rd.nextDouble();
        } while (p > Math.exp((double) -lambda));
        return k - 1;
    }

    public static final boolean isInArrayOfColors(String color) {
        boolean ret = false;
        for (int i = 0; i < arrayOfColors.length; i++) {
            if (arrayOfColors[i].equals(color)) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Color name --> integer
     *
     * @param color
     * @return
     */
    public static final int getColorIndex(String color) {
        int ret = -1; //invalid
        for (int i = 0; i < arrayOfColors.length; i++) {
            if (arrayOfColors[i].equals(color)) {
                ret = i;
            }
        }
        return ret;
    }

    public static ColorRGBA translateIntoColor(String colorName) {

        if (colorName.equals(Constants.colorRED)) {
            return ColorRGBA.red;
        } else if (colorName.equals(Constants.colorGREEN)) {
            return ColorRGBA.green;
        } else if (colorName.equals(Constants.colorBLUE)) {
            return ColorRGBA.blue;
        } else if (colorName.equals(Constants.colorYELLOW)) {
            return ColorRGBA.yellow;
        } else if (colorName.equals(Constants.colorMAGENTA)) {
            return ColorRGBA.magenta;
        } else if (colorName.equals(Constants.colorWHITE)) {
            return ColorRGBA.white;
        } else if (colorName.equals(Constants.colorDARKGRAY_SPOILED)) {
            return ColorRGBA.darkGray;
        } else if (colorName.equals(Constants.colorORANGE)) {
            return ColorRGBA.orange;
        } else {
            return ColorRGBA.magenta; //default
        }
    }

    public static String getNameFromColor(ColorRGBA color) {
        initColorHashMap();
        String ret = "";
        for (String s : colorMap.keySet()) {
            if (colorMap.get(s).equals(color)) {
                ret = s;
            }
        }
        return ret;
    }

    /**
     * Integer color index --> color name (opposite of getColorIndex() )
     *
     * @param index
     * @return
     */
    public static String getColorItem(int index) {

        String ret = null;
        switch (index) {
            case 0:
                ret = colorRED;
                break;
            case 1:
                ret = colorGREEN;
                break;
            case 2:
                ret = colorBLUE;
                break;
            case 3:
                ret = colorYELLOW;
                break;
            case 4:
                ret = colorMAGENTA;
                break;
            case 5:
                ret = colorWHITE;
                break;
//            case 6: ret =  colorDARKGRAY; break; //only spoiled!!!!!
//            case 7: ret =  colorORANGE; break;
        }
        return ret;
    }

    public static int getColorPayment(int index) {

        int ret = 0;
        switch (index) {
            case 0:
                ret = 10;       //colorRED
                break;
            case 1:
                ret = 8;        //colorGREEN
                break;
            case 2:
                ret = 6;        //colorBLUE
                break;
            case 3:
                ret = 4;        //colorYELLOW
                break;
            case 4:
                ret = 2;        //colorMAGENTA
                break;
            case 5:
                ret = 1;        //colorWHITE
                break;
        }
        return ret;
    }

    public static Color translateIntoColorAWT(String colorName) {

        if (colorName.equals(Constants.colorRED)) {
            return Color.RED;
        } else if (colorName.equals(Constants.colorGREEN)) {
            return Color.GREEN;
        } else if (colorName.equals(Constants.colorBLUE)) {
            return Color.BLUE;
        } else if (colorName.equals(Constants.colorYELLOW)) {
            return Color.YELLOW;
        } else if (colorName.equals(Constants.colorMAGENTA)) {
            return Color.MAGENTA;
        } else if (colorName.equals(Constants.colorWHITE)) {
            return Color.WHITE;
        } else if (colorName.equals(Constants.colorDARKGRAY_SPOILED)) {
            return Color.DARK_GRAY;
        } else if (colorName.equals(Constants.colorORANGE)) {
            return Color.ORANGE;
        } else {
            return Color.MAGENTA; //default
        }
    }
}
