/** ***************************************************************************
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
 **************************************************************************** */
package model;

/**
 * @author Ricardo Ribeiro Gudwin
 * @author eccastro
 *
 */
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.renderer.ColorRGBA;
import java.util.ArrayList;
import java.util.List;
import worldserver3d.CreatureNodeFactory;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import com.jme.scene.Node;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import util.Constants;
import worldserver3d.IconFactory;
import worldserver3d.RobotCamera;
import xml.ReadFromXMLFile;
import xml.WriteToXMLFile;

public class Environment {

    private boolean gameStarted = false;
    // Creatures
    private List<Creature> cpool;
    // Obstacles
    private List<Thing> opool;
    //these lists are used to inform the jme scene renderer which Thing must be updated:
    private List<Thing> opoolModified;
    private List<Creature> cpoolModified;

    private List<Thing> opoolShapeModified;
    private List<Thing> cpoolShapeModified;

    public HashMap<String, Thing> thingMap;
    public List<Node> rmiPool; //rememberMeIcom pool of arrows
    public List<Node> wpPool;  // waypoint pool of arrows
    public List<Node> dsPool;  //delivery spot pool.
    // There is only one DS in current version.
    public HashMap<String, Node> wpNdsPoolMap;
    public List<Thing> deletelist;
    public List<Node> deleteArrowDSlist;
    //Materials
    public List<Material3D> colorpool;
    public int width;
    public int height;
    public LightState ls;
    public MaterialState defaultms;
    public MaterialState creatureMS;
    //for rememberMeIcon: "arrow" to indicate a hiden thing:
    public MaterialState flagMS;
    public MaterialState wpMS; //used for waypoints (along a path)
    public TextureState wpTS;
    public TextureState dsTS;
    public double[] deliverySpotLocation = {Constants.deliverySpotCoords[0], Constants.deliverySpotCoords[1]};
    public boolean dsIsShown = false;
    public CreatureNodeFactory cnf;
    private int[] camera = new int[Constants.NUMBER_CAMERAS]; //even and odd camera
    public int deleteallnodes = 0;
    public int auxx = 0;
    public int auxy = 0;
    public HashMap<Long, MaterialState> oMsPool;
    public HashMap<Long, TextureState> thingTsPool;
    public HashMap<String, ColorRGBA> colorPool;
    private TreeMap<Long, Leaflet> leafletPool;
    public CreaturePoolNotifier cpoolNotifier;
    public LeafletNotifier leafletNotifier;
    public static final String nonPerishableFood = "Non Perishable";
    public static final String perishableFood = "Perishable";
    public List<Creature> delClist;
    public List<Thing> delTlist;
    public List<Node> delRMIlist;
    final public Environment semaphore = this;
    final public Environment semaphore2 = this;
    final public Environment semaphore3 = this;
    public RobotCamera rcnEven;
    public RobotCamera rcnOdd;
    static Logger log = Logger.getLogger(Environment.class.getCanonicalName());

    public Environment(int nwidth, int nheight) {
        width = nwidth;
        height = nheight;
        cpool = new ArrayList<Creature>();
        cpoolNotifier = new CreaturePoolNotifier();
        leafletNotifier = new LeafletNotifier();
        opool = new ArrayList<Thing>();
        opoolModified = new ArrayList<Thing>();
        cpoolModified = new ArrayList<Creature>();
        opoolShapeModified = new ArrayList<Thing>();
        cpoolShapeModified = new ArrayList<Thing>();
        rmiPool = new ArrayList<Node>();
        wpPool = new ArrayList<Node>();
        dsPool = new ArrayList<Node>();
        wpNdsPoolMap = new HashMap();
        deletelist = new ArrayList<Thing>();
        deleteArrowDSlist = new ArrayList<Node>();
        thingMap = new HashMap();
        oMsPool = new HashMap();
        thingTsPool = new HashMap();
        colorPool = new HashMap();
        leafletPool = new TreeMap();
        colorPool.put(Constants.colorGREEN, ColorRGBA.green);
        colorPool.put(Constants.colorRED, ColorRGBA.red);
        colorPool.put(Constants.colorBLUE, ColorRGBA.blue);
        colorPool.put(Constants.colorYELLOW, ColorRGBA.yellow);
        colorPool.put(Constants.colorMAGENTA, ColorRGBA.magenta);
        colorPool.put(Constants.colorWHITE, ColorRGBA.white);
        colorPool.put(Constants.colorDARKGRAY_SPOILED, ColorRGBA.darkGray);
        colorPool.put(Constants.colorORANGE, ColorRGBA.orange);
        camera[0] = -1; //camera for even robots
        camera[1] = -1;//camera for odd robots
        delClist = new ArrayList<Creature>();
        delTlist = new ArrayList<Thing>();
        delRMIlist = new ArrayList<Node>();
    }

    public void updateDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public synchronized List<Thing> getOpool() {
        return opool;
    }

    public synchronized List<Creature> getCpool() {
        return cpool;

    }

    public synchronized List<Thing> getOpoolModified() {
        return this.opoolModified;
    }

    public synchronized void addToOpoolModified(Thing th) {
        this.opoolModified.add(th);
    }

    public synchronized void delFromOpoolModified(Thing th) {
        this.opoolModified.remove(th);
    }

    public synchronized List<Creature> getCpoolModified() {
        return this.cpoolModified;
    }

    public synchronized List<Thing> getOpoolShapeModified() {
        return this.opoolShapeModified;
    }

    public synchronized List<Thing> getCpoolShapeModified() {
        return this.cpoolShapeModified;
    }

    public synchronized void setOpoolShapeModified(Thing t) {
        this.opoolShapeModified.add(t);
    }

    public synchronized void setCpoolShapeModified(Thing t) {
        this.cpoolShapeModified.add(t);
    }

    public synchronized void startTheGame(boolean start) { //0 false; 1 true
        this.gameStarted = start;
        log.info("Game has started: " + this.gameStarted);
    }

    public synchronized boolean gameStarted() {
        return this.gameStarted;
    }

    public int getCamera(int index) {
        return camera[index];
    }

    public void resetCameras() {
        for (int i = 0; i < Constants.NUMBER_CAMERAS; i++) {
            camera[i] = -1;
        }
    }

    public synchronized void setCamera(int index, int creatureIndex) {
        int oldOwner = camera[index];
        camera[index] = creatureIndex;

        if (oldOwner != -1) {
            cpool.get(oldOwner).isVisualSensorActivated = false;
        }
        if (creatureIndex != -1) {
            cpool.get(creatureIndex).isVisualSensorActivated = true;
        }
    }

    public void updateCameras(int deadCreatureIndex) {
        if (deadCreatureIndex != -1) {
            if (deadCreatureIndex % 2 == 0) { //even must change
                if (camera[1] != -1) {
                    camera[0] = camera[1] - 1;
                } else {
                    camera[0] = -1;
                }

            }
            camera[1] = -1;

        }

    }

    public RobotCamera getRobotCamera(int index) {

        if (index % 2 == 0) {
            return rcnEven;
        } else {
            return rcnOdd;
        }
    }

    public Creature getCreature(int i) {
        return cpool.get(i);
    }

    public int getCreatureIndex(String nameID) {
        int i = -1; //none
        synchronized (semaphore3) {
            Thing c = thingMap.get(nameID);
            if (c != null) {
                i = cpool.indexOf(c);
            }
        }
        return i;
    }

    public int addThing(Thing th) {
        int ret = -1;

        if (th.category == Constants.categoryCREATURE) {
            synchronized (semaphore3) {
                cpool.add((Creature) th);
                cpoolModified.add((Creature) th);
                this.notifyNumberOfCreatureObservers();
                ret = cpool.indexOf(th);
            }
        }
        if (th.category != Constants.categoryCREATURE) {
            synchronized (semaphore2) {
                opool.add(th);
                //opoolModified.add(th);
                this.addToOpoolModified(th);
                ret = opool.indexOf(th);
            }
        }
        return ret;
    }

    /**
     * Remove a Thing (do not apply to Creatures). For Creatures, use
     * removeCreature.
     *
     * @param o a Thing except Creature.
     */
    public synchronized void removeThing(Thing o) {
        synchronized (semaphore2) {
            oMsPool.remove(o.ID);
            thingTsPool.remove(o.ID);
            if (thingTsPool.containsKey(o.ID)) {
                thingTsPool.remove(o.ID);
            }
            opool.remove(o);
            thingMap.remove(o.getMyName());
            deletelist.add(o);
        }
    }

    /**
     * *******************************************************************************
     * ATTENTION:::: Currently, the game dos not support deletion of creatures
     * during the simulation. Many methods use the index of the Creature in the
     * cpool and therefore it is not supposed to change once the game is
     * started. The purpose of this method is to help the setup of the
     * environment before the start of the simulation!
     * ******************************************************************************
     */
    public void removeCreature(Creature c) {
        synchronized (semaphore3) {
            int dead_index = getCpool().indexOf(c);
            thingTsPool.remove(c.ID);
            cpool.remove(c);
            thingMap.remove(c.getMyName());
            deletelist.add(c);
            updateCameras(dead_index);
            this.notifyNumberOfCreatureObservers();
        }
    }

    /**
     * This method remove "zero" dimension bricks:
     */
    public void cleanUp() {

        for (Iterator iterator = opool.iterator(); iterator.hasNext();) {
            Thing thing = (Thing) iterator.next();
            if (thing.category == Constants.categoryBRICK) {
                if ((thing.getX1() == thing.getX2()) && (thing.getY1() == thing.getY2())) {
                    iterator.remove();
                }
            }
        }

    }

    public void open(String fileName) {

        ReadFromXMLFile reader = new ReadFromXMLFile(fileName);
        reader.readFromFile(this);
        this.notifyNumberOfCreatureObservers();
    }

    public void save(String fileName) {

        WriteToXMLFile writer = new WriteToXMLFile(fileName);
        writer.writeToFile(width, height, opool, cpool);
    }

    public boolean colideWithObstacle(double x, double y, double dist) {
        for (Thing o : opool) {
            if (x >= o.getX1() - dist
                    && x <= o.getX2() + dist
                    && y >= o.getY1() - dist
                    && y <= o.getY2() + dist) {
                return true;
            }
        }
        return false;
    }

    public String[] returnArrayOfColors() {
        Set colorSet = colorPool.keySet();
        String[] arrayOfColors = (String[]) colorSet.toArray();
        return arrayOfColors;
    }

    public void addWaypointIcon(IconFactory wp) {
        Node n = wp.getWaypointIcon();
        n.setIsCollidable(false);
        n.setRenderState(wp.ms);
        n.setRenderState(ls);
        String str = wp.myModelName;
        wpNdsPoolMap.put(str, n);
        wpPool.add(n);
    }

    public void addDSIcon(IconFactory wp) {
        Node n = wp.getDeliverySpot();
        n.setIsCollidable(false);
        n.setRenderState(wp.ms);
        n.setRenderState(ls);
        String str = wp.myModelName;
        wpNdsPoolMap.put(str, n);
        dsPool.add(n);
        dsIsShown = true;
    }

    public String removeDSIcon(double x, double y) { //synchronized
        String ret = "";
        String toBeDel = "ModelDeliverySpotIcon_" + x + "_" + y;
        if (wpNdsPoolMap.containsKey(toBeDel)) {
            Node n = (Node) wpNdsPoolMap.get(toBeDel);
            deleteArrowDSlist.add(n);
            wpNdsPoolMap.remove(toBeDel);
            dsPool.remove(n);
            dsIsShown = false;
            ret = "Deleted delivery spot at " + x + "_" + y;
        } else {
            ret = "Delivery spot at " + x + "_" + y + " does not exist!!!";
        }
        return ret;

    }

    public String removeWaypointIcon(double x, double y) {
        String ret = "";
        String toBeDel = "ModelWaypointIcon_" + x + "_" + y;
        if (wpNdsPoolMap.containsKey(toBeDel)) {
            Node n = (Node) wpNdsPoolMap.get(toBeDel);
            deleteArrowDSlist.add(n);
            wpNdsPoolMap.remove(toBeDel);
            wpPool.remove(n);
            //System.out.println("======= Deleted wp named " + toBeDel);
            ret = "Deleted waypoint at " + x + "_" + y;
        } else {
            log.info("======= Waypoint named " + toBeDel + " does not exist!!!");
            ret = "Waypoint at " + x + "_" + y + " does not exist!!!";
        }
        return ret;

    }

    public TreeMap getLeafletPool() {

        return this.leafletPool;
    }

    public void addLeaflet(Leaflet leaflet) {

        this.leafletPool.put(leaflet.getID(), leaflet);

    }

    public void resetLeafletPool() {
        this.leafletPool.clear();
    }

    public List<Leaflet> getLeafletsOfOwner(Long ownerID) {
        List<Leaflet> list = new ArrayList<Leaflet>();
        //ATTENTION: use getLeafletPool() to get the list ordered from oldest to newest leaflet
        for (Iterator<Leaflet> iter = getLeafletPool().values().iterator(); iter.hasNext();) {
            Leaflet leaflet = iter.next();
            // -1 means owned by all creatures
            if ((leaflet.getOwner().compareTo(new Long(-1)) == 0) || (leaflet.getOwner().compareTo(ownerID) == 0)) {
                list.add(leaflet);
            }
            //return only the 3 newer leaflets. Older ones are ignored.
            TreeSet tree = new TreeSet();
            for (Leaflet l : list) {
                tree.add(l.getID());
            }
            list.clear();
            for (Iterator<Long> iterDesc = tree.descendingIterator(); iterDesc.hasNext();) {
                Long key = iterDesc.next();
                list.add((Leaflet) getLeafletPool().get(key));
                if (list.size() == Constants.MAX_NUMBER_OF_LEAFLETS) {
                    break;
                }
            }

        }
        return list;
    }

    public void notifyLeafletObservers() {
        this.leafletNotifier.changed();
    }

    public void notifyNumberOfCreatureObservers() {
        this.cpoolNotifier.changed();

    }

    public class LeafletNotifier extends Observable {

        public void addAnObserver(Observer ob) {
            this.addObserver(ob);
        }

        public void changed() {
            setChanged();
            notifyObservers(new Long(1));
        }
    }

    public class CreaturePoolNotifier extends Observable {

        public void addAnObserver(Observer ob) {
            this.addObserver(ob);
        }

        public void changed() {
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Return the list of all THINGs except the creature itself.
     *
     * @param me the creature put in perspective
     * @return list of anything else in the environment except the creature
     */
    public List<Thing> getEveryThingExceptMe(Thing me) {

        List<Thing> thingPool = new ArrayList<Thing>();
        synchronized (semaphore3) {
            for (Thing c : cpool) {
                if (c.getID() != me.ID) {
                    thingPool.add(c);
                }
            }
        }
        synchronized (semaphore2) {
            for (Thing o : opool) {
                if (o.getID() != me.ID) {
                    thingPool.add(o);
                }
            }
        }
        return thingPool;
    }

    public List<Creature> getCreaturesExceptMe(Creature cr) {
        List<Creature> temp = new ArrayList<Creature>();
        synchronized (semaphore3) {
            for (Creature c : cpool) {
                if (!c.equals(cr)) {
                    temp.add(c);
                }
            }
        }
        return temp;
    }

    public Thing getThingFromName(String name) {

        Thing ret = null;

        for (Thing o : opool) {
            try {
                if (o != null) {
                    if (o.getMyName() != null) {
                        if (o.getMyName().equals(name)) {
                            log.info("Gotcha: " + o.getMyName());
                            ret = o;
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return ret;
    }

    public synchronized void deleteAllThing() {

        List<Thing> thList = new ArrayList<Thing>();
        List<Creature> cList = new ArrayList<Creature>();

        for (Thing th : this.opool) {
            thList.add(th);
        }
        for (Iterator<Thing> it = thList.iterator(); it.hasNext();) {
            Thing th = it.next();
            this.removeThing(th);
        }
        synchronized (semaphore3) {
            for (Creature c : this.cpool) {
                cList.add(c);
            }
        }
        for (Iterator<Creature> it = cList.iterator(); it.hasNext();) {
            Creature c = it.next();
            this.removeCreature(c);
        }

    }
}
