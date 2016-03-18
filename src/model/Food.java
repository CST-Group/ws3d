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

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import worldserver3d.IconFactory;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import util.Constants;

/**
 *
 * @author eccastro
 */
public abstract class Food extends Thing{

    //default is not perishable:
    public boolean perishable = false;
    protected boolean stillValid = true;

    //perishable food:
    protected int validPeriod;  //in seconds

    abstract void myLifeCycle();


    public Food(){ //Savable matters only!

        perishable = false;
        stillValid = true;
        ts = null;
    }
    public Food(double x, double y, Environment ev, MaterialState ms) {
        super(x,y,ev);
        this.category = Constants.categoryFOOD;

        x1 = comX - (Constants.FOOD_SIZE/2);
        y1 = comY - (Constants.FOOD_SIZE/2);
        x2 = comX + (Constants.FOOD_SIZE/2);
        y2 = comY + (Constants.FOOD_SIZE/2);

        //System.out.println("***coords of initial point: x= " + x + " and y= " + y);

        this.ms = ms;
        setMaterial(new Material3D(ColorRGBA.orange, 1.0, 1.0, 0.0, ms)); //default, but currently properly set in ThingCreator

        affordances = new ArrayList<Integer>();
        affordances.add(Constants.Affordance__VIEWABLE);
        affordances.add(Constants.Affordance__EATABLE);
        affordances.add(Constants.Affordance__HIDEABLE);
        affordances.add(Constants.Affordance__UNHIDEABLE);
        affordances.add(Constants.Affordance__GRASPABLE);
        affordances.add(Constants.Affordance__PUTINBAGABLE);
    }


    @Override
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
      public void write(JMEExporter jmee) throws IOException {
        super.write(jmee);
        jmee.getCapsule(this).write(perishable, "perishable", false);
        jmee.getCapsule(this).write(stillValid, "stillValid", true);
        jmee.getCapsule(this).write(ts, "ts", null);
        jmee.getCapsule(this).write(validPeriod, "validPeriod", 0);

    }

    @Override
    public void read(JMEImporter jmei) throws IOException {
        super.read(jmei);
        perishable = jmei.getCapsule(this).readBoolean("perishable", false);
        stillValid = jmei.getCapsule(this).readBoolean("stillValid", true);
        ts = (TextureState) jmei.getCapsule(this).readSavable("ts", null);
        validPeriod = jmei.getCapsule(this).readInt("validPeriod", 0);

    }

    @Override
     public Class getClassTag() {
        return this.getClass();
    }
}
