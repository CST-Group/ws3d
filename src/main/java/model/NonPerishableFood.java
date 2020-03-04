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

import com.jme.math.Quaternion;
import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Constants;


/**
 *
 * @author eccastro
 */
public class NonPerishableFood extends Food {
    
    static Logger log = Logger.getLogger(NonPerishableFood.class.getCanonicalName());


    public NonPerishableFood(){ //Savable matters only!

    }

    public NonPerishableFood(double x, double y, Environment ev, MaterialState ms, TextureState ts){

        super(x,y,ev,ms);
        try {
            this.category = Constants.categoryNPFOOD;
            this.subCategory = Constants.categoryNPFOOD;
            this.ts = ts;
            sf = new ThingShapeFactory("images/nut.3ds", this);
            shape = sf.getNode(0);

            shape.setRenderState(ms);
            shape.setRenderState(ev.ls);
            //shape.setLocalTranslation(new Vector3f((float) ((getX2() + getX1()) / 20 - e.width / 20), depth, (float) (((getY2() + getY1()) / 20) - e.height / 20)));
            shape.updateRenderState();
            perishable = false;
            stillValid = true;
        } catch (IOException ex) {
            log.severe("!!!!!NonPerishableFood: Erro ! ");
            ex.printStackTrace();
        }
    }
    
    @Override
    void myLifeCycle() {
         // I do nothing.
    }

    public Node myLocalTransformations(Node modelw){
        modelw.setLocalTranslation(0,1.7f,0);
        modelw.getLocalRotation().fromAngles(270 * 3.141592f/180, 3.141592f, 0f);
        return modelw;
    }
@Override
 public void setID(Long id, Environment e){
      this.ID = id;
      String name = Constants.NPFOOD_PREFIX;
      this.shape.setName(name.concat(id.toString()));
      myName  = name.concat(id.toString());
      //System.out.println("====  My name is "+this.shape.getName());
      e.thingMap.put(myName, this);
  }

    @Override
    public Knapsack putMeInKnapsack(Knapsack sack) {
        sack.incNonPerishableFoodInKnapsack(this);
        return sack;
    }

}
