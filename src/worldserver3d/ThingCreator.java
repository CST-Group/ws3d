/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldserver3d;

import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.*;
import util.Constants;

/**
 * Controller invoked by viewers to create Thing components.
 *
 * @author ecalhau
 */
public class ThingCreator {

    Environment e;
    //the motor system of the robot is differential steering by default:
    int motorSys = 2;

    public ThingCreator(final Environment e) {
        this.e = e;
    }

    private String getTimestampFormatted(Long t) {
     
        Date nowD = new Date(t.longValue());
        String dateFormat = "HH:mm:ss:SS";
        SimpleDateFormat sdf =  new SimpleDateFormat(dateFormat);
        
        return sdf.format(nowD);
    }
    /*
     * Method to create Things "first time". Do not use it to "reload" Things
     * from a saved environment: use reCreateThing method instead.
     */
    public synchronized Thing createThing(int category, double x, double y) {

        Long t = System.currentTimeMillis();
        
        e.oMsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createMaterialState());
        e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
        Thing th = null;

        try {
            switch (category) {

                case Constants.categoryBRICK:
                    e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                    th = new Brick(x, y, e, e.oMsPool.get(t));
                    break;

                case Constants.categoryJEWEL:
                    e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                    th = new Jewel(x, y, e, e.oMsPool.get(t));
                    th.setMaterial(new Material3D(1.0, ColorRGBA.green, e.oMsPool.get(t)));//only jewel shines
                    break;

                case Constants.categoryNPFOOD:
                    e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                    th = new NonPerishableFood(x, y, e, e.oMsPool.get(t), e.thingTsPool.get(t));
                    th.setMaterial(new Material3D(ColorRGBA.green, 1.0, Constants.NPFOOD_ENERGY, 0.0, e.oMsPool.get(t))); //non-perishable food has energy = 2.0!
                    th.subCategory = Constants.categoryNPFOOD;
                    break;

                case Constants.categoryPFOOD: // one apple => more calories than one nut. But it may be rotten.
                    e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                    //10 minutes food
                    th = new PerishableFood(Constants.VALID_PERIOD_SECS, x, y, e, e.oMsPool.get(t), e.thingTsPool.get(t));
                    th.setMaterial(new Material3D(ColorRGBA.red, 1.0, Constants.PFOOD_ENERGY, 0.0, e.oMsPool.get(t))); //perishable food has energy = 1.0!
                    th.subCategory = Constants.categoryPFOOD;
                    break;

                case Constants.categoryCAGE:
                    e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                    th = new Cage(x, y, e, e.oMsPool.get(t), e.thingTsPool.get(t));
                    th.setMaterial(new Material3D(ColorRGBA.yellow, 1.0, 0, 0, e.oMsPool.get(t)));
                    break;
//            case Constants.categoryDeliverySPOT:
//                e.oMsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createMaterialState());
//                //reuse texture pool:
//                e.foodTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
//                th = new DeliverySpot(x, y, e, e.oMsPool.get(t), e.foodTsPool.get(t));
//                break;
            }
            th.setTxState(e.thingTsPool.get(t));

            //add index to make sure that each id is actually different.
            //There might be a problem when recreating from xml file
            int idx = e.addThing(th);
            th.setID(t + idx, e);
            System.out.println(">>>>>>>>Thing created: "+th.getMyName()+" at "+getTimestampFormatted(t));
        } catch (Exception ex) {
            System.out.println("!!!!!Error in ThingCreator ! ");

        }
        return th;
    }

    public synchronized Thing createBrick(int color, double x1, double y1, double x2, double y2) {

        Long t = System.currentTimeMillis();
        e.oMsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createMaterialState());
        e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
        Thing th = new Brick(x1, y1, x2, y2, e, e.oMsPool.get(t));
        Material3D m3D = th.getMaterial();
        m3D.setColor(Constants.translateIntoColor(Constants.getColorItem(color)));
        th.setMaterial(m3D);
        th.setTxState(e.thingTsPool.get(t));
        int idx = e.addThing(th);
        th.setID(t + idx, e);
        return th;
    }

    public synchronized RobotCreature createCreature(boolean ifRed, double x, double y, double pitch) {

        Long t = System.currentTimeMillis();

        RobotCreature c = new RobotCreature(x, y, pitch, e, motorSys);

        e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());

        c.setTxState(e.thingTsPool.get(t));
        //default:
        c.shape = c.getNode();
        c.setTexture("images/bright_red.jpg");
        c.getMaterial().setColor(ColorRGBA.red);
        //curently ifRed:  true: red, false: yellow
        if (!ifRed) {

            c.setTexture("images/yellow_metal.jpg");
            c.getMaterial().setColor(ColorRGBA.yellow);
        }

        //add index to make sure that each id is actually different.
        //There might be a problem when recreating from xml file
        int idx = e.addThing(c);
        c.setID(t + idx, e);
        System.out.println(">>>>>>>>Creature created: "+c.getMyName()+" at "+getTimestampFormatted(t));
        return c;

    }

    public Thing createCrystalOfType(int color, double x, double y) {
        Thing th = createThing(Constants.categoryJEWEL, x, y);
        Material3D m3D = th.getMaterial();
        m3D.setColor(Constants.translateIntoColor(Constants.getColorItem(color)));
        m3D.setShininess(1.0);//only jewel shines
        th.setMaterial(m3D);
        th.setTxState(e.thingTsPool.get(th.getID()));
        return th;
    }

    /**
     * This method is used to change the creature motor system. 
     *
     * @param ifRed
     * @param x
     * @param y
     * @param pitch
     * @param motorSys
     * @return
     */
    public synchronized RobotCreature createCreatureMS(boolean ifRed, double x, double y, double pitch, int motorSys) {
        this.motorSys = motorSys;
        return this.createCreature(ifRed, x, y, pitch);
    }
    
    /**
     * Used when it is "reloaded" from an environment previously saved.
     *
     * @param category
     * @param x
     * @param y
     * @return
     */
    public synchronized Thing reCreateThing(int category, int subcategory, String color, double x, double y, double x2, double y2, boolean wasHidden) {

        Long t = System.currentTimeMillis();
        e.oMsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createMaterialState());
        e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
        Thing th = null;

        switch (category) {

            case Constants.categoryBRICK:
                th = new Brick(x, y, e, e.oMsPool.get(t));
                th.setX2(x2);
                th.setY2(y2);
                th.getMaterial().setColor(Constants.translateIntoColor(color));
                break;

            case Constants.categoryJEWEL:
                th = new Jewel(x, y, e, e.oMsPool.get(t));
                th.getMaterial().setColor(Constants.translateIntoColor(color));
                break;

            case Constants.categoryNPFOOD:

                e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                th = new NonPerishableFood(x, y, e, e.oMsPool.get(t), e.thingTsPool.get(t));
                th.setMaterial(new Material3D(ColorRGBA.orange, 1.0, Constants.NPFOOD_ENERGY, 0.0, e.oMsPool.get(t))); //non-perishable food has energy = 2.0!
                break;
            case Constants.categoryPFOOD:
                /*
                 * Currently does not save if PFood has expired!
                 */

                e.thingTsPool.put(t, DisplaySystem.getDisplaySystem().getRenderer().createTextureState());
                //10 minutes food
                th = new PerishableFood(Constants.VALID_PERIOD_SECS, x, y, e, e.oMsPool.get(t), e.thingTsPool.get(t));
                th.setMaterial(new Material3D(ColorRGBA.red, 1.0, Constants.PFOOD_ENERGY, 0.0, e.oMsPool.get(t))); //perishable food has energy = 1.0!
                break;

        }

        th.setTxState(e.thingTsPool.get(t));
        th.wasHidden = wasHidden;
        if (wasHidden) {
            th.hideMe(e);
        }
        //add index to make sure that each id is actually different.
        //There might be a problem when recreating from xml file
        int idx = e.addThing(th);
        th.setID(t + idx, e);

        return th;
    }
}
