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
import com.jme.math.Quaternion;
import com.jme.scene.Node;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Octahedron;
import com.jme.scene.state.MaterialState;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import java.io.IOException;
import java.util.ArrayList;
import util.Constants;

/**
 * This class represents the jewel that compounds a leaflet.
 *
 * @author eccastro
 */
public class Jewel extends Thing {

    public Octahedron jewel;

    public Jewel() { //Savable matters only!
    }

    private Jewel(double x, double y, Environment ev) {

        super(x,y,ev);
        this.category = Constants.categoryJEWEL;

        x1 = comX - (Constants.CRYSTAL_SIZE/2);
        y1 = comY - (Constants.CRYSTAL_SIZE/2);
        x2 = comX + (Constants.CRYSTAL_SIZE/2);
        y2 = comY + (Constants.CRYSTAL_SIZE/2);


        float sideLength = 0.8f;
        jewel = new Octahedron(Constants.CRYSTAL_PREFIX, sideLength);
   
        sf = new ThingShapeFactory(x, y, this);
        
        affordances = new ArrayList<Integer>();
        affordances.add(Constants.Affordance__VIEWABLE);
        affordances.add(Constants.Affordance__HIDEABLE);
        affordances.add(Constants.Affordance__UNHIDEABLE);
        affordances.add(Constants.Affordance__GRASPABLE);
        affordances.add(Constants.Affordance__PUTINBAGABLE);
    }

    public Jewel(double x, double y, Environment ev, MaterialState ms) {
        this(x, y, ev);
        this.ms = ms;
        setMaterial(new Material3D(1.0, ColorRGBA.green, ms));//only jewel shines
        shape = sf.getJewelNode(jewel, ev);
        setDepth(1.0f);
    }

    public void setType(ColorRGBA materialColor) {
        this.getMaterial().setShininess(1.0);
        Material3D m3D = this.getMaterial();
        m3D.setColor(materialColor);
        this.setMaterial(m3D);
    }

    public void moveTo(double dx, double dy) {
        synchronized (e.semaphore) {
            x1 += dx;
            y1 += dy;
            x2 += dx;
            y2 += dy;
            comX += dx;
            comY += dy;
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
        }
    }


    @Override
    public void initPlace() {

    }

    @Override
    public void setID(Long id, Environment e) {
        this.ID = id;
        String name = Constants.CRYSTAL_PREFIX;
        this.shape.setName(name.concat(id.toString()));
        myName = name.concat(id.toString());
        e.thingMap.put(myName, this);
    }

    @Override
    public Node myLocalTransformations(Node modelw) {
        modelw.getLocalRotation().fromAngles(270 * 3.141592f/180, 270 * 3.141592f/180, 0f);
        return modelw;
    }

    @Override
    public Knapsack putMeInKnapsack(Knapsack sack) {
        sack.incJewelInKnapsack(this.getMaterial().getColorName());
        return sack;
    }

    @Override
        public void write(JMEExporter jmee) throws IOException {
        super.write(jmee);
        jmee.getCapsule(this).write(jewel, "jewel", null);

    }

    @Override
    public void read(JMEImporter jmei) throws IOException {
        super.read(jmei);
        jewel = (Octahedron) jmei.getCapsule(this).readSavable("jewel", null);

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
