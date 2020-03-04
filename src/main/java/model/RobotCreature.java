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

import motorcontrol.CarLikeCreatureKinematics;
import motorcontrol.CreatureKinematicsInterface;
import motorcontrol.TwoWheeledRobotKinematics;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author eccastro
 */
public class RobotCreature extends Creature {
    
    static Logger log = Logger.getLogger(RobotCreature.class.getCanonicalName());

    public RobotCreature(double initialX, double initialY, double iPitch, Environment env, int motorSys) {

        super(initialX, initialY, iPitch, env, motorSys);

        switch (motorSys){
            case 1:  kinematics = new CarLikeCreatureKinematics(this);  break;

            case 2:  kinematics = new TwoWheeledRobotKinematics(this); break;

            default: kinematics = new TwoWheeledRobotKinematics(this);
        }
        
        try {
             sf = new ThingShapeFactory("images/robo.3ds", this);

        } catch (IOException ex) {
            log.severe("!!!!!Creature: Erro ! ");
            ex.printStackTrace();
        }

    }

    public void setSpeedExternally(){
        ((TwoWheeledRobotKinematics)this.kinematics).setDeriveSpeed(false);
    }

   @Override
    public void updateMyPosition() {
        this.kinematics.updatePosition();
    }

    @Override
    public Knapsack putMeInKnapsack(Knapsack sack) {
        throw new UnsupportedOperationException("Makes no sense for my type of Thing!!!");
    }

}
