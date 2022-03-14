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

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.util.TreeMap;
import java.util.logging.Logger;
import model.Creature;
import model.Environment;
import model.Sensor;
import model.Thing;

/**
 *
 * @author eccastro
 */
public class VisualSensor extends Sensor{

    //private RobotCamera camera;
    private Node scene;
    WorldAppState wap;

    private TreeMap<Float, Thing> list = new TreeMap<Float, Thing>();
    
    static Logger log = Logger.getLogger(VisualSensor.class.getCanonicalName());

    public VisualSensor() { 
    }

    public VisualSensor(Creature creature, Node scene, WorldAppState wap){
        this.scene =  scene;
        this.owner = creature;
        this.owner.setClosest(null);
        this.wap = wap;
    }
    
//    private Node createLine(Ray ray, float size) {
//        Vector3f origin = ray.getOrigin();
//        Vector3f direction = ray.getDirection();
//        Vector3f enldirection = new Vector3f(direction.x*size,direction.y*size,direction.z*size);
//        Mesh mesh = new Mesh();
//        mesh.setMode(Mesh.Mode.Lines);
//        
//        // create an array for the points of the line
//        Vector3f[] vertices = new Vector3f[2];
//        vertices[0] = origin;
//        vertices[1] = origin.add(enldirection);
//        
//        // set the UV for the endpoint to the distance between origin and 
//        // endpoint of the line to maintain scale of dashing when used with 
//        // Texture.WrapMode.Repeat below, which will repeat the texture in 
//        // unit length distances across the full length of the line
//        float distance = vertices[1].subtract(vertices[0]).length();
//        
//        Vector2f[] texCoords = new Vector2f[2];
//        texCoords[0] = new Vector2f(0f, 0f);
//        texCoords[1] = new Vector2f(distance, 0f);
//
//        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
//        mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
//        mesh.updateBound();
//        
//        // Transparent texture is required (png with alpha channel) 
//        Material unlitMaterial = new Material(wap.am,"Common/MatDefs/Misc/Unshaded.j3md");
//        Texture texture = wap.am.loadTexture("Textures/DashedLine_128x8.png");
//        texture.setWrap(Texture.WrapMode.Repeat);
//        unlitMaterial.setTexture("ColorMap", texture);
//        unlitMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        //unlitMaterial.setParam("AlphaDiscardThreshold", VarType.Float, 0.9f);
//        unlitMaterial.getAdditionalRenderState().setLineWidth(2f);
//        
//        Geometry geom = new Geometry("Dashed Line Geometry", mesh);
//        geom.setMaterial(unlitMaterial);
//        Node n = new Node();
//        n.attachChild(geom);
//        return(n);
//    }

    /**
     * Return ALL Things in the creature's field-of-view, but those that has
     * been occluded has the corresponding flag "isOccluded" properly set.
     * It is used by the clients to correctly display the Thing.
     * @param th
     * @return
     */
    public boolean returnIfCaptured(Thing th, Environment e){
        
        boolean ret = false;
        th.isOccluded = 0; //false - let's check it now

        synchronized (e.semaphore3) {
        

            try {
                
            CameraNode robot = wap.rcams.get(owner).camNode;//e.getRobotCamera(cameraIndex).getCameraNode();
            //direction from the camera to the thing
            Node thingshape = wap.shapes.get(th);
            
//            int stateBKP = robot.getCamera().getPlaneState();
//            robot.getCamera().setPlaneState(0);
//            Camera.FrustumIntersect result = robot.getCamera().contains(thingshape.getWorldBound());
//            robot.getCamera().setPlaneState(stateBKP);
//            if (result != Camera.FrustumIntersect.Outside) return(true);
//            else return(false);
            
            
            if (thingshape == null) return false;
            Vector3f directionToCenter = thingshape.getLocalTranslation().subtract(robot.getWorldTranslation()).normalizeLocal();

            //Vector3f lowerLeft = new Vector3f((float) (th.getX1() / 10 - e.width / 20), (float) (th.getZ() + th.getDepth()), (float) (th.getY1() / 10 - e.height / 20));
            //Vector3f upperRight = new Vector3f((float) (th.getX2() / 10 - e.width / 20), (float) (th.getZ() + th.getDepth()), (float) (th.getY2() / 10 - e.height / 20));
            Vector3f lowerLeft = new Vector3f((float) (th.getX1()), (float) (th.getY1()),(float) (th.getZ() + th.getDepth()));
            Vector3f upperRight = new Vector3f((float) (th.getX2()), (float) (th.getY2()),(float) (th.getZ() + th.getDepth()));
            
            Vector3f directionToLowerLeft = lowerLeft.subtract(robot.getWorldTranslation()).normalizeLocal();
            Vector3f directionToUpperRight = upperRight.subtract(robot.getWorldTranslation()).normalizeLocal();


            // check if thing is within the camera view:
            //Vector3f difference = robot.getLocalRotation().getRotationColumn(2).subtract(directionToCenter);

            //the state must be backup and then set to 0 before using "camera.contains".
            //This is a "must do" procedure documented by JME.
            int stateBKP = robot.getCamera().getPlaneState();
            robot.getCamera().setPlaneState(0);

            Camera.FrustumIntersect aux1 = robot.getCamera().contains(wap.shapes.get(th).getWorldBound());
            if ( aux1 == Camera.FrustumIntersect.Outside){
                return false;
            }
            
            ret = true;
            robot.getCamera().setPlaneState(stateBKP);
            /**
             * Thing is within the camera view, BUT maybe be occluded by another
             * thing.
             */
                // Ray ray = new Ray(e.rcnEven.getCameraNode().getLocalTranslation(), direction);
                Ray rayCenter = new Ray(robot.getWorldTranslation(), directionToCenter);
                //System.out.println("Differences: "+robot.getLocalTranslation()+" "+robot.getWorldTranslation());
                //wap.addRay(rayCenter,1000);
                Ray rayLowerLeft = new Ray(robot.getWorldTranslation(), directionToLowerLeft);
                //wap.addRay(rayLowerLeft, 1000);
                Ray rayUpperRight = new Ray(robot.getWorldTranslation(), directionToUpperRight);
                //wap.addRay(rayUpperRight, 1000);

                //if at least one of these 3 reference points is visible, then the thing is not occluded
                boolean centerB = checkIfThingPointIsOccluded(e, th, rayCenter);
                boolean lowerLeftB = checkIfThingPointIsOccluded(e, th, rayLowerLeft);
                boolean upperRightB = checkIfThingPointIsOccluded(e, th, rayUpperRight);

                if(centerB && lowerLeftB && upperRightB){
                    th.isOccluded = 1;
                }

            
        } catch (Exception ev) {
            log.severe("Error when getting things from camera...");
            ev.printStackTrace();
        }

        }
        return ret;
    }
    
    

    private boolean checkIfThingPointIsOccluded(Environment e, Thing th, Ray ray) {
        CollisionResults results = new CollisionResults();
        float distanceToThing = ray.origin.subtract(wap.shapes.get(th).getWorldTranslation()).length();
        for (Thing thing : e.getOpool()) {
            Node nthing = wap.shapes.get(thing);
            if (nthing != null) {
                nthing.collideWith(ray, results);
                if (results.size() > 0 && results.getClosestCollision().getDistance() < distanceToThing && thing != th) return true;
            }    
                
        }
        for (Creature c : e.getCreaturesExceptMe(owner)) {
            Node othercreature = wap.shapes.get(c);
            othercreature.collideWith(ray, results);
            if (results.size() > 0 && results.getClosestCollision().getDistance() < distanceToThing && c != th) return true;
        }
        return(false);
    }
    
    
    private boolean checkIfThingPointIsOccluded2(Environment e, Thing th, Ray ray) {

        boolean ret = false;
        //PickResults results = new BoundingPickResults();
        CollisionResults results = new CollisionResults();
        //results...setCheckDistance(true);
        scene.collideWith(ray, results);
                //.findPick(ray, results);
        synchronized (e.semaphore3) {
        if (results.size() > 0) {
            System.out.format("Checking %d collisions: ",results.size());
            for (int it = 0; it < results.size(); it++) {
                Node geom = results.getCollision(it).getGeometry().getParent();
                System.out.format("%d:%s ",it,results.getCollision(it).getGeometry().getName());
                // if (containsNode(geom, e.rcnEven.getCameraNode())) {
                CameraNode cam = wap.rcams.get(owner).camNode;//e.getRobotCamera(cameraIndex).getCameraNode();
                Geometry hit = results.getCollision(it).getGeometry();
                if (hit.getName().contentEquals("Cube") ||
                    hit.getName().contentEquals("armour_0") || 
                    hit.getName().contentEquals("Helmet_1")    ) {
                    continue;
                }
                if (containsNode(geom, cam)) {
                    System.out.print("Its the camera...");
                    // oops, ignore that
                    continue;
                }
//                Node thing = (Node)wap.shapes.get(e.getCpool().get(e.getCamera(cameraIndex)));
//                if (containsNode(geom, thing)) { //@@@@ attention for camera index
//                    // oops, ignore that
//                    continue;
//                }
                if (containsNode(geom, (Node)wap.shapes.get(th))) {
                    System.out.print(th.getMyName());
                    /// got our target
                    Float d = results.getCollision(it).getDistance();
                    list.put(d, th);
                    break;
                } else {
                    /**
                     * the ray hit something else than the obstacle being checked.
                     * It is occluded by another Thing.
                     */

                    ret = true; //it is occluded

                    // the ray hit something else than the thing
                    Node aux = results.getCollision(it).getGeometry().getParent();
                    System.out.print("hit "+aux.getName());
                    break;

                }
                
            }
            System.out.println(".");
        }

        if (list.size() > 0){
            Thing o = list.firstEntry().getValue();
            float dist = list.firstEntry().getKey();
            owner.setClosest(list.firstEntry().getValue());
        } else owner.setClosest(null);
    }
        return ret;
    }
    
    public boolean containsNode(Node toCheck, Node toFind) {
         if (toCheck == toFind) {
            return true;
        }
        if (toCheck.getParent() != null) {
            return containsNode(toCheck.getParent(), toFind);
        } else {
            return false;
        }

     }
  
}
