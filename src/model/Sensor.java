/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
