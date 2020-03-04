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
package model;

import com.jme.scene.state.MaterialState;
import java.util.*;
import util.Constants;

/**
 * Generic class container of a Thing.
 *
 * @author ecalhau
 */
public abstract class Container extends Thing {

    /**
     * The container is a list of drawers. Each drawer contain a list of Thing
     * of a certain category.
     */
    //Each drawer is labeled a certaing entity category and its contents
    // are all of this same category:  <Label, Drawer>
    protected HashMap<Integer, Container.Drawer> elements = new HashMap<Integer, Container.Drawer>();
//    /**
//     * State of container. 
//     * 
//     */
//    
//    protected int state = Constants.EMPTY_OPENED;
    protected boolean isOpened = true;

    /**
     * Container may have a graphical representation in the world or not.
     *
     * Constructors for non-graphical ones:
     */
    public Container(int drawerLabel) {
        affordances = new ArrayList<Integer>();
        affordances.add(Constants.Affordance__INSERTABLE);
        affordances.add(Constants.Affordance__REMOVEFROMABLE);
    }

    public Container() {
        this(Constants.categoryPFOOD);
    }

    /**
     * Constructors for graphical ones:
     */
    public Container(double x, double y, Environment ev, MaterialState ms, int drawerLabel) {
        super(x, y, ms, ev);
        affordances = new ArrayList<Integer>();
        affordances.add(Constants.Affordance__INSERTABLE);
        affordances.add(Constants.Affordance__REMOVEFROMABLE);
    }

    public Container(double x, double y, Environment ev, MaterialState ms) {
        this(x, y, ev, ms, Constants.categoryPFOOD);
    }

    public synchronized void createDrawer(int drawerLabel) {
        Container.Drawer drawer = new Container.Drawer(drawerLabel);
        elements.put(new Integer(drawerLabel), drawer);
        sendAddDelEvent(drawerLabel, true);
    }

    public synchronized void removeDrawer(int drawerLabel) {
        elements.remove(new Integer(drawerLabel));
        sendAddDelEvent(drawerLabel, false);
    }

    public synchronized void putIn(Thing o) {
        Integer cI = new Integer(o.category);
        if (!elements.containsKey(cI)) {
            createDrawer(cI);
        }
        elements.get(cI).putIn(o);
        notifyMyObservers();
    }

    public synchronized Thing remove(Thing o) {
        Integer categoryInt = new Integer(o.category);
        Container.Drawer d = elements.get(categoryInt);
        Thing th = d.remove(o);
        notifyMyObservers();
        if (d.isEmpty(false)) { //not test mode
            removeDrawer(categoryInt);
        }
        return th;
    }

    /**
     * For testing purposes only!!!!!!!!!!!!!!!!!!!!!!!!! Do not need to store
     * the class instance
     *
     * These methods are used in ContainerViewer when Edit Mode is enabled
     */
    public synchronized void inc(int category, int colorIndex) {
        if (!elements.containsKey(category)) {
            createDrawer(category);
        }
        elements.get(category).inc(category, colorIndex);
        notifyMyObservers();
    }

    public synchronized void dec(int category, int colorIndex) {
        Integer categoryInteger = new Integer(category);
        Container.Drawer d = elements.get(categoryInteger);
        if (d != null) {
            d.dec(category, colorIndex);
            if (d.isEmpty(true)) { //test mode
                removeDrawer(categoryInteger);
            }
            notifyMyObservers();
        }

    }
    /*
     * ------------------------------------------------------------------
     */

    /**
     * Generate the event for the FSM (Finite State Machine of the Container.
     * This method generates an "add" or "delete" event of a specific category.
     * The event is sent to the proper implementation of FSM.
     *
     * @param category Thing category
     * @param isAdd true if add event and false if delete
     */
    protected abstract void sendAddDelEvent(int category, boolean isAdd);

    /**
     * Generate the event for the FSM (Finite State Machine of the Container.
     * This method generates an "open" or "close" event. The event is sent to
     * the proper implementation of FSM.
     *
     * @param isOpen
     *
     */
    protected abstract void sendCloseOpenEvent(boolean isOpen);

    public synchronized void setOpenState(boolean opened) {
        isOpened = opened;
        sendCloseOpenEvent(opened);
    }

    public synchronized boolean getIfOpened() {
        return isOpened;
    }

    public synchronized int getNumberOfCategories() {

        return elements.size();

    }

    public synchronized List<Thing> getElementsOfCategory(int category) {
        //returns null if category does not exist

        return elements.get(new Integer(category)).getContents();
    }

    /**
     *
     * @param category
     * @param colorIndex not necessary for types of Thing like Food
     * @return
     */
    public synchronized int getNumberOfElementsOfCategory(int category, int colorIndex) {
        int n = 0;
        Integer i = new Integer(category);
        if (elements.containsKey(i)) {
            n = elements.get(i).getNumber(colorIndex);
        }
        return n;
    }

    /**
     *
     * @param ifTestMode true if only testing (through Edit panel; false if
     * regular execution
     * @return
     */
    public synchronized boolean isEmpty(boolean ifTestMode) {
        for (Iterator<Integer> iter = elements.keySet().iterator(); iter.hasNext();) {
            Integer cat = iter.next(); //for each category
            if (!elements.get(cat).isEmpty(ifTestMode)) {
                return false;
            }
        }
        return true;
    }

    public void notifyMyObservers() {
        setChanged();
        notifyObservers(new Long(0));
    }

    private class Drawer {

        private int category; //is the "label" of the drawer
        //The content of each drawer is a list of Thing of certain category
        private List<Thing> contents = Collections.synchronizedList(new ArrayList());
        /**
         * Integer key: category of Thing Object: Inner HashMap (color of item],
         * number of items) color is not necessary for types like Food Example:
         * [categoryJewel, (0, 11])] --> 11 red jewels Color indexes are defined
         * in Constants::arrayOfColors
         */
        private HashMap<Integer, HashMap<Integer, Integer>> numberOfTypeMap = new HashMap<Integer, HashMap<Integer, Integer>>();

        public Drawer(int label) {
            category = label;
        }

        public synchronized int getLabel() {
            return category;
        }

        public synchronized void putIn(Thing o) {
            contents.add(o);
            if (!numberOfTypeMap.containsKey(o.category)) {
                HashMap<Integer, Integer> attrMap = new HashMap<Integer, Integer>();
                attrMap.put(o.getMaterial().getColorTypeIndex(), 0);
                numberOfTypeMap.put(o.category, attrMap);
            }
            int numItems = numberOfTypeMap.get(o.category).get(o.getMaterial().getColorTypeIndex());
            numberOfTypeMap.get(o.category).put(o.getMaterial().getColorTypeIndex(), numItems + 1);

        }

        public synchronized Thing remove(Thing o) {
            contents.remove(o);

            int numItems = numberOfTypeMap.get(o.category).get(o.getMaterial().getColorTypeIndex());
            if (numItems > 0) {
                numberOfTypeMap.get(o.category).put(o.getMaterial().getColorTypeIndex(), numItems - 1);
            }

            return o;
        }

        /**
         * For testing purposes only!!!!!!!!!!!!!!!!!!
         *
         * @param category
         * @param jewelType
         */
        public synchronized void inc(int category, int colorIndex) {

            if (!numberOfTypeMap.containsKey(category)) {
                HashMap<Integer, Integer> attrMap = new HashMap<Integer, Integer>();
                attrMap.put(colorIndex, 1);
                numberOfTypeMap.put(category, attrMap);
            } else { //category already exists
                // but a new type (color) must be inserted:
                if (!numberOfTypeMap.get(category).containsKey(colorIndex)) {
                    numberOfTypeMap.get(category).put(colorIndex, 1);
                } else {
                    int numItems = numberOfTypeMap.get(category).get(colorIndex);
                    numberOfTypeMap.get(category).put(colorIndex, numItems + 1);
                }

            }

        }

        public synchronized void dec(int category, int colorIndex) {

            int numItems = numberOfTypeMap.get(category).get(colorIndex);
            if (numItems > 0) {
                numberOfTypeMap.get(category).put(colorIndex, numItems - 1);
            }

        }
        /*
         * ------------------------------------------------------------------
         */

        public synchronized List<Thing> getContents() {
            return contents;
        }

        /**
         * @param colorIndex not necessary for Food
         * @return
         */
        public synchronized int getNumber(Integer colorIndex) {
            int r = 0;
            if (numberOfTypeMap.containsKey(category)) {
                if (numberOfTypeMap.get(category).containsKey(colorIndex)) {
                    r = numberOfTypeMap.get(category).get(colorIndex);
                }

            }
            return r;
        }

        public synchronized boolean isEmpty(boolean ifTestMode) {
            boolean ret = true;
            if (ifTestMode) {
                HashMap hm = numberOfTypeMap.get(category);//inner HashMap [colorIndex, number of Items]
                for (Iterator<Integer> iter = hm.keySet().iterator(); iter.hasNext();) { //traverse though colors
                    Integer colorIndex = iter.next(); //color index
                    Integer numItems = (Integer) hm.get(colorIndex);
                    if (numItems > 0) { //there is at least 1 item of certain color
                        ret = false;
                        break;
                    }

                }

            } else {
                ret = contents.isEmpty();
            }
            return ret;
        }
    }
}
