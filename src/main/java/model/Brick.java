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

import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.MaterialState;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import util.Constants;

/**
 * This class is used to build walls.
 * 
 * @author eccastro
 */
public class Brick extends Thing {

    public Box box;
    static Logger log = Logger.getLogger(Brick.class.getCanonicalName());

    public Brick() {  //Savable matters only
    }

    private Brick(double x, double y, Environment ev) {
        super(x,y,ev);

        this.category = Constants.categoryBRICK;
        x1 = x;
        y1 = y;
        x2 = x;
        y2 = y;
        
        //System.out.println("***coords of initial point: x= " + x + " and y= " + y);

        float dx = (float) (getX2() - getX1()) / 10;
        float dy = (float) (getY2() - getY1()) / 10;

        box = new Box("Brick_" + System.currentTimeMillis(), new Vector3f(), dx / 2, 2.0f, dy / 2);
        sf = new ThingShapeFactory(x,y,this);
        
        affordances = new ArrayList<Integer>();
        affordances.add(Constants.Affordance__VIEWABLE);
        affordances.add(Constants.Affordance__HIDEABLE);
        affordances.add(Constants.Affordance__UNHIDEABLE);
    }

        private Brick(double x1, double y1, double x2, double y2, Environment ev) {
        super(x1,y1,ev);

        this.category = Constants.categoryBRICK;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        log.info("box vertexes: x1= " + x1 + " , y1= " + y1+ " and  x2= " + x2 + " , y2= " + y2);

        float dx = (float) (getX2() - getX1()) / 10;
        float dy = (float) (getY2() - getY1()) / 10;

        box = new Box("Brick_" + System.currentTimeMillis(), new Vector3f(), dx / 2, 2.0f, dy / 2);
        sf = new ThingShapeFactory(comX,comY,this);
    }
        /**
         * Use this one when creating a Brick from CLI or within another application.
         * 
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         * @param ev
         * @param ms 
         */
public Brick(double x1, double y1, double x2, double y2,Environment ev, MaterialState ms) {
        this(x1,y1,x2, y2,ev);
        this.ms = ms;
        setMaterial(new Material3D(ColorRGBA.magenta, ms));
        shape = sf.getBrickNode(box, ev);

    }
/**
 * Used when creating a Brick using mouse dragging.
 * 
 * @param x
 * @param y
 * @param ev
 * @param ms 
 */
public Brick(double x, double y, Environment ev, MaterialState ms) {
        this(x,y,ev);
        this.ms = ms;
        setMaterial(new Material3D(ColorRGBA.magenta, ms));
        shape = sf.getBrickNode(box, ev);
        
    }

    
    public void moveTo(double dx, double dy) {
        synchronized (e.semaphore) {
            x1 += dx;
            y1 += dy;
            x2 += dx;
            y2 += dy;
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
        }
    }

//    public void draw() {
//        /* Calculate rectangle coordinates */
//        double x = (x1 < x2) ? x1 : x2;
//        double y = (y1 < y2) ? y1 : y2;
//        double width = (x1 < x2) ? (x2 - x1) : (x1 - x2);
//        double height = (y1 < y2) ? (y2 - y1) : (y1 - y2);
//
//        /* Fill big rectangle */
//        //g2D.setColor(material.getColor());
//        try {
//            myGraphics2D.fillRect((int) x, (int) y, (int) width, (int) height);
//            renderMyMaterial();
//        } catch (Exception ev) {
//            ev.printStackTrace();
//        }
//
//
//    }
 
 @Override
    public void initPlace() {
        float dx = (float)(getX2()-getX1())/10;
        float dy = (float)(getY2()-getY1())/10;
        setDepth(2.0f); //wall are fixed in this depth
        box.setData(box.getCenter(),dx/2,2.0f,dy/2);// old version of JME
        //currently using version 2006-2008.
    }
@Override
 public void setID(Long id, Environment e){
      this.ID = id;
      String name = Constants.BRICK_PREFIX;
      this.shape.setName(name.concat(id.toString()));
      myName  = name.concat(id.toString());
      //System.out.println("====  My name is "+this.shape.getName());
      e.thingMap.put(myName, this);
  }

    @Override
    public Node myLocalTransformations(Node modelw) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Knapsack putMeInKnapsack(Knapsack sack) {
        throw new UnsupportedOperationException("Makes no sense for my type os Thing!!!");
    }

    @Override
    public void write(JMEExporter jmee) throws IOException {
        super.write(jmee);
        jmee.getCapsule(this).write(box, "box", null);

    }

    @Override
    public void read(JMEImporter jmei) throws IOException {
        super.read(jmei);
        box = (Box) jmei.getCapsule(this).readSavable("box", null);

    }

    @Override
     public Class getClassTag() {
        return this.getClass();
    }

    @Override
    public void updateShape(String model, float scale, Environment e) {
        throw new UnsupportedOperationException("This type of Thing does not support this operation yet");
    }
 
}
