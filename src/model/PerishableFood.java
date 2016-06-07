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
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import com.jme.scene.state.MaterialState;

import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.renderer.ColorRGBA;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Constants;

public class PerishableFood extends Food {
    
    static Logger log = Logger.getLogger(PerishableFood.class.getCanonicalName());

    private Timer timer;

    public PerishableFood() { //Savable matters only!
    }

    public PerishableFood(int periodInSecs, double x, double y, Environment ev, MaterialState ms, TextureState ts){
        super(x,y,ev,ms);
        try {
            this.category = Constants.categoryPFOOD;
            this.subCategory = Constants.categoryPFOOD;
            this.ts = ts;
            this.validPeriod = periodInSecs;
            sf = new ThingShapeFactory("images/apple.3ds", this);
            shape = sf.getNode(0.03f);

            shape.setRenderState(ms);
            shape.setRenderState(ev.ls);
            shape.updateRenderState();
            perishable = true;
            stillValid = true;
            myLifeCycle();
        } catch (IOException ex) {
            log.severe("!!!!!PerishableFood: Erro ! ");
            ex.printStackTrace();
        }
    }

    
    @Override
    void myLifeCycle() {
        timer = new Timer();
        timer.schedule(new RemindTask(this, "images/green_metalic.jpg"), validPeriod*1000);
    }

    public void setSpoiledTexture(String pathToTexture){

         ts.setTexture(TextureManager.loadTexture(PerishableFood.class.getClassLoader().getResource(
                pathToTexture),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear));
         ts.setEnabled(true);
         shape.setRenderState(ts);
         //maybe has to be called from external class (due to enhiritance):
         material.setColor(ColorRGBA.lightGray);//for client display only

    }

   public Node myLocalTransformations(Node modelw){
        modelw.setLocalTranslation(0,0.8f,0);        
        modelw.getLocalRotation().fromAngles(270 * 3.141592f/180, 3.141592f, 0f);
        return modelw;
    }
   @Override
 public void setID(Long id, Environment e){
      this.ID = id;
      String name = Constants.PFOOD_PREFIX;
      this.shape.setName(name.concat(id.toString()));
      myName  = name.concat(id.toString());
      //System.out.println("====  My name is "+this.shape.getName());
      e.thingMap.put(myName, this);
  }

    @Override
    public Knapsack putMeInKnapsack(Knapsack sack) {
        sack.incPerishableFoodInKnapsack(this);
        return sack;
    }



    class RemindTask extends TimerTask implements Savable{
        PerishableFood pf;
        String pathToTexture;
        RemindTask(PerishableFood pf, String pathToTexture){
          this.pf = pf;
          this.pathToTexture = pathToTexture;
        }
        public void run() {
            log.info("***** Food expired! *****");
            this.pf.setSpoiledTexture(pathToTexture);
            material.setEnergy(0.0);
            material.setColor(ColorRGBA.darkGray); //spoiled
            stillValid = false;
            timer.cancel(); //Terminate the timer thread
        }

        public void write(JMEExporter jmee) throws IOException {
            OutputCapsule capsule = jmee.getCapsule(this);
             capsule.write(pf, "pf", null);
             capsule.write(pathToTexture, "pathToTexture", null);

        }

        public void read(JMEImporter jmei) throws IOException {
            InputCapsule ic = jmei.getCapsule(this);
            pf = (PerishableFood) ic.readSavable("pf", null);
            pathToTexture = ic.readString("pathToTexture", null);

        }

        public Class getClassTag() {
            return this.getClass();
        }
    }

}
