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
package motorcontrol;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import java.io.IOException;
import model.Creature;
import model.Environment;
import util.Constants;

/**
 *
 * @author eccastro
 */
public class TwoWheeledRobotKinematics implements CreatureKinematicsInterface {

    private Creature c;
    private boolean deriveSpeed = true;

    public TwoWheeledRobotKinematics(Creature creature) {
        this.c = creature;
    }

    public synchronized void setDeriveSpeed(boolean b){
        this.deriveSpeed = b;
    }
    public synchronized void updatePosition() {
        // double D = 1.0 * 50.0;
        //double D = 1.0 * 3.0;
        double D = 20 / Constants.M_PI;
        double pRad = Math.toRadians(c.getPitch());
        double aux = (1 - c.getFriction());
        double w = (c.getVleft() - c.getVright()) / D; //clockwise is positive
        double cosp, senp, a, cosWP, senWP;

        cosp = Math.cos(-pRad); //counterclockwise is negative
        senp = Math.sin(-pRad);

        if (deriveSpeed) {
            if (c.getVleft() != -c.getVright()) {
                c.setSpeed((c.getVright() + c.getVleft()) / 2);
            } else {
                c.setSpeed(Math.abs(c.getVleft()));
            }
        }
        //Backwards motion:
        if ((c.getVleft() < 0) && (c.getVright() < 0)) {
            pRad = Constants.M_PI + pRad;
            cosp = Math.cos(-pRad); //counterclockwise is negative
            senp = Math.sin(-pRad);
            c.setSpeed(Math.abs(c.getVleft()));
            
            a = 1 * c.getSpeed(); // aux = 1

            c.setX(c.getX() + a * cosp);
            c.setY(c.getY() - a * senp);

        } else
        
        
        //.....Evaluating coords of new position:
        //1st case: wheels have same velocity => follow a straight line
        if (c.getVleft() == c.getVright()) {

                //System.out.println("...............................1st and speed= " + c.getSpeed());
                //a = aux * (c.getVleft() + c.getVright()) / 2;
                a = aux * c.getSpeed();

                c.setX(c.getX() + a * cosp);
                c.setY(c.getY() - a * senp);
        } //2nd case: same velocities; oposite direction -> robot rotates in place
        else if (c.getVleft() == -c.getVright()) {
            //System.out.println("...............................2nd and speed= " + c.getSpeed());

            //Reference:  Equations 3.50, 3.51, 3.52 on pag. 84 (chap 3) Sigwart/Nourbakhsh
            //c.setPitch(Math.toDegrees(c.getW()));    Alterado por Ricardo Gudwin em 06/01/2017
            c.setPitch(c.getPitch() + c.getW());
            //System.out.println("Pitch:"+c.getPitch());

        } //3rd case: different velocities => change direction
        else {
            //System.out.println("...............................3rd and speed= " + c.getSpeed());
//            a = aux * (D / 2) * ((c.getVleft() + c.getVright()) / (c.getVleft() - c.getVright()));
//            senWP = Math.sin(w - pRad);
//            cosWP = Math.cos(w - pRad);
//
//            c.setX(c.getX() + a * (senWP + Math.sin(pRad)));
//            c.setY(c.getY() - a * (cosWP - Math.cos(pRad)));

            //a = aux * (c.getVleft() + c.getVright()) / 2;
            a = aux * c.getSpeed();
            c.setX(c.getX() + a * cosp);
            c.setY(c.getY() - a * senp);

        }

        
    }
}
