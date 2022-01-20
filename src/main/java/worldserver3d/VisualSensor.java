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
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
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

    /**
     * Return ALL Things in the creature's field-of-view, but those that has
     * been occluded has the corresponding flag "isOccluded" properly set.
     * It is used by the clients to correctly display the Thing.
     * @param th
     * @return
     */
    public boolean returnIfCaptured(Thing th, Environment e){
        int cameraIndex;
        boolean ret = false;
        th.isOccluded = 0; //false - let's check it now

        synchronized (e.semaphore3) {
        if (e.getCpool().indexOf(owner) % 2 == 0) cameraIndex = 0;
        else cameraIndex = 1;

            try {
                
            CameraNode robot = wap.rcams.get(owner).camNode;//e.getRobotCamera(cameraIndex).getCameraNode();
            //direction from the camera to the thing
            Vector3f directionToCenter = wap.shapes.get(th).getLocalTranslation().subtract(robot.getLocalTranslation()).normalizeLocal();

            Vector3f lowerLeft = new Vector3f((float) (th.getX1() / 10 - e.width / 20), (float) (th.getZ() + th.getDepth()), (float) (th.getY1() / 10 - e.height / 20));
            Vector3f upperRight = new Vector3f((float) (th.getX2() / 10 - e.width / 20), (float) (th.getZ() + th.getDepth()), (float) (th.getY2() / 10 - e.height / 20));

            Vector3f directionToLowerLeft = lowerLeft.subtract(robot.getLocalTranslation()).normalizeLocal();

            Vector3f directionToUpperRight = upperRight.subtract(robot.getLocalTranslation()).normalizeLocal();


            // check if thing is within the camera view:
            Vector3f difference = robot.getLocalRotation().getRotationColumn(2).subtract(directionToCenter);

            //the state must be backup and then set to 0 before using "camera.contais".
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
                Ray rayCenter = new Ray(robot.getLocalTranslation(), directionToCenter);
                Ray rayLowerLeft = new Ray(robot.getLocalTranslation(), directionToLowerLeft);
                Ray rayUpperRight = new Ray(robot.getLocalTranslation(), directionToUpperRight);

                //if at least one of these 3 reference points is visible, then the thing is not occluded
                boolean centerB = checkIfThingPointIsOccluded(e, cameraIndex, th, rayCenter);
                boolean lowerLeftB = checkIfThingPointIsOccluded(e, cameraIndex, th, rayLowerLeft);
                boolean upperRightB = checkIfThingPointIsOccluded(e, cameraIndex, th, rayUpperRight);

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

    private boolean checkIfThingPointIsOccluded(Environment e, int cameraIndex, Thing th, Ray ray) {

        boolean ret = false;
        //PickResults results = new BoundingPickResults();
        CollisionResults results = new CollisionResults();
        //results...setCheckDistance(true);
        scene.collideWith(ray, results);
                //.findPick(ray, results);
        synchronized (e.semaphore3) {
        if (results.size() > 0) {
            for (int it = 0; it < results.size(); it++) {
                Node geom = results.getCollision(it).getGeometry().getParent();
                // if (containsNode(geom, e.rcnEven.getCameraNode())) {
                CameraNode cam = wap.rcams.get(owner).camNode;//e.getRobotCamera(cameraIndex).getCameraNode();
                if (containsNode(geom, cam)) {
                    // oops, ignore that
                    continue;
                }
//                Node thing = (Node)wap.shapes.get(e.getCpool().get(e.getCamera(cameraIndex)));
//                if (containsNode(geom, thing)) { //@@@@ attention for camera index
//                    // oops, ignore that
//                    continue;
//                }
                if (containsNode(geom, (Node)wap.shapes.get(th))) {
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
                    break;

                }

            }
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
