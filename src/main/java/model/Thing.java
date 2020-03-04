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
 *
 * @author eccastro
 */

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.scene.state.MaterialState;
import java.io.IOException;
import worldserver3d.IconFactory;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.math.Ray;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Octahedron;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.MaxToJme;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;
import util.Constants;

//public abstract class Thing extends Spatial{
public abstract class Thing extends Observable implements Savable {

    public Node shape;
    protected double x1, y1;
    protected double x2, y2;
    protected double zZ = 0.0d; //hidden obstacles have Z < 0
    protected double previousZ = 0.0d;
    protected double comX = 0.0d; //center of mass
    protected double comY = 0.0d; //center of mass
    protected double size = 0.0d; //creature matters
    protected double pitch; //creature only; other Thing objects will be -11111
    // -1 indicates that is supposed to be someTHING else than a creature
    protected int motorSystem = 2; //creature only; 1- car-like, 2- two-wheeled differential steering
    public int category = -1;
    public int subCategory = -1; //food only
    protected final double CORNER_SIZE = 4.0;
    //public boolean imADeliverySot = false;
    //protected BoundingBox boundingBox;
    protected BoundingBox boundingBox;
    //abstraction of characteristics of its composition
    protected Material3D material;
    public MaterialState ms;
    public TextureState ts;
    protected ThingShapeFactory sf;
    /**
     * This is used for Brick creation, when user is concerned with the x,y
     * location. The bricks "height" (user perspective) will be "depth" as
     * default. "Depth" is also used to adjust the local translation. See Jewel
     * and SimulationGamestate.
     */
    protected float depth = 0f; //default
    protected Environment e;
    public boolean wasHidden = false;
    public int isOccluded = 0; //false
    public Node arrow; //rememberMeIcon indicates the center position of a hidden obstacle
    protected Long ID;
    protected String myName; //set through setID
    
    protected List<Integer>affordances;
    Logger log;

    public Thing() {       //default values: SAVABLE MATTERS!!!!!!!!!!!!!!!
        log = Logger.getLogger(Thing.class.getCanonicalName());
        zZ = 0.0d;
        previousZ = 0.0d;
        comX = 0.0d;
        comY = 0.0d;
        size = 0.0d;
        pitch = Constants.PITCH_INEXISTENT; //creature only; other Thing objects will be -11111
        category = -1;
        //imADeliverySot = false;
        boundingBox = null;
        this.material = new Material3D();
        ms = null;
        //ts = null;
        depth = 0f;
        wasHidden = false;
        isOccluded = 0;
        ID = 0L;
        arrow = null;
        myName = null;
    }

    public Thing(double x, double y, Environment e) {
        this();
        comX = x;
        comY = y;
        x1 = x;
        y1 = y;
        x2 = x;
        y2 = y;
        zZ = 0.0d;
        this.e = e;
        //this.material = new Material3D();
    }

    public Thing(double x, double y, MaterialState ms, Environment ev) {

        this(x, y, ev);

        this.ms = ms;
    }
    
    public String getAffordances(){
        String aff = "";
        for (Integer i: affordances){
            aff = aff+" "+i.toString();
        }
        return aff;
    }

    public abstract void moveTo(double dx, double dy);

    public abstract void initPlace();

    public abstract Node myLocalTransformations(Node modelw);

    public abstract Knapsack putMeInKnapsack(Knapsack sack);

    /**
     * Method to redefine a shape for a Thing. Currently, not all types of Thing
     * support this operation.
     *
     * @param model path for the 3DS model file in the system
     * @param scale depends on the dimension of the 3D model image
     * @param e Environment where the Thing lists are stored
     */
    public void updateShape(String pathToModel, float scale, Environment e) {
        MaxToJme C1 = new MaxToJme();

        sf.BO = new ByteArrayOutputStream();
        URL maxFile = ThingShapeFactory.class.getClassLoader().getResource(pathToModel);
        try {
            C1.convert(new BufferedInputStream(maxFile.openStream()), sf.BO);
        } catch (IOException exc) {
            log.severe("Error in ThingShapeFactory !");
        }
        try {
            ByteArrayInputStream ModelInputStream = new ByteArrayInputStream(sf.BO.toByteArray());
            sf.modelw = (Node) BinaryImporter.getInstance().load(ModelInputStream);
            sf.modelw.setName("Model shape");

        } catch (IOException exc) {
            log.severe("Error in Thing::updateShape()");
        }
        if (scale > 0) {
            sf.modelw.setLocalScale(scale);
        }
        sf.modelw = myLocalTransformations(sf.modelw);

        sf.modelw.setRenderState(ts);

        Node shapeNode = new Node("Shape Node");
        shapeNode.attachChild(sf.modelw);
        boundingBox = new BoundingBox();
        shapeNode.setModelBound(boundingBox);
        shapeNode.updateModelBound();
        shapeNode.updateWorldBound();
        shape.detachAllChildren();
        shape = shapeNode;
        shape.updateModelBound();
        shape.updateWorldBound();
        switch (category) {

            case Constants.categoryCREATURE:
                e.setCpoolShapeModified(this);
                break;

            default:
                e.setOpoolShapeModified(this);
                break;
        }

    }

    public void hideMe(Environment e) //synchronized???
    {
        synchronized (e.semaphore) {
            log.info("====  public void hideMe() ===");
            // myGraphics2D = graphics;
            log.info("====  z= " + zZ);
            previousZ = zZ;
            wasHidden = true;
            //refresh myself
            setZ(previousZ - 5.0d);
            //Vector3f vector = new Vector3f((float)((getX2()+getX1())/20-env.width/20), 5f , (float)(((getY2()+getY1())/20)-env.height/20));
            //System.out.println("====  Obstacle:  x1= "+getX1()+" y1= "+getY1()+"x2= "+getX2()+" y2= "+getY2()+" center: "+vector.getX()+"  "+vector.getZ());
            //rememberMeIcon = new RememberMeIconFactory(ms);
            //drawRectangle(graphics);
            addRememberMeIcon(e);
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
        }
    }

    public void undoHideMe(Environment e) //synchronizedS
    {
        synchronized (e.semaphore) {
            log.info("====  public void undoHideMe() ===");
            wasHidden = false;
            //visible again
            setZ(previousZ);

            // drawRectangle(graphics);
            removeRememberMeIcon(e);
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
        }
    }

    public synchronized void addRememberMeIcon(Environment e) { //synchronized???

        IconFactory icf = new IconFactory(e.flagMS, e.width, e.height);

        arrow = icf.getRememberMeIcon(e.flagMS, (this.getX2() + this.getX1()), (this.getY2() + this.getY1()));
        arrow.setIsCollidable(false);
        arrow.setRenderState(e.flagMS);
        arrow.setRenderState(e.ls);
        e.rmiPool.add(this.arrow);
    }

    public synchronized void removeRememberMeIcon(Environment e) { //synchronized
        log.info("======= RememberMeIcon removed! ======");
        e.rmiPool.remove(this.arrow);
        e.deleteArrowDSlist.add(this.arrow);
    }

    public double getZ() {
        return zZ;
    }

    public float getDepth() {
        return depth;
    }

    public String getMyName() {
        return myName;
    }

    public void setZ(double z) {
        this.zZ = z;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public boolean returnIfWasHidden() {
        return wasHidden;
    }

    public double getX1() {

        return x1;
    }

    public double getY1() {

        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
       return y2;
    }

    public double getHeight() {
        return y2 - y1;
    }

    public double getWidth() {
        return x2 - x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public void setHeight(double height) {
        y2 = y1 + height;
    }

    public void setWidth(double width) {
        x2 = x1 + width;
    }

    public void moveX1(double dx) {
        synchronized (e.semaphore) {
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
            x1 += dx;
        }
    }

    public void moveY1(double dy) {
        synchronized (e.semaphore) {
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
            y1 += dy;
        }
    }

    public void moveX2(double dx) {
        synchronized (e.semaphore) {
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
            x2 += dx;
        }
    }

    public void moveY2(double dy) {
        synchronized (e.semaphore) {
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
            y2 += dy;
        }
    }

    public boolean contains(double x, double y) {
        int radius = 8;
        switch (category) {
            case Constants.categoryBRICK:
                return ((x >= x1 && x <= x2 || x >= x2 && x <= x1)
                        && (y >= y1 && y <= y2 || y >= y2 && y <= y1)) ? true : false;
            case Constants.categoryCREATURE:
                radius = 10;
                Rectangle2D.Double meC = new Rectangle2D.Double((int) x1 - radius, (int) y1 - radius, (radius * 2) + 1, (radius * 2) + 1);
                if (meC.contains(x, y)) {
                    return true;
                } else {
                    return false;
                }

            case Constants.categoryPFOOD:
                radius = 6;
                Ellipse2D.Double mePF = new Ellipse2D.Double((int) x1 - radius, (int) y1 - radius, (radius * 2) + 1, (radius * 2) + 1);
                if (mePF.contains(x, y)) {
                    return true;
                } else {
                    return false;
                }
            case Constants.categoryNPFOOD:
                radius = 6;
                Ellipse2D.Double meNPF = new Ellipse2D.Double((int) x1 - radius, (int) y1 - radius, (radius * 2) + 1, (radius * 2) + 1);
                if (meNPF.contains(x, y)) {
                    return true;
                } else {
                    return false;
                }
            default:
                radius = 4;
                Rectangle2D.Double meD = new Rectangle2D.Double((int) x1 - radius, (int) y1 - radius, (radius * 2) + 1, (radius * 2) + 1);
                if (meD.contains(x, y)) {
                    return true;
                } else {
                    return false;
                }
        }

    }

    public boolean contains3D(Ray mouseRay) {

        boolean ret = false;
        try {
            if (this.shape.getWorldBound() == null) {
                //System.out.println("=====contains3D:  null ==== ");
                return false;
            } else if (this.shape.getWorldBound().intersects(mouseRay)) {
                //System.out.println("=====contains3D:  YES ==== ");
                ret = true;
            } else {
                //System.out.println("=====contains3D:  NO ==== ");
                ret = false;
            }
        } catch (Exception ev) {
            log.severe("Error --- contains3D... ");
            ev.printStackTrace();
        }

        return ret;
    }

    public boolean containsX1Y1(double x, double y) {
        return (x >= x1 - CORNER_SIZE / 2 && x <= x1 + CORNER_SIZE / 2
                && y >= y1 - CORNER_SIZE / 2 && y <= y1 + CORNER_SIZE / 2) ? true : false;
    }

    public boolean containsX1Y2(double x, double y) {
        return (x >= x1 - CORNER_SIZE / 2 && x <= x1 + CORNER_SIZE / 2
                && y >= y2 - CORNER_SIZE / 2 && y <= y2 + CORNER_SIZE / 2) ? true : false;
    }

    public boolean containsX2Y1(double x, double y) {
        return (x >= x2 - CORNER_SIZE / 2 && x <= x2 + CORNER_SIZE / 2
                && y >= y1 - CORNER_SIZE / 2 && y <= y1 + CORNER_SIZE / 2) ? true : false;
    }

    public boolean containsX2Y2(double x, double y) {
        return (x >= x2 - CORNER_SIZE / 2 && x <= x2 + CORNER_SIZE / 2
                && y >= y2 - CORNER_SIZE / 2 && y <= y2 + CORNER_SIZE / 2) ? true : false;
    }

    public void revertX() {
        double temp = x1;
        x1 = x2;
        x2 = temp;
    }

    public void revertY() {
        double temp = x1;
        x1 = x2;
        x2 = temp;
    }

    public void setMaterial(Material3D material) {
        this.material = material;
    }

    public Material3D getMaterial() {
        return material;
    }

    public void renderMyMaterial() {

        this.shape.setRenderState(ms);
        this.shape.updateRenderState();
    }

    public double getHardness() {
        //System.out.println("====  getHardness ======");
        if (wasHidden) { //obstacle (x,y) is considered to calculate collision
            return 0.0;
        } else {
            //System.out.println("====  Im not hidden!!!");
            return this.material.getHardness();
        }
    }

    public abstract void setID(Long id, Environment e);

    public Long getID() {
        return this.ID;
    }

    public void setTxState(TextureState txState) {
        this.ts = txState;
    }

    public void setTexture(String pathToImageTexture) {

        this.ts.setTexture(TextureManager.loadTexture(Thing.class.getClassLoader().getResource(
                pathToImageTexture),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear));
        this.ts.setEnabled(true);

        shape.setRenderState(ts);

    }

    public void write(JMEExporter jmee) throws IOException {
        OutputCapsule capsule = jmee.getCapsule(this);
        capsule.write(shape, "shape", null);
        capsule.write(x1, "x1", 0.0d);
        capsule.write(y1, "y1", 0.0d);
        capsule.write(x2, "x2", 0.0d);
        capsule.write(y2, "y2", 0.0d);
        capsule.write(zZ, "zZ", 0.0d);
        capsule.write(previousZ, "previousZ", 0.0d);
        capsule.write(comX, "x", 0.0d);
        capsule.write(comY, "y", 0.0d);
        capsule.write(size, "size", 0);
        capsule.write(pitch, "pitch", -1);
        capsule.write(category, "category", -1);
        //capsule.write(imADeliverySot, "imADeliverySot", false);
        capsule.write(boundingBox, "boundingBox", null);
        //capsule.write(material, "material", null);
        capsule.write(ms, "ms", null);
        capsule.write(depth, "depth", 0f);
        capsule.write(wasHidden, "wasHidden", false);
        capsule.write(isOccluded, "isOccluded", 0);
        capsule.write(arrow, "arrow", null);
        capsule.write(ID, "ID", 0L);
        capsule.write(myName, "myName", null);
    }

    public void read(JMEImporter jmei) throws IOException {
        InputCapsule ic = jmei.getCapsule(this);
//        hardness = ic.readDouble("hardness", 1.0);
//        previousColor = (ColorRGBA) ic.readSavable("previousColor", ColorRGBA.blue);
        shape = (Node) ic.readSavable("shape", null);
        x1 = ic.readDouble("x1", 0.0d);
        y1 = ic.readDouble("y1", 0.0d);
        x2 = ic.readDouble("x2", 0.0d);
        y2 = ic.readDouble("y2", 0.0d);
        zZ = ic.readDouble("zZ", 0.0d);
        previousZ = ic.readDouble("previousZ", 0.0d);
        comX = ic.readDouble("x", 0.0d);
        comY = ic.readDouble("y", 0.0d);
        size = ic.readDouble("size", 0);
        pitch = ic.readDouble("pitch", -1);
        category = ic.readInt("category", -1);
        //imADeliverySot = ic.readBoolean("imADeliverySot", false);
        boundingBox = (BoundingBox) ic.readSavable("boundingBox", null);
        material = (Material3D) ic.readSavable("material", null);
        ms = (MaterialState) ic.readSavable("ms", null);
        depth = ic.readFloat("depth", 0f);
        wasHidden = ic.readBoolean("wasHidden", false);
        isOccluded = ic.readInt("isOccluded", 0);
        arrow = (Node) ic.readSavable("arrow", null);
        ID = ic.readLong("ID", 0);
        myName = ic.readString("myName", null);
    }

    public Class getClassTag() {
        return this.getClass();
    }

    protected class ThingShapeFactory {

        ByteArrayOutputStream BO;
        String pathToImageTexture;
        Double x;
        Double y;
        Thing th;
        Node modelw = new Node("Model shape");

        public ThingShapeFactory() {
        }

        public ThingShapeFactory(double x, double y, Thing t) {
            this.x = x;
            this.y = y;
            this.th = t;
        }

        public ThingShapeFactory(String pathToModel, Thing t) throws IOException {
            MaxToJme C1 = new MaxToJme();
            this.th = t;

            BO = new ByteArrayOutputStream();
            URL maxFile = ThingShapeFactory.class.getClassLoader().getResource(pathToModel);
            try {
                C1.convert(new BufferedInputStream(maxFile.openStream()), BO);
            } catch (IOException exc) {
                log.severe("Error in ThingShapeFactory !");
            }
        }

        protected Node getNode(float scale) {
            try {
                ByteArrayInputStream ModelInputStream = new ByteArrayInputStream(BO.toByteArray());
                modelw = (Node) BinaryImporter.getInstance().load(ModelInputStream);
                modelw.setName("Model shape");

            } catch (IOException exc) {
                log.severe("Error in Thing::getNode()");
            }
            if (scale > 0) {
                modelw.setLocalScale(scale);
            }
            modelw = myLocalTransformations(modelw);

            modelw.setRenderState(ts);
            Node shapeNode = new Node("Shape Node");
            shapeNode.attachChild(modelw);
            boundingBox = new BoundingBox();
            shapeNode.setModelBound(boundingBox);
            shapeNode.updateModelBound();
            shapeNode.updateWorldBound();
            return (shapeNode);
        }

        protected Node getBrickNode(Box box, Environment e) {
            float dx = (float) (getX2() - getX1()) / 10;
            float dy = (float) (getY2() - getY1()) / 10;

            box.setRenderState(th.ms);
            box.setRenderState(e.ls);
            Node shapeNode = new Node("Shape Node");
            shapeNode.setLocalTranslation(new Vector3f((float) ((getX2() + getX1()) / 20 - e.width / 20), depth, (float) (((getY2() + getY1()) / 20) - e.height / 20)));
            shapeNode.attachChild(box);
            boundingBox = new BoundingBox();
            shapeNode.setModelBound(boundingBox);
            shapeNode.updateModelBound();
            shapeNode.updateGeometricState(0, true);
            return (shapeNode);
        }

        protected Node getJewelNode(Octahedron jewel, Environment e) {

            jewel.setRenderState(th.ms);
            jewel.setRenderState(e.ls);

            Node shapeNode = new Node("Shape Node");

            shapeNode = myLocalTransformations(shapeNode);
            shapeNode.attachChild(jewel);
            boundingBox = new BoundingBox();
            shapeNode.setModelBound(boundingBox);
            shapeNode.updateModelBound();
            shapeNode.updateGeometricState(0, true);
            return (shapeNode);
        }
    }

    /**
     * Retrieves the CENTER OF MASS (COM)
     * @return the abscissa of the COM
     */
    public synchronized double getX() {
        return comX;
    }

    /**
     * Retrieves the CENTER OF MASS (COM)
     * @return the ordinate of the COM
     */
    public synchronized double getY() {
        return comY;
    }
    //for creature matters only:

    public synchronized double getPitch() {
        return pitch;
    }

    public synchronized int getMotorSystem() {
        return motorSystem;
    }

    /**
     * Returns de "size" of the creature. It also corresponds to the distance
     * between the wheels (used in the robot's kinematics).
     *
     * @return
     */
    public synchronized double getSize() {
//        if (shape != null) {
//            this.shape.updateGeometricState(0, true);
//            double aux = this.shape.getWorldBound().getVolume();
//            size = Math.round(Math.cbrt(aux));
//        }
//        return size;
        
        return Constants.CREATURE_SIZE;
    }

}
