/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldserver3d;

import com.jme3.app.SimpleApplication;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import util.Constants;

/**
 *
 * @author gudwin
 */
public class KeyboardHandler {
    
    float fps=0;
    
    private WorldApplication app;
    float ang_row = 0;
    float ang_pitch = 0;
    float ang_yaw = 0;
//    float x_l = -7;
//    float y_l = 2;
//    float z_l = 0;
    private InputManager im;
    Camera cam;
    float xoo=0,yoo=0;
    
    public KeyboardHandler(WorldApplication appo) {
        super();
        app = appo;
       im = app.getInputManager();    
       cam = app.getCamera();
    }
    
    private void PrintQuaternion(Quaternion q) {
        System.out.println("Quaternion x: "+q.getX()+" y: "+q.getY()+" z: "+q.getZ()+" w: "+q.getW());
    }
    
    private void getrpy() {
        Quaternion q = cam.getRotation();
        float rpy[] = q.toAngles(null);
        ang_row = rpy[0] * 180 / 3.14159265f;
        ang_pitch = rpy[1] * 180 / 3.14159265f;
        ang_yaw = rpy[2] * 180 / 3.14159265f;
    }
    
    private void setrpy() {
        Quaternion q1 = new Quaternion();
        q1.fromAngles(ang_row * 3.14159265f / 180, ang_pitch * 3.14159265f / 180, ang_yaw * 3.14159265f / 180);
        q1.normalizeLocal();
        cam.setAxes(q1);
        cam.update();
    }
    
    private void printrpy() {
        getrpy();
        Vector3f l = cam.getLocation();
        System.out.print("row: "+ang_row+" pitch: "+ang_pitch+" yaw: "+ang_yaw+ " x: "+l.x+" y:"+l.y+" z: "+l.z);
        PrintQuaternion(cam.getRotation());
    }
    
    public void initKeys() {
    // You can map one or several inputs to one named action
    im.clearMappings();    
    im.addMapping("row-", new KeyTrigger(KeyInput.KEY_I));
    im.addMapping("row+", new KeyTrigger(KeyInput.KEY_O));
    im.addMapping("pitch-", new KeyTrigger(KeyInput.KEY_J));
    im.addMapping("pitch+", new KeyTrigger(KeyInput.KEY_K));
    im.addMapping("yaw-", new KeyTrigger(KeyInput.KEY_N));
    im.addMapping("yaw+", new KeyTrigger(KeyInput.KEY_M));
    im.addMapping("xm", new KeyTrigger(KeyInput.KEY_1));
    im.addMapping("xM", new KeyTrigger(KeyInput.KEY_2));
    im.addMapping("ym", new KeyTrigger(KeyInput.KEY_3));
    im.addMapping("yM", new KeyTrigger(KeyInput.KEY_4));
    im.addMapping("zm", new KeyTrigger(KeyInput.KEY_5));
    im.addMapping("zM", new KeyTrigger(KeyInput.KEY_6));
    im.addMapping("resetcamera", new KeyTrigger(KeyInput.KEY_R));
    im.addMapping("resetcamera2", new KeyTrigger(KeyInput.KEY_E));
    im.addMapping("orthocamera", new KeyTrigger(KeyInput.KEY_T));
    im.addMapping("printrpy", new KeyTrigger(KeyInput.KEY_7));
    
    im.addMapping("forward", new KeyTrigger(KeyInput.KEY_W));
    im.addMapping("backward", new KeyTrigger(KeyInput.KEY_S));
    im.addMapping("strafeLeft", new KeyTrigger(KeyInput.KEY_A));
    im.addMapping("strafeRight", new KeyTrigger(KeyInput.KEY_D));
    im.addMapping("lookUp", new KeyTrigger(KeyInput.KEY_M));
    im.addMapping("lookDown", new KeyTrigger(KeyInput.KEY_N));
    im.addMapping("turnRight", new KeyTrigger(KeyInput.KEY_O));
    im.addMapping("turnLeft", new KeyTrigger(KeyInput.KEY_P));
    im.addMapping("elevateUp", new KeyTrigger(KeyInput.KEY_Q));
    im.addMapping("elevateDown", new KeyTrigger(KeyInput.KEY_Z));
    im.addMapping("tleft", new KeyTrigger(KeyInput.KEY_C));
    im.addMapping("tright", new KeyTrigger(KeyInput.KEY_V));
    im.addMapping("ascend", new KeyTrigger(KeyInput.KEY_Y));
    im.addMapping("descend", new KeyTrigger(KeyInput.KEY_H));
    im.addMapping("moveforward", new KeyTrigger(KeyInput.KEY_UP));
    im.addMapping("movebackward", new KeyTrigger(KeyInput.KEY_DOWN));
    im.addMapping("spinleft", new KeyTrigger(KeyInput.KEY_LEFT));
    im.addMapping("spinright", new KeyTrigger(KeyInput.KEY_RIGHT));
    
    
    // Add the names to the action listener.
    im.addListener(al, new String[]{"row-","pitch-","yaw-",
        "row+","pitch+","yaw+","xm","xM","ym","yM","zm","zM",
        "resetcamera","resetcamera2","orthocamera","printrpy",
        "forward", "backward", "strafeLeft","strafeRight",
        "lookUp","lookDown", "turnRight", "turnLeft",
        "elevateUp","elevateDown",
        "ascend", "descend", "moveforward", "movebackward",
        "spinleft","spinright"});
    im.addListener(actl, new String[]{"tleft","tright"});
 
  }
   
  private ActionListener actl = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (keyPressed) {
          if (name.equals("tleft")) ProcTLeft();
          if (name.equals("tright")) ProcTRight();
        }
    }
  };  
  
  private AnalogListener al = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {
      fps = tpf;  
      
      if (name.equals("printrpy")) {
            printrpy();
      }  
      if (name.equals("resetcamera")) {
            //cam.setLocation(new Vector3f(0, -80, 60));
            cam.setLocation(new Vector3f(app.e.width/2, -app.e.height/2, 300));
            cam.lookAt(new Vector3f(app.e.width/2, app.e.height/2, 0), new Vector3f(0, 0, 1));
            cam.update();
            //ang_row = -60; ang_pitch = -180; ang_yaw = 0;
            //cam.setLocation(new Vector3f(0, 0, 3));
            //ang_row = 0; ang_pitch = 180; ang_yaw = 90;
            //setrpy();
            //cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));
            //cam.update();
      }
      if (name.equals("resetcamera2")) {
            //cam.setLocation(new Vector3f(0, -80, 46));
            //cam.setLocation(new Vector3f(0, 0, 3));
            cam.setLocation(new Vector3f(0,-60,40));
            cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));
            cam.update();
            //ang_row = -60; ang_pitch = 180; ang_yaw = 0;
            //ang_row = 0; ang_pitch = 180; ang_yaw = 100;
            //setrpy();
            //cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1));
            //cam.update();
      }
      if (name.equals("orthocamera")) {
          cam.setLocation(new Vector3f(app.e.width/2, app.e.height/2, 980));
            cam.lookAt(new Vector3f(app.e.width/2, app.e.height/2, 0), new Vector3f(0, 0, 1));
        }
        //float delta = 0.5f;
        float delta = fps * Constants.mvel;
        if (name.equals("xm")) {
            Vector3f location = cam.getLocation();
            cam.setLocation(new Vector3f(location.x - delta, location.y, location.z));
        }
        if (name.equals("xM")) {
            Vector3f location = cam.getLocation();
            cam.setLocation(new Vector3f(location.x + delta, location.y, location.z));
        }
        if (name.equals("ym")) {
            Vector3f location = cam.getLocation();
            cam.setLocation(new Vector3f(location.x, location.y - delta, location.z));
        }
        if (name.equals("yM")) {
            Vector3f location = cam.getLocation();
            cam.setLocation(new Vector3f(location.x, location.y + delta, location.z));
        }
        if (name.equals("zm")) {
            Vector3f location = cam.getLocation();
            cam.setLocation(new Vector3f(location.x, location.y, location.z - delta));
        }
        if (name.equals("zM")) {
            Vector3f location = cam.getLocation();
            cam.setLocation(new Vector3f(location.x, location.y, location.z + delta));
        }
        if (name.equals("row-")) {
            getrpy();
            ang_row -= delta;
            setrpy();
        }
        if (name.equals("row+")) {
            getrpy();
            ang_row += delta;
            setrpy();
        }
        if (name.equals("pitch-")) {
            getrpy();
            ang_pitch -= delta;
            setrpy();
        }
        if (name.equals("pitch+")) {
            getrpy();
            ang_pitch += delta;
            setrpy();
        }
        if (name.equals("yaw-")) {
            getrpy();
            ang_yaw -= delta;
            setrpy();
        }
        if (name.equals("yaw+")) {
            getrpy();
            ang_yaw += delta;
            setrpy();
        }
        if (name.equals("forward")) ProcForward();
        if (name.equals("backward")) ProcBackward();
        if (name.equals("strafeLeft")) ProcStrafeLeft();
        if (name.equals("strafeRight")) ProcStrafeRight();
        if (name.equals("lookUp")) ProcLookUp();
        if (name.equals("lookDown")) ProcLookDown();
        if (name.equals("turnRight")) ProcTurnRight();
        if (name.equals("turnLeft")) ProcTurnLeft();
        if (name.equals("elevateUp")) ProcElevateUp();
        if (name.equals("elevateDown")) ProcElevateDown();
        if (name.equals("tleft")) ProcTLeft();
        if (name.equals("tright")) ProcTRight();
        if (name.equals("ascend")) ProcAscend();
        if (name.equals("descend")) ProcDescend();
        if (name.equals("moveforward")) ProcMoveForward();
        if (name.equals("movebackward")) ProcMoveBackward();
        if (name.equals("spinleft")) ProcSpinLeft();
        if (name.equals("spinright")) ProcSpinRight();
      
    }
  };  
    
  void ProcForward() {
      
  }
  
  void ProcBackward() {
      
  }
  
  void ProcStrafeLeft() {
      
  }
  
  void ProcStrafeRight() {
      
  }
  
  void ProcLookUp() {
      
  }
  
  void ProcLookDown() {
      
  }

  void ProcTurnRight() {
      
  }
  
  void ProcTurnLeft() {
      
  }
  
  void ProcElevateUp() {
      
  }
  
  void ProcElevateDown() {
      
  }
  
  void ProcTLeft() {
      System.out.println("-");
      ProcSpinLeft();
  }
  
  void ProcTRight() {
      System.out.println("+");
      ProcSpinRight();
  }
  
  void ProcAscend() {
      float x_l,y_l,z_l;
         float xo,yo;
        //DisplaySystem display = DisplaySystem.getDisplaySystem();
        // Detecta centro deslocado do foco de aten�ao
        //Vector2f mouse_xy = new Vector2f(0,0);
        //Vector3f worldCoords = cam.getWorldCoordinates(mouse_xy, 0);
        //Vector3f worldCoords2 = cam.getWorldCoordinates(mouse_xy, 1);
        //Vector3f direction = worldCoords2.subtractLocal(worldCoords).normalizeLocal();
        //Ray mouseRay = new Ray(worldCoords, direction);        
        Ray mouseRay = new Ray(cam.getLocation(), cam.getDirection());        
        float planeZ = 0;
        float startZ = mouseRay.origin.z;
        float endZ = mouseRay.direction.z;
        float coef = (planeZ - startZ) / endZ;
        z_l = cam.getLocation().z;
        if (z_l != 0) {
          xo = mouseRay.origin.x + (coef * mouseRay.direction.x);
          yo = mouseRay.origin.y + (coef * mouseRay.direction.y); 
        }
        else {
          xo = xoo;
          yo = yoo;
        }
        xoo = xo;
        yoo = yo;
        // Detecta circunferencia sobre o centro deslocado
        x_l = cam.getLocation().x-xo;
        y_l = cam.getLocation().y-yo;
        float L = (float) Math.sqrt( x_l * x_l + y_l * y_l);
        float ang = (float)Math.atan2(y_l, x_l);
        //ang += 1f/180f * Math.PI;
        //System.out.println("y:"+y_l+" L:"+L);
        x_l = (float)(L*Math.cos(ang))+xo;
        y_l = (float)(L*Math.sin(ang))+yo;
        if (z_l < -10 || z_l > 10)
           z_l += Constants.mvel * fps;
        else z_l  += Constants.mvel * fps/5;
        //System.out.println("xo:"+xo+" zo:"+zo+" x:"+x_l+" z:"+z_l);
        Vector3f newlocation = new Vector3f(x_l,y_l,z_l);
        cam.setLocation(newlocation);
        //System.out.println("newlocation:"+newlocation.toString());
        //camera.lookAt(new Vector3f(planeX,0,planeZ), new Vector3f(0,1f,0));
        Vector3f focusofattention = new Vector3f(xo,yo,0);
        //System.out.println("focusofattention:"+focusofattention.toString());
        cam.lookAt(focusofattention, cam.getUp());
        //System.out.println("xd:"+camera.getLocation().x+" yd:"+camera.getLocation().y+" zd:"+camera.getLocation().z);        
        cam.normalize();
        cam.update();    
  }
  
  void ProcDescend() {
     float x_l,y_l,z_l;
         float xo,yo;
        // Detecta centro deslocado do foco de aten�ao
//        Vector2f mouse_xy = new Vector2f(0,0);
//        Vector3f worldCoords = cam.getWorldCoordinates(mouse_xy, 0);
//        Vector3f worldCoords2 = cam.getWorldCoordinates(mouse_xy, 1);
//        Vector3f direction = worldCoords2.subtractLocal(worldCoords).normalizeLocal();
//        Ray mouseRay = new Ray(worldCoords, direction);
        Ray mouseRay = new Ray(cam.getLocation(), cam.getDirection());    
        float planeZ = 0;
        float startZ = mouseRay.origin.z;
        float endZ = mouseRay.direction.z;
        float coef = (planeZ - startZ) / endZ;
        z_l = cam.getLocation().z;
        if (z_l != 0) {
            xo = mouseRay.origin.x + (coef * mouseRay.direction.x);
            yo = mouseRay.origin.y + (coef * mouseRay.direction.y); 
        }
        else {
            xo = xoo;
            yo = yoo;
        }
        xoo = xo;
        yoo = yo;
        // Detecta circunferencia sobre o centro deslocado
        x_l = cam.getLocation().x-xo;
        y_l = cam.getLocation().y-yo;
        float L = (float) Math.sqrt( x_l * x_l + y_l * y_l);
        float ang = (float)Math.atan2(y_l, x_l);
        //ang += 1f/180f * Math.PI;
        if (z_l < -10 || z_l > 10)
            z_l -= Constants.mvel * fps;
        else z_l -= Constants.mvel * fps/5;
        //System.out.println("y:"+y_l+" L:"+L);
        x_l = (float)(L*Math.cos(ang))+xo;
        y_l = (float)(L*Math.sin(ang))+yo;
        //System.out.println("xo:"+xo+" zo:"+zo+" x:"+x_l+" z:"+z_l);
        Vector3f newlocation = new Vector3f(x_l,y_l,z_l);
        cam.setLocation(newlocation);
        Vector3f focusofattention = new Vector3f(xo,yo,0);
        cam.lookAt(focusofattention, cam.getUp());
        //System.out.println("xd:"+camera.getLocation().x+" yd:"+camera.getLocation().y+" zd:"+camera.getLocation().z);
        cam.normalize();
        cam.update(); 
  }
  
  void ProcMoveForward() {
      float x_l,y_l,z_l;
        z_l = cam.getLocation().z;
        x_l = cam.getLocation().x + cam.getDirection().x * Constants.mvel * fps;
        y_l = cam.getLocation().y + cam.getDirection().y * Constants.mvel * fps;
        Vector3f newlocation = new Vector3f(x_l,y_l,z_l);
        cam.setLocation(newlocation);
        cam.normalize();
        cam.update();
      
  }
  
  void ProcMoveBackward() {
      float x_l,y_l,z_l;
        z_l = cam.getLocation().z;
        x_l = cam.getLocation().x - cam.getDirection().x * Constants.mvel * fps;
        y_l = cam.getLocation().y - cam.getDirection().y * Constants.mvel * fps;
        Vector3f newlocation = new Vector3f(x_l,y_l,z_l);
        cam.setLocation(newlocation);
        cam.normalize();
        cam.update();
      
  }
  
  void ProcSpinLeft() {
        float x_l,y_l,z_l;
        Ray mouseRay = new Ray(cam.getLocation(), cam.getDirection());  
        float planeZ = 0;
        float startZ = mouseRay.origin.z;
        float endZ = mouseRay.direction.z;
        float coef = (planeZ - startZ) / endZ;
        float xo = mouseRay.origin.x + (coef * mouseRay.direction.x);
        float yo = mouseRay.origin.y + (coef * mouseRay.direction.y); 
        // Detecta circunferencia sobre o centro deslocado
        x_l = cam.getLocation().x-xo;
        y_l = cam.getLocation().y-yo;
        z_l = cam.getLocation().z;
        float L = (float) Math.sqrt(x_l * x_l + y_l * y_l);
        float ang = (float)Math.atan2(y_l, x_l);
        ang += Constants.mvel * fps/180f * Math.PI;
        //System.out.println("y:"+y_l+" L:"+L);
        x_l = (float)(L*Math.cos(ang))+xo;
        y_l = (float)(L*Math.sin(ang))+yo;
        //System.out.println("xo:"+xo+" zo:"+zo+" x:"+x_l+" z:"+z_l);
        Vector3f newlocation = new Vector3f(x_l,y_l,z_l);
        cam.setLocation(newlocation);
        //System.out.println("newlocation:"+newlocation.toString());
        //camera.lookAt(new Vector3f(planeX,0,planeZ), new Vector3f(0,1f,0));
        Vector3f focusofattention = new Vector3f(xo,yo,0);
        //System.out.println("focusofattention:"+focusofattention.toString());
        Vector3f newdirection = focusofattention.subtract(newlocation);
        //System.out.println("newdirection(antes de normalizar):"+newdirection);
        newdirection = newdirection.normalize();
        //System.out.println("newdirection:"+newdirection);
        Vector3f tangent = new Vector3f((float)(L*Math.cos(ang-Math.PI/2)+xo),(float)(L*Math.sin(ang-Math.PI/2)+yo),z_l);
        //tangent = tangent.normalize();
        //System.out.println("tangent:"+tangent);
        Vector3f centerhigh = new Vector3f(xo,yo,z_l);
        //centerhigh = centerhigh.normalize();
        Vector3f newleft = tangent.subtract(centerhigh);
        //System.out.println("newleft(antes de normalizar:"+newleft);
        newleft = newleft.normalize();
        //System.out.println("newleft:"+newleft);  
        Vector3f newup = newdirection.cross(newleft);
        newup = newup.normalize();
        cam.setAxes(newleft,newup,newdirection);
        //System.out.println("xd:"+camera.getLocation().x+" yd:"+camera.getLocation().y+" zd:"+camera.getLocation().z);
        
        cam.normalize();
        cam.update();
  }
  
  void ProcSpinRight() {
      float x_l,y_l,z_l;
        // Detecta centro deslocado do foco de aten�ao
        Ray mouseRay = new Ray(cam.getLocation(), cam.getDirection());  
        float planeZ = 0;
        float startZ = mouseRay.origin.z;
        float endZ = mouseRay.direction.z;
        float coef = (planeZ - startZ) / endZ;
        float xo = mouseRay.origin.x + (coef * mouseRay.direction.x);
        float yo = mouseRay.origin.y + (coef * mouseRay.direction.y); 
        // Detecta circunferencia sobre o centro deslocado
        x_l = cam.getLocation().x-xo;
        y_l = cam.getLocation().y-yo;
        z_l = cam.getLocation().z;
        float L = (float) Math.sqrt( x_l * x_l + y_l * y_l);
        float ang = (float)Math.atan2(y_l, x_l);
        ang -= Constants.mvel * fps/180f * Math.PI;
        //System.out.println("y:"+y_l+" L:"+L);
        x_l = (float)(L*Math.cos(ang))+xo;
        y_l = (float)(L*Math.sin(ang))+yo;
        //System.out.println("xo:"+xo+" zo:"+zo+" x:"+x_l+" z:"+z_l);
        Vector3f newlocation2 = new Vector3f(x_l,y_l,z_l);
        cam.setLocation(newlocation2);
        //System.out.println("newlocation:"+newlocation.toString());
        //camera.lookAt(new Vector3f(planeX,0,planeZ), new Vector3f(0,1f,0));
        Vector3f focusofattention = new Vector3f(xo,yo,0);
        //System.out.println("focusofattention:"+focusofattention.toString());
        Vector3f newdirection = focusofattention.subtract(newlocation2);
        //System.out.println("newdirection(antes de normalizar):"+newdirection);
        newdirection = newdirection.normalize();
        //System.out.println("newdirection:"+newdirection);
        
        Vector3f tangent = new Vector3f((float)(L*Math.cos(ang-Math.PI/2)+xo),(float)(L*Math.sin(ang-Math.PI/2)+yo),z_l);
        //System.out.println("tangent:"+tangent);
        Vector3f centerhigh = new Vector3f(xo,yo,z_l);
        Vector3f newleft = tangent.subtract(centerhigh);
        //System.out.println("newleft(antes de normalizar:"+newleft);
        newleft = newleft.normalize();
        //System.out.println("newleft:"+newleft);
        Vector3f newup = newdirection.cross(newleft);
        newup = newup.normalize();
        //System.out.println("newup:"+newup);
        cam.setAxes(newleft,newup,newdirection);
        //System.out.println("xd:"+camera.getLocation().x+" yd:"+camera.getLocation().y+" zd:"+camera.getLocation().z);
        
        cam.normalize();
        cam.update();
  }
    
}
