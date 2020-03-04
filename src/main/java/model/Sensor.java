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
 * @author User
 */
public abstract class Sensor {

    protected double x, y;
    protected Material3D material;
    protected Thing thing;
    protected Creature owner;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Material3D getMaterial() {
        if (thing != null) {
            return thing.getMaterial();
        } else {
            //System.out.println("====  thing is null ======");
            return null;
        }
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setThing(Thing th) {
        thing = th;
    }

    public Thing getThing() {
        return thing;
    }

    public Creature getCreature() {

        return owner;
    }
    
}
