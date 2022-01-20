/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldserver3d;

import com.jme3.collision.CollisionResults;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import model.Creature;
import model.Environment;
import model.Material3D;
import model.Thing;
import util.Constants;
import util.Cursor;

/**
 *
 * @author gudwin
 */
public class MouseHandler {
    
    private class XYRay {
        float x;
        float y;
        Ray ray;
        
        void clone(XYRay old) {
            x = old.x;
            y = old.y;
            ray = old.ray;
        }
    }
    
    XYRay firstclick = new XYRay();
    XYRay xyr = new XYRay();
    private WorldApplication app;
    float ang_row = 0;
    float ang_pitch = 0;
    float ang_yaw = 0;
    float x_l = -7;
    float y_l = 2;
    float z_l = 0;
    private InputManager im;
    Camera cam;
    float xoo=0,zoo=0;
    float x=0,y=0;
    boolean drag=false;
    
    // Variáveis herdadas ... rever quando possível
    
    //public Main m;
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

    private float mouseXini, mouseYini;
    private float mouseXfin, mouseYfin;
    private boolean isMoving = false;
    private int objectID = -1;
    private int creatureID = -1;
    private int obstacleIdx = -1;
    public int bpressed = -1;
    private int clickcount = 0;
    private boolean hasChanged = true;
    ////DisplaySystem display;
    
    Logger logger;
    JmeCursor defaultcursor;
    
    public enum TCursor {

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
    
    
    public MouseHandler(WorldApplication appo) {
        super();
        app = appo;
       im = app.getInputManager();    
       cam = app.getCamera();
       defaultcursor = new JmeCursor();
    }
    
    public void initMouse() {
        
        im.addMapping("mright", new MouseAxisTrigger(MouseInput.AXIS_X,false));
        im.addMapping("mleft", new MouseAxisTrigger(MouseInput.AXIS_X,true));
        im.addMapping("mup", new MouseAxisTrigger(MouseInput.AXIS_Y,false));
        im.addMapping("mdown", new MouseAxisTrigger(MouseInput.AXIS_Y,true));
        im.addMapping("mlb", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        im.addMapping("mrb", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        im.addMapping("mcb", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        im.addListener(al, new String[]{"mright","mleft","mup","mdown"});
        im.addListener(acl, new String[]{"mlb","mrb","mcb"});
    }
    
    
    
    public void getMouseClickRay() {
        CollisionResults results = new CollisionResults(); 
        Vector2f click2d = im.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        app.getRootNode().collideWith(ray, results);
        for (int i = 0; i < results.size(); i++) {
           //float dist = results.getCollision(i).getDistance();
           Vector3f pt = results.getCollision(i).getContactPoint();
           String target = results.getCollision(i).getGeometry().getName();
           if (target.equalsIgnoreCase("Floor")) {
               xyr.x = pt.x;
               xyr.y = pt.y;
               xyr.ray = ray;
           }                                     
        }   
    }
    
    private ActionListener acl = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
      int button = -1;  
      if (name.equals("mlb")) button = 0;
      else if (name.equals("mcb")) button = 1;
      else if (name.equals("mrb")) button = 2;
      if (button != -1) {
          getMouseClickRay();
          if (keyPressed) {
              drag=true;
              firstclick.clone(xyr);
              mousePressed(button,xyr.x, xyr.y, xyr.ray);
          }
          else {
              drag=false;
              mouseReleased(button,xyr.x, xyr.y, xyr.ray);
          }
      }
    }
  };
    
    private AnalogListener al = new AnalogListener() {
       public void onAnalog(String name, float value, float tpf) {  
          if (name.equals("mright") || name.equals("mleft") || name.equals("mup") || name.equals("mdown")) {
              getMouseClickRay();
            if(drag) mouseDragged(xyr.x, xyr.y, firstclick.x-xyr.x, firstclick.y-xyr.y);
            else mouseMoved(xyr.x,xyr.y,xyr.ray);
          }
       }
    };
    
    public void Test() {
        CollisionResults results = new CollisionResults(); 
        Vector2f click2d = im.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        //System.out.println(click3d+","+dir);
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
              
        app.getRootNode().collideWith(ray, results);
        for (int i = 0; i < results.size(); i++) {
         // (For each “hit”, we know distance, impact point, geometry.)
              float dist = results.getCollision(i).getDistance();
              Vector3f pt = results.getCollision(i).getContactPoint();
              String target = results.getCollision(i).getGeometry().getName();
              if (target.equalsIgnoreCase("Floor"))
                  System.out.println(pt.x + ", "+pt.y);
              //System.out.println("Selection #" + i + ": " + target + " at " + pt + ", " + dist + " WU away.");
        }
    }
    
    
    
    public void mousePressed(int button, float x, float y, Ray ray) {
        //Test();
        //System.out.println(" *** MouseInputListener3D:   Mouse pressed. ");
        //System.out.println(" *** x= " + x + "  y= " + y);
        Environment e = app.e;
        bpressed = button;
        clickcount++;
        if (clickcount > 2) {
            clickcount = 1;
        }
        try {
            mouseXini = x;
            mouseYini = y;

            if (button == 0 && !isMoving) {
                switch (objectID = ClassifyMouseClick(mouseXini, mouseYini, ray)) {
                    case NONE:
                        ThingCreator tc = new ThingCreator(app.e);
                        Thing th = tc.createThing(Constants.categoryBRICK, mouseXini, mouseYini);
                        //th.addBrick(app);
                        //app.wap.addBrick((Brick)th);
                        objectID = CORNER_X2Y2;
                        obstacleIdx = e.getOpool().size() - 1;
                        editstate = oscaling;
                        th.state = 1;
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
                    // Abre painel para decisão do tipo de inserção
//                    m.i.ep.foodTab.setXY(mouseXini, mouseYini);
//                    m.i.ep.foodTab.setTitle("Creature, Jewel and Food Creation ");
//                    m.i.ep.foodTab.showForCreation();
//                    m.i.ep.foodTab.setVisible(true);
                }
                // Se precisar fazer o update é aqui ...
                //m.sf.gameState.ThingsRN.updateRenderState();
            }
        } catch (Exception ev) {

            System.out.println("aha ... coisa feia ...");
            ev.printStackTrace();
        }
        //System.out.println("xi: "+mouseXini+" yi: "+mouseYini+" xf: "+mouseXfin+" yf:"+mouseYfin);
    }
    
    public void mouseReleased(int button, float x, float y, Ray ray) {
        //System.out.println("MouseReleased: Button="+button+" x: "+x+" y: "+y);
        Environment e = app.e;
        if (button == 0 && clickcount == 2) {
            if (obstacleIdx != -1) {
                obstacleIdx = ClassifyMouseClick(x, y, ray);
                if (obstacleIdx >= 0) {
                    Thing o = e.getOpool().get(obstacleIdx);

                    if (o.category == Constants.categoryFOOD) {
                        System.out.println("Double click in FOOD");
//                        m.i.ep.foodTab.setFood(o);
//                        m.i.ep.foodTab.setXY(x, y);
//                        m.i.ep.foodTab.setTitle("Food " + e.getOpool().indexOf(o) + " visibility edition");
//                        m.i.ep.foodTab.update();
//                        m.i.ep.foodTab.setVisible(true);
                    }
                    if (o.category == Constants.categoryPFOOD) {
                        System.out.println("Double click in PFOOD");
//                        m.i.ep.foodTab.setFood(o);
//                        m.i.ep.foodTab.setXY(x, y);
//                        m.i.ep.foodTab.setTitle("Food " + e.getOpool().indexOf(o) + " visibility edition");
//                        m.i.ep.foodTab.update();
//                        m.i.ep.foodTab.setVisible(true);
                    }
                    if (o.category == Constants.categoryNPFOOD) {
                        System.out.println("Double click in NPFOOD");
//                        m.i.ep.foodTab.setFood(o);
//                        m.i.ep.foodTab.setXY(x, y);
//                        m.i.ep.foodTab.setTitle("Food " + e.getOpool().indexOf(o) + " visibility edition");
//                        m.i.ep.foodTab.update();
//                        m.i.ep.foodTab.setVisible(true);
                    }
                    else if (o.category == Constants.categoryJEWEL) {
                          System.out.println("Double click in JEWEL");
//                        m.i.ep.jewelTab.setObstacle(o);
//                        m.i.ep.jewelTab.setTitle("Edit Jewel " + obstacleIdx);
//
//                        m.i.ep.jewelTab.update();
//                        m.i.ep.jewelTab.setVisible(true);

                    } else if (o.category == Constants.categoryBRICK) {
                        System.out.println("Double click in BRICK");
//                        m.i.ep.obstacleTab.setObstacle(o);
//                        m.i.ep.obstacleTab.setTitle("Edit Obstacle " + obstacleIdx);
//                        m.i.ep.obstacleTab.update();
//                        m.i.ep.obstacleTab.setVisible(true);
//                        System.out.println("Obstacle--- z= " + o.getZ());
                    }
                }

            }
            for (Creature c : e.getCpool()) {
                if (c.contains((double) x, (double) y)) {
                    System.out.println("Double click in CREATURE");
//                    int c_index = e.getCpool().indexOf(c);
//                    if (c_index % 2 == 0) {
//                        e.setCamera(0, c_index);
//                        System.out.println("Even Camera in robot " + e.getCamera(0));
//                    } else {
//                        e.setCamera(1, c_index);
//                        System.out.println("Odd Camera in robot " + e.getCamera(1));
//                    }        
                }
            }
        }//Check if just created brick is sufficiently big: dx and dy >= 1
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
                        e.getOpoolModified().add(o);
                        if ((Math.abs(o.getX2() - o.getX1()) < 1) || (Math.abs(o.getY2() - o.getY1()) < 1)) {
                            e.removeThing(o);
                            System.out.println("****** Invalid obstacle size!!! Removed!!!");
                        }
                    }
                    o.state = 0;
                    isMoving = false;
                    //System.out.println("Finished the creation of new Brick");
                }
            }
            for (Creature c : e.getCpool()) {
                c.draggedState = false;
            }
        } //middle button:
        else if (button == 2) {
            Thing th = getClickedThing(x,y);
            if (th == null) {
                //System.out.println("Right Button released ...");
                JPopupMenu popup = new JPopupMenu();
                JMenuItem jm1 = new JMenuItem("Insert Creature");
                java.awt.event.ActionListener al = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert Creature at "+x+","+y);
                            ThingCreator tc = new ThingCreator(app.e);
                            Creature c = tc.createCreature(false,x,y,0);
                            c.state = 1;
                        }
                };
                jm1.addActionListener(al);
                JMenuItem jm2 = new JMenuItem("Insert Perishable Food");
                java.awt.event.ActionListener al2;
                al2 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert Perishable Food");
                            ThingCreator tc = new ThingCreator(app.e);
                            Thing pf = tc.createThing(Constants.categoryPFOOD, x,y);
                            pf.state = 1;
                        }
                };
                jm2.addActionListener(al2);
                JMenuItem jm3 = new JMenuItem("Insert Non-Perishable Food");
                java.awt.event.ActionListener al3;
                al3 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert Non-Perishable Food");
                            ThingCreator tc = new ThingCreator(app.e);
                            Thing npf = tc.createThing(Constants.categoryNPFOOD, x,y);
                            npf.state = 1;
                        }
                };
                jm3.addActionListener(al3);
                JMenuItem jm4 = new JMenuItem("Insert Red Jewel");
                java.awt.event.ActionListener al4;
                al4 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert Red Jewel");
                            ThingCreator tc = new ThingCreator(app.e);
                            Thing j = tc.createThing(Constants.categoryJEWEL, x,y);
                            j.setMaterial(new Material3D(Color.red));
                            j.state = 1;
                        }
                };
                jm4.addActionListener(al4);
                JMenuItem jm5 = new JMenuItem("Insert Green Jewel");
                java.awt.event.ActionListener al5;
                al5 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert Green Jewel");
                            ThingCreator tc = new ThingCreator(app.e);
                            Thing j = tc.createThing(Constants.categoryJEWEL, x,y);
                            j.setMaterial(new Material3D(Color.green));
                            j.state = 1;
                        }
                };
                jm5.addActionListener(al5);
                JMenuItem jm6 = new JMenuItem("Insert Blue Jewel");
                java.awt.event.ActionListener al6;
                al6 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert Blue Jewel");
                            ThingCreator tc = new ThingCreator(app.e);
                            Thing j = tc.createThing(Constants.categoryJEWEL, x,y);
                            j.setMaterial(new Material3D(Color.blue));
                            j.state = 1;
                        }
                };
                jm6.addActionListener(al6);
                JMenuItem jm7 = new JMenuItem("Insert Yellow Jewel");
                java.awt.event.ActionListener al7;
                al7 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert Yellow Jewel");
                            ThingCreator tc = new ThingCreator(app.e);
                            Thing j = tc.createThing(Constants.categoryJEWEL, x,y);
                            j.setMaterial(new Material3D(Color.yellow));
                            j.state = 1;
                        }
                };
                jm7.addActionListener(al7);
                JMenuItem jm8 = new JMenuItem("Insert Magenta Jewel");
                java.awt.event.ActionListener al8;
                al8 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert Magenta Jewel");
                            ThingCreator tc = new ThingCreator(app.e);
                            Thing j = tc.createThing(Constants.categoryJEWEL, x,y);
                            j.setMaterial(new Material3D(Color.magenta));
                            j.state = 1;
                        }
                };
                jm8.addActionListener(al8);
                JMenuItem jm9 = new JMenuItem("Insert White Jewel");
                java.awt.event.ActionListener al9;
                al9 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Insert White Jewel");
                            ThingCreator tc = new ThingCreator(app.e);
                            Thing j = tc.createThing(Constants.categoryJEWEL, x,y);
                            j.setMaterial(new Material3D(Color.white));
                            j.state = 1;
                        }
                };
                jm9.addActionListener(al9);
                popup.add(jm1);
                popup.add(jm2);
                popup.add(jm3);
                popup.add(jm4);
                popup.add(jm5);
                popup.add(jm6);
                popup.add(jm7);
                popup.add(jm8);
                popup.add(jm9);
                Point p = app.wf.getMousePosition();
                popup.show(app.wf,(int)p.x,(int)p.y);
            }
            else if (th.category == Constants.categoryCREATURE) {
                //System.out.println("I right clicked a Creature");
                JPopupMenu popup = new JPopupMenu();
                JMenuItem jm1 = new JMenuItem("Delete "+th.getMyName());
                java.awt.event.ActionListener al = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Deleting "+th.getMyName());
                            app.wap.scoreTab.remove((Creature)th);
                            app.e.removeCreature((Creature)th);
                            app.wap.updateScoreSubmenu();
                        }
                };
                jm1.addActionListener(al);
                popup.add(jm1);
                Point p = app.wf.getMousePosition();
                popup.show(app.wf,(int)p.x,(int)p.y);
            }
            else {//if (th.category == Constants.categoryPFOOD) {
                //System.out.println("I right clicked an Apple");
                JPopupMenu popup = new JPopupMenu();
                JMenuItem jm1 = new JMenuItem("Delete "+th.getMyName());
                java.awt.event.ActionListener al = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Deleting "+th.getMyName());
                            app.e.removeThing(th);
                        }
                };
                jm1.addActionListener(al);
                popup.add(jm1);
                JMenuItem jm2;
                if (th.wasHidden == false) jm2 = new JMenuItem("Hide "+th.getMyName());
                else jm2 = new JMenuItem("Unhide "+th.getMyName());
                java.awt.event.ActionListener al2 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (th.wasHidden == false) {
                                System.out.println("Hide "+th.getMyName());
                                th.hideMe(app.e);
                            }
                            else {
                                System.out.println("Unhide "+th.getMyName());
                                th.undoHideMe(app.e);
                            }
                        }
                };
                jm2.addActionListener(al2);
                popup.add(jm2);
                if (th.category == Constants.categoryBRICK) {
                    JMenuItem jm3;
                    jm3 = new JMenuItem("Change color do Red ");
                    java.awt.event.ActionListener al3 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            th.setMaterial(new Material3D(Color.red));
                            th.state = 1;
                        }
                    };
                    jm3.addActionListener(al3);
                    popup.add(jm3);
                    JMenuItem jm4;
                    jm4 = new JMenuItem("Change color do Green ");
                    java.awt.event.ActionListener al4 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            th.setMaterial(new Material3D(Color.green));
                            th.state = 1;
                        }
                    };
                    jm4.addActionListener(al4);
                    popup.add(jm4);
                    JMenuItem jm5;
                    jm5 = new JMenuItem("Change color do Blue ");
                    java.awt.event.ActionListener al5 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            th.setMaterial(new Material3D(Color.blue));
                            th.state = 1;
                        }
                    };
                    jm5.addActionListener(al5);
                    popup.add(jm5);
                    JMenuItem jm6;
                    jm6 = new JMenuItem("Change color do Yellow ");
                    java.awt.event.ActionListener al6 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            th.setMaterial(new Material3D(Color.yellow));
                            th.state = 1;
                        }
                    };
                    jm6.addActionListener(al6);
                    popup.add(jm6);
                    JMenuItem jm7;
                    jm7 = new JMenuItem("Change color do Magenta ");
                    java.awt.event.ActionListener al7 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            th.setMaterial(new Material3D(Color.magenta));
                            th.state = 1;
                        }
                    };
                    jm7.addActionListener(al7);
                    popup.add(jm7);
                    JMenuItem jm8;
                    jm8 = new JMenuItem("Change color do White ");
                    java.awt.event.ActionListener al8 = new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            th.setMaterial(new Material3D(Color.white));
                            th.state = 1;
                        }
                    };
                    jm8.addActionListener(al8);
                    popup.add(jm8);
                }
                Point p = app.wf.getMousePosition();
                popup.show(app.wf,(int)p.x,(int)p.y);
            }
            
            for (Creature c : e.getCpool()) {
                //Panel with score of the creature:
                if (c.contains((double) x, (double) y)) {
                    System.out.println("****** Middle button on creature " + e.getCpool().indexOf(c));
//                    if (!m.i.ep.scoreTabList.containsKey(c.getID())) {
//                        try {
//                            KnapsackAndScoreFrame ksf = new KnapsackAndScoreFrame();
//                            ksf.setEnvironment(e);
//                            ksf.setCreature(c);
//                            ksf.setTitle("Knapsack and Score - creature " + e.getCpool().indexOf(c));
//                            ksf.update();
//                            m.i.ep.scoreTabList.put(c.getID(), ksf);
//                        } catch (InvocationTargetException ex) {
//                            Logger.getLogger(MouseInputListener3D.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                    m.i.ep.scoreTabList.get(c.getID()).update();
//                    m.i.ep.scoreTabList.get(c.getID()).setVisible(true);
                }
            }
        }
        else if (button == 1 && clickcount == 1) {
            System.out.println("Middle Button released ...");
        } 
        else if (button == 1 && clickcount == 2) {

            Creature dead = null;
            for (Creature c : e.getCpool()) {
                if (c.contains((double) x, (double) y)) {
                    dead = c;
                }
            }
            int dead_index = -1;
            if (dead != null) {

//                dead_index = e.getCpool().indexOf(dead);
//
//                for (KnapsackAndScoreFrame kasf : m.i.ep.scoreTabList.values()) {
//                    if (kasf.isVisible()) {
//                        kasf.dispose();
//                    }
//
//                }
//
//                m.i.ep.scoreTabList.clear();
//                e.removeCreature(dead);
            }
            //e.updateCameras(dead_index);
        }
        e.cleanUp();
        bpressed = -1;
        int nobjs = e.getOpool().size();
//        System.out.println("Número de bricks: "+nobjs);
//        for (Thing t : e.getOpool()) {
//            ((Brick)t).print();
//        }
    }
    
    public void mouseDragged(float x, float y, float dx, float dy) {
        //System.out.println("MouseDragged: x: "+x+" y: "+y+" dx: "+dx+" dy: "+dy+" fcx: "+firstclick.x+" fcy: "+firstclick.y);
        Environment e = app.e;
        Thing o;
        Creature c;
        //System.out.println("Env:"+e+" bpressed:"+bpressed);
        //System.out.println("Bpressed: "+bpressed);
        clickcount = 0;
        if (bpressed == 0) {
            setChanged(true);
            mouseXfin = x;
            mouseYfin = y;
            //System.out.println("ObjectID: "+objectID);
            try {
                //System.out.println("editstate: "+editstate);
                switch (editstate) {
                    case cmoving:
                        c = e.getCpool().get(creatureID);
                        c.moveTo(mouseXfin - mouseXini, mouseYfin - mouseYini);
                        //repaint();
                        c.draggedState = true;
                        c.state = 1;
                        break;

                    case crotating:
                        c = e.getCpool().get(creatureID);
                        c.rotate(mouseXfin, mouseYfin);
                        //repaint();
                        c.draggedState = true;
                        c.state = 1;
                        break;

                    //case TARGET:
                    //target.move(mouseXfin-mouseXini, mouseYfin-mouseYini);
                    //	repaint();
                    //	break;
                    case oscaling:
                        //System.out.println("objectID:"+objectID);
                        switch (objectID) {
                            case CORNER_X1Y1:
                                //System.out.println("********* CORNER_X1Y1");
                                o = e.getOpool().get(obstacleIdx);
                                o.moveX1(mouseXfin - mouseXini);
                                o.moveY1(mouseYfin - mouseYini);
                                o.state = 1;
                                //repaint();
                                break;

                            case CORNER_X1Y2:
                                //System.out.println("********* CORNER_X1Y2");
                                o = e.getOpool().get(obstacleIdx);
                                o.moveX1(mouseXfin - mouseXini);
                                o.moveY2(mouseYfin - mouseYini);
                                o.state = 1;
                                //repaint();
                                break;

                            case CORNER_X2Y1:
                                //System.out.println("********* CORNER_X2Y1");
                                o = e.getOpool().get(obstacleIdx);
                                o.moveX2(mouseXfin - mouseXini);
                                o.moveY1(mouseYfin - mouseYini);
                                o.state = 1;
                                //repaint();
                                break;

                            case CORNER_X2Y2:
                                //System.out.println("********* CORNER_X2Y2");
                                //System.out.println("opool:"+e.getOpool()+" obstacleIdx:"+obstacleIdx);
                                o = e.getOpool().get(obstacleIdx);
                                //System.out.println("o:"+o);
                                o.moveX2(mouseXfin - mouseXini);
                                o.moveY2(mouseYfin - mouseYini);
                                o.state = 1;
                                //System.out.println("saiu?");
                                //repaint();
                                break;
                        }
                        break;

                    case NONE:
                        break;

                    case omoving:
                        o = e.getOpool().get(obstacleIdx);
                        o.moveTo(mouseXfin - mouseXini, mouseYfin - mouseYini);
                        o.state = 1;
                    //repaint();
                    default:
                }

                mouseXini = mouseXfin;
                mouseYini = mouseYfin;
            } catch (Exception ev) {
                System.out.println(ev + "..." + bpressed + " editstate:" + editstate);
            }
        } else if (bpressed == 2) {
            mouseXfin = x;
            mouseYfin = y;
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
        //System.out.println("xi: "+mouseXini+" yi: "+mouseYini+" xf: "+mouseXfin+" yf:"+mouseYfin);
        //System.out.println("Número de bricks: "+e.getOpool().size());
    }
    
    public void mouseMoved(float x, float y, Ray ray) {
        //System.out.println("MouseMoved: x: "+x+" y: "+y);
        mouseXfin = x;
        mouseYfin = y;
        clickcount = 0;
        int newObjectID = ClassifyMouseClick(mouseXfin, mouseYfin, ray);
        obstacleIdx = GetObjectIndex(mouseXfin, mouseYfin);
        //System.out.println(">>>>>>>>>>> "+newObjectID+" "+obstacleIdx+" "+app.e.getCpool().size());
        if (objectID != newObjectID) {
            switch (objectID = newObjectID) {
                case NONE:
                    setCursor(TCursor.Default);
                    break;

                case CORNER_X1Y1:
                    setCursor(TCursor.NW_Resize);
                    break;

                case CORNER_X1Y2:
                    setCursor(TCursor.SW_Resize);
                    break;

                case CORNER_X2Y1:
                    setCursor(TCursor.NE_Resize);
                    break;

                case CORNER_X2Y2:
                    setCursor(TCursor.SE_Resize);
                    break;
                case CAR_POINT:
                    setCursor(TCursor.Dot);
                    break;
                default:
                    setCursor(TCursor.Hand);
            }
        }
        mouseXini = mouseXfin;
        mouseYini = mouseYfin;
    }
    
    public boolean canCreateHere(Ray ray) {
        Environment e = app.e;
        int pos = ClassifyMouseClick(mouseXini, mouseYini, ray);
        System.out.println(" *** pos= " + pos);
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
            System.out.println("--- This is the Even Camera ---");
            return false;
        } else if (pos == ODDCAMERA) {
            System.out.println("--- This is the Odd Camera ---");
            return false;
        } else {
            return false;
        }
    }
    
    public int ClassifyMouseClick(float x, float y, Ray mouseRay) {
        Environment e = app.e;
        for (Creature c : e.getCpool()) {
            if (c.pointContains(x, y)) {
                return CAR_POINT;
            }
            if (c.contains(x, y)) {
                return CAR;
            }
            //TODO: in order to use the following 3D version, mouseDragged
            //and other methods must be changed to be fully 3D compliants.
            //if (c.contains3D(mouseRay)) return CAR;
            
            //if (target.  contains(x, y)) return TARGET;
        }
        for (Thing o : e.getOpool()) {
            if ((o.category == Constants.categoryFOOD) || 
                (o.category == Constants.categoryPFOOD) ||     
                (o.category == Constants.categoryNPFOOD) ||         
                (o.category == Constants.categoryJEWEL)) {
                //if (o.contains    (x, y)) return e.opool.indexOf(o);
                if (o.contains(x, y)) {
                    return e.getOpool().indexOf(o);
                }
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

    public int GetCreatureIndex(float x, float y) {
        Environment e = app.e;
        for (Creature c : e.getCpool()) {
            if (c.contains(x, y) || c.pointContains(x, y)) {
                return e.getCpool().indexOf(c);
            }
        }
        return -1;
    }

    public Thing getClickedThing(float x, float y) {
        Environment e = app.e;
        for (Creature c : app.e.getCpool()) {
            if (c.contains(x,y)) return(c);
        }
        for (Thing o : e.getOpool()) {
            if (o.contains(x, y)) {
                return(o);
            }
        }
        return(null);
    }
    
    public int GetObjectIndex(float x, float y) {
        Environment e = app.e;
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

    public void setCursor(TCursor c) {

        // Será preciso refazer todo este módulo ... 
        switch (c) {
            case Default:
                im.setCursorVisible(false);
                im.setMouseCursor(Cursor.arrow(app));
                im.setCursorVisible(true);
                //MouseInput.get().setHardwareCursor(default_cursor);
                break;
            case Cross:
                im.setCursorVisible(false);
                im.setMouseCursor(Cursor.cross(app));
                im.setCursorVisible(true);
                break;
            case NW_Resize:
                im.setCursorVisible(false);
                im.setMouseCursor(Cursor.nwse(app));
                im.setCursorVisible(true);
                break;
            case NE_Resize:
                im.setCursorVisible(false);
                im.setMouseCursor(Cursor.nesw(app));
                im.setCursorVisible(true);
                break;
            case SW_Resize:
                im.setCursorVisible(false);
                im.setMouseCursor(Cursor.nesw(app));
                im.setCursorVisible(true);
                break;
            case SE_Resize:
                im.setCursorVisible(false);
                im.setMouseCursor(Cursor.nwse(app));
                im.setCursorVisible(true);
                break;
            case Hand:
                im.setCursorVisible(false);
                im.setMouseCursor(Cursor.hand(app));
                im.setCursorVisible(true);
                break;
            case Dot:
                im.setCursorVisible(false);
                im.setMouseCursor(Cursor.dot(app));
                im.setCursorVisible(true);
                break;
            default:
                im.setCursorVisible(false);
                im.setMouseCursor(defaultcursor);
                im.setCursorVisible(true);
                break;
        }
    }
    
}
