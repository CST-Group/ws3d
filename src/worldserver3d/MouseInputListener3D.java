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
package worldserver3d;

import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.system.DisplaySystem;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.Creature;
import model.Environment;
import model.Thing;
import util.Constants;
import worldserver3d.view.KnapsackAndScoreFrame;

/**
 *
 * @author gudwin
 */
public class MouseInputListener3D implements MouseInputListener {

    public Main m;
    public int editstate = 3;
    private final int NONE = -1;
    private final int CAR = -2;
    private final int CAR_POINT = -3;
    private final int TARGET = -4;
    private final int CORNER_X1Y1 = -6;
    private final int CORNER_X1Y2 = -7;
    private final int CORNER_X2Y1 = -8;
    private final int CORNER_X2Y2 = -9;
    private final int EVENCAMERA = -10;
    private final int ODDCAMERA = -11;
    private final int crotating = 0;
    private final int cmoving = 1;
    private final int omoving = 2;
    private final int free = 3;
    private final int oscaling = 4;
    private final double dTime = 15;
    private final String newCreaturePlaceErrorTitle = "Place a creature!";
    private final String newCreaturePlaceErrorMsg = "Not on an obstacle!!!";

    private int mouseXini, mouseYini;
    private int mouseXfin, mouseYfin;
    private boolean isMoving = false;
    private int objectID = -1;
    private int creatureID = -1;
    private int obstacleIdx = -1;
    public int bpressed = -1;
    private int clickcount = 0;
    private boolean hasChanged = true;
    DisplaySystem display;
    Logger log;

    public enum Cursor {

        Default, Cross, NW_Resize, SW_Resize, NE_Resize, SE_Resize, Hand, Dot;
    }
    URL default_cursor;
    URL cross;
    URL nw_resize;
    URL sw_resize;
    URL ne_resize;
    URL se_resize;
    URL hand;
    URL dot;

    public MouseInputListener3D(Main mm) {
        m = mm;
        display = DisplaySystem.getDisplaySystem();
        log = Logger.getLogger(MouseInputListener3D.class.getCanonicalName());

        try {
            default_cursor = MouseInputListener3D.class.getClassLoader().getResource("images/arrow.png");
            cross = MouseInputListener3D.class.getClassLoader().getResource("images/cross.png");
            nw_resize = MouseInputListener3D.class.getClassLoader().getResource("images/nesw.png");
            sw_resize = MouseInputListener3D.class.getClassLoader().getResource("images/nwse.png");
            ne_resize = MouseInputListener3D.class.getClassLoader().getResource("images/nwse.png");
            se_resize = MouseInputListener3D.class.getClassLoader().getResource("images/nesw.png");
            hand = MouseInputListener3D.class.getClassLoader().getResource("images/hand.png");
            dot = MouseInputListener3D.class.getClassLoader().getResource("images/dot.png");
        } catch (Exception ev) {
            log.severe("Erro no download dos cursores ...");
        }

    }

    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever a mouse button is pressed or released.
     * @param button index of the mouse button that was pressed/released
     * @param pressed true if button was pressed, false if released
     * @param x x position of the mouse while button was pressed/released
     * @param y y position of the mouse while button was pressed/released
     */
    public void onButton(int button, boolean pressed, int x, int y) {
        Environment e = m.i.ep.e;
        int mouse_x = x;
        int mouse_y = y;
        //System.out.println("world coords>>>> x:" + x + " y:" + y);
        Vector2f mouse_xy = new Vector2f(mouse_x, mouse_y);
        Vector3f worldCoords = display.getWorldCoordinates(mouse_xy, 0);
        Vector3f worldCoords2 = display.getWorldCoordinates(mouse_xy, 1);
        Vector3f direction = worldCoords2.subtractLocal(worldCoords).normalizeLocal();
        Ray mouseRay = new Ray(worldCoords, direction);
        float planeY = 0;
        float startY = mouseRay.origin.y;
        float endY = mouseRay.direction.y;
        float coef = (planeY - startY) / endY;
        float planeX = mouseRay.origin.x + (coef * mouseRay.direction.x);
        float planeZ = mouseRay.origin.z + (coef * mouseRay.direction.z);
        mouse_x = (int) (planeX * 10) + e.width / 2;
        mouse_y = (int) (planeZ * 10) + e.height / 2;
        if (pressed) {
            mousePressed(button, mouse_x, mouse_y, mouseRay);
            bpressed = button;
        } else {
            mouseReleased(button, mouse_x, mouse_y, mouseRay);
            bpressed = -1;
        }
    }

    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever the mouse wheel is rotated.
     * @param wheelDelta steps the wheel was rotated
     * @param x x position of the mouse while wheel was rotated
     * @param y y position of the mouse while wheel was rotated
     */
    public void onWheel(int wheelDelta, int x, int y) {
        Camera cam = m.sf.gameState.getCamera();
        float xloc, yloc, zloc, alpha = 0.05f;
        Vector3f location, direction;
        location = cam.getLocation();
        direction = cam.getDirection();
        xloc = location.x;
        yloc = location.y;
        zloc = location.z;
        xloc += alpha * wheelDelta * direction.x;
        yloc += alpha * wheelDelta * direction.y;
        zloc += alpha * wheelDelta * direction.z;
        Vector3f newlocation = new Vector3f(xloc, yloc, zloc);
        cam.setLocation(newlocation);
    }

    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever the mouse is moved.
     * @param xDelta delta of the x coordinate since the last mouse movement event
     * @param yDelta delta of the y coordinate since the last mouse movement event
     * @param newX x position of the mouse after the mouse was moved
     * @param newY y position of the mouse after the mouse was moved
     */
    public void onMove(int xDelta, int yDelta, int newX, int newY) {
        Environment e = m.i.ep.e;
        int mouse_x = newX;
        int mouse_y = newY;
        clickcount = 0;
        Vector2f mouse_xy = new Vector2f(mouse_x, mouse_y);
        Vector3f worldCoords = display.getWorldCoordinates(mouse_xy, 0);
        Vector3f worldCoords2 = display.getWorldCoordinates(mouse_xy, 1);
        Vector3f direction = worldCoords2.subtractLocal(worldCoords).normalizeLocal();
        Ray mouseRay = new Ray(worldCoords, direction);
        float planeY = 0;
        float startY = mouseRay.origin.y;
        float endY = mouseRay.direction.y;
        float coef = (planeY - startY) / endY;
        float planeX = mouseRay.origin.x + (coef * mouseRay.direction.x);
        float planeZ = mouseRay.origin.z + (coef * mouseRay.direction.z);
        mouse_x = (int) (planeX * 10) + e.width / 2;
        mouse_y = (int) (planeZ * 10) + e.height / 2;

        if (bpressed != -1) {
            mouseDragged(mouse_x, mouse_y, xDelta, yDelta);
        } else {
            mouseMoved(mouse_x, mouse_y, mouseRay);
        }

    }

    public boolean canCreateHere(Ray ray) {
        Environment e = m.i.ep.e;
        int pos = ClassifyMouseClick(mouseXini, mouseYini, ray);
        log.info(" *** pos= " + pos);
        if (pos == NONE) {
            //free space
            return true;

        } else if (pos == CAR) {
            //launch knapsack display
            return false;
        } else if (pos >= 0) {
            Thing ob = e.getOpool().get(pos);
            if (!ob.wasHidden) {
                JOptionPane.showMessageDialog(null,
                        newCreaturePlaceErrorMsg, newCreaturePlaceErrorTitle, JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                return true;
            }
        } else if (pos == EVENCAMERA) {
            log.info("--- This is the Even Camera ---");
            return false;
        } else if (pos == ODDCAMERA) {
            log.info("--- This is the Odd Camera ---");
            return false;
        } else {
            return false;
        }
    }

    public void mousePressed(int button, int x, int y, Ray ray) {
        //System.out.println(" *** MouseInputListener3D:   Mouse pressed. ");
        //System.out.println(" *** x= " + x + "  y= " + y);
        Environment e = m.i.ep.e;
        clickcount++;
        if (clickcount > 2) {
            clickcount = 0;
        }
        try {
            mouseXini = x;
            mouseYini = y;

            if (button == 0 && !isMoving) {
                switch (objectID = ClassifyMouseClick(mouseXini, mouseYini, ray)) {
                    case NONE:
                        ThingCreator tc = new ThingCreator(e);
                        tc.createThing(Constants.categoryBRICK, mouseXini, mouseYini);

                        objectID = CORNER_X2Y2;
                        obstacleIdx = e.getOpool().size() - 1;
                        editstate = oscaling;
                        break;

                    case CAR:
                        editstate = cmoving;
                        creatureID = GetCreatureIndex(mouseXfin, mouseYfin);
                        break;
                    case CAR_POINT:
                        editstate = crotating;
                        creatureID = GetCreatureIndex(mouseXfin, mouseYfin);
                        break;
                    case TARGET:
                    //obstacleIdx = -1;

                    case CORNER_X1Y1:
                    case CORNER_X1Y2:
                    case CORNER_X2Y1:
                    case CORNER_X2Y2:
                        editstate = oscaling;
                        break;

                    default:
                        editstate = omoving;
                        obstacleIdx = objectID;
                }
            } else if (button == 1 && !isMoving) {
                //Panel for creature or food creation:
                if (canCreateHere(ray)) {
                    m.i.ep.foodTab.setXY(mouseXini, mouseYini);
                    m.i.ep.foodTab.setTitle("Creature, Jewel and Food Creation ");
                    m.i.ep.foodTab.showForCreation();
                    m.i.ep.foodTab.setVisible(true);
                }

                m.sf.gameState.ThingsRN.updateRenderState();
            }
        } catch (Exception ev) {

            log.severe("aha ... coisa feia ... em mousePressed()");
            ev.printStackTrace();
        }
    }

    public void mouseReleased(int button, int x, int y, Ray ray) {
        log.info(" *** MouseInputListener3D:   Mouse released. ");

        Environment e = m.i.ep.e;
        if (button == 0 && clickcount == 2) {
            if (obstacleIdx != -1) {
                obstacleIdx = ClassifyMouseClick(x, y, ray);
                if (obstacleIdx >= 0) {
                    Thing o = e.getOpool().get(obstacleIdx);

                    if ((o.category == Constants.categoryFOOD) || (o.category == Constants.categoryPFOOD) || (o.category == Constants.categoryNPFOOD)){
                        m.i.ep.foodTab.setFood(o);
                        m.i.ep.foodTab.setXY(x, y);
                        m.i.ep.foodTab.setTitle("Food " + e.getOpool().indexOf(o) + " visibility edition");
                        m.i.ep.foodTab.update();
                        m.i.ep.foodTab.setVisible(true);
                    } else if (o.category == Constants.categoryJEWEL) {
                        m.i.ep.jewelTab.setObstacle(o);
                        m.i.ep.jewelTab.setTitle("Edit Jewel " + obstacleIdx);

                        m.i.ep.jewelTab.update();
                        m.i.ep.jewelTab.setVisible(true);

                    } else if (o.category == Constants.categoryBRICK) {
                        m.i.ep.obstacleTab.setObstacle(o);
                        m.i.ep.obstacleTab.setTitle("Edit Obstacle " + obstacleIdx);
                        m.i.ep.obstacleTab.update();
                        m.i.ep.obstacleTab.setVisible(true);
                        log.info("Obstacle--- z= " + o.getZ());
                    }
                    else if (o.category == Constants.categoryCAGE) {
                        m.i.ep.containerViewer.setContainer(o);
                        m.i.ep.containerViewer.setTitle(o.getMyName());
                        m.i.ep.containerViewer.setVisible(true);
                    }
                }

            }
            for (Creature c : e.getCpool()) {
                if (c.contains((double) x, (double) y)) {
                    int c_index = e.getCpool().indexOf(c);
                    if (c_index % 2 == 0) {
                        e.setCamera(0, c_index);
                        log.info("Even Camera attached to robot " + e.getCamera(0));
                    } else {
                        e.setCamera(1, c_index);
                        log.info("Odd Camera attached to robot " + e.getCamera(1));
                    }        
                }
            }
        }//Check if just created brick is sufficiently big: dx and dy >= 5
        // Small ones are automatically deleted.
        else if (button == 0) {
            if (obstacleIdx != -1) {
                if (obstacleIdx >= 0) {
                    Thing o = e.getOpool().get(obstacleIdx);
                    if (o.category == Constants.categoryBRICK) {
                        if (o.getX1() > o.getX2()) {
                            double tmp = o.getX1();
                            o.setX1(o.getX2());
                            o.setX2(tmp);
                        }
                        if (o.getY1() > o.getY2()) {
                            double tmp = o.getY1();
                            o.setY1(o.getY2());
                            o.setY2(tmp);
                        }
                        e.addToOpoolModified(o);
                        if ((Math.abs(o.getX2() - o.getX1()) < 5) || (Math.abs(o.getY2() - o.getY1()) < 5)) {
                            e.removeThing(o);
                            log.info("****** Invalid obstacle size!!! Removed!!!");
                        }
                    }
                }
            }
            for (Creature c : e.getCpool()) {
                c.draggedState = false;
            }
        } //middle button:
        else if (button == 2) {
            for (Creature c : e.getCpool()) {
                //Panel with score of the creature:
                if (c.contains((double) x, (double) y)) {
                    log.info("****** Middle button on creature " + e.getCpool().indexOf(c));
                    if (!m.i.ep.scoreTabList.containsKey(c.getID())) {
                        try {
                            KnapsackAndScoreFrame ksf = new KnapsackAndScoreFrame();
                            ksf.setEnvironment(e);
                            ksf.setCreature(c);
                            ksf.setTitle("Knapsack and Score - creature " + e.getCpool().indexOf(c));
                            ksf.update();
                            m.i.ep.scoreTabList.put(c.getID(), ksf);
                        } catch (InvocationTargetException ex) {
                            Logger.getLogger(MouseInputListener3D.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    m.i.ep.scoreTabList.get(c.getID()).update();
                    m.i.ep.scoreTabList.get(c.getID()).setVisible(true);
                }
            }
//            //test only:
//            for (Thing o : e.getOpool()) {
//                if (o.contains((double) x, (double) y)) {
//                    o.updateShape("images/apple.3ds", 0.03f, e);
//                }
//            }

        }
        if (button == 1 && clickcount == 2) {

            Creature dead = null;
            for (Creature c : e.getCpool()) {
                if (c.contains((double) x, (double) y)) {
                    dead = c;
                }
            }
            if (dead != null) {

                for (KnapsackAndScoreFrame kasf : m.i.ep.scoreTabList.values()) {
                    if (kasf.isVisible()) {
                        kasf.dispose();
                    }

                }

                m.i.ep.scoreTabList.clear();
                e.removeCreature(dead);
            }

        }

        e.cleanUp();
    }


    public void mouseMoved(int x, int y, Ray ray) {
        //System.out.println(" *** MouseInputListener3D:   Mouse moved. ");
        //System.out.println("mouse moved: "+event.getX()+' '+event.getY());
        mouseXfin = x;
        mouseYfin = y;

        int newObjectID = ClassifyMouseClick(mouseXfin, mouseYfin, ray);
        obstacleIdx = GetObjectIndex(mouseXfin, mouseYfin);
        //System.out.println(">>>>>>>>>>> "+newObjectID);
        if (objectID != newObjectID) {
            switch (objectID = newObjectID) {
                case NONE:
                    setCursor(Cursor.Default);
                    break;

                case CORNER_X1Y1:
                    setCursor(Cursor.NW_Resize);
                    break;

                case CORNER_X1Y2:
                    setCursor(Cursor.SW_Resize);
                    break;

                case CORNER_X2Y1:
                    setCursor(Cursor.NE_Resize);
                    break;

                case CORNER_X2Y2:
                    setCursor(Cursor.SE_Resize);
                    break;
                case CAR_POINT:
                    setCursor(Cursor.Dot);
                    break;
                default:
                    setCursor(Cursor.Hand);
            }
        }
        mouseXini = mouseXfin;
        mouseYini = mouseYfin;
        //repaint();
        //System.out.println("object ID: "+objectID+','+obstacleIdx); System.out.flush();
    }

    //TODO: use approach of 2D: consider changes for full 3D compliance.
    public void mouseDragged(int x, int y, int dx, int dy) {
        //System.out.println(" *** MouseInputListener3D:   Mouse dragged. ");
        Environment e = m.i.ep.e;
        Thing o;
        Creature c;
        if (bpressed == 0) {
            setChanged(true);
            mouseXfin = x;
            mouseYfin = y;
            //System.out.println("ObjectID: "+objectID);
            try {
                switch (editstate) {
                    case cmoving:
                        c = e.getCpool().get(creatureID);
                        c.moveTo(mouseXfin - mouseXini, mouseYfin - mouseYini);
                        //repaint();
                        c.draggedState = true;
                        break;

                    case crotating:
                        c = e.getCpool().get(creatureID);
                        c.rotate(mouseXfin, mouseYfin);
                        //repaint();
                        c.draggedState = true;
                        break;

                    //case TARGET:
                    //target.move(mouseXfin-mouseXini, mouseYfin-mouseYini);
                    //	repaint();
                    //	break;
                    case oscaling:
                        switch (objectID) {
                            case CORNER_X1Y1:
                                //System.out.println("********* CORNER_X1Y1");
                                o = e.getOpool().get(obstacleIdx);
                                o.moveX1(mouseXfin - mouseXini);
                                o.moveY1(mouseYfin - mouseYini);
                                //repaint();
                                break;

                            case CORNER_X1Y2:
                                o = e.getOpool().get(obstacleIdx);
                                o.moveX1(mouseXfin - mouseXini);
                                o.moveY2(mouseYfin - mouseYini);
                                //repaint();
                                break;

                            case CORNER_X2Y1:
                                o = e.getOpool().get(obstacleIdx);
                                o.moveX2(mouseXfin - mouseXini);
                                o.moveY1(mouseYfin - mouseYini);
                                //repaint();
                                break;

                            case CORNER_X2Y2:
                                o = e.getOpool().get(obstacleIdx);
                                o.moveX2(mouseXfin - mouseXini);
                                o.moveY2(mouseYfin - mouseYini);

                                //repaint();
                                break;
                        }
                        break;

                    case NONE:
                        break;

                    case omoving:
                        o = e.getOpool().get(obstacleIdx);
                        o.moveTo(mouseXfin - mouseXini, mouseYfin - mouseYini);
                    //repaint();
                    default:
                }

                mouseXini = mouseXfin;
                mouseYini = mouseYfin;
            } catch (Exception ev) {
                log.severe(ev + "..." + bpressed + " editstate:" + editstate+" in MouseInputListener3D::mouseDragged()");
            }
        } else if (bpressed == 2) {
            mouseXfin = x;
            mouseYfin = y;
            Camera cam = m.sf.gameState.getCamera();
            float xloc, yloc, zloc;
            Vector3f location;
            location = cam.getLocation();
            float deltax = mouseXfin - mouseXini;
            float deltaz = mouseYfin - mouseYini;
            //System.out.println("x:"+x+" z:"+y+" dx:"+deltax+" dz:"+deltaz);
            xloc = location.x - 0.07f * dx;
            yloc = location.y;
            zloc = location.z + 0.1f * dy;
            //System.out.println("xloc:"+xloc+" zloc:"+zloc);
            Vector3f newlocation = new Vector3f(xloc, yloc, zloc);
            cam.setLocation(newlocation);
            cam.update();
            mouseXini = mouseXfin;
            mouseYini = mouseYfin;
        }
    }

    public int ClassifyMouseClick(int x, int y, Ray mouseRay) {
        Environment e = m.i.ep.e;
        for (Creature c : e.getCpool()) {
            if (c.contains(x, y)) {
                return CAR;
            }
            //TODO: in order to use the following 3D version, mouseDragged
            //and other methods must be changed to be fully 3D compliants.
            //if (c.contains3D(mouseRay)) return CAR;
            if (c.pointContains(x, y)) {
                return CAR_POINT;
            }
            //if (target.  contains(x, y)) return TARGET;
        }
        for (Thing o : e.getOpool()) {
            if ((o.category == Constants.categoryFOOD) || (o.category == Constants.categoryPFOOD) || (o.category == Constants.categoryNPFOOD) || (o.category == Constants.categoryJEWEL) || (o.category == Constants.categoryCAGE)) {
                //if (o.contains    (x, y)) return e.opool.indexOf(o);
                if (o.contains3D(mouseRay)) {
                    return e.getOpool().indexOf(o);
                }

            } else {
                if (o.containsX1Y1(x, y)) {
                    return CORNER_X1Y1;
                }
                if (o.containsX1Y2(x, y)) {
                    return CORNER_X1Y2;
                }
                if (o.containsX2Y1(x, y)) {
                    return CORNER_X2Y1;
                }
                if (o.containsX2Y2(x, y)) {
                    return CORNER_X2Y2;
                }
                if (o.contains(x, y)) {
                    return e.getOpool().indexOf(o);
                }
            }
        }

        return NONE;
    }

    public int GetCreatureIndex(int x, int y) {
        Environment e = m.i.ep.e;
        for (Creature c : e.getCpool()) {
            if (c.contains(x, y) || c.pointContains(x, y)) {
                return e.getCpool().indexOf(c);
            }
        }
        return -1;
    }

    public int GetObjectIndex(int x, int y) {
        Environment e = m.i.ep.e;
        for (Thing o : e.getOpool()) {
            if (o.containsX1Y1(x, y) || o.containsX1Y2(x, y) || o.containsX2Y1(x, y) || o.containsX2Y2(x, y) || o.contains(x, y)) {
                return e.getOpool().indexOf(o);
            }
        }
        return -1;
    }

//Retorna se o cen?rio foi alterado manualmente
    public boolean hasChanged() {
        return this.hasChanged;
    }

    //ajusta se o cen?rio foi alterado manualmente
    public void setChanged(boolean changed) {
        this.hasChanged = changed;
    }

    public void setCursor(Cursor c) {

        switch (c) {
            case Default:
                MouseInput.get().setHardwareCursor(default_cursor);
                break;
            case Cross:
                MouseInput.get().setHardwareCursor(cross);
                break;
            case NW_Resize:
                MouseInput.get().setHardwareCursor(nw_resize, 0, 0);
                break;
            case NE_Resize:
                MouseInput.get().setHardwareCursor(ne_resize, 0, 0);
                break;
            case SW_Resize:
                MouseInput.get().setHardwareCursor(sw_resize, 0, 0);
                break;
            case SE_Resize:
                MouseInput.get().setHardwareCursor(se_resize, m.i.ep.e.auxx, m.i.ep.e.auxy);
                log.info("xy:" + m.i.ep.e.auxx + " " + m.i.ep.e.auxy);
                break;
            case Hand:
                MouseInput.get().setHardwareCursor(hand);
                break;
            case Dot:
                MouseInput.get().setHardwareCursor(dot);
                break;
            default:
                MouseInput.get().setHardwareCursor(default_cursor);
                break;
        }
    }
}
