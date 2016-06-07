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
package worldserver3d.gamestate;

import java.util.logging.Level;
import java.util.logging.Logger;
import worldserver3d.view.KnapsackAndScoreFrame;
import worldserver3d.view.WorldFrame;
import worldserver3d.view.NewWorldFrame;
import worldserver3d.*;
import java.util.Observable;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.awt.swingui.JMEDesktop;
import com.jmex.game.state.CameraGameState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import model.Creature;
import model.Environment;
import model.Thing;
import java.util.HashMap;
import java.util.Observer;
import com.jme.intersection.PickResults;
import com.jme.intersection.BoundingPickResults;
import com.jme.scene.Spatial;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import model.VisualSensor;
import util.Constants;
import worldserver3d.view.EnvironmentPanel;
import xml.ReadFromXMLFile;

/**
 *
 * @author gudwin
 * @author eccastro
 */
public class SimulationGameState extends CameraGameState implements ActionListener, MenuListener {

    private KeyboardWorldInputHandler input;
    //private InputHandler input;
    public Main m;
    public Node ThingsRN; //only parent of arrows
    //All Things (except arrows) are seen by the robot (in the monitor)
    private Node robotThingsRN; //all Things are children of this node now
    public Environment wEnv;
    Quaternion qKey = new Quaternion();
    float ang_row = 270;
    float ang_pitch = 180;
    float ang_yaw = 0;
    float x_l = -7;
    float y_l = 2;
    float z_l = 0;
    public RobotCamera rcnEven;
    public RobotCamera rcnOdd;
    float turn = 0;
    DisplaySystem display;
    // Default material state
    public MaterialState dms;
    private ScoreMenuUpdater up = new ScoreMenuUpdater();
    JDesktopPane jdp;
    private KnapsackAndScoreFrame scoreTab;
    private JMenu creatureScoreItem = new JMenu("Creatures Score");
    //public DeliverySpot deliverySpot;
    public Thing  deliverySpot;
    WorldFrame i;
    NewWorldFrame nwf;
    Quad floorQuad;
    private StandardGame game;
    int[]envDim;

    static Logger log = Logger.getLogger(SimulationGameState.class.getCanonicalName());

    public SimulationGameState(String name, Main m, StandardGame game) {
        super(name);
        this.m = m;
        this.game = game;
        display = DisplaySystem.getDisplaySystem();
        nwf = new NewWorldFrame(m, up);
        wEnv = m.i.ep.getEnvironment();
        i = m.i;
        try {
            scoreTab = new KnapsackAndScoreFrame();
        } catch (InvocationTargetException ex) {
            log.severe("KnapsackAndScoreFrame instantiation error." + ex);
        }
        wEnv.cpoolNotifier.addAnObserver(up);

        // Move the camera a bit.
        cam.setLocation(new Vector3f(0, 40, 60));
        cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        cam.update();

        // Create a Quad.
        floorQuad = new Quad("Floor_Quad", wEnv.width / 10, wEnv.height / 10);
        floorQuad.setModelBound(new BoundingBox());
        floorQuad.updateModelBound();
        floorQuad.setIsCollidable(false);
        floorQuad.setLocalRotation(new Quaternion(new float[]{90 * FastMath.DEG_TO_RAD, 0, 0}));
        initInput();
        // Apply a texture to it.
        TextureState ts = display.getRenderer().createTextureState();
        Texture texture =
                TextureManager.loadTexture(
                SimulationGameState.class.getClassLoader().getResource(
                "images/checker_medium.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        texture.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(texture);
        ts.setEnabled(true);
        floorQuad.setRenderState(ts);
        // Add it to the scene.
        floorQuad.setIsCollidable(false);

        robotThingsRN = new Node("Things Visible by robot"); // all except arrows (RMIcon)
        robotThingsRN.attachChild(floorQuad);

        ThingsRN = new Node("Arrows root node");
        rootNode.attachChild(ThingsRN);
        rootNode.attachChild(robotThingsRN);
        nwf.setFloorQuad(floorQuad);
        nwf.setWorldFrame(i);
        nwf.setRootNode(ThingsRN);

        // Get a MaterialState
        dms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        // Create a point light
        PointLight l = new PointLight();
        // Give it a location
        l.setLocation(new Vector3f(wEnv.width, 100, wEnv.height));

        l.setDiffuse(ColorRGBA.white);
        l.setSpecular(ColorRGBA.white);
        // Enable it
        l.setEnabled(true);

        PointLight l2 = new PointLight();
        // Give it a location
        l2.setLocation(new Vector3f(-wEnv.width, 100, -wEnv.height));

        l2.setDiffuse(ColorRGBA.white);
        l2.setSpecular(ColorRGBA.white);
        // Enable it
        l2.setEnabled(true);

        // Create a LightState to put my light in
        LightState ls = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        // Attach the light
        ls.attach(l);
        ls.attach(l2);
        wEnv.ls = ls;
        wEnv.defaultms = dms;
        wEnv.flagMS = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        wEnv.creatureMS = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
        Node modelw = null;
        wEnv.cnf = new CreatureNodeFactory();

        KeyBindingManager.getKeyBindingManager().set("row", KeyInput.KEY_I);
        KeyBindingManager.getKeyBindingManager().set("pitch", KeyInput.KEY_J);
        KeyBindingManager.getKeyBindingManager().set("yaw", KeyInput.KEY_K);
        KeyBindingManager.getKeyBindingManager().set("xm", KeyInput.KEY_1);
        KeyBindingManager.getKeyBindingManager().set("xM", KeyInput.KEY_2);
        KeyBindingManager.getKeyBindingManager().set("ym", KeyInput.KEY_3);
        KeyBindingManager.getKeyBindingManager().set("yM", KeyInput.KEY_4);
        KeyBindingManager.getKeyBindingManager().set("zm", KeyInput.KEY_5);
        KeyBindingManager.getKeyBindingManager().set("zM", KeyInput.KEY_6);
        KeyBindingManager.getKeyBindingManager().set("resetcamera", KeyInput.KEY_R);
        KeyBindingManager.getKeyBindingManager().set("orthocamera", KeyInput.KEY_T);


        initCameras();
        JMEDesktop desktop = new JMEDesktop("Desktop");
        desktop.setup(display.getWidth(), display.getHeight(), false, input);
        desktop.getLocalRotation().set(0, 0, 0, 1);
        desktop.getLocalTranslation().set(display.getWidth() / 2, display.getHeight() / 2, 0);
        desktop.getLocalScale().set(1, 1, 1);
        desktop.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        desktop.updateRenderState();
        Node teste2 = new Node("Teste");
        teste2.attachChild(desktop);
        teste2.updateModelBound();
        teste2.updateRenderState();
        this.getRootNode().attachChild(teste2);
        jdp = desktop.getJDesktop();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    // Only access the Swing UI from the Swing event dispatch thread!
                    // See SwingUtilities.invokeLater()
                    // and http://java.sun.com/docs/books/tutorial/uiswing/concurrency/index.html for details.
                    createSwingStuff();
                }
            });
        } catch (InterruptedException e) {
            // ok - just leave
            return;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        // Remember to update the rootNode before you get going.
        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();

    }

    void createSwingStuff() {

        JMenuBar mbar = new JMenuBar();
        mbar.setVisible(true);

        mbar.setLocation(0, 0);
        mbar.setSize(jdp.getWidth(), 30);

        JMenu fileMenu = new JMenu("File");
        fileMenu.addMenuListener(this);
        JMenu optionMenu = new JMenu("Options");
        optionMenu.addMenuListener(this);
        JMenu runMenu = new JMenu("Run");
        runMenu.addMenuListener(this);
        JMenuItem saveItem;
        JMenuItem newItem;
        JMenuItem exitItem;
        JMenuItem openItem = new JMenuItem("Open", new ImageIcon("../images/open.gif")); // Abrir
        newItem = new JMenuItem(" New", new ImageIcon("../images/new.gif"));   // Limpar
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        saveItem = new JMenuItem(" Save", new ImageIcon("../images/save.gif"));   // Salvar
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        exitItem = new JMenuItem(" Exit", new ImageIcon("../images/sair.gif"));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        JMenuItem deliverySpotItem = new JMenuItem("Create Delivery spot");
        deliverySpotItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {

                if (i.ep.getEnvironment().dsIsShown) {
                    i.ep.getEnvironment().removeDSIcon(Constants.deliverySpotCoords[0],Constants.deliverySpotCoords[1]);
                } else {
                    //Delivery spot is (0,0)
                    i.ep.getEnvironment().dsTS = i.ep.display.getRenderer().createTextureState();
                    IconFactory wp = new IconFactory(wEnv.dsTS, Constants.deliverySpotCoords[0], Constants.deliverySpotCoords[1], wEnv.width, wEnv.height);
                    wEnv.addDSIcon(wp);
                }
            }
        });
        JMenuItem runSimulationItem = new JMenuItem("Start the Game");
        runSimulationItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                i.ep.getEnvironment().startTheGame(true); //true
            }
        });

        mbar.add(makeMenu(fileMenu,
                new Object[]{newItem, openItem, saveItem, null, null, exitItem}, this));
        creatureScoreItem = createScoreSubmenu(creatureScoreItem);
        mbar.add(makeMenu(optionMenu,
                new Object[]{
                    creatureScoreItem,
                    deliverySpotItem, 
                },
                this));
        mbar.add(makeMenu(runMenu,
                new Object[]{runSimulationItem,
                },
                this));
        jdp.add(mbar);
    }

    public static JMenu makeMenu(Object parent, Object[] items, Object target) {
        JMenu m = null;
        if (parent instanceof JMenu) {
            m = (JMenu) parent;
        } else if (parent instanceof String) {
            m = new JMenu((String) parent);
        } else {
            return null;
        }
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                m.addSeparator();
            } else {
                m.add(makeMenuItem(items[i], target));
            }
        }
        return m;
    }

    public static JMenuItem makeMenuItem(Object item, Object target) {
        JMenuItem r = null;
        if (item instanceof String) {
            r = new JMenuItem((String) item);
        } else if (item instanceof JMenuItem) {
            r = (JMenuItem) item;
        } else {
            return null;
        }

        if (target instanceof ActionListener) {
            r.addActionListener((ActionListener) target);
        }
        return r;
    }

    /**
     * Gets called every time the game state manager switches to this game state.
     * Sets the window title.
     */
    public void onActivate() {
        DisplaySystem.getDisplaySystem().
                setTitle("WorldServer 3D - Copyright @ Gudwin Soft");
        super.onActivate();
    }

    /**
     * Gets called from super constructor. Sets up the input handler that let
     * us walk around using the w,s,a,d keys and mouse.
     */
    /**
     * - SPACE enable / disable the GUI GameState
     * - ESC to quit the game
     * - TAB to score of score game state
     */
    private void initInput() {
        input = new KeyboardWorldInputHandler(cam, 10, 1);
//	    // Bind the exit action to the escape key.
//	    KeyBindingManager.getKeyBindingManager().set(
//	        "exit",
//	        KeyInput.KEY_ESCAPE);
        MouseInputListener3D mil = new MouseInputListener3D(m);
        MouseInput.get().addListener(mil);

        input.addAction(new InputAction() {

            public void performAction(InputActionEvent evt) {
                if (evt.getTriggerPressed()) {
                    GameStateManager.getInstance().deactivateAllChildren();
                    game.finish();
                }
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_ESCAPE, InputHandler.AXIS_NONE, false);

    }

    protected void stateUpdate(float tpf) {

        wEnv = i.ep.getEnvironment();
        input.update(tpf);
        try {
            /**
             * New/modified creatures are rendered:
             */
                for (Iterator<Creature> iter = wEnv.getCpoolModified().iterator(); iter.hasNext();) {
                    Creature c = iter.next();
                    if (!robotThingsRN.hasChild(c.shape)) {
                        c.shape.setRenderState(dms);
                        c.shape.setRenderState(wEnv.ls);

                        c.shape.updateRenderState();
                        robotThingsRN.attachChild(c.shape);

                    }
                    iter.remove();
                }

            /**
             * New obstacles/objects are rendered:
             */
            synchronized (wEnv.semaphore) {
                for (Iterator<Thing> iter = wEnv.getOpoolModified().iterator(); iter.hasNext();) {
                    Thing o = iter.next();
                    o.initPlace();
                    o.shape.setLocalTranslation(new Vector3f((float) ((o.getX2() + o.getX1()) / 20 - wEnv.width / 20), (float) o.getZ() + o.getDepth(), (float) (((o.getY2() + o.getY1()) / 20) - wEnv.height / 20)));

                    if (!robotThingsRN.hasChild(o.shape)) {
                        o.shape.setRenderState(o.ms);
                        o.shape.setRenderState(wEnv.ls);
                        robotThingsRN.attachChild(o.shape);
                        o.shape.updateRenderState();
                    }
                    iter.remove();
                }
            }

        
            //RememberMeIcon:
            for (Node arrow : wEnv.rmiPool) {
                ThingsRN.attachChild(arrow);
            }
            //WaypointIcon:
            for (Node arrow : wEnv.wpPool) {
                ThingsRN.attachChild(arrow);
            }
            //Delivery spot:
            for (Node ds : wEnv.dsPool) {
                robotThingsRN.attachChild(ds);
            }

        } catch (Exception ev) {
            log.severe("StateUpdate error while building the scene.");
            ev.printStackTrace();
        }

        /**
         * Remove deleted objects and creatures from the scene:
         */
        for (Thing dob : wEnv.deletelist) {
            if (ThingsRN.hasChild(dob.shape)) {
                ThingsRN.detachChild(dob.shape);
            }
            if (ThingsRN.hasChild(dob.arrow)) {
                ThingsRN.detachChild(dob.arrow);
            }

            if (robotThingsRN.hasChild(dob.shape)) {
                robotThingsRN.detachChild(dob.shape);
            }
        }
        wEnv.deletelist.removeAll(wEnv.deletelist);

            for (Node arrow : wEnv.deleteArrowDSlist) {
                if (ThingsRN.hasChild(arrow)) {
                    ThingsRN.detachChild(arrow);
                }else if (robotThingsRN.hasChild(arrow)) {
                    robotThingsRN.detachChild(arrow);
                }
            }
            wEnv.deleteArrowDSlist.removeAll(wEnv.deleteArrowDSlist);
            
            
        /**
         * Update cameras:
         */
        wEnv.rcnEven = rcnEven;
        wEnv.rcnOdd = rcnOdd;

        synchronized (wEnv.semaphore3) {
        if (wEnv.getCpool().size() == 0 || wEnv.rcnEven.robot_view == -1) {
            wEnv.rcnEven.setRobot(-1);
        } else {
            wEnv.rcnEven.setPosition(wEnv, true);
        }
        if (wEnv.getCpool().size() == 0 || wEnv.rcnOdd.robot_view == -1) {
            wEnv.rcnOdd.setRobot(-1);
        } else {
            wEnv.rcnOdd.setPosition(wEnv, false);
        }
        //visual cameras setup:
        wEnv.rcnEven.setRobot(wEnv.getCamera(0));
        wEnv.rcnOdd.setRobot(wEnv.getCamera(1));
        }
        for (Node arrow : wEnv.rmiPool) {
            if (robotThingsRN.hasChild(arrow)) {
                robotThingsRN.detachChild(arrow);
            }
        }
        
        synchronized (wEnv.semaphore3) {
        //creature movement:
        for (Creature c : wEnv.getCpool()) {
            boolean coll = checkIfCreatureHasCollided(c);

            if (((c.draggedState) || (!coll) || (c.getReverseMode()))) {
                if ((c.draggedState) || (c.hasStarted)) { //states where the following transformations are primarily required
                    if ((c.hasStarted) && (c.getFuel() > 0)) {
                        c.move(wEnv);
                    }

                }

                float newx = (float) c.getX() / 10 - wEnv.width / 20;
                float newy = (float) c.getY() / 10 - wEnv.height / 20;
                c.shape.setLocalTranslation(newx, 0f, newy);

                Quaternion q1 = new Quaternion();

                q1.fromAngles(0f, -(float) c.getPitch() * 3.14159265f / 180, 0f);
                
                c.shape.getLocalRotation().slerp(q1, 0.1f);

            }
        }
        robotThingsRN.updateModelBound();
        robotThingsRN.updateRenderState();

        ThingsRN.updateModelBound();
        ThingsRN.updateRenderState();
        robotThingsRN.updateWorldBound();
        wEnv.rcnEven.render(tpf, robotThingsRN);
        wEnv.rcnOdd.render(tpf, robotThingsRN);
        
        rootNode.updateWorldBound();
        rootNode.updateWorldData(tpf);
        
        for (Creature c : wEnv.getCpool()) {
            synchronized (c.semaphore) {
                c.setVisualSensor(new VisualSensor(c, robotThingsRN));
                c.clearThingsInCamera();
                if (c.isVisualSensorActivated) {
                    synchronized (wEnv.semaphore2) {
                        for (Thing o : wEnv.getOpool()) {
                            if (!o.wasHidden) {

                                if (c.getVisualSensor().returnIfCaptured(o, wEnv)) {
                                    c.addToThingsInCamera(o);
                                }
                                //c.getVisualSensor().cameraTest(o);

                            }
                        }
                    }
                    //add other creatures in game (except the current creature)
                    for (Thing oc : wEnv.getCreaturesExceptMe(c)) {

                        if (c.getVisualSensor().returnIfCaptured(oc, wEnv)) {
                            c.addToThingsInCamera(oc);
                        }

                    }
                }
            }
        }
    }
                
        
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("resetcamera", false)) {
            cam.setLocation(new Vector3f(0, 40, 60));
            cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
            cam.update();
        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("orthocamera", false)) {
            ang_row += 10;
            if (ang_row > 360) {
                ang_row = 0;
            }
            float ar = 90 * 3.14159265f / 180;
            float ap = 180 * 3.14159265f / 180;
            float ay = 0 * 3.14159265f / 180;
            qKey.fromAngles(ar, ap, ay);
            cam.setFrame(new Vector3f(0, 80, 0), qKey);
            cam.update();
        }
        float delta = 0.5f;
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("xm", false)) {

            Vector3f location = cam.getLocation();
            Vector3f direction = cam.getDirection();
            log.info("camera:" + cam.getLocation() + cam.getDirection());
            cam.setLocation(new Vector3f(location.x - delta, location.y, location.z));
            cam.setDirection(new Vector3f(direction.x, direction.y, direction.z));
        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("xM", false)) {

            Vector3f location = cam.getLocation();
            Vector3f direction = cam.getDirection();
            log.info("camera:" + cam.getLocation() + cam.getDirection());
            cam.setLocation(new Vector3f(location.x + delta, location.y, location.z));
            cam.setDirection(new Vector3f(direction.x, direction.y, direction.z));

        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("ym", false)) {

            Vector3f location = cam.getLocation();
            Vector3f direction = cam.getDirection();
            log.info("camera:" + cam.getLocation() + cam.getDirection());
            cam.setLocation(new Vector3f(location.x, location.y - delta, location.z));
            cam.setDirection(new Vector3f(direction.x, direction.y, direction.z));
        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("yM", false)) {

            Vector3f location = cam.getLocation();
            Vector3f direction = cam.getDirection();
            log.info("camera:" + cam.getLocation() + cam.getDirection());
            cam.setLocation(new Vector3f(location.x, location.y + delta, location.z));
            cam.setDirection(new Vector3f(direction.x, direction.y, direction.z));

        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("zm", false)) {
            Vector3f location = cam.getLocation();
            Vector3f direction = cam.getDirection();
            log.info("camera:" + cam.getLocation() + cam.getDirection());
            cam.setLocation(new Vector3f(location.x, location.y, location.z - delta));
            cam.setDirection(new Vector3f(direction.x, direction.y, direction.z));
        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("zM", false)) {
            Vector3f location = cam.getLocation();
            Vector3f direction = cam.getDirection();
            log.info("camera:" + cam.getLocation() + cam.getDirection());
            cam.setLocation(new Vector3f(location.x, location.y, location.z + delta));
            cam.setDirection(new Vector3f(direction.x, direction.y, direction.z));

        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("row", false)) {
            ang_row += 10;
            if (ang_row == 360) {
                ang_row = 0;
            }
            qKey.fromAngles(ang_row * 3.14159265f / 180, ang_pitch * 3.141592f / 180, ang_yaw * 3.141592f / 180);
            cam.setAxes(qKey);
        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("pitch", false)) {
            x_l = cam.getLocation().x;
            y_l = cam.getLocation().y;
            z_l = cam.getLocation().z;
            float L = (float) Math.sqrt(x_l * x_l + z_l * z_l);
            float ang = (float) Math.atan2(z_l, x_l);
            ang += 10f / 180f * Math.PI;
            //System.out.println("newang:"+ang);
            int mouse_x = 512;
            int mouse_y = 384;
            Vector2f mouse_xy = new Vector2f(mouse_x, mouse_y);
            Vector3f worldCoords = display.getWorldCoordinates(mouse_xy, 0);
            Vector3f worldCoords2 = display.getWorldCoordinates(mouse_xy, 1);
            Vector3f direction = worldCoords2.subtractLocal(worldCoords).normalizeLocal();
            Ray mouseRay = new Ray(worldCoords, direction);
            float planeY = mouseRay.origin.y;
            float startY = mouseRay.origin.y;
            float endY = mouseRay.direction.y;
            float coef = (planeY - startY) / endY;
            float planeX = mouseRay.origin.x + (coef * mouseRay.direction.x);
            float planeZ = mouseRay.origin.z + (coef * mouseRay.direction.z);
            mouse_x = (int) (planeX * 10) + wEnv.width / 2;
            mouse_y = (int) (planeZ * 10) + wEnv.height / 2;

            cam.setLocation(new Vector3f((float) (L * Math.cos(ang)), y_l, (float) (L * Math.sin(ang))));

            cam.lookAt(new Vector3f(planeX, 0, planeZ), new Vector3f(0, 1, 0));
        }
        if (KeyBindingManager.getKeyBindingManager().
                isValidCommand("yaw", false)) {
            ang_yaw += 10;
            if (ang_yaw == 360) {
                ang_yaw = 0;
            }
            qKey.fromAngles(ang_row * 3.14159265f / 180, ang_pitch * 3.141592f / 180, ang_yaw * 3.141592f / 180);
            cam.setAxes(qKey);
        }

    }

    public void actionPerformed(ActionEvent evt) {
        String arg = evt.getActionCommand();
        if (arg.equals(" Exit")) {
            System.exit(0);
            System.runFinalization();
            System.gc();
        } else //			codigo para abrir uma janela de arquivos;
        if (arg.equals("Open")) {
            try {
                processNewWorld();
                String fileName;
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileName = chooser.getSelectedFile().getCanonicalPath();
                    log.info("You chose to open this file: " + fileName);
                    this.readEnvDimensionsFromFile(fileName);
                    updateEnvironment(envDim[0], envDim[1], null);
                    wEnv.open(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (arg.equals(" Save")) {
            try {
                String fileName;
                JFileChooser chooser = new JFileChooser();

                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileName = chooser.getSelectedFile().getCanonicalPath();
                    log.info("You chose to save in this file: " + fileName);
                    wEnv.save(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (arg.equals(" New")) {
            processNewWorld();
            //Launch the frame to recreate the environment.
            //A new Environment object is instantiated.
            nwf.setVisible(true);
        }


    }

    private synchronized boolean checkIfCreatureHasCollided(Creature c) {
       // synchronized (wEnv.semaphore2) {
            c.collisionResults.clear();
            for (Thing th : wEnv.getEveryThingExceptMe(c)) {

                if (c.checkContactSensor(th, wEnv)) {
                    return true;
                }
            }
            return false;

       // }
    }

    public void menuSelected(MenuEvent evt) {
        //saveItem.setEnabled(!readonlyItem.isSelected());
        //saveAsItem.setEnabled(!readonlyItem.isSelected());
    }

    public void menuDeselected(MenuEvent evt) {
    }

    public void menuCanceled(MenuEvent evt) {
    }

    public JMenu createScoreSubmenu(JMenu target) {
        if (wEnv.getCpool().size() == 0) {
            this.creatureScoreItem.removeAll();
            target.add(new JMenuItem("No creatures yet!"));
        } else {
            for (Creature c : wEnv.getCpool()) {
                target.add(new JMenuItem("" + wEnv.getCpool().indexOf(c)));
            }
        }
        return target;
    }

    public void updateScoreSubmenu() {
        if (wEnv.getCpool().size() == 0) {
            this.creatureScoreItem.removeAll();
            this.creatureScoreItem.add(new JMenuItem("No creatures yet!"));
        } else {
            this.creatureScoreItem.removeAll();
            for (final Creature c : wEnv.getCpool()) {
                JMenuItem item = new JMenuItem("Creature " + wEnv.getCpool().indexOf(c));
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ev) {
                        scoreTab.setEnvironment(wEnv);
                        scoreTab.setCreature(c);
                        scoreTab.setTitle("Knapsack and Score - creature " + wEnv.getCpool().indexOf(c));
                        //scoreTab.setVisible(true);
                        scoreTab.update();
                        scoreTab.setVisible(true);
                    }
                });
                this.creatureScoreItem.add(item);
            }
        }
    }

    public void setScoreTab(KnapsackAndScoreFrame ksFrame) {
        scoreTab = ksFrame;
    }


    public void cameraTest(Thing th, Node scene) {
        try {

            //direction from the camera to the thing
            // Vector3f direction = th.shape.getLocalTranslation().subtract(e.rcnEven.getCameraNode().getLocalTranslation()).normalizeLocal();
            Vector3f direction = th.shape.getLocalTranslation().subtract(wEnv.getRobotCamera(0).getCameraNode().getLocalTranslation()).normalizeLocal();

            // check if thing is within the camera view
//            Vector3f difference =e.rcnEven.getCameraNode().getLocalRotation().getRotationColumn(2).subtract(direction);
            Vector3f difference = wEnv.getRobotCamera(0).getCameraNode().getLocalRotation().getRotationColumn(2).subtract(direction);
            if (difference.length() > 0.5f) {
                // 2   == 180°
                // 1   == 90°
                // 0.5 == 45°
                // not in camera field of view
                //System.out.println("!!!!! Food " + wEnv.getOpool().indexOf(th) + "is NOT in view");
                return;
            }

            Ray ray = new Ray(wEnv.getRobotCamera(0).getCameraNode().getLocalTranslation(), direction);

            PickResults results = new BoundingPickResults();
            //results.clear();
            results.setCheckDistance(true);
            scene.findPick(ray, results);
            if (results.getNumber() > 0) {
                for (int it = 0; it < results.getNumber(); it++) {
                    Node geom = results.getPickData(it).getTargetMesh().getParent();
                    // if (containsNode(geom, e.rcnEven.getCameraNode())) {
                    if (containsNode(geom, wEnv.getRobotCamera(0).getCameraNode())) {
                        // oops, ignore that
                        continue;
                    }
                    if (containsNode(geom, (wEnv.getCpool().get(wEnv.getCamera(0))).shape)) { //@@@@ attention for camera index
                        // oops, ignore that
                        continue;
                    }
                    if (containsNode(geom, th.shape)) {
                        // got our target
                        //System.out.println("!!!!!   I see food: " + e.opool.indexOf(th));

                        break;
                    } else {
                        // our ray hit something else than the food
                        //System.out.println("!!!!! Food " + e.opool.indexOf(th) + " is OCCLUDED!!!!");
                        //String na = new String();
                        Node aux = results.getPickData(it).getTargetMesh().getParent();
                        do {
                            //na = aux.getName();
                            //if(aux.getParent() != null)aux = aux.getParent();
                            if (aux.getParent() != null) {
                                aux = aux.getParent();
                            }

                        } while (aux.getParent() != rootNode);
                        break;
                    }

                }
            }
        } catch (Exception ev) {

            log.severe("cameraTest error...");
            ev.printStackTrace();
        }
    }

    public boolean containsNode(Node toCheck, Node toFind) {
        if (toCheck == toFind) {
            return true;
        }
        if (toCheck.getParent() != null) {
            return containsNode(toCheck.getParent(), toFind);
        } else {
            return false;
        }

    }

    public String getNodeName(Node aux) {
        String na = new String();
        do {
            na = aux.getName();
            if (aux.getParent() != null) {
                aux = aux.getParent();
            }
        } while (aux.getParent() != rootNode);

        return na;
    }

    private void initCameras() {
        rcnEven = new RobotCamera(DisplaySystem.getDisplaySystem().getWidth() - 125, 643, wEnv.width, wEnv.height);
        rcnEven.getMonitorNode().setRenderQueueMode(Renderer.QUEUE_ORTHO);
        this.getRootNode().attachChild(rcnEven.getMonitorNode());
        rcnEven.setRCNodeName("Even Camera");

        rcnOdd = new RobotCamera(DisplaySystem.getDisplaySystem().getWidth() - 380, 643, wEnv.width, wEnv.height);
        rcnOdd.getMonitorNode().setRenderQueueMode(Renderer.QUEUE_ORTHO);
        this.getRootNode().attachChild(rcnOdd.getMonitorNode());
        rcnOdd.setRCNodeName("Odd Camera");
        wEnv.rcnEven = rcnEven;
        wEnv.rcnOdd = rcnOdd;

    }
    
/*
 * This method simply prepares the environment to be completely recreated.
 * All nodes are cleaned and the cameras reset. The current Environment object
 * is subsequently instantiated again.
 */
    private void processNewWorld() {
        wEnv.startTheGame(false);
        wEnv.resetCameras();
        ThingsRN.detachAllChildren(); //Quad is atached only to robotThingsRN
        detachEntitiesChildren(robotThingsRN);
        wEnv.rcnEven.robot_view= -1;
        wEnv.rcnOdd.robot_view= -1;
        wEnv.getCpool().clear();
        wEnv.getOpool().clear();
        wEnv.thingMap.clear();
        wEnv.wpPool.removeAll(wEnv.wpPool);
        wEnv.wpNdsPoolMap.clear();
        wEnv.rmiPool.removeAll(wEnv.rmiPool);
        ThingsRN.updateWorldData(0);
        wEnv.notifyNumberOfCreatureObservers();
    }
    public void resetWorld(){
        wEnv.deleteAllThing();
    }
    
    //detach all rootNodes children excep the "floor" Quad:

    private void detachEntitiesChildren(Node node) {
        node.detachAllChildren();
        node.attachChild(floorQuad);//floor
    }

    
    public Node getTheRootNode() {

        return rootNode;
    }

    public void updateEnvironment( int width, int height, String textureFloor) {

        nwf.setDimensions(width, height);
        nwf.updateQuadSettings();

        if(textureFloor != null){
            setFloorTexture(textureFloor);
        }
    }

    public void setFloorTexture(String pathToTexture) {
        TextureState ts = display.getRenderer().createTextureState();
        Texture texture =
                TextureManager.loadTexture(
                SimulationGameState.class.getClassLoader().getResource(
                pathToTexture),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        texture.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(texture);
        ts.setEnabled(true);
        floorQuad.setRenderState(ts);
    }

    public void readEnvDimensionsFromFile(String fileName) {
        ReadFromXMLFile reader = new ReadFromXMLFile(fileName);
        envDim = reader.readDimFromFile();
        log.info("SGS::envDim: "+" "+envDim[0]+" "+envDim[1]);
    }

    class ScoreMenuUpdater implements Observer {

        public void update(Observable arg0, Object arg1) {
            updateScoreSubmenu();
        }
    }
}
