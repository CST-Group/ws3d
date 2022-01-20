/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldserver3d;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import model.Brick;
import model.Creature;
import model.Environment;
import model.Thing;
import util.Constants;
import util.Materials;
import static worldserver3d.IconFactory.log;
import worldserver3d.view.KnapsackAndScoreFrame;
import xml.ReadFromXMLFile;


public class WorldAppState extends AbstractAppState {
 
    private WorldApplication app;
    private Node rootNode;
    private Camera cam;
    private AssetManager am;
    private KeyboardHandler kh;
    private MouseHandler mh;
    private Environment e;
    public HashMap<Thing,Node> shapes;
    private HashMap<Thing,Node> hshapes;
    public HashMap<Creature,KnapsackAndScoreFrame> scoreTab;
    public HashMap<Creature,CameraRenderer> rcams;
    int[]envDim;
    
    //Logger logger;
 
    public WorldAppState(Environment ne) {
        super();
        e = ne;
    }
    
    public void addBrick(Brick b) {
        Node s = getBrickNode(b);
        shapes.put(b,s);
        float x = (float) (b.x2 - b.x1);  //((Brick)o).box.xExtent;
        float y = (float) (b.y2 - b.y1); //((Brick)o).box.yExtent;
        Vector3f scale = new Vector3f(x/2,y/2,1.0f);
        s.setLocalScale(scale);
        s.setLocalTranslation(new Vector3f((float) b.getX1()+x/2, (float) b.getY1()+y/2, 0f));
        rootNode.attachChild(s);
    }
    
    protected Node getBrickNode(Brick b) {
         //float dx = (float) (b.getX2() - b.getX1());
         //float dy = (float) (b.getY2() - b.getY1());
         //Box box = new Box(dx, dy, 2.0f);
         Box box = new Box(1, 1, 4);
         Geometry bgeo = new Geometry("Box", box);
         bgeo.setMaterial(Materials.getMaterial(b.getMaterial().getColorName()));
         //rootNode.attachChild(bgeo);
         Node shapeNode = new Node("Shape Node");
         shapeNode.attachChild(bgeo);
         return (shapeNode);
        }
    
    protected Node createCreature(float x, float y, boolean different) {
        Spatial screature = am.loadModel("models/robot3.glb");
        Node creature = new Node();
        creature.attachChild(screature);
        creature.setLocalScale(.03f);
        //Quaternion quat90 = new Quaternion();
        //quat90.fromAngles((float)Math.PI/2, 0f, (float)Math.PI);
        //creature.setLocalRotation(quat90);
        creature.setLocalTranslation(x,y,2.5f);
        Texture t;
        if (different) t = am.loadTexture("textures/yellow_metal.jpg");
        else t = am.loadTexture("textures/red.jpg");
        Material rmat = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        rmat.setFloat("Shininess", 5f);
        rmat.setBoolean("UseMaterialColors",true);
        rmat.setColor("Specular",ColorRGBA.White);
        rmat.setColor("Diffuse",ColorRGBA.Black);
        rmat.setTexture("SpecularMap", t);
        creature.setMaterial(rmat);
        return(creature);
    }
    
//    public void addNut(NonPerishableFood pf) {
//        Node s = createNut((float)pf.getX(),(float)pf.getY());
//        shapes.put(pf,s);
//        rootNode.attachChild(s);
//    }
    
    public Node createNut(float x, float y) {
        Spatial snut = am.loadModel("models/nut2.glb");
        Node nut = new Node();
        nut.attachChild(snut);
        nut.setLocalScale(1f);
        Quaternion quat90 = new Quaternion();
        quat90.fromAngles((float)Math.PI/2, 0f, 0f);
        nut.setLocalRotation(quat90);
        nut.setLocalTranslation(x,y,1.7f);
        //nut.setLocalTranslation(0,0,1.7f);
        return(nut);
    }
    
    public Node createArrow(float x, float y) {
        Spatial sarrow = am.loadModel("models/arrow.glb");
        Node arrow = new Node();
        arrow.attachChild(sarrow);
        arrow.setLocalScale(1f);
        Quaternion quat90 = new Quaternion();
        quat90.fromAngles(0f, (float)Math.PI/2, 0f);
        arrow.setLocalRotation(quat90);
        arrow.setLocalTranslation(x,y,1.7f);
        arrow.setMaterial(Materials.getMaterial("orange"));
        return(arrow);
    }
    
    public Node createApple(float x, float y) {
        Spatial sapple = am.loadModel("models/apple2.glb");
        Node apple = new Node();
        apple.attachChild(sapple);
        apple.setLocalScale(0.03f);
        //Quaternion quat90 = new Quaternion();
        apple.setLocalTranslation(x,y,1.0f);
        return(apple);
    }
    
    public Node createJewel(float x, float y, Color color) {
        Spatial sjewel = am.loadModel("models/jewel.glb");
        Node jewel = new Node();
        jewel.attachChild(sjewel);
        if (color == Color.red) {
           jewel.setMaterial(Materials.getMaterial("red"));
        }
        else if (color == Color.green) {
           jewel.setMaterial(Materials.getMaterial("green"));
        }
        else if (color == Color.blue) {
           jewel.setMaterial(Materials.getMaterial("blue"));
        }
        else if (color == Color.yellow) {
           jewel.setMaterial(Materials.getMaterial("yellow"));
        }
        else if (color == Color.magenta) {
           jewel.setMaterial(Materials.getMaterial("magenta"));
        }
        else if (color == Color.white) {
           jewel.setMaterial(Materials.getMaterial("white"));
        }
        jewel.setLocalScale(0.6f);
        Quaternion quat90 = new Quaternion();
        quat90.fromAngles((float)Math.PI/2, 0f, 0f);
        jewel.setLocalRotation(quat90);
        jewel.setLocalTranslation(x,y,1.8f);
        return(jewel);
    }
 
    @Override
    public void initialize(AppStateManager stateManager, Application appo) {
        super.initialize(stateManager, app); 
        app = (WorldApplication)appo;
        kh = new KeyboardHandler(app);
        mh = new MouseHandler(app);
        cam = app.getCamera();
        rootNode = app.getRootNode();
        am = app.getAssetManager();
        shapes = new HashMap<Thing,Node>();
        hshapes = new HashMap<Thing,Node>();
        scoreTab = new HashMap<Creature,KnapsackAndScoreFrame>();
        rcams = new HashMap<Creature,CameraRenderer>();
        
        // Move the camera a bit.
        //cam = this.getCamera().cam;
        //cam.setLocation(new Vector3f(50, 135, 50));
//        cam.setLocation(new Vector3f(0, 0, 50));
//        cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));
//        cam.update();
        
        cam.setLocation(new Vector3f(0, -80, 60));
        cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));
        cam.update();
        
        HashMap<String,Material> mpool = new HashMap();
        Material m = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors",true);
        m.setColor("Ambient", ColorRGBA.Red);
        m.setColor("Diffuse", ColorRGBA.Red);
        mpool.put("red", m);
        m = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors",true);
        m.setColor("Ambient", ColorRGBA.Green);
        m.setColor("Diffuse", ColorRGBA.Green);
        mpool.put("green", m);
        m = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors",true);
        m.setColor("Ambient", ColorRGBA.Blue);
        m.setColor("Diffuse", ColorRGBA.Blue);
        mpool.put("blue", m);
        m = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors",true);
        m.setColor("Ambient", ColorRGBA.Yellow);
        m.setColor("Diffuse", ColorRGBA.Yellow);
        mpool.put("yellow", m);
        
        //m = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        m = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors",true);
        m.setColor("Ambient", ColorRGBA.Magenta);
        m.setColor("Diffuse", ColorRGBA.Magenta);
        //Texture tt;
        //tt = am.loadTexture("images/red.jpg");
        //tt.setWrap(Texture.WrapMode.MirroredRepeat);
        //m.setTexture("DiffuseMap", tt);
        
//        Material mat = new Material(assetManager,"Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (illuminated).
//        mat.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
//        mat.setColor("Ambient", ColorRGBA.Blue);   // ... color of this object
//        mat.setColor("Diffuse", ColorRGBA.Blue);   // ... color of light being reflected
//        myGeometry.setMaterial(mat);               // Use new material on this Geometry.
        mpool.put("magenta", m);
        m = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors",true);
        m.setColor("Ambient", ColorRGBA.White);
        m.setColor("Diffuse", ColorRGBA.White);
        mpool.put("white", m);
        m = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors",true);
        m.setColor("Ambient", ColorRGBA.DarkGray);
        m.setColor("Diffuse", ColorRGBA.DarkGray);
        mpool.put("darkgray", m);
        m = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        m.setBoolean("UseMaterialColors",true);
        m.setColor("Ambient", ColorRGBA.Orange);
        m.setColor("Diffuse", ColorRGBA.Orange);
        mpool.put("orange", m);
        Materials.setMPool(mpool);
        Quad floorQuad = new Quad(e.width / 10, e.height / 10);
        TangentBinormalGenerator.generate(floorQuad); 
        //floorQuad.setBound(new BoundingBox());
        //floorQuad.updateBound();
        //floorQuad.setIsCollidable(false);
        Texture texture;
        texture = am.loadTexture("textures/checker_medium.jpg");
        texture.setWrap(Texture.WrapMode.Repeat);
        Material mat_brick = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_brick.setTexture("ColorMap", texture);
        //mat_brick.setFloat("Tex2Scale", 32f);
        Geometry gg = new Geometry("Floor",floorQuad);
        gg.setMaterial(mat_brick);
        gg.center();
        //gg.setLocalRotation(new Quaternion(new float[]{90 * FastMath.DEG_TO_RAD, 0, 0}));
        rootNode.attachChild(gg);
        
        
        float altura = 50;
        float posicao = 300;
        PointLight l1 = new PointLight();
        l1.setPosition(new Vector3f(posicao, 0, altura));
        l1.setPosition(new Vector3f(e.width, e.height,100));
        //l1.setEnabled(true);
        //l1.setRadius(1);
        PointLight l2 = new PointLight();
        l2.setPosition(new Vector3f(-posicao, 0, altura));
        l2.setPosition(new Vector3f(-e.width, -e.height,100));
        PointLight l3 = new PointLight();
        l3.setPosition(new Vector3f(0, posicao, altura));
        PointLight l4 = new PointLight();
        l4.setPosition(new Vector3f(0, -posicao, altura));
        rootNode.addLight(l1);
        rootNode.addLight(l2);
        //rootNode.addLight(l3);
       // rootNode.addLight(l4);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        //rootNode.addLight(sun);
        DirectionalLight down = new DirectionalLight();
        down.setDirection(new Vector3f(1,0,2).normalizeLocal());
        down.setColor(ColorRGBA.White);
        //rootNode.addLight(down);
        kh.initKeys();
        mh.initMouse();
        //getFlyByCamera().setDragToRotate(true);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        //setupOffscreenView(app.offBuffer);
        System.out.println("Finished initialization ...");
   }
    
    float radtodeg(float rad) {
        return(180*rad/(float)Math.PI);
    }
    
//    public void locateCamera(Creature c) {
//        Camera cam = rcams.get(c).getCamera();
//        if(cam != null) {
//            cam.setLocation(shapes.get(c).getLocalTranslation());
//            Vector3f location = new Vector3f(shapes.get(c).getLocalTranslation());
//            //location.x -= 1.1;
//            location.z += 2.5;
//            cam.setLocation(location);
//            Quaternion crot = new Quaternion(shapes.get(c).getLocalRotation());
//            Quaternion turn = new Quaternion();
//            turn.fromAngles(0,-(float)Math.PI/2,0);
//            float angs[] = crot.toAngles(null);
//            //crot.fromAngles(angs[0],angs[1],angs[2]-(float)Math.PI/2);
//            System.out.format("row: %.0f pitch: %.0f yaw: %.0f x:%.1f y:%.1f z:%.1f w:%.1f",radtodeg(angs[0]),radtodeg(angs[1]),radtodeg(angs[2]-(float)Math.PI/2),crot.getX(),crot.getY(),crot.getZ(),crot.getW());
//            cam.setRotation(crot.mult(turn));
//        }    
//    }
 
//    public void setupOffscreenView(CameraRenderer cr, Camera offCamera, FrameBuffer offBuffer, Creature c){
//        //Camera offCamera = new Camera(Constants.CamResolutionX, Constants.CamResolutionY);
//        //Camera offCamera = app.getCamera().clone();
//        System.out.println("The original camera has dimensions: "+offCamera.getWidth()+","+offCamera.getHeight());
//        ViewPort offView = app.getRenderManager().createPreView("Offscreen View", offCamera);        
//        offView.setBackgroundColor(ColorRGBA.DarkGray);
//        offView.setClearFlags(true, true, true);
//        offView.addProcessor(cr);
//        offCamera.setFrustumPerspective(80f, 1f, 0.01f, 1000f);
//        locateCamera(offCamera,c);
//        offBuffer.setDepthBuffer(Image.Format.Depth);
//        offBuffer.setColorBuffer(Image.Format.RGBA8);
//        offView.setOutputFrameBuffer(offBuffer);
//        offView.attachScene(rootNode);
//    }
    
    
   @Override
    public void cleanup() {
      super.cleanup();
    }
 
    @Override
    public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
    }
 
    Node n;
    @Override
    public void update(float tpf) {
        for (Thing o : e.getOpool()) {
            float x = (float) (o.x2 - o.x1);  //((Brick)o).box.xExtent;
            float y = (float) (o.y2 - o.y1); //((Brick)o).box.yExtent;
            if (o.state == 1) {
                switch(o.category) {
                    case (Constants.categoryBRICK):
                        if (shapes.get(o) == null) {
                            addBrick((Brick)o);
                        }
                        else {
                            
                            Node s = shapes.get(o);
                            s.setMaterial(Materials.getMaterial(o.getMaterial().getColorName()));
                            s.setLocalTranslation(new Vector3f((float) o.getX1()+x/2, (float) o.getY1()+y/2, 0f));
                            if (x < 0) x = -x;
                            if (y < 0) y = -y;
                            Vector3f scale = new Vector3f(x/2,y/2,1.0f);
                            s.setLocalScale(scale);
                            
                            o.state = 0;
                        }
                        break;
                    case (Constants.categoryNPFOOD):
                        if (shapes.get(o) == null) {
                            Node nut = createNut((float)o.getX(),(float)o.getY());
                            System.out.format("Nut: %.0f,%.0f",o.getX(),o.getY());
                            shapes.put(o, nut);
                            Node hnut = createArrow((float)o.getX(),(float)o.getY());
                            hshapes.put(o,hnut);
                            rootNode.attachChild(hnut);
                            rootNode.attachChild(nut);
                            o.state = 0;
                        }
                        else {
                            Node nut = shapes.get(o);
                            nut.setLocalTranslation((float)o.getX(), (float)o.getY(), 1.7f);
                            //Spatial hnut = hshapes.get(o);
                            //hnut.setLocalTranslation((float)o.getX(), (float)o.getY(), 1.7f);
                            o.state = 0;
                        }   
                        break;
                    case (Constants.categoryPFOOD):
                        if (shapes.get(o) == null) {
                            Node apple = createApple((float)o.getX(),(float)o.getY());
                            rootNode.attachChild(apple);
                            shapes.put(o, apple);
                            Node happle = createArrow((float)o.getX(),(float)o.getY());
                            hshapes.put(o,happle);
                            rootNode.attachChild(happle);
                            o.state = 0;
                        }
                        else {
                            Node apple = shapes.get(o);
                            apple.setLocalTranslation((float)o.getX(), (float)o.getY(), 1.0f);
                            o.state = 0;
                        }   
                        break;
                    case (Constants.categoryJEWEL):
                        if (shapes.get(o) == null) {
                            Node jewel = createJewel((float)o.getX(),(float)o.getY(),o.getMaterial().getColor());
                            rootNode.attachChild(jewel);
                            shapes.put(o, jewel);
                            Node hjewel = createArrow((float)o.getX(),(float)o.getY());
                            hshapes.put(o,hjewel);
                            rootNode.attachChild(hjewel);
                            o.state = 0;
                        }
                        else {
                            Node apple = shapes.get(o);
                            apple.setLocalTranslation((float)o.getX(), (float)o.getY(), 1.8f);
                            o.state = 0;
                        }   
                        break;    
                }        
                    
            }        
                    
       }
      for (Creature c : e.getCpool()) {
        if (c.state == 1) {  
            if (shapes.get((Thing)c) == null) {
                Node s=null;
                try {                
                        // Create the character for the new creature
                        s = createCreature((float)c.getX(),(float)c.getY(),false);
                        rootNode.attachChild(s);
                        shapes.put(c, s);
                        // Create the Knapsack and Score JFrame and update the menu
                        KnapsackAndScoreFrame ksf = new KnapsackAndScoreFrame();
                        ksf.setEnvironment(e);
                        ksf.setCreature(c);
                        ksf.setTitle("Knapsack and Score - creature " + e.getCpool().indexOf(c));
                        ksf.update();
                        JMenu scoreMenu = app.wf.getScoreMenu();
                        scoreTab.put(c, ksf);
                        //ksf.setVisible(true);
                } catch(Exception e) {
                    System.out.println(e);
                }
                updateScoreSubmenu();
                // Create the camera for new creature
                CameraRenderer cr = new CameraRenderer(app,rootNode,c);
                rcams.put(c, cr);
                Quaternion turn = new Quaternion();
                turn.fromAngles(0,-(float)Math.PI/2,0);
                cr.camNode.setLocalRotation(turn);
                //locateCamera(c);
                s.attachChild(cr.camNode);
                cr.createDisplayFrame();
                c.state = 0;
            }
          else {
                Node s = shapes.get((Thing)c);
                s.setLocalTranslation((float)c.getX(), (float)c.getY(), 2.5f);
                Quaternion q = new Quaternion();
                //q.fromAngles((float)Math.PI/2,0,(float)(c.getPitch()-Math.PI));
                q.fromAngles((float)Math.PI/2,0,(float)((c.getPitch()-180)*Math.PI)/180);
                //System.out.println(c.getPitch());
                s.setLocalRotation(q);
                c.state = 0;
          }   
        }
        else {
            // First moves the creature to its new position
            Node s = shapes.get((Thing)c);
            if (s != null) {
                s.setLocalTranslation((float)c.getX(), (float)c.getY(), 2.5f);
                Quaternion q = new Quaternion();
                //q.fromAngles((float)Math.PI/2,0,(float)(c.getPitch()*Math.PI)/180);
                //q.fromAngles((float)Math.PI/2,0,(float)(c.getPitch()-Math.PI));
                q.fromAngles((float)Math.PI/2,0,(float)((c.getPitch()-180)*Math.PI)/180);
                s.setLocalRotation(q);
            }
            // Then, update its visual sensor for the new position
            updateVisualSensor(c);
            
        } 
      }
      for (Thing t : e.deletelist) {
          Node s = shapes.get(t);
          if (s != null) {
            rootNode.detachChild(s);
            shapes.remove(t);
          }
          else System.out.println("Was not able to find spatial "+t.getMyName());
          Node hs = hshapes.get(t);
          if (hs != null) {
            rootNode.detachChild(hs);
            hshapes.remove(t);
          }
          else System.out.println("Was not able to find spatial "+t.getMyName());
          if (t.category == Constants.categoryCREATURE) {
                KnapsackAndScoreFrame frame = scoreTab.get(t);
                frame.setVisible(false);
                frame.dispose();
                scoreTab.remove(t);
                CameraRenderer cr = rcams.get(t);
                JFrame jf = cr.frame;
                jf.setVisible(false);
                jf.dispose();
                rcams.remove(t);
          }
      }
      e.deletelist = new ArrayList<Thing>();
      for (Thing t : e.getOpoolModified()) {
          Node s = shapes.get(t);
          Node hs = hshapes.get(t);
          if (s != null && hs != null) {
              if (t.wasHidden == true) {
                    Vector3f old = s.getLocalTranslation();
                    s.setLocalTranslation(old.x, old.y, old.z-10);
                    hs.setLocalTranslation(old.x,old.y, 1.7f);
              }
              else {
                    Vector3f old = s.getLocalTranslation();
                    s.setLocalTranslation(old.x, old.y, old.z);
                    hs.setLocalTranslation(old.x,old.y, -10);
              }
          }
          t.state = 0;
      }
      // Now this loop provides dynamics to the creatures
      for (Creature c : e.getCpool()) {
          boolean coll = checkIfCreatureHasCollided(c);
          if (!coll) c.move(e);
          //System.out.format("%s: %.2f,%.2f,%.2f\r\n",c.getMyName(),c.getX(),c.getY(),c.getPitch());
      }
    }
    
    private synchronized boolean checkIfCreatureHasCollided(Creature c) {
       // synchronized (wEnv.semaphore2) {
            for (Thing th : e.getEveryThingExceptMe(c)) {
                Node thing = shapes.get(th);
                Node creature = shapes.get(c);
                BoundingVolume bthing = thing.getWorldBound();
                Vector3f thingpoint = thing.getLocalTranslation();
                double np[] = c.calculateNextPosition();
                Vector3f cpoint = creature.getLocalTranslation();
                Vector3f cnpoint = new Vector3f((float)np[0],(float)np[1],cpoint.z);
                //System.out.format("current:%.0f,%.0f next:%.0f,%.0f ",cpoint.x,cpoint.y,cnpoint.x,cnpoint.y);
                Vector3f direction = new Vector3f(cnpoint.x-cpoint.x,cnpoint.y-cpoint.y,cnpoint.z-cpoint.z);
                Ray r = new Ray(cpoint,direction);
                double x = cpoint.x-cnpoint.x;
                double y = cpoint.y-cnpoint.y;
                double diag = Math.sqrt(x*x+y*y);
                //System.out.format("x:%.2f y:%.2f diag:%.2f",x,y,diag);
                r.limit = (float)diag;
                Vector3f block = bthing.getCenter();
                //System.out.format("b: %.0f,%.0f direction:%.0f,%.0f,%.0f",block.x,block.y,direction.x,direction.y,direction.z);
                //System.out.format(" The ray has lenght %.2f ",r.limit);
                //System.out.format(" %s",r);
                
                //BoundingVolume bcreature = creature.getWorldBound();
                CollisionResults collisionResults = new CollisionResults(); 
                collisionResults.clear();
                bthing.collideWith(r,collisionResults);
                //bcreature.collideWith(bthing,collisionResults);
                if (collisionResults.size() > 0) {
                    Vector3f colision = collisionResults.getClosestCollision().getContactPoint();
                    //System.out.format(" colision:%.0f,%.0f,%.0f ",colision.x,colision.y,colision.z);
                    x = cpoint.x - colision.x;
                    y = cpoint.y - colision.y;
                    double distance = Math.sqrt(x*x+y*y);
                    BoundingSphere bs = new BoundingSphere((float)Constants.CREATURE_SIZE/2,cnpoint);
                    int cols = bs.collideWith(bthing);
                    //System.out.format(" dcalc:%.1f d:%.1f",distance,collisionResults.getClosestCollision().getDistance());
                    if (distance < diag || cols > 0) {
                        System.out.format("%s collides with %s in next move\r\n",c.getMyName(),th.getMyName());
                        return true;
                    }    
                }
            }
            return false;
    }
    
            
    
    public void updateScoreSubmenu() {
        JMenu creatureScore = app.wf.getScoreMenu();
        JMenu camMenu = app.wf.getCamMenu();
        if (e.getCpool().size() == 0) {
            creatureScore.removeAll();
            creatureScore.add(new JMenuItem("No creatures yet!"));
            camMenu.removeAll();
            camMenu.add(new JMenuItem("No creatures yet!"));
        } else {
            creatureScore.removeAll();
            camMenu.removeAll();
            for (final Creature c : e.getCpool()) {
                JMenuItem item = new JMenuItem("Creature " + c.getMyName());
                item.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ev) {
                        KnapsackAndScoreFrame st = scoreTab.get(c);
                        if (st != null) {
                            st.setEnvironment(e);
                            st.setCreature(c);
                            st.setTitle("Knapsack and Score - creature " + c.getMyName());
                            st.update();
                            st.setVisible(true);
                        }
                    }    
                });
                creatureScore.add(item);
                JMenuItem item2 = new JMenuItem(c.getMyName());
                item2.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ev) {
                        CameraRenderer st = rcams.get(c);
                        if (st != null) {
                            JFrame jf = st.frame;
                            jf.setVisible(true);
                        }
                    }    
                });
                camMenu.add(item2);
            }
        }
    }
    
    public void updateVisualSensor(Creature c) {
        VisualSensor vs = new VisualSensor(c,rootNode,this);
        c.clearThingsInCamera();
        c.isVisualSensorActivated = true;
        if (c.isVisualSensorActivated) {
            synchronized (e.semaphore2) {
                for (Thing o : e.getOpool()) {
                    if (!o.wasHidden) {
                        if (vs.returnIfCaptured(o, e)) {
                            c.addToThingsInCamera(o);
                        }
                                //c.getVisualSensor().cameraTest(o);

                    }
                }
            }
            //add other creatures in game (except the current creature)
            for (Thing oc : e.getCreaturesExceptMe(c)) {
                if (vs.returnIfCaptured(oc, e)) {
                    c.addToThingsInCamera(oc);
                }
            }
        }
//        System.out.print("Viewed things: ");
//        for (Thing t : c.getThingsInCamera()) {
//            System.out.print(t.getMyName()+" ");
//        }
//        System.out.println("");
    }
    
   public void open() {
       try {
                processNewWorld();
                String fileName;
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileName = chooser.getSelectedFile().getCanonicalPath();
                    log.info("You chose to open this file: " + fileName);
                    readEnvDimensionsFromFile(fileName);
                    //updateEnvironment(envDim[0], envDim[1], null);
                    e.open(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
   } 
   
   public void save() {
       try {
                String fileName;
                JFileChooser chooser = new JFileChooser();

                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    fileName = chooser.getSelectedFile().getCanonicalPath();
                    log.info("You chose to save in this file: " + fileName);
                    e.save(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
   }
   
   public void newworld() {
       processNewWorld();
       updateScoreSubmenu();
       e.print();
            //Launch the frame to recreate the environment.
            //A new Environment object is instantiated.
            //nwf.setVisible(true);
   }
   
   private void processNewWorld() {
       for (Creature c: e.getCpool()) {
           e.deletelist.add(c);
       }
       for (Thing t: e.getOpool()) {
           e.deletelist.add(t);
       } 
       e.startTheGame(false);
        //ThingsRN.detachAllChildren(); //Quad is atached only to robotThingsRN
        //detachEntitiesChildren(robotThingsRN);
        //e.rcnEven.robot_view= -1;
        //e.rcnOdd.robot_view= -1;
        e.getCpool().clear();
        e.getOpool().clear();
        e.thingMap.clear();
        //e.wpPool.removeAll(e.wpPool);
        //e.wpNdsPoolMap.clear();
        //e.rmiPool.removeAll(e.rmiPool);
        //ThingsRN.updateWorldData(0);
        e.notifyNumberOfCreatureObservers();
    }
   
   public void readEnvDimensionsFromFile(String fileName) {
        ReadFromXMLFile reader = new ReadFromXMLFile(fileName);
        envDim = reader.readDimFromFile();
        log.info("SGS::envDim: "+" "+envDim[0]+" "+envDim[1]);
    }
    
}