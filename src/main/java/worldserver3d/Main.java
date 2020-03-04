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

import worldserver3d.view.WorldFrame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Creature;
import model.Thing;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import model.*;
import util.Constants;
import util.NativeUtils;

/**
 * Main class of the server (WorldServer 3D - WS3D). This class defines the
 * protocol (list of commands) upon which server and clients must agree in order
 * to establish communication. A server socket is also implemented. A network
 * socket is opened for each client that requests communication with the server.
 *
 * @author rgudwin
 * @author ecalhau
 */
public class Main {

    String version = "1.0";
    public WorldFrame i;
    public SimulationFrame sf;
    StringBuffer outToClient;
    List<ServerThread> clientsConnected;
    Logger log;

    public Main() {
        log = Logger.getLogger(Main.class.getCanonicalName());
        Logger.getLogger("com.jme").setLevel(Level.OFF);
        Logger.getLogger("com.jmex").setLevel(Level.OFF);
        Logger.getLogger("worldserver3d").setLevel(Level.WARNING);
        Logger.getLogger("util").setLevel(Level.WARNING);
        Logger.getLogger("model").setLevel(Level.WARNING);
        Logger.getLogger("motorcontrol").setLevel(Level.WARNING);
        NativeUtils.setLibraryPath(".");
        NativeUtils.prepareNativeLibs();
        //Logger.getLogger("com.jme").setLevel(Level.ALL);
        i = new WorldFrame();
        sf = new SimulationFrame(this);
        clientsConnected = new ArrayList<ServerThread>();
        try {
            ServerSocket ss = new ServerSocket(Constants.PORT);
            while (true) {
                clientsConnected.add(new ServerThread(ss.accept()));
            }
        } catch (Exception e) {
            log.severe("Error while trying to connect! " + e.toString());
            //System.out.println("Error while trying to connect! " + e.toString());
        }
    }

    private class ServerThread extends Thread {

        private final Socket socket;
        private String name;

        public ServerThread(Socket socket) {
            this.socket = socket;
            start();
        }

        public void run() {
            int cond = 0;
            String s;
            PrintWriter out;
            BufferedReader in;
            String inputLine, outputLine;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out.println(processMessage("version"));
                while (cond == 0) {
                    inputLine = in.readLine();
                    outputLine = processMessage(inputLine);
                    out.println(outputLine);
                    if (inputLine.equals("shutdown")) {
                        System.exit(0);
                    }
                    if (inputLine.equals("quit")) {
                        cond = 1;
                    }
                }
                socket.close();
            } catch (Exception t) {
                String reason = t.getMessage();
                if (reason != null)
                  if (reason.equalsIgnoreCase("Connection reset") == false)
                    log.severe("Listening to the client... Exception caught: " + reason);
                //System.out.println("Listening to the client... Exception caught: " + t);
            }

        }
    }

    public synchronized StringBuffer getOutBuffer() {
        return this.outToClient;
    }

    /**
     * Definition of the "protocol". The protocol is a list of commands that the
     * server understands and is ready to process. Each command may also has a
     * list of parameters.
     *
     * @param input command
     * @return message if the command was successfully processed or not.
     */
    public synchronized String processMessage(String input) {
        //System.out.println("processMessage()::input: " + input);
        outToClient = new StringBuffer("");
        if (input.equalsIgnoreCase("")) {
            return (Constants.ERROR_CODE + " Null message received ...");
        }
        StringTokenizer st = new StringTokenizer(input);
        if (!st.hasMoreTokens()) {
            return (Constants.ERROR_CODE + " ... No recognized command");
        }
        String command = st.nextToken();
        if (command.equalsIgnoreCase("game")) {
            ProcessRequestIfGameRunning();
        } else if (command.equalsIgnoreCase("startGame")) {
            ProcessStartGame();
        } else if (command.equalsIgnoreCase("VERSION")) {
            ProcessVersion();
        } else if (command.equalsIgnoreCase("quit")) {
            return ProcessQuit();
        } else if (command.equalsIgnoreCase("shutdown")) {
            ProcessShutdown();
        } else if (command.equalsIgnoreCase("getclock")) {
            ProcessGetClock();
        } else if (command.equalsIgnoreCase("memory")) {
            ProcessGetMemory();
        } else if (command.equalsIgnoreCase(("setenv"))) {
            ProcessInitialEnvironmentParameters(st);
        } else if (command.equalsIgnoreCase(("getenvironment"))) {
            ProcessGetEnvironment();
        } else if (command.equalsIgnoreCase(("getcreatcoords"))) {
            ProcessGetCreatureCoords(st);
        } else if (command.equalsIgnoreCase(("getcreatinfo"))) {
            ProcessGetCreatureInfo(st);
        } else if (command.equalsIgnoreCase(("getvs"))) {
            ProcessGetVisualSensor(st);
        } else if (command.equalsIgnoreCase(("getcreaturestate"))) {
            ProcessGetFullStatus3D(st);
        } else if (command.equalsIgnoreCase(("getsack"))) {
            ProcessGetSackContent(st);
        } else if (command.equalsIgnoreCase(("camera"))) {
            ProcessActivateCamera(st);
        } else if (command.equalsIgnoreCase(("affordances"))) {
            ProcessGetAffordances(st);
        } else if (command.equalsIgnoreCase(("getsimulpars"))) {
            ProcessGetSimulPars();
        } else if (command.equalsIgnoreCase(("worldReset"))) {
            ProcessWorldReset();
        } else if (command.equalsIgnoreCase(("setgoTo"))) {
            ProcessSetDifferentialStatusCML(st);
        } else if (command.equalsIgnoreCase(("setAngle"))) {
            ProcessSetDifferentialStatus3D(st);
        } else if (command.equalsIgnoreCase(("sackit"))) {
            ProcessSackIt(st);
        } else if (command.equalsIgnoreCase(("eatit"))) {
            ProcessEatIt(st);
        } else if (command.equalsIgnoreCase(("hideit"))) {
            ProcessHideIt(st);
        } else if (command.equalsIgnoreCase(("unhideit"))) {
            ProcessUnhideIt(st);
        } else if (command.equalsIgnoreCase(("setTurn"))) {
            ProcessSetDifferentialStatus3DT(st);
        } else if (command.equalsIgnoreCase(("leaflet"))) {
            ProcessSetLeaflet(st);
        } else if (command.equalsIgnoreCase(("deliver"))) {
            ProcessDeliverLeaflet(st);
        } else if (command.equalsIgnoreCase("newDeliverySpot")){
            ProcessNewDeliverySpot(st);
        }
        else if (command.equalsIgnoreCase("help")) {
            ProcessHelp();
        } else if (command.equalsIgnoreCase("drop")) {
            ProcessDrop(st);
        } else if (command.equalsIgnoreCase("new")) {
            ProcessNewCreature(st);
        } else if (command.equalsIgnoreCase("start")) {
            ProcessStartCreature(st);
        } else if (command.equalsIgnoreCase("stop")) {
            ProcessStopCreature(st);
        } else if (command.equalsIgnoreCase("refuel")) {
            ProcessRefuel(st);
        } else if (command.equalsIgnoreCase("mindName")) {
            ProcessSetThreadName(st);
        } else if (command.equalsIgnoreCase("getNumEntities")) {
            ProcessGetWorldNumEntities();
        } else if (command.equalsIgnoreCase("check")) {
            ProcessCheckCreature(st);
        } else if (command.equalsIgnoreCase("checkXY")) {
            ProcessCheckXYCreature(st);
        } else if (command.equalsIgnoreCase("getall")) {
            ProcessGetAllThings();
        } else if (command.equalsIgnoreCase("newwp")) {
            ProcessNewWaypoint(st);
        } else if (command.equalsIgnoreCase("delwp")) {
            ProcessDeleteWaypoint(st);
        } else if (command.equalsIgnoreCase("closest")) {
            ProcessClosest(st);
        } else if (command.equalsIgnoreCase("food")) {
            ProcessNewFood(st);
        } else if (command.equalsIgnoreCase("jewel")) {
            ProcessNewJewel(st);
        } else if (command.equalsIgnoreCase("brick")) {
            ProcessNewBrick(st);
        } else if (command.equalsIgnoreCase("deleteth")) {
            ProcessDeleteThing(st);
        } else if (command.equalsIgnoreCase("batchoffood")) {
            ProcessNewFoodInBatch(st);
        } else if (command.equalsIgnoreCase("batchofjewels")) {
            ProcessNewJewelsInBatch(st);
        } else if (command.equalsIgnoreCase("cage")) {
            ProcessNewCage(st);
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... Cannot understand " + command);
        }
        return getOutBuffer().toString();
    }

    /*
     * Command: "game". Game status requisition (if it has started or not).
     * @return "yes" or "not"
     */
    void ProcessRequestIfGameRunning() {
        if (i.ep.e.gameStarted()) {
            getOutBuffer().append("yes");
        } else {
            getOutBuffer().append("no");
        }
    }

    /*
     * Command: "version" Information about the server version. @return the
     * version
     */
    void ProcessVersion() {
        getOutBuffer().append("WorldServer3D Version " + version + ". Welcome to the game!!!\r\n");
    }
    /*
     * Command: "startGame" The game is started.
     */

    void ProcessStartGame() {
        i.ep.e.startTheGame(true);
        getOutBuffer().append("Game started!!!\r\n");
    }

    /**
     * Command: "quit" Quit the client connection established with the server.
     *
     * @return
     */
    String ProcessQuit() {
        getOutBuffer().append("Quiting the system");
        return ("quit");

    }
    /*
     * Command: "shutdown" The server is shutdown.
     */

    void ProcessShutdown() {
        getOutBuffer().append("WorldServer is shuting down ...");
        System.exit(0);
    }

    /*
     * Command: "getclock" @return current time in milliseconds.
     */
    void ProcessGetClock() {
        getOutBuffer().append(new Time(System.currentTimeMillis()).toString() + "\r\n");
    }

    /**
     * Command: "memory"
     *
     * @return the amount of free memory in the Java Virtual Machine.
     */
    void ProcessGetMemory() {
        getOutBuffer().append(Runtime.getRuntime().freeMemory() + "\r\n");
    }

    /**
     * Command: "getenvironment"
     *
     * @return information about the game environment in format: "width"
     * "height"
     */
    void ProcessGetEnvironment() {
        getOutBuffer().append("" + i.ep.getPreferredSize().width + " " + i.ep.getPreferredSize().height + "\r\n");
    }

    /**
     * Old "start" command. Not in use in this version.
     */
    void ProcessStartSimulation() {
        i.ep.startMoving();
        getOutBuffer().append("Starting the simulation ...\r\n");
    }

    /**
     * Old "stop" command. Not in use in this version.
     */
    void ProcessStopSimulation() {
        i.ep.stopMoving();
        getOutBuffer().append("Stopping the simulation ...\r\n");

    }

    /**
     * ****************************************************************************
     * NOTE: The following list of commands has a first parameter called
     * "creature index". It refers to the ordinal position in the list of
     * creatures in the game. It changes whenever an older creature (created
     * before and hence with a lower index) is deleted, causing a shift of
     * indexes. Example: creature 0 is deleted. The creature with index 1 now
     * assumes index 0. It must not be confused with the ID attribute of each
     * Thing in the environment which is unique and never changes during the
     * game.
     * ****************************************************************************
     */
    /**
     * Command: "getcreatcoords"
     *
     * @param st the creature index within the environment (NOT the actual ID).
     * Please see comment "NOTE" above.
     */
    void ProcessGetCreatureCoords(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.\r\n");
            return;
        }
        Creature c = i.ep.e.getCpool().get(creatID);
        getOutBuffer().append("" + c.getX() + " " + c.getY() + " " + c.getPitch() + "\r\n");
    }
    
    
    void ProcessNewDeliverySpot(StringTokenizer st){
        String s;
        int type;
        double x, y;

        if (st.hasMoreTokens()) {
            s = st.nextToken();
            type = Integer.parseInt(s);
            if (type != 4) {
                getOutBuffer().append(Constants.ERROR_CODE + " Invalid type of delivery spot! Try: 4");
                return;
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Specify the type of thing: 4-Delivery Spot");
            return;
        }
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);

                ThingCreator tc = new ThingCreator(i.ep.e);
                DeliverySpot delivery = (DeliverySpot) tc.createThing(type, x, y);
                this.sf.gameState.ThingsRN.updateRenderState();
                getOutBuffer().append(delivery.getMyName() + " " + delivery.getX()
                        + " " + delivery.getY() + "\r\n");

            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
        }
        
    }

    /**
     * Command: "getcreatinfo" Returns the speed and fuel values
     *
     * @param st the creature index within the environment (NOT the actual ID).
     * Please see comment "NOTE" above.
     */
    void ProcessGetCreatureInfo(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        Creature c = i.ep.e.getCpool().get(creatID);

        getOutBuffer().append("" + c.getSpeed() + " " + c.getFuel() + "\r\n");

    }

        /**
     * Command: "getsack" Returns the content (food and crystals) in the 
     * creature's bag.
     *
     * @param st the creature index within the environment (NOT the actual ID).
     * Please see comment "NOTE" above.
     */
    void ProcessGetSackContent(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        Creature c = i.ep.e.getCpool().get(creatID);

        getOutBuffer().append(c.getSackContent());

    }
    
    /**
     * Command: "closest"
     *
     * @param st the creature index within the environment (NOT the actual ID).
     * Please see comment "NOTE" above.
     */
    void ProcessClosest(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        Creature c = i.ep.e.getCpool().get(creatID);
        Thing o = c.getClosest();
        if (o != null) {
            log.info("The closest thing name is: " + o.getMyName());

            StringBuffer vs = new StringBuffer("");

            vs.append(" ");
            vs.append(o.getMyName());
            vs.append(" ");
            vs.append(o.category); //flag to indicate the type of Thing
            vs.append(" ");
            vs.append(o.isOccluded); //indicates if occluded by another Thing
            vs.append(" ");
            vs.append(o.getX1());
            vs.append(" ");
            vs.append(o.getX2());
            vs.append(" ");
            vs.append(o.getY1());
            vs.append(" ");
            vs.append(o.getY2());
            vs.append(" ");
            vs.append(o.getPitch());
            vs.append(" ");
            vs.append(o.getMaterial().getHardness());
            vs.append(" ");
            vs.append(o.getMaterial().getEnergy());
            vs.append(" ");
            vs.append(o.getMaterial().getShininess());
            vs.append(" ");
            vs.append(o.getMaterial().getColorName());

            getOutBuffer().append(vs + "\r\n");
        } else {
            log.info("No closest Thing detected.");
            getOutBuffer().append(Constants.ERROR_CODE + " No closest Thing detected.\r\n");
        }
    }

    /**
     * Command: "getvs". Lists the content of the creature visual system
     *
     * @param st the creature index within the environment (NOT the actual ID).
     * Please see comment "NOTE" above.
     * @return content of the creature's camera. Format: "numberOfItems ||
     * Thing1 || Thing2 ... Thing?: ThingName category ifIsOccluded X1 X2 Y1 Y2
     * Pitch hardness energy shininess color || ... Example: 2 ||
     * Brick_1364939720793 1 0 541.0 583.0 282.0 363.0 -1.0 1.0 0.0 1.0 Magenta
     * || NPFood_1364939723933 22 0 553.0 553.0 442.0 442.0 -1.0 1.0 2.0 1.0
     * Green
     */
    void ProcessGetVisualSensor(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        Creature c = i.ep.e.getCpool().get(creatID);
        synchronized (i.ep.e.getCpool().get(creatID).semaphore) {
            c.updateVisualSensor(i.ep.e);
            StringBuffer vs = new StringBuffer("");
            vs = getVisualContent(c);
            getOutBuffer().append(vs);
        }
        getOutBuffer().append("\r\n");
    }

    private StringBuffer getVisualContent(Creature c) {
        StringBuffer vs = new StringBuffer("");
        vs.append(" ");
        vs.append(c.getThingsInCamera().size()); //number of things in camera

        ListIterator<Thing> iter = c.getThingsInCamera().listIterator();

        Thing o;
        while (iter.hasNext()) {
            o = (Thing) iter.next();
            vs.append(" || ");
            vs.append(o.getMyName());
            vs.append(" ");
            vs.append(o.category); //flag to indicate the type of Thing
            vs.append(" ");
            vs.append(o.isOccluded); //indicates if occluded by another Thing
            vs.append(" ");
            vs.append(o.getX1());
            vs.append(" ");
            vs.append(o.getX2());
            vs.append(" ");
            vs.append(o.getY1());
            vs.append(" ");
            vs.append(o.getY2());
            vs.append(" ");
            vs.append(o.getPitch());
            vs.append(" ");
            vs.append(o.getMaterial().getHardness());
            vs.append(" ");
            vs.append(o.getMaterial().getEnergy());
            vs.append(" ");
            vs.append(o.getMaterial().getShininess());
            vs.append(" ");
            vs.append(o.getMaterial().getColorName());
            vs.append(" ");
            vs.append(o.getX()); //center of mass
            vs.append(" ");
            vs.append(o.getY());//center of mass
        }

        return vs;

    }

    /**
     * Creature's visual sensor is started.
     *
     * @param st creature ID
     */
    synchronized void ProcessActivateCamera(StringTokenizer st) {
        try {
            int creatID = -1;
            String cID;
            if (st.hasMoreTokens()) {
                cID = st.nextToken();
                creatID = Integer.parseInt(cID);
            }
            if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
                getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
                return;
            }
            Creature c = i.ep.e.getCpool().get(creatID);
            int c_index = i.ep.e.getCpool().indexOf(c);
            if (c_index % 2 == 0) {
                i.ep.e.setCamera(0, c_index);
                getOutBuffer().append(i.ep.e.getCamera(0) + "\r\n");
            } else {
                i.ep.e.setCamera(1, c_index);
                getOutBuffer().append(i.ep.e.getCamera(1) + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the affordances of a Thing.
     *
     * @param st refers to a ThingID. Example: Brick_1364939987440
     *
     * The server respond with the list of affordances code. Codes:
     * Affordance__VIEWABLE = 30; Affordance__HIDEABLE = 31;
     * Affordance__UNHIDEABLE = 32; Affordance__GRASPABLE = 33;
     * Affordance__EATABLE = 34; Affordance__PUTINBAGABLE = 35;//sth that can be
     * put in a bag; Affordance__OPENABLE = 36; //sth that can be opened (eg. a
     * cage); Affordance__CLOSEABLE = 37;//sth than be closed (eg. a cage);
     * Affordance__INSERTABLE = 38;//sth than can contain another thing (which
     * had been inserted into this sth)[eg. a container];
     * Affordance__REMOVEFROMABLE = 39;//sth from whose inside another thing is
     * removed (eg. a container) Example: 30 31 32 Meaning: Affordance__VIEWABLE
     * Affordance__HIDEABLE Affordance__UNHIDEABLE
     */
    synchronized void ProcessGetAffordances(StringTokenizer st) {
        try {
            String eID = "";
            Thing th;

            if (st.hasMoreTokens()) {
                eID = st.nextToken();
            }

            if (!i.ep.e.thingMap.containsKey(eID)) {
                getOutBuffer().append(Constants.ERROR_CODE + " The Thing ID does not exist.");
                return;
            }
            th = i.ep.e.thingMap.get(eID);
            getOutBuffer().append(th.getAffordances() + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the state of the creature.
     *
     * @param st Creature's nameID (e.g. Creature_1365907148706)
     */
    synchronized void ProcessGetFullStatus3D(StringTokenizer st) {
        Long t = System.currentTimeMillis();

        Date nowD = new Date(t.longValue());
        String dateFormat = "HH:mm:ss:SS";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            int creatID = -1;
            String cID;

            if (st.hasMoreTokens()) {
                cID = st.nextToken();
                creatID = i.ep.e.getCreatureIndex(cID);//returns -1 if none exists
                //creatID = Integer.parseInt(cID);
            }
            if (creatID == -1) {
                getOutBuffer().append(Constants.ERROR_CODE + " Creature does not exist.");
                return;
            }
            synchronized (i.ep.e.semaphore3) {
                Creature c = i.ep.e.getCpool().get(creatID);
                synchronized (i.ep.e.getCpool().get(creatID).semaphore) {
                    c.updateVisualSensor(i.ep.e);
                    c.updateContactSensor(i.ep.e);

                    //Leaflet pool:
                    StringBuffer lp = new StringBuffer("");
                    StringBuffer vs = new StringBuffer("");
                    lp.append(" ");
                    lp.append(c.getActiveLeaflets().size()); //number of leaflets not delivered yet
                    ListIterator<Leaflet> iterator = c.getActiveLeaflets().listIterator();
                    Leaflet leaflet;
                    while (iterator.hasNext()) {
                        lp.append(" ");
                        leaflet = (Leaflet) iterator.next();
                        lp.append(" ");
                        lp.append(leaflet.getID());
                        lp.append(" ");
                        lp.append(leaflet.getNumberOfItemTypes());
                        lp.append(" ");
                        lp.append(leaflet.toStringFormatted());

                    }
                    //Visual System:
                    vs = getVisualContent(c);

                    //Creature info:
                    getOutBuffer().append(" "+
                            c.getMyName() + " "
                            + i.ep.e.getCpool().indexOf(c) + " "
                            + c.getX() + " "
                            + c.getY() + " "
                            + c.getSize() + " "
                            + c.getPitch() + " "
                            + c.getMotorSys() + " "
                            + c.getWheel() + " "
                            + c.getSpeed() + " "
                            + c.getFuel() + " "
                            + c.getSerotonin() + " "
                            + c.getEndorphine() + " "
                            + c.sack.getScore() + " "        
                            + c.getX1() + " "
                            + c.getY1() + " "
                            + c.getX2() + " "
                            + c.getY2() + " "
                            + //color of creature in client frame
                            c.getMaterial().getColorName() + " "
                            + c.getActionExecutedAndTarget() + " "
                            + c.getIfCollided() + " "
                            + c.ifHasActiveLeaflet() + " "
                            + lp //leaflet pool
                            + //content of visual system
                            vs + "\r\n");
                    
                    if (!vs.toString().equals(" 0"))log.info(">>>>>>>>Server sending: "+vs+"  at "+sdf.format(nowD));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ProcessGetSimulPars() {

        getOutBuffer().append(i.ep.getPreferredSize().width + " "
                + i.ep.getPreferredSize().height + " "
                + //delivery spot:
                i.ep.e.deliverySpotLocation[0] + " " + //x
                i.ep.e.deliverySpotLocation[1] //y
                + "\r\n");
    }

    void ProcessWorldReset() {
        sf.gameState.resetWorld();
    }

    void ProcessInitialEnvironmentParameters(StringTokenizer st) {

        int width, height;
        String w, h, pathToFloorTexture;
        if (st.hasMoreTokens()) {
            w = st.nextToken();
            width = Integer.parseInt(w);
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Environment settings: width is missing!");
            return;
        }

        if (st.hasMoreTokens()) {
            h = st.nextToken();
            height = Integer.parseInt(h);
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Environment settings: height is missing!");
            return;
        }
        if (st.hasMoreTokens()) {
            pathToFloorTexture = st.nextToken();
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Environment settings: path to texture is missing!");
            return;
        }

        sf.gameState.updateEnvironment(width, height, pathToFloorTexture);
        getOutBuffer().append("" + width + " " + height + " " + pathToFloorTexture + "\r\n");
    }

    /**
     * Currently not in use on the client. Format: setgoTo creatureID Vr Vl
     * Xdirection Ydirection The creature speed is the arithmetic mean of Vr and
     * Vl The angle of the turn is evaluated according to the direction
     * established between the points: robot location and (X,Y) position.
     *
     * @param st
     */
    void ProcessSetDifferentialStatusCML(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        getOutBuffer().append("Creature ID:" + creatID + "\r\n");
        Creature c = i.ep.e.getCpool().get(creatID);
        try {
            synchronized (c) {
                String vr = "", vl = "", xf = "", yf = "";

                if (st.hasMoreTokens()) {
                    vr = st.nextToken();
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " Right wheel velocity is missing");
                    return;
                }
                if (st.hasMoreTokens()) {
                    vl = st.nextToken();
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " Left wheel velocity is missing");
                    return;
                }


                if (st.hasMoreTokens()) {
                    xf = st.nextToken();
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " X final is missing");
                    return;
                }
                if (st.hasMoreTokens()) {
                    yf = st.nextToken();
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " Y final is missing");
                    return;
                }

                c.setVright(Double.parseDouble(vr));
                
                c.setVleft(Double.parseDouble(vl));
                
                if (c.getVleft() != -c.getVright()) {
                    c.setSpeed((c.getVright() + c.getVleft()) / 2);
                } else {
                    c.setSpeed(Math.abs(c.getVleft()));
                }


                c.setPitch(Math.toDegrees(Math.atan2(Double.parseDouble(yf) - c.getY(), Double.parseDouble(xf) - c.getX())));

                c.setW(Math.toRadians(c.getPitch()));

                i.ep.repaint();

                getOutBuffer().append("" + c.getSpeed() + " " + c.getPitch() + "\r\n");
            } //end syncronized
        } //end try
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This command is often used on clients that autonomously control the
     * creature.
     *
     * * Format: setAngle creatureID Vr Vl W
     *
     * @param st
     */
    void ProcessSetDifferentialStatus3D(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
        Creature c = i.ep.e.getCpool().get(creatID);
        try {
            synchronized (c) {
                String vr = "", vl = "", w = "", xf = "", yf = "";

                if (st.hasMoreTokens()) {
                    vr = st.nextToken();
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " Right wheel velocity is missing");
                    return;
                }
                if (st.hasMoreTokens()) {
                    vl = st.nextToken();
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " Left wheel velocity is missing");
                    return;
                }
                if (st.hasMoreTokens()) {
                    w = st.nextToken();
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " W is missing");
                    return;
                }

                c.setVright(Double.parseDouble(vr));
                c.setVleft(Double.parseDouble(vl));
                if ((c.getVleft() < 0) && (c.getVright() < 0)) {//reverse motion
                    c.setSpeed(Math.abs(c.getVright() + c.getVleft()) / 2);
                    c.setReverseMode(true);
                } else {
                    c.setReverseMode(false);
                    c.setSpeed((c.getVright() + c.getVleft()) / 2);
                }


                if (Double.parseDouble(w) != Constants.WNULL) {
                    c.setW(Double.parseDouble(w)%(2*Constants.M_PI));
                }
                //c.setPitch(Math.toDegrees(c.getW()));

                i.ep.repaint();

                getOutBuffer().append("" + c.getSpeed() + " " + c.getW() + "\r\n");


            } //end syncronized
        } //end try
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    void ProcessSackIt(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        synchronized (i.ep.e.semaphore3) {
            Creature c = i.ep.e.getCpool().get(creatID);
            try {
                synchronized (c) {
                    String thingName = "";

                    if (st.hasMoreTokens()) {
                        thingName = st.nextToken();
                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " Thing to grasp is missing");
                        return;
                    }

                    // System.out.println("...............................thing to grasp: "+ thingName);

                    Thing th = i.ep.e.getThingFromName(thingName);
                    if (th != null) {
                        //c.graspIt((Jewel)th);
                        c.putInSack(th, i.ep.e);   
                        i.ep.repaint();
                    }
                    getOutBuffer().append("" + thingName + "\r\n");


                } //end syncronized
            } //end try
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void ProcessEatIt(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        synchronized (i.ep.e.semaphore3) {
            Creature c = i.ep.e.getCpool().get(creatID);
            try {
                synchronized (c) {
                    String thingName = "";

                    if (st.hasMoreTokens()) {
                        thingName = st.nextToken();
                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " Food to be eaten is missing");
                        return;
                    }

                    log.info("...........eatIt................ food name: "+ thingName);

                    Thing th = i.ep.e.getThingFromName(thingName);
                    if (th != null) {
                        c.eatIt((Food) th, i.ep.e);
                        i.ep.repaint();
                    }
                    getOutBuffer().append("" + thingName + "\r\n");


                } //end syncronized

            } //end try
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("--------------end process eat------ ");
    }

    void ProcessHideIt(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
        Creature c = i.ep.e.getCpool().get(creatID);
        try {
            synchronized (c) {
                String thingName = "";

                if (st.hasMoreTokens()) {
                    thingName = st.nextToken();
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " Thing to be hidden is missing");
                    return;
                }


                //System.out.println("........................... thing name: "+ thingName);

                Thing th = i.ep.e.getThingFromName(thingName);
                if (th != null) {
                    c.digAndHideIt(th, i.ep.e);
                    i.ep.repaint();
                }
                getOutBuffer().append("" + thingName + "\r\n");


            } //end syncronized
        } //end try
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    void ProcessUnhideIt(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
        Creature c = i.ep.e.getCpool().get(creatID);
        try {
            synchronized (c) {
                String thingName = "";

                if (st.hasMoreTokens()) {
                    thingName = st.nextToken();
                } else {
                    getOutBuffer().append("Thing to be unhidden is missing");
                    return;
                }

                Thing th = i.ep.e.getThingFromName(thingName);
                if (th != null) {
                    th.undoHideMe(i.ep.e);
                    i.ep.repaint();
                }
                getOutBuffer().append("" + thingName + "\r\n");


            } //end syncronized
        } //end try
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    void ProcessDrop(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
        Creature c = i.ep.e.getCpool().get(creatID);
        try {
            synchronized (c) {
                String s;
                int category;
                int colorIdx;
                double x, y;
                if (st.hasMoreTokens()) {
                    s = st.nextToken();
                    category = Integer.parseInt(s);
                    if (st.hasMoreTokens()) {
                        s = st.nextToken();
                        colorIdx = Integer.parseInt(s);
                        c.drop(category, colorIdx);
                        getOutBuffer().append("" + c.getX()
                                + " y: " + c.getY() + "\r\n");

                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
                    }
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
                }

            } //end syncronized
        } //end try
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Angle of the turn is determined by the wheel speeds Vr (right wheel) and
     * Vl (left wheel).
     *
     * Format: setTurn creatureID speed Vr Vl
     *
     * @param st
     */
    void ProcessSetDifferentialStatus3DT(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
            Creature c = i.ep.e.getCpool().get(creatID);
            try {
                synchronized (c) {
                    String vr = "", vl = "", speed = "";

                    if (st.hasMoreTokens()) {
                        speed = st.nextToken();
                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " Creature speed is missing");
                        return;
                    }
                    if (st.hasMoreTokens()) {
                        vr = st.nextToken();
                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " Right wheel velocity is missing");
                        return;
                    }
                    if (st.hasMoreTokens()) {
                        vl = st.nextToken();
                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " Left wheel velocity is missing");
                        return;
                    }

                    c.setVright(Double.parseDouble(vr));
                    c.setVleft(Double.parseDouble(vl));

                    if (c.getMotorSys() == 2) {
                        ((RobotCreature) c).setSpeedExternally();
                    }
                    c.setSpeed(Double.parseDouble(speed));

                    double turn = (c.getVleft() - c.getVright()) / c.getSize();
                    double currW = Math.toRadians(c.getPitch());
                    double ang = turn+currW;
                    c.setW(ang % (2*Constants.M_PI));
//                    System.out.println("$$$$$$$$$$$$$$ turn::getW= "+c.getW());
                    c.setPitch(Math.toDegrees(c.getW()));

                    i.ep.repaint();

                    log.info("Angle of turn according to Vr and Vl: " + "in radian: " + turn + " in degree: " + Math.toDegrees(turn));
                    log.info("Pitch changed to (in degrees)= "+c.getPitch());
                    getOutBuffer().append("" + c.getSpeed() + " " + c.getW() + c.getPitch() + "\r\n");


                } //end syncronized
            } //end try
            catch (Exception e) {
                e.printStackTrace();
            }

        }

    void ProcessSetLeaflet(StringTokenizer st) {
        int jewel1 = 0;
        int jewel2 = 1;
        int jewel3 = 2;
        Leaflet leaflet = null;
        int creatID = -1;
        String cID;
        String s1 = "", s2 = "", s3 = "";
        List<Leaflet> leafletList = new ArrayList<Leaflet>();

        if (!st.hasMoreTokens()) {
            for (int it = 0; it <= (Constants.MAX_NUMBER_OF_LEAFLETS - 1); it++) {
                leaflet = generateRandomLeaflet();
                leafletList.add(leaflet);
                waitAMilli();
            }
            getOutBuffer().append("\r\n");
            i.ep.e.resetLeafletPool();
            for (Leaflet l : leafletList) {
                i.ep.e.addLeaflet(l);
                getOutBuffer().append(l.toStringFormatted());
            }
            i.ep.e.notifyLeafletObservers();
            //   synchronized (i.ep.e.semaphore) {

            for (Creature cr : i.ep.e.getCpool()) {
                synchronized (cr) {
                    cr.setActiveLeaflets(i.ep.e.getLeafletsOfOwner(cr.getID()));
                }
            }
            //  }

        } else {

            if (st.hasMoreTokens()) {
                cID = st.nextToken();
                creatID = Integer.parseInt(cID);
            }
            if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
                getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
                return;
            }
            //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
            Creature c = i.ep.e.getCpool().get(creatID);
            if (!st.hasMoreTokens()) {
                getOutBuffer().append(Constants.ERROR_CODE + " Leaflet is missing!!!");
                return;
            } else {
                s1 = st.nextToken();
                jewel1 = Integer.parseInt(s1);

                if (Constants.getColorItem(jewel1) == null) {
                    getOutBuffer().append(Constants.ERROR_CODE + " First type entered is not alowed! Try again.");
                    return;
                }
                //System.out.println("Jewel1: " + Constants.getColorItem(jewel1) + "\r\n");
                if (st.hasMoreTokens()) {
                    s2 = st.nextToken();
                    jewel2 = Integer.parseInt(s2);
                }
                if (Constants.getColorItem(jewel2) == null) {
                    getOutBuffer().append(Constants.ERROR_CODE + " Second type entered is not alowed! Try again.");
                    return;
                }
                //System.out.println("Jewel2: " + Constants.getColorItem(jewel2) + "\r\n");
                if (st.hasMoreTokens()) {
                    s3 = st.nextToken();
                    jewel3 = Integer.parseInt(s3);
                }
                if (Constants.getColorItem(jewel3) == null) {
                    getOutBuffer().append(Constants.ERROR_CODE + " Third type entered is not alowed! Try again.");
                    return;
                }
                //System.out.println("Jewel3: " + Constants.getColorItem(jewel3) + "\r\n");
                //ATTENTION: formatted message: (client depends on this format)
                int[] jewels = {jewel1, jewel2, jewel3};
                leaflet = new Leaflet(jewels, c.getID());
                leafletList.add(leaflet);
            }
            synchronized (c) {
                getOutBuffer().append(" \r\n");
                for (Leaflet l : leafletList) {
                    i.ep.e.addLeaflet(l);
                    getOutBuffer().append(l.toStringFormatted());
                }
                i.ep.e.notifyLeafletObservers();
                c.setActiveLeaflets(i.ep.e.getLeafletsOfOwner(c.getID()));

            }
        }

    }

    Leaflet generateRandomLeaflet() {
        LeafletGenerator lg = new LeafletGenerator();

        return lg.getLeaflet();

    }

    Leaflet generateRandomLeaflet(Long owner) {
        LeafletGenerator lg = new LeafletGenerator(owner);

        return lg.getLeaflet();

    }

    void ProcessHelp() {
        log.info("====  ProcessHelp() ===");
        getOutBuffer().append("List of available commands :\r\n"
                + "  \r\n"
                + "  -----------Game settings---------------------------------------------------------------------------\r\n"
                + "  help - Show this list\r\n"
                + "  memory - Show available free memory\r\n"
                + "  getclock - Show the server current time\r\n"
                + "  quit - Quit the communication session\r\n"
                + "  startGame - Start the game: creatures start moving.\r\n"
                + "  shutdown - Shutdown the server application\r\n"
                + "  version - Show current version\r\n"
                + "  setenv <environment width> <environment height> <path to texture image of the floor> \r\n"
                + "  getenvironment - Get Environment Geometry\r\n"
                + "  getall - Lists all creatures and things at the simulation environment\r\n"
                + "  getsimulpars - Get the width and height of the active environment and the location of the DeliverySpot."
                + "     Return: width height xDS yDS. \r\n"
                + "  -----------Create and delete waypoints-------------------------------------------------------------\r\n"
                + "  newwp <X> <Y> - New waypoint at position (x y) \r\n"
                + "  delwp <X> <Y> - Delete waypoint at position (x y) \r\n"
                + "  -----------Manage entities (objects at the environment)-------------------------------------------\r\n"
                + "  brick <type> <x1> <y1> <x2> <y2>- Type: 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White with "
                + "  vertexes x1, x2, y1, y2 \r\n"
                + "  food <type> <x> <y> - Type: 0-perishable 1-non-perishable at position (x,y) \r\n"
                + "  jewel <type> <x> <y> - Type: 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White at position (x,y) \r\n"
                + "  cage <x> <y> - Create a cage at position (x,y) \r\n"
                + "  leaflet <CreaturePoolIndex> <jewel1> <jewel2> <jewel3> - Create a leaflet. Do not enter anything in order\r\n"
                + "    to randomize: >leaflet or >leaflet robotID Red Green Blue\r\n"
                + "    Example: >leaflet\r\n"
                + "              White  2 0 Yellow  1 0 6  White  1 0 Magenta  2 0 5  Blue  1 0 Red  1 0 Green  1 0 24\r\n"
                + "              Leaflet1: 2 white jewels and 1 yellow jewel. None already collected. This leaflet increases the score in 6 points\r\n"
                + "              Leaflet2: 1 white jewel and 2 magenta jewels. None already collected. This leaflet increases the score in 5 points\r\n"
                + "              Leaflet3: 1 Blue, 1 Red and 1 green jewel. None already collected. This leaflet increases the score in 24 points\r\n"
                + "  deleteth <Type> <ThingPoolIndex> - Delete an object or creature. Type: 0: food, jewel or blick; 1: creature.\r\n\r\n"
                + "  deliver <CreaturePoolIndex> - Deliver the leaflet and increase the creature's score. \r\n"
                + "  drop <CreaturePoolIndex> <Type> <Thing color (for jewels) or any number for food> - Type: 3-Jewel; "
                + "  21-Perishable food; 22- Non-Perishable food; Color: 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White\r\n\r\n"
                + "  -----------Creature control-------------------------------------------------------------------------\r\n"
                + "  new <X> <Y> <PITCH> - Insert new creature at positions\r\n"
                + "  checkXY <X> <Y> <PITCH> - Check if a creature exists at (x, y). \r\n"
                + "     If it exists but with different pitch, this is changed.\r\n"
                + "  camera <CreaturePoolIndex> - Start robot's visual sensor\r\n"
                + "  start <CreaturePoolIndex> - Start creature motor system. \r\n"
                + "  stop <CreaturePoolIndex> - Stop creature motor system.\r\n"
                + "  setgoTo <CreaturePoolIndex> <Vr> <Vl> <Xdirection> <Ydirection> - Creature turns towards the direction \r\n"
                + "   of point (x, y). But does not stop at the point. Linear velocity is the arithmetic mean of Vr \r\n"
                + "   and Vl.\r\n"
                + "  setTurn <CreaturePoolIndex> <speed> <Vr> <Vl> - The creature tuns according to the angle specified. \r\n"
                + "   The angle of the turn is evaluated according to the wheel velocities (Vr-right Vl-left). \r\n"
                + "   The velocity of the creature's overall movement corresponds o the <speed> parameter.\r\n"
                + "   Note: The final pitch of the robot depends on its original orientation (pitch).\r\n"
                + "   HINTs: +~30deg: Vl= 11 and Vr= 0.6; +~45deg: Vl= 16 and Vr= 0.2; +~60deg: Vl= 22 and Vr= 1;\r\n"
                + "          +~90deg: Vl= 32 and Vr= 0.6; +~180deg: Vl= 63 and Vr= 0.2; +~270deg: Vl= 95 and Vr= 0.8;\r\n"
                + "  setAngle <CreaturePoolIndex> <Vr> <Vl> <w (radians)> - Set the creature's pitch to a specific angle.\r\n"
                + "    Reference is 0 radian that is the X axis (horizontal) and positive angles go clockwise.\r\n"
                + "    This command is often used when autonomously controlling the creature.\r\n"
                + "    Linear velocity is the arithmetic mean of Vr and Vl.\r\n"
                + "  getcreaturestate <NameID> - Creature data: X Y size pitch motorSys wheel speed fuel color camera \r\n"
                + "  getsack <CreaturePoolIndex> -  Return the creature's bag content: Total number of Food; Total number of Crystals; \n"
                + "          Number of PerishableFood; Number of Non-PerishableFood;  Number of crystals of \n" 
                + "          each color in the sequence: RED, GREEN, BLUE, YELLOW, MAGENTA, WHITE.\r\n" 
                + "  affordances <Thing ID (Format: Name__Timestamp)> - Get list of affordances of a Thing\r\n"
                + "        Codes: \n"
                + "           Affordance__VIEWABLE = 30; Affordance__HIDEABLE = 31; Affordance__UNHIDEABLE = 32;\n"
                + "           Affordance__GRASPABLE = 33; Affordance__EATABLE = 34; Affordance__PUTINBAGABLE = 35; \n"
                + "           Affordance__OPENABLE = 36;//sth that can be opened (eg. a cage); \n"
                + "           Affordance__CLOSEABLE = 37;//sth than be closed (eg. a cage); \n"
                + "           Affordance__INSERTABLE = 38;//sth than can contain another thing [eg. a container]; \n"
                + "           Affordance__REMOVEFROMABLE = 39;//sth from whose inside another thing is removed [eg. a container].\n"
                + "        Example: affordances Brick_1364939720793\n"
                + "               30 31 32    \n"
                + "         Meaning: Affordance__VIEWABLE Affordance__HIDEABLE  Affordance__UNHIDEABLE \r\n"
                + "   getvs <ThingID> - Returns the list of Things within the creature's visual system.\r\n"
                + "         Format: numberOfItems || Thing1 || Thing2 ...\n"
                + "               Thing?: ThingName category ifIsOccluded X1 X2 Y1 Y2 Pitch hardness energy shininess color || ...\n"
                + "         Example: 2 || Brick_1364939720793 1 0 541.0 583.0 282.0 363.0 -1.0 1.0 0.0 1.0 Magenta || NPFood_1364939723933 22 0 553.0 553.0 442.0 442.0 -1.0 1.0 2.0 1.0 Green\r\n"
                + "  refuel <CreaturePoolIndex> - Set full energy for a specific creature.\r\n");


    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Main m = new Main();
    }

    void ProcessNewCreature(StringTokenizer st) {
        String s, motor;
        double x, y, pitch;
        int motorSys;
        boolean color = false;
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);
                if (st.hasMoreTokens()) {
                    s = st.nextToken();
                    pitch = Double.parseDouble(s);
                    if (st.hasMoreTokens()) {
                        s = st.nextToken();
                        if (s.equalsIgnoreCase("1")) color = true;
                    }

                    ThingCreator tc = new ThingCreator(i.ep.e);
                    RobotCreature c = tc.createCreature(color, x, y, pitch);

                    this.sf.gameState.ThingsRN.updateRenderState();

                    getOutBuffer().append("" + i.ep.e.getCpool().indexOf(c) + " " + c.getMyName() + " " + c.getX() + " " + c.getY() + " " + c.getPitch() + "\r\n");

                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
                }
            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
            }
        }

    }

    void ProcessCheckXYCreature(StringTokenizer st) {
        String s;
        double x, y, pitch;
        String msg = "", ID = "", nameID = "";
        int index = 0;
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);
                if (st.hasMoreTokens()) {
                    s = st.nextToken();
                    pitch = Double.parseDouble(s);
                    for (Creature c : i.ep.e.getCpool()) {
                        if ((c.getX() == x) && (c.getY() == y)) {
                            index = i.ep.e.getCpool().indexOf(c);
                            ID = "" + index;
                            i.ep.e.getCpool().get(index).setPitch(pitch);
                            nameID = c.getMyName();
                            msg = ID + " " + nameID;
                            break;
                        }
                    }

                    getOutBuffer().append(msg);

                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
                }
            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
        }
    }

    void ProcessNewWaypoint(StringTokenizer st) {
        String s;
        double x, y;
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);
                //Creature c = new Creature(x, y, pitch, i.ep.e);
                i.ep.e.wpTS = i.ep.display.getRenderer().createTextureState();
                //IconFactory wp = new IconFactory(i.ep.e.wpTS);
                IconFactory wp = new IconFactory(i.ep.e.wpTS, x, y, i.ep.e.width, i.ep.e.height);
                i.ep.e.addWaypointIcon(wp);
                this.sf.gameState.ThingsRN.updateRenderState();
                getOutBuffer().append("" + wp.x + " y: " + wp.y);

            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
        }
    }

    void ProcessDeleteWaypoint(StringTokenizer st) {
        String s;
        double x, y;
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);
                //Creature c = new Creature(x, y, pitch, i.ep.e);
                i.ep.e.wpTS = i.ep.display.getRenderer().createTextureState();
                //IconFactory wp = new IconFactory(i.ep.e.wpTS);
                String str = i.ep.e.removeWaypointIcon(x, y);
                this.sf.gameState.ThingsRN.updateRenderState();
                getOutBuffer().append("" + x + " " + y);
                getOutBuffer().append(str + "\r\n");

            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
        }
    }

    //deleteth <0 or 1> ThingIndex
    //0: Objects (food, crystal, brick)
    //1: Creature
    void ProcessDeleteThing(StringTokenizer st) {
        int type = -1;
        int thingID = -1;
        if (st.hasMoreTokens()) {
            type = Integer.parseInt(st.nextToken());
            if (type == 0) { //object
                if (st.hasMoreTokens()) {
                    thingID = Integer.parseInt(st.nextToken());
                    if (i.ep.e.getOpool().size() > 0) {
                        if (thingID < 0 || thingID > i.ep.e.getOpool().size() - 1) {
                            getOutBuffer().append(Constants.ERROR_CODE + " Object does not exist. Try again!");
                            return;
                        }
                        Thing th = i.ep.e.getOpool().get(thingID);
                        if ((th != null) && (th.category != Constants.categoryCREATURE)) {
                            th.removeRememberMeIcon(i.ep.e);
                            i.ep.e.removeThing(th);
                            getOutBuffer().append("" + th.getMyName() + "\r\n");
                        }
                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " Object does not exist. Try again!");
                        return;
                    }

                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
                }
            } else if (type == 1) { //Creature
                if (st.hasMoreTokens()) {
                    thingID = Integer.parseInt(st.nextToken());
                    if (i.ep.e.getCpool().size() > 0) {
                        if (thingID < 0 || thingID > i.ep.e.getCpool().size() - 1) {
                            getOutBuffer().append(Constants.ERROR_CODE + " Creature does not exist. Try again!");
                            return;
                        }

                        Thing th = i.ep.e.getCpool().get(thingID);
                        if (th.category == Constants.categoryCREATURE) {
                            int dead = i.ep.e.getCpool().indexOf(th);

                            i.ep.scoreTabList.clear();
                            i.ep.e.removeCreature((Creature) th);
                            i.ep.e.updateCameras(dead);
                            getOutBuffer().append("" + th.getMyName() + "\r\n");
                        }
                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " Object does not exist. Try again!");
                        return;
                    }

                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
                    return;
                }
            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... Type is invalid!");
                return;
            }

        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
        }
    }

    void ProcessNewCage(StringTokenizer st) {
        String s;
        double x, y;
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);

                ThingCreator tc = new ThingCreator(i.ep.e);
                Cage cage;
                cage = (Cage) tc.createThing(Constants.categoryCAGE, x, y);
                this.sf.gameState.ThingsRN.updateRenderState();
                getOutBuffer().append(cage.getMyName() + " " + cage.getX() + " " + cage.getY() + "\r\n");

            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... Must specify where to place the cage (x, y)");
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
        }
    }

    void ProcessNewFood(StringTokenizer st) {
        String s;
        int type;
        double x, y;

        if (st.hasMoreTokens()) {
            s = st.nextToken();
            type = Integer.parseInt(s);
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Must specify the type of food: 0-perishable or 1-non-perishable.");
            return;
        }
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);

                ThingCreator tc = new ThingCreator(i.ep.e);
                Food food;
                if (type == 0) {
                    food = (Food) tc.createThing(Constants.categoryPFOOD, x, y);
                } else if (type == 1) {
                    food = (Food) tc.createThing(Constants.categoryNPFOOD, x, y);
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " Invalid type of food: 0-perishable or 1-non-perishable.");
                    return;
                }

                this.sf.gameState.ThingsRN.updateRenderState();
                getOutBuffer().append(food.getMyName() + " " + food.getX() + " " + food.getY() + "\r\n");

            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
        }
    }

    void ProcessNewFoodInBatch(StringTokenizer st) {
        String s;
        int type;
        double x, y;
        int number;

        if (st.hasMoreTokens()) {
            s = st.nextToken();
            type = Integer.parseInt(s);
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Must specify the type of food: 0-perishable or 1-non-perishable.");
            return;
        }
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            number = Integer.parseInt(s);
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Number of food is missing.");
            return;
        }
        Food food;

        for (int j = 0; j < number; j++) {
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                x = Double.parseDouble(s);
            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " x is missing.");
                return;
            }
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);
            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " y is missing.");
                return;
            }

            ThingCreator tc = new ThingCreator(i.ep.e);

            if (type == 0) {
                food = (Food) tc.createThing(Constants.categoryPFOOD, x, y);
            } else if (type == 1) {
                food = (Food) tc.createThing(Constants.categoryNPFOOD, x, y);
            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " Invalid type of food: 0-perishable or 1-non-perishable.");
                return;
            }
            getOutBuffer().append(food.getMyName() + " " + food.getX() + " " + food.getY() + "\r\n");
        }
        getOutBuffer().append("\r\n ");
        this.sf.gameState.ThingsRN.updateRenderState();
    }

    void ProcessNewBrick(StringTokenizer st) {
        String s;
        int type;
        double x1, y1, x2, y2;

        if (st.hasMoreTokens()) {
            s = st.nextToken();
            type = Integer.parseInt(s);
            if ((type < 0) || (type > 5)) {
                getOutBuffer().append(Constants.ERROR_CODE + " Invalid type of brick! Try: 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White ");
                return;
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Specify the type of brick: 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White ");
            return;
        }
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x1 = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y1 = Double.parseDouble(s);
                if (st.hasMoreTokens()) {
                    s = st.nextToken();
                    x2 = Double.parseDouble(s);
                    if (st.hasMoreTokens()) {
                        s = st.nextToken();
                        y2 = Double.parseDouble(s);
                        ThingCreator tc = new ThingCreator(i.ep.e);
                        Brick brick = (Brick) tc.createBrick(type, x1, y1, x2, y2);
                        this.sf.gameState.ThingsRN.updateRenderState();
                        getOutBuffer().append(brick.getMyName() + " " + brick.getX()
                                + " " + brick.getY() + "\r\n");
                    } else {
                        getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
                    }
                } else {
                    getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
                }



            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
            }
        } else {
            getOutBuffer().append("... No recognized command");
        }
    }

    void ProcessNewJewel(StringTokenizer st) {
        String s;
        int type;
        double x, y;

        if (st.hasMoreTokens()) {
            s = st.nextToken();
            type = Integer.parseInt(s);
            if ((type < 0) || (type > 5)) {
                getOutBuffer().append(Constants.ERROR_CODE + " Invalid type of jewel! Try: 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White ");
                return;
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Specify the type of jewel: 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White ");
            return;
        }
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            x = Double.parseDouble(s);
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);

                ThingCreator tc = new ThingCreator(i.ep.e);
                Jewel jewel = (Jewel) tc.createCrystalOfType(type, x, y);
                this.sf.gameState.ThingsRN.updateRenderState();
                getOutBuffer().append(jewel.getMyName() + " " + jewel.getX()
                        + " " + jewel.getY() + "\r\n");

            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " ... No recognized command");
        }
    }

    void ProcessNewJewelsInBatch(StringTokenizer st) {

        String s;
        int type;
        double x, y;
        int number;

        if (st.hasMoreTokens()) {
            s = st.nextToken();
            type = Integer.parseInt(s);
            if ((type < 0) || (type > 5)) {
                getOutBuffer().append(Constants.ERROR_CODE + " Invalid type of jewel! Try: 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White ");
                return;
            }
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Must specify the type of jewel.");
            return;
        }
        if (st.hasMoreTokens()) {
            s = st.nextToken();
            number = Integer.parseInt(s);
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Number of jewels is missing.");
            return;
        }

        for (int j = 0; j < number; j++) {
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                x = Double.parseDouble(s);
            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " x is missing.");
                return;
            }
            if (st.hasMoreTokens()) {
                s = st.nextToken();
                y = Double.parseDouble(s);
            } else {
                getOutBuffer().append(Constants.ERROR_CODE + " y is missing.");
                return;
            }

            ThingCreator tc = new ThingCreator(i.ep.e);

            Jewel jewel = (Jewel) tc.createCrystalOfType(type, x, y);
            getOutBuffer().append(jewel.getMyName() + " " + jewel.getX() + " y: " + jewel.getY());
        }
        getOutBuffer().append("\r\n ");
        this.sf.gameState.ThingsRN.updateRenderState();
    }

    /**
     * Creatures starts moving according to the parameters previously configured
     * by setDiff3D.
     *
     * @param st the creature ID.
     */
    void ProcessStartCreature(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
        Creature c = i.ep.e.getCpool().get(creatID);
        c.hasStarted = true;
        getOutBuffer().append("\r\n Run creature run...\r\n");
    }

    /**
     * Creature delivers the current leaflet.
     *
     * @param st the creature ID.
     */
    void ProcessDeliverLeaflet(StringTokenizer st) {
        int creatID = -1;
        String cID;
        String leafletID = "";
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
        if (st.hasMoreTokens()) {
            leafletID = st.nextToken();
        } else {
            getOutBuffer().append(Constants.ERROR_CODE + " Leaflet ID is missing");
            return;
        }
        Creature c = i.ep.e.getCpool().get(creatID);
        Leaflet l = (Leaflet) i.ep.e.getLeafletPool().get(Long.parseLong(leafletID));
        
         // Avoid deliver a leaflet already delivered
        if (l.getActivity() != 0) {          
            // Avoid deliver of a incomplete leaflet
            //if (l.isIfCompleted()) {
                c.deliver(l);
                getOutBuffer().append("\r\n Leaflet delivered!\r\n");
            //} else {
            //    getOutBuffer().append(Constants.ERROR_CODE).append(" Leaflet ").append(leafletID).append(" was not completed yet.\r\n");
            //}
        } else {
//            getOutBuffer().append(Constants.ERROR_CODE).append(" Leaflet ").append(leafletID).append(" was already delivered.\r\n");
            getOutBuffer().append(" Leaflet ").append(leafletID).append(" was already delivered.\r\n");
        }       
        /** Suelen comentou aqui
         c.deliver(l);
        getOutBuffer().append("\r\n Leaflet delivered!\r\n");*/
    }

    /**
     * Creatures stops moving ahead.
     *
     * @param st the creature ID.
     */
    void ProcessStopCreature(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID);
        Creature c = i.ep.e.getCpool().get(creatID);
        c.hasStarted = false;
        getOutBuffer().append("Creature has stopped!!!");
    }

    synchronized void ProcessSetThreadName(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist. Must specify a creature.");
            return;
        }
        String name = "RobotMind_" + creatID;
        this.clientsConnected.get(clientsConnected.size() - 1).setName(name);
        getOutBuffer().append(name);
    }

    void ProcessRefuel(StringTokenizer st) {
        int creatID = -1;
        String cID;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            getOutBuffer().append(Constants.ERROR_CODE + " Creature ID does not exist. Must specify a creature.");
            return;
        }
        //getOutBuffer().append("Creature ID:" + creatID + " \r\n");
        Creature c = i.ep.e.getCpool().get(creatID);
        c.refill();
        getOutBuffer().append(c.getFuel() + "\r\n");
    }

    void ProcessGetWorldNumEntities() {
        getOutBuffer().append(i.ep.e.getCpool().size() + " " + i.ep.e.getOpool().size() + "\r\n");

    }

//    void ProcessShowWorldThings() {
//        int ii = 0;
//        //getOutBuffer().append("World Situation ... " + i.ep.e.getCpool().size() + " creatures and " + i.ep.e.getOpool().size() + " Things" + "\n");
//        for (Creature c : i.ep.e.getCpool()) {
//            getOutBuffer().append("Creature " + ii++ + ": " + c.getX() + "," + c.getY() + " " + c.getPitch() + " | ");
//        }
//        ii = 0;
//        for (Thing o : i.ep.e.getOpool()) {
//            getOutBuffer().append(" | Thing " + ii++ + " " + o.getX1() + " " + o.getY1() + " " + o.getX2() + " re" + o.getY2() );
//        }
//        getOutBuffer().append("\r\n");
//    }
    synchronized void ProcessGetAllThings() {
        try {

            StringBuffer all = new StringBuffer("");
            //   synchronized (i.ep.e.semaphore) {

            all.append(" ");
            int number = i.ep.e.getCpool().size() + i.ep.e.getOpool().size();
            all.append(number); //number of things in camera

            List<Thing> allThings = new ArrayList<Thing>();
            for (Thing c : i.ep.e.getCpool()) {
                allThings.add(c);
            }
            for (Thing th : i.ep.e.getOpool()) {
                allThings.add(th);
            }
            ListIterator<Thing> iter = allThings.listIterator();
            Thing o;
            while (iter.hasNext()) {
                o = (Thing) iter.next();
                all.append(" || ");
                all.append(o.getMyName());
                all.append(" ");
                all.append(o.category); //flag to indicate the type of Thing
                all.append(" ");
                all.append(o.isOccluded); //indicates if occluded by another Thing
                all.append(" ");
                all.append(o.getX1());
                all.append(" ");
                all.append(o.getX2());
                all.append(" ");
                all.append(o.getY1());
                all.append(" ");
                all.append(o.getY2());
                all.append(" ");
                all.append(o.getPitch());
                all.append(" ");
                all.append(o.getMaterial().getHardness());
                all.append(" ");
                all.append(o.getMaterial().getEnergy());
                all.append(" ");
                all.append(o.getMaterial().getShininess());
                all.append(" ");
                all.append(o.getMaterial().getColorName());
                all.append(" ");
                all.append(o.getX()); //center of mass
                all.append(" ");
                all.append(o.getY());//""
            }

            getOutBuffer().append(all);

            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void ProcessCheckCreature(StringTokenizer st) {
        int creatID = -1;
        String cID;
        String msg;
        if (st.hasMoreTokens()) {
            cID = st.nextToken();
            creatID = Integer.parseInt(cID);
        }
        if (creatID < 0 || creatID > i.ep.e.getCpool().size() - 1) {
            msg = "no";
        } else {
            msg = "yes " + i.ep.e.getCpool().get(creatID).getMyName();
        }
        getOutBuffer().append(msg);
        return;
    }

    //Force the system to wait a millisecond
    public void waitAMilli() {
        long t0, t1;
        t0 = System.currentTimeMillis();
        do {
            t1 = System.currentTimeMillis();
        } while (t1 - t0 < 1);
    }
}
