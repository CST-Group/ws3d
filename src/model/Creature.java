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

/**
 * @author Ricardo R. Gudwin
 * @author eccastro
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import com.jme.intersection.CollisionResults;
import com.jme.intersection.BoundingCollisionResults;
import com.jme.math.Quaternion;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import motorcontrol.CreatureKinematicsInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Logger;
import util.Constants;

public abstract class Creature extends Thing {
    
    Logger log = Logger.getLogger(Creature.class.getCanonicalName());
    private int period = 10;  //in seconds
    private int delayPeriod = 60;

    protected double vr, vl, w, xd, yd;
    /**
     * Instantiate according to the type of creature. For instance a car-like
     * creature (with two front driven wheels), a robot (two-wheeled creature
     * using differential drive approach, such as a pioneer P3-DX) etc.
     */
    //protected int motorSystem = 2; //1- car-like, 2- two-wheeled differential steering

    public ContactSensor contactSensors[];
    public CameraNode cn;
    protected double wheel;
    protected double speed;
    protected double fuel;
    protected double serotonin;
    protected double endorphine;
    protected double friction;
    //public double M_PI_2 = Math.PI / 2;
    public boolean hasStarted = false;
    //protected Environment e;
    public Knapsack sack;
    protected VisualSensor visualSensor;
    public boolean isVisualSensorActivated = false;

    public Thing collidedWithThing = null;
    public boolean draggedState = false; //e.g. user is dragging robot using a mouse
    private String thingInHandsName = "None";
    private List<Leaflet> myActiveLeaflets = new ArrayList();

    private Vector<Thing> thingsInCamera = new Vector<Thing>();
    private Timer energyTimer;
    private Timer serotoninTimer;
    private Timer endorphineTimer;
    protected CreatureKinematicsInterface kinematics;
    final public Creature semaphore = this;
    public CollisionResults collisionResults;
    public CreatureEnergyNotifier fuelNotifier;
    public CreatureSerotoninNotifier serotoninNotifier;
    public CreatureEndorphineNotifier endorphineNotifier;
    public CreatureLeafletNotifier leafletNotifier;

    private int hasLeaflet = 0; //false

    private Thing closestInVision = null;

    protected int hasCollided = 0;
    protected boolean reverseMode = false;
    
    protected ContactSensor handsActionSensor;
            

    public Creature(double initialX, double initialY, double iPitch, Environment env) {
        super(initialX, initialY, env);
        this.category = Constants.categoryCREATURE; //yes I'm a creature!!!
        //Generate object ID bsed on timestamp:
        ID = new Long(System.currentTimeMillis());
        /* Initialize car position */
        x1 = comX - (Constants.CREATURE_SIZE/2);
        y1 = comY - (Constants.CREATURE_SIZE/2);
        x2 = comX + (Constants.CREATURE_SIZE/2);
        y2 = comY + (Constants.CREATURE_SIZE/2);

        sack = new Knapsack();
        /* Initialize car properties */
        pitch = iPitch;
        wheel = 0.0;
        speed = 0.0;

        fuel = Constants.CREATURE_MAX_FUEL;
        serotonin = Constants.CREATURE_MAX_SEROTONIN;
        endorphine = Constants.CREATURE_MAX_ENDORPHINE;
        
        fuelNotifier = new CreatureEnergyNotifier();
        serotoninNotifier = new CreatureSerotoninNotifier();
        endorphineNotifier = new CreatureEndorphineNotifier();
        leafletNotifier = new CreatureLeafletNotifier();
        //friction = 0.0;

        /* Initialize contact sensors */
        contactSensors = new ContactSensor[4];
        for (int index = 0; index < 4; ++index) {
            contactSensors[index] = new ContactSensor(this);
        }
        updateContactSensorPosition();

        collisionResults = new BoundingCollisionResults();
        setMaterial(new Material3D(null));
        startEnergyCycle();
        startSerotoninCycle();
        startEndorphineCycle();
        
        affordances = new ArrayList<Integer>();
        affordances.add(Constants.Affordance__VIEWABLE);
        
        handsActionSensor = new ContactSensor(this);
        
    }

    public Creature(double initialX, double initialY, double iPitch, Environment env, int motorSys) {
        this(initialX, initialY, iPitch, env);
        if ((motorSys == 1) || (motorSys == 2)) {
            this.motorSystem = motorSys;
        } //else default is 2.
    }

    abstract public void updateMyPosition(); //synchronized
//	public double getX() { return x; }
//	public double getY() { return y; }

//	public double getPitch() { return pitch; } //moved to class Thing
//    public CreatureKinematicsInterface getKinematics() { return kinematics; }
    public double getWheel() {
        return wheel;
    }

    public int getMotorSys() {
        return motorSystem;
    }

    ;

    public double getSpeed() {
        return speed;
    }
    //public double getRotation() { return rotation; }

    public double getVright() {
        return vr;
    }

    public double getVleft() {
        return vl;
    }

    public synchronized int getFuel() {
        return (int) fuel;
    }

    public synchronized int getSerotonin() {
        return (int) serotonin;
    }

    public synchronized int getEndorphine() {
        return (int) endorphine;
    }

    public synchronized double getFriction() {
        return friction;
    }

    public ContactSensor getContactSensor(int i) {
        return contactSensors[i];
    }

    public void setWheel(double wheel) {
        this.wheel = wheel;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public synchronized void setReverseMode(boolean b){
        this.reverseMode = b;
    }
    
     public synchronized boolean getReverseMode(){
        return this.reverseMode;
    }

    //    public void setRotation(double rot) { this.rotation = rot; }
    public void setVright(double v) {
        this.vr = v;
    }

    public void setVleft(double v) {
        this.vl = v;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getW() {
        return this.w;
    }

//    public void setXd(double xd) {
//        this.xd = xd;
//    }
//
//    public double getXd() {
//        return this.xd;
//    }
//
//    public void setYd(double yd) {
//        this.yd = yd;
//    }
//
//    public double getYd() {
//        return this.yd;
//    }

    public synchronized void setFuel(double fuel) {
        if (fuel <= 0) {
            this.fuel = 0;
            stopEnergyCycle();
        } else {
            this.fuel = fuel;
        }
        fuelNotifier.changed();

    }

    public synchronized void setSerotonin(double ser) {
        if (ser < 0) {
            stopSerotoninCycle();
        } else {
            this.serotonin = ser;
        }
        serotoninNotifier.changed();

    }

    public synchronized void setEndorphine(double endor) {
        if (endor < 0) {
            stopEndorphineCycle();
        } else {
            this.endorphine = endor;
        }
        endorphineNotifier.changed();

    }

    public boolean ifHasAnyLeaflet() {
        if (this.hasLeaflet == 0) return false;
        else return true;
    }
        
    public int ifHasActiveLeaflet() {
        if (this.myActiveLeaflets.isEmpty()) {
            return 0;
        } else {
            return 1;
        }
    }

   /* private void deleteFromActiveLeaflets(Long ID) {
        myActiveLeaflets.remove(ID);
    }*/
    
    private void deleteFromActiveLeaflets(Leaflet l) {
        myActiveLeaflets.remove(l);
        if (myActiveLeaflets.isEmpty()) {
            this.hasLeaflet = 0;
        }
    }

    public synchronized void setFriction(double friction) {
        //System.out.println("---setFriction  to: "+friction);
        this.friction = friction;
    }

    public void setVisualSensor(VisualSensor vs) {
        this.visualSensor = vs;
    }

    public VisualSensor getVisualSensor() {
        return this.visualSensor;
    }

    public synchronized void setX(double x) {
        this.comX = x;

        this.x1 = comX - (Constants.CREATURE_SIZE / 2);
        this.x2 = comX + (Constants.CREATURE_SIZE / 2);

        updateContactSensorPosition();
        updateVisualSensorPosition();
    }

    public synchronized void setY(double y) {
        this.comY = y;
        this.y1 = comY - (Constants.CREATURE_SIZE / 2);
        this.y2 = comY + (Constants.CREATURE_SIZE / 2);

        updateContactSensorPosition();
        updateVisualSensorPosition();
    }

    public synchronized void setPitch(double pitch) //moved to class Thing
    {
        this.pitch = pitch;

        updateContactSensorPosition();
        updateVisualSensorPosition();
    }

    //TODO consider changing it in WS3D
    public boolean pointContains(double pointX, double pointY) {
        //stuff from old time
        double SIZE = 50.0;
        double POINT_DISTANCE = 0.35 * SIZE;
        double POINT_SIZE = 0.1 * SIZE;
        double zoom = 1.0;

        return (Math.abs(comX + zoom * POINT_DISTANCE * Math.cos(Math.toRadians(pitch)) - pointX) < zoom * POINT_SIZE / 2
                && Math.abs(comY + zoom * POINT_DISTANCE * Math.sin(Math.toRadians(pitch)) - pointY) < zoom * POINT_SIZE / 2)
                ? true : false;
    }

    //TODO consider changing it in WS3D. But other methods such as mouseDragged must be changed as well.
    public boolean contains(double pointX, double pointY) {
        return ((pointX - contactSensors[0].getX()) * (contactSensors[1].getY() - contactSensors[0].getY()) - (contactSensors[1].getX() - contactSensors[0].getX()) * (pointY - contactSensors[0].getY()) < 0.0
                && (pointX - contactSensors[1].getX()) * (contactSensors[2].getY() - contactSensors[1].getY()) - (contactSensors[2].getX() - contactSensors[1].getX()) * (pointY - contactSensors[1].getY()) < 0.0
                && (pointX - contactSensors[2].getX()) * (contactSensors[3].getY() - contactSensors[2].getY()) - (contactSensors[3].getX() - contactSensors[2].getX()) * (pointY - contactSensors[2].getY()) < 0.0
                && (pointX - contactSensors[3].getX()) * (contactSensors[0].getY() - contactSensors[3].getY()) - (contactSensors[0].getX() - contactSensors[3].getX()) * (pointY - contactSensors[3].getY()) < 0.0)
                ? true : false;
    }

    public synchronized void updateObservers(Environment e) {
        updateContactSensor(e);
        updateVisualSensor(e);

        setChanged();
        notifyObservers();
    }

    public synchronized void moveTo(double dx, double dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
        comX += dx;
        comY += dy;

        if (!e.getCpoolModified().contains(this)) {
            e.getCpoolModified().add(this);
        }
        updateContactSensorPosition();
        updateVisualSensorPosition();

        setChanged();

    }

    /**
     * This method is used in the version of WS3D where each "step" of the
     * creature is directly called in the stateUpdate method of
     * SimulationGameState. This version allows the start/stop of the motion
     * system of each creature independently.
     *
     * @param e
     */
    public synchronized void move(Environment e) {
        if ((this.hasStarted) && (getFuel() > 0)) {

            updateMyPosition();

        }

        if (comX > e.width) {
            comX = e.width;

        }
        if (comX < 0) {
            comX = 0;

        }

        if (comY > e.height) {
            comY = e.height;

        }
        if (comY < 0) {
            comY = 0;

        }
        updateContactSensor(e);
        updateVisualSensor(e);

        setChanged();
        notifyObservers();
    }

    /**
     * Currently not in use anymore.
     *
     * @param dTime
     * @param e
     */
    public synchronized void move(double dTime, Environment e) {
        if ((this.hasStarted) && (getFuel() > 0)) {

            updateMyPosition();

        }

        if (comX > e.width) {
            comX = e.width;

        }
        if (comX < 0) {
            comX = 0;

        }

        if (comY > e.height) {
            comY = e.height;

        }
        if (comY < 0) {
            comY = 0;

        }
        updateContactSensor(e);
        updateVisualSensor(e);

//        updateXnYn(x,y);
        setChanged();
        notifyObservers();
    }

    public synchronized void rotate(double pointX, double pointY) {
        pitch = Math.toDegrees(Math.atan2(pointY - comY, pointX - comX));

        updateContactSensorPosition();
        updateVisualSensorPosition();

        setChanged();
        notifyObservers();
    }

    public synchronized void updateVisualSensor(Environment e) {
    }

    /**
     * Did not remove from code completely --> it is current in use! But
     * consider improve it to fully port to 3D.
     *
     * @param e
     */
    public synchronized void updateContactSensor(Environment e) {
        updateContactSensorPosition();

        for (int it = 0; it < 4; ++it) {
            contactSensors[it].setThing(collidedWithThing);
        }
    }

    public synchronized void updateVisualSensorPosition() {
    }

    public void updateContactSensorPosition() {
        //stuff from old times
        double zoom = 1.0;
        double SIZE = 50.0;
        double WIDTH = 0.43 * SIZE;
        double LENGTH = 0.43 * SIZE;

        double x0 = -zoom * LENGTH / 2, y0 = -zoom * WIDTH / 2;
        double x1_ = +zoom * LENGTH / 2, y1_ = +zoom * WIDTH / 2;

        // Coordenandas dos sensores antes da rota��o
        // sensor0 = (x0,y0); sensor1 = (x1,y0); sensor2 = (x1,y1); sensor3 = (x0,y1)
        // Rota��o o ponto
        double cosPitch = Math.cos(Math.toRadians(pitch));
        double sinPitch = Math.sin(Math.toRadians(pitch));

        contactSensors[0].setX(comX + x0 * cosPitch - y0 * sinPitch);
        contactSensors[0].setY(comY + x0 * sinPitch + y0 * cosPitch);
        contactSensors[1].setX(comX + x1_ * cosPitch - y0 * sinPitch);
        contactSensors[1].setY(comY + x1_ * sinPitch + y0 * cosPitch);
        contactSensors[2].setX(comX + x1_ * cosPitch - y1_ * sinPitch);
        contactSensors[2].setY(comY + x1_ * sinPitch + y1_ * cosPitch);
        contactSensors[3].setX(comX + x0 * cosPitch - y1_ * sinPitch);
        contactSensors[3].setY(comY + x0 * sinPitch + y1_ * cosPitch);
    }

    //TODO: consider removing it in WS3D
    public void draw(Graphics2D g2D, boolean contactPanel) {
        //stuff from old times
        double SIZE = 50.0;
        double POINT_DISTANCE = 0.35 * SIZE;
        double zoom = 1.0;
        double WIDTH = 0.43 * SIZE;
        double LENGTH = 0.43 * SIZE;
        double CS_SIZE = 7.0;
        double vsSquareX, vsSquareY;
        double VS_DISTANCE_MIN = 10.0;
        double VS_DISTANCE_MAX = 150.0;
        double vsDistance = (VS_DISTANCE_MIN + VS_DISTANCE_MAX) / 2;
        double VS_ANGLE_MIN = -60.0;
        double VS_ANGLE_MAX = 60.0;
        double vsAngle = (VS_ANGLE_MIN + VS_ANGLE_MAX) / 2;

        vsSquareX = comX + zoom * vsDistance * Math.cos(Math.toRadians(pitch + vsAngle));
        vsSquareY = comY + zoom * vsDistance * Math.sin(Math.toRadians(pitch + vsAngle));

        //System.out.println ("transform: x:" + x + " y:" + y + " ang:" + pitch + " zoom:" + zoom);
        AffineTransform transform = new AffineTransform();
        if (!contactPanel) {
            transform.translate(comX, comY);
        }
        if (!contactPanel) {
            transform.scale(zoom, zoom);
        }
        transform.rotate(Math.toRadians(pitch), 0, 0);

        AffineTransform tyreFLTransform = new AffineTransform();
        tyreFLTransform.translate(0.24 * SIZE, +0.155 * SIZE);
        tyreFLTransform.rotate(Math.toRadians(wheel), 0, 0);

        AffineTransform tyreFRTransform = new AffineTransform();
        tyreFRTransform.translate(0.24 * SIZE, -0.155 * SIZE);
        tyreFRTransform.rotate(Math.toRadians(wheel), 0, 0);


        /* Define car objects */
        Area tyreBL = new Area(new Rectangle2D.Double(-0.30 * SIZE, -0.190 * SIZE, +0.10 * SIZE, 0.08 * SIZE));
        tyreBL.transform(transform);
        Area tyreBR = new Area(new Rectangle2D.Double(-0.30 * SIZE, +0.110 * SIZE, +0.10 * SIZE, 0.08 * SIZE));
        tyreBR.transform(transform);
        Area tyreFL = new Area(new Rectangle2D.Double(-0.05 * SIZE, -0.035 * SIZE, +0.10 * SIZE, 0.07 * SIZE));
        tyreFL.transform(tyreFLTransform);
        tyreFL.transform(transform);
        Area tyreFR = new Area(new Rectangle2D.Double(-0.05 * SIZE, -0.035 * SIZE, +0.10 * SIZE, 0.07 * SIZE));
        tyreFR.transform(tyreFRTransform);
        tyreFR.transform(transform);

        Area airfoilDetail = new Area(new Rectangle2D.Double(-0.40 * SIZE, -0.12 * SIZE, 0.04 * SIZE, 0.24 * SIZE));
        airfoilDetail.transform(transform);
        Area airfoilBase = new Area(new Rectangle2D.Double(-0.36 * SIZE, -0.12 * SIZE, 0.04 * SIZE, 0.24 * SIZE));
        airfoilBase.transform(transform);
        Area airfoilSupport = new Area(new Rectangle2D.Double(-0.33 * SIZE, -0.04 * SIZE, 0.08 * SIZE, 0.08 * SIZE));
        airfoilSupport.transform(transform);

        Area cockpit = new Area(new Ellipse2D.Double(-0.08 * SIZE, -0.06 * SIZE, 0.20 * SIZE, 0.12 * SIZE));
        cockpit.transform(transform);
        Area pilot = new Area(new Ellipse2D.Double(-0.04 * SIZE, -0.04 * SIZE, 0.08 * SIZE, 0.08 * SIZE));
        pilot.transform(transform);

        Area nose = new Area(new QuadCurve2D.Double(+0.10 * SIZE, -0.05 * SIZE, +0.70 * SIZE, 0.0, +0.10 * SIZE, +0.05 * SIZE));
        nose.transform(transform);
        Area leftWing = new Area(new Rectangle2D.Double(+0.32 * SIZE, -0.13 * SIZE, 0.06 * SIZE, 0.13 * SIZE));
        leftWing.transform(transform);
        Area rghtWing = new Area(new Rectangle2D.Double(+0.32 * SIZE, +0.00 * SIZE, 0.06 * SIZE, 0.13 * SIZE));
        rghtWing.transform(transform);
        Area leftBorder = new Area(new Rectangle2D.Double(+0.30 * SIZE, -0.14 * SIZE, 0.10 * SIZE, 0.02 * SIZE));
        leftBorder.transform(transform);
        Area rghtBorder = new Area(new Rectangle2D.Double(+0.30 * SIZE, +0.12 * SIZE, 0.10 * SIZE, 0.02 * SIZE));
        rghtBorder.transform(transform);

        Area chassis1 = new Area(new RoundRectangle2D.Double(-0.19 * SIZE, -0.14 * SIZE, 0.24 * SIZE, 0.28 * SIZE, 0.00 * SIZE, 0.00 * SIZE));
        chassis1.transform(transform);
        Area chassis2 = new Area(new QuadCurve2D.Double(+0.05 * SIZE, -0.14 * SIZE, +0.12 * SIZE, 0.0, +0.05 * SIZE, +0.14 * SIZE));
        chassis2.transform(transform);
        Area chassis3 = new Area(new QuadCurve2D.Double(+0.05 * SIZE, -0.09 * SIZE, +0.25 * SIZE, 0.0, +0.05 * SIZE, +0.09 * SIZE));
        chassis3.transform(transform);
        Area chassis4 = new Area(new QuadCurve2D.Double(-0.18 * SIZE, -0.11 * SIZE, -0.33 * SIZE, 0.0, -0.18 * SIZE, +0.11 * SIZE));
        chassis4.transform(transform);

        Area rearAxle = new Area(new Rectangle2D.Double(-0.26 * SIZE, -0.12 * SIZE, 0.021 * SIZE, 0.24 * SIZE));
        rearAxle.transform(transform);
        Area frntAxle = new Area(new Rectangle2D.Double(+0.23 * SIZE, -0.13 * SIZE, 0.021 * SIZE, 0.26 * SIZE));
        frntAxle.transform(transform);

        Area contour = new Area(new Rectangle2D.Double(-LENGTH / 2, -WIDTH / 2, LENGTH, WIDTH));
        contour.transform(transform);
        Area vsSquare = new Area(new Rectangle2D.Double(vsSquareX - zoom * SIZE / 2, vsSquareY - zoom * SIZE / 2, zoom * SIZE, zoom * SIZE));

        Area cSensors[] = new Area[4];
        cSensors[0] = new Area(new Ellipse2D.Double(-LENGTH / 2 - CS_SIZE / 2, -WIDTH / 2 - CS_SIZE / 2, CS_SIZE, CS_SIZE));
        cSensors[0].transform(transform);
        cSensors[1] = new Area(new Ellipse2D.Double(LENGTH / 2 - CS_SIZE / 2, -WIDTH / 2 - CS_SIZE / 2, CS_SIZE, CS_SIZE));
        cSensors[1].transform(transform);
        cSensors[2] = new Area(new Ellipse2D.Double(LENGTH / 2 - CS_SIZE / 2, WIDTH / 2 - CS_SIZE / 2, CS_SIZE, CS_SIZE));
        cSensors[2].transform(transform);
        cSensors[3] = new Area(new Ellipse2D.Double(-LENGTH / 2 - CS_SIZE / 2, WIDTH / 2 - CS_SIZE / 2, CS_SIZE, CS_SIZE));
        cSensors[3].transform(transform);


        /* Draw axles, and support of airfoil */
        g2D.setColor(Color.black);
        g2D.fill(rearAxle);
        g2D.fill(frntAxle);
        g2D.fill(airfoilSupport);

        /* Draw detail of airfoil and wings */
        g2D.setColor(new Color(0.8f, 0.8f, 0.8f));
        g2D.fill(airfoilDetail);
        g2D.fill(leftWing);
        g2D.fill(rghtWing);

        /* Draw airfoil, borders, nose, and chassis parts */
        g2D.setColor(new Color(0.8f, 0.1f, 0.1f));
        g2D.fill(airfoilBase);
        g2D.fill(leftBorder);
        g2D.fill(rghtBorder);
        g2D.fill(nose);
        g2D.fill(chassis1);
        g2D.fill(chassis2);
        g2D.fill(chassis3);
        g2D.fill(chassis4);

        /* Draw tyres and cockpit */
        g2D.setColor(Color.black);
        g2D.fill(tyreBL);
        g2D.fill(tyreBR);
        g2D.fill(tyreFL);
        g2D.fill(tyreFR);
        g2D.fill(cockpit);

        /* Draw pilot */
        g2D.setColor(Color.red);
        g2D.fill(pilot);

        if (!contactPanel) {
            /* Draw visual sensor square and points */
            g2D.setColor(Color.black);
            g2D.draw(vsSquare);
//			for (int i = 0; i < VS_NSQUARES; ++i)
//				for (int j = 0; j < VS_NSQUARES; ++j)
//					g2D.drawLine((int)visualSensors[i][j].getX(),(int)visualSensors[i][j].getY(),
//								 (int)visualSensors[i][j].getX(),(int)visualSensors[i][j].getY());

            /* Draw speed vector */
            g2D.setColor(Color.blue);
            g2D.drawLine((int) comX, (int) comY,
                    (int) (comX + zoom * POINT_DISTANCE * Math.cos(Math.toRadians(pitch))),
                    (int) (comY + zoom * POINT_DISTANCE * Math.sin(Math.toRadians(pitch))));
        } else {
            /* Draw contact sensor circles and contour */
            //g2D.draw(contour);
            for (int i = 0; i < 4; ++i) {
                if (contactSensors[i].getMaterial() != null) {
                } //not sure if necessairy (commented to compile)	g2D.setColor(contactSensors[i].getMaterial().getColor());
                else {
                    g2D.setColor(Color.white);
                }
                g2D.fill(cSensors[i]);

                g2D.setColor(Color.black);
                g2D.draw(cSensors[i]);
            }
        }
    }

    public synchronized void handleCollision(Thing o) {
        double hardness;
        collidedWithThing = o;
        if (o.getMaterial() != null) {
            hardness = o.getHardness();
            if (hardness == 0.0) {
                if (o.wasHidden) {
                    handleHiddenObstacleDetection(o);
                }
            }

            if ((o.getMyName().equals(this.thingInHandsName)) || (this.thingInHandsName.equals("Dropping"))) {
                //System.out.println("--->>> Collided with Thing to drop!");

            } else {
                //System.out.println("--->>> Collided but NOT with Thing to drop! " + this.thingInHandsName);
                if ((!thingInHandsName.equals("Dropping")) && (!thingInHandsName.equals("None"))) {
                    thingInHandsName = "None";
                }
                double h = o.getHardness();
                if (this.reverseMode) setFriction(0);
                else setFriction(h); //might stop the creature if necessary
                //this.putInSack(o, e); //TEST
            }

        }

    }

    public synchronized void setClosest(Thing th) {
        this.closestInVision = th;
    }

    public synchronized Thing getClosest() {
        return this.closestInVision;
    }

    public boolean checkIfHidden(Thing o) {
        return o.wasHidden;
    }

    public void handleHiddenObstacleDetection(Thing o) {

        log.severe("--->>>Hidden obstacle detected! What should I do??? <<<---");
        //unearthIt(o);
    }

    /**
     * Grasp something (jewel or food) and put it in knapsack.
     *
     * @param aThing currently support for Jewel or Food.
     */
    public void putInSack(Thing aThing, Environment e) {
        this.sack = aThing.putMeInKnapsack(this.sack);
        e.removeThing(aThing);
        setFriction(0.0);
        //TODO: check if crystal is part of leaflet
        if (aThing.category == Constants.categoryJEWEL) {
             updateActiveLeaflets(aThing, true);
            //increaseSerotonin();          
             decresedEnergy();
           
        }
        handsActionSensor.setAction(Constants.ACTION_NAME_PUTINTOBAG);
        handsActionSensor.setThing(aThing);
        setChanged();
        notifyObservers();
    }
    
    
    public void updateActiveLeaflets(Thing aThing, boolean inc) {
        String type = aThing.getMaterial().getColorName();
        for (Leaflet l : this.getActiveLeaflets()) {
            if (l.ifInLeaflet(type)) {
                if (!l.ifCompleted() && !l.ifAllCollected(type)) {
                    l.updateCollected(type, inc);

                    if (l.ifCompleted()) {
                        l.setIfCompleted(true);
                        log.info("_______Consegui completar um Leaflet!_______");
                        l.printLeafletSituation();
                    }

                    
                    break;
                }
            }
            
        }
    }
    
    
    
    
    
    public StringBuffer getSackContent(){
        return this.sack.getSackContent();
    }

    public void deliver(Leaflet leaflet) {
        //add in Factory list
        int[] aux = leaflet.getItems();

        /*String[] array = {Constants.getColorItem(aux[0]), Constants.getColorItem(aux[1]), Constants.getColorItem(aux[2])};
        for (int i = 0; i < array.length; ++i) {
            this.sack.removeJewelFromKnapsack(array[i]);
        }*/
        
        for (int i = 0; i < aux.length; i++) {
            this.sack.removeJewelFromKnapsack(Constants.getColorItem(aux[i]));
        }

        this.sack.incScore(leaflet.getPayment());
        //leaflet.setActivity(0);
        leaflet.setIfCompleted(false);
        leaflet.resetLeaflet();
        //this.deleteFromActiveLeaflets(leaflet);
        leafletNotifier.changed(leaflet.getID());
        handsActionSensor.setAction(Constants.ACTION_NAME_DELIVER);
        
    }

    /**
     * Drop the Thing that has been put into the knapsack.
     *
     * @param category of the Thing: Jewel, Non-Perishable or Perishable
     * @param color of the Jewel (any color in case of Food)
     */
    public synchronized void drop(int category, int color) {
        Long t = System.currentTimeMillis();
        e.oMsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createMaterialState());
        Thing th = null;
        this.setThingInHandsName("Dropping");

        switch (category) {

            case Constants.categoryJEWEL:
                th = new Jewel(comX, comY, e, e.oMsPool.get(t));
                ((Jewel) th).setType(Constants.translateIntoColor(Constants.getColorItem(color)));

                this.sack.removeJewelFromKnapsack(Constants.getColorItem(color));
                break;

            case Constants.categoryNPFOOD:

                e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                th = new NonPerishableFood(comX, comY, e, e.oMsPool.get(t), e.thingTsPool.get(t));
                th.setMaterial(new Material3D(ColorRGBA.orange, 1.0, 2.0, 0, e.oMsPool.get(t)));
                th.subCategory = Constants.categoryNPFOOD;
                this.sack.decNonPerishableFoodInKnapsack();
                break;

            case Constants.categoryPFOOD:
                e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                //10 minutes food
                th = new PerishableFood(Constants.VALID_PERIOD_SECS, comX, comY, e, e.oMsPool.get(t), e.thingTsPool.get(t));
                th.setMaterial(new Material3D(ColorRGBA.red, 1.0, 1.0, 0, e.oMsPool.get(t)));
                th.subCategory = Constants.categoryPFOOD;
                this.sack.decPerishableFoodInKnapsack();
                break;

        }
        int idx = e.addThing(th);
        th.setID(t + idx, e);
        this.thingInHandsName = th.getMyName();
        handsActionSensor.setAction(Constants.ACTION_NAME_DROP);
        handsActionSensor.setThing(th);
    }

    public void digAndHideIt(Thing aThing, Environment e) {
        aThing.hideMe(e);
        handsActionSensor.setAction(Constants.ACTION_NAME_HIDE);
        handsActionSensor.setThing(aThing);
    }

    public void unearthIt(Thing aThing, Environment e) {
        aThing.undoHideMe(e);
        handsActionSensor.setAction(Constants.ACTION_NAME_UNHIDE);
        handsActionSensor.setThing(aThing);
    }

    public void eatIt(Food aFood, Environment e) {
        this.haveSnack(aFood.getMaterial().getEnergy());
        e.removeThing(aFood);
        setFriction(0.0);
        handsActionSensor.setAction(Constants.ACTION_NAME_EAT);
        handsActionSensor.setThing(aFood);
        setChanged();
        notifyObservers();      
    }

    public void eatFoodInKnapsack(String nameOfFood) {
        this.sack.foodMap.remove(nameOfFood);
        if(ifNonPerishable(nameOfFood)){
            this.haveSnack(Constants.NPFOOD_ENERGY);            
        } else this.haveSnack(Constants.PFOOD_ENERGY); 
        handsActionSensor.setAction(Constants.ACTION_NAME_EAT);
        setChanged();
        notifyObservers();
    }

    public boolean ifNonPerishable(String f) {
        String np;
        np = Constants.NPFOOD_PREFIX; //non-perishable
        return f.contains(np);
    }
        
    @Override
    public void initPlace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node getNode() {
        Node nd = sf.getNode(.02f);
        String name = new String("Creature_");
        nd.setName(name.concat(this.ID.toString()));
        return nd;
    }

    @Override
    //currently not necessairy for creature
    public void setID(Long id, Environment e) {
        this.ID = id;
        String name = Constants.CREATURE_PREFIX;
        this.shape.setName(name.concat(id.toString()));
        myName = name.concat(id.toString());
        e.thingMap.put(myName, this);
    }

    public Node myLocalTransformations(Node modelw) {
        Quaternion quat90 = new Quaternion();
        quat90.fromAngles(270 * 3.141592f / 180, 3.141592f, 0f);
        modelw.setLocalRotation(quat90);
        modelw.setLocalTranslation(-7f, 2.0f, 0);     
        return modelw;
    }

    public synchronized void addToThingsInCamera(Thing o) {

        thingsInCamera.add(o);
    }

    public synchronized Vector<Thing> getThingsInCamera() {

        return thingsInCamera;
    }

    public synchronized void clearThingsInCamera() {

        thingsInCamera.clear();
    }

    public synchronized void setThingInHandsName(String n) {
        this.thingInHandsName = n;
    }

    public synchronized String getThingInHandsName() {
        return this.thingInHandsName;
    }
    
    public synchronized String getActionExecutedAndTarget() {
        String ret = this.handsActionSensor.getActionExecutedAndTarget();
        
        this.handsActionSensor.setAction("NONE"); //reset action
        return ret;
    }

    public boolean checkContactSensor(Thing eachThing, Environment e) {

        this.collisionResults.clear();
        shape.findCollisions(eachThing.shape, collisionResults);
        if (collisionResults.getNumber() > 0) {
            handleCollision(eachThing);
            if ((eachThing.getMyName().equals(this.thingInHandsName)) || (this.thingInHandsName.equals("Dropping"))) {
                hasCollided = 0;
            } else {
                hasCollided = 1;

            }

        } else {

            //not collided!!
            this.setFriction(0.0);//make creature move again (same old behaviour)
            collidedWithThing = null;
            hasCollided = 0;
        }
        updateContactSensor(e);
        if(hasCollided == 0) return false;
        else return true;
    }
    
    public int getIfCollided(){        
      return hasCollided;
    }

    public void refill() {

        fuel = Constants.CREATURE_MAX_FUEL;
        restartEnergyCycle();
        fuelNotifier.changed();

    }
    
    public void haveSnack(double snack) {

        if (snack > 0.0) {
            double sum = fuel + snack;
            fuel = (sum < Constants.CREATURE_MAX_FUEL) ? sum : Constants.CREATURE_MAX_FUEL;
            fuelNotifier.changed();
            fuelNotifier.notifyObservers();
        }
    }
    
    
    public void decresedEnergy() {
        
        log.info("***** Energy has decreased!!! *****");
        setFuel(getFuel() - Constants.CREATURE_FUEL_DEC);
        fuelNotifier.changed();
        fuelNotifier.notifyObservers();

    }

    public void increaseSerotonin() {

        serotonin = Constants.CREATURE_MAX_SEROTONIN;
        restartSerotoninCycle();
        serotoninNotifier.changed();

    }

    public void increaseEndorphine() {

        endorphine = Constants.CREATURE_MAX_ENDORPHINE;
        restartEndorphineCycle();
        endorphineNotifier.changed();

    }

    public void startEnergyCycle() {
        energyTimer = new Timer();
        energyTimer.scheduleAtFixedRate(new EnergyCycleTask(this), delayPeriod * 1000, period * 1000);
    }

    public void stopEnergyCycle() {
        energyTimer.cancel();
    }

    public void restartEnergyCycle() {
        stopEnergyCycle();
        startEnergyCycle();
    }

    public void startSerotoninCycle() {
        serotoninTimer = new Timer();
        serotoninTimer.scheduleAtFixedRate(new SerotoninCycleTask(this), delayPeriod * 1000, period * 1000);
    }

    public void stopSerotoninCycle() {
        serotoninTimer.cancel();
    }

    public void restartSerotoninCycle() {
        stopSerotoninCycle();
        startSerotoninCycle();
    }
    
    
     public void startEndorphineCycle() {
        endorphineTimer = new Timer();
        endorphineTimer.scheduleAtFixedRate(new EndorphineCycleTask(this), delayPeriod * 1000, period * 1000);
    }

    public void stopEndorphineCycle() {
        endorphineTimer.cancel();
    }

    public void restartEndorphineCycle() {
        stopEndorphineCycle();
        startEndorphineCycle();
    }

    

    public synchronized List<Leaflet> getActiveLeaflets() {

        return myActiveLeaflets;
    }

    public synchronized void setActiveLeaflets(List<Leaflet> leaflets) {
        this.myActiveLeaflets = leaflets;
        this.hasLeaflet = 1; //true
    }
    
    
    

    /**
     * This method is used when a collision between the creature and a Thing is
     * detected and a creature's specific behavior is expected.
     *
     * @param eachThing
     */
    private void doWhenCollided(Thing eachThing) {

        //Example of behavior: if the creature collides with a food,
        //this food is hidden.
        if (eachThing.category == Constants.categoryFOOD) {
            this.digAndHideIt(eachThing, e);
        }
    }

    class EnergyCycleTask extends TimerTask {

        Creature creature;

        EnergyCycleTask(Creature c) {
            this.creature = c;
        }

        public void run() {
            log.info("***** Energy has decreased!!! *****");
            setFuel(getFuel() - Constants.CREATURE_FUEL_DEC);

        }
    }

    public class CreatureEnergyNotifier extends Observable {

        public void addAnObserver(Observer ob) {
            this.addObserver(ob);
        }

        public void changed() {
            setChanged();
            notifyObservers();
        }
    }

    class SerotoninCycleTask extends TimerTask {

        Creature creature;

        SerotoninCycleTask(Creature c) {
            this.creature = c;
        }

        public void run() {
            log.info("***** Serotonin has decreased!!! *****");
            setSerotonin(getSerotonin() - Constants.CREATURE_SEROTONIN_DEC);

        }
    }

    class EndorphineCycleTask extends TimerTask {

        Creature creature;

        EndorphineCycleTask(Creature c) {
            this.creature = c;
        }

        public void run() {
            log.info("***** Endorphine has decreased!!! *****");
            setEndorphine(getEndorphine() - Constants.CREATURE_ENDORPHINE_DEC);

        }
    }
    
    

    public class CreatureSerotoninNotifier extends Observable {

        public void addAnObserver(Observer ob) {
            this.addObserver(ob);
        }

        public void changed() {
            setChanged();
            notifyObservers();
        }
    }

    public class CreatureEndorphineNotifier extends Observable {

        public void addAnObserver(Observer ob) {
            this.addObserver(ob);
        }

        public void changed() {
            setChanged();
            notifyObservers();
        }
    }
        
    public class CreatureLeafletNotifier extends Observable {

        public void addAnObserver(Observer ob) {
            this.addObserver(ob);
        }

        public void changed(Long leafletID) {
            setChanged();
            notifyObservers(leafletID);
        }
    }
}
