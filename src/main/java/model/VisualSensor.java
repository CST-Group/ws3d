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

import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 *
 * @author eccastro
 */
public class VisualSensor extends Sensor{

    //private RobotCamera camera;
    private Node scene;

    private TreeMap<Float, Thing> list = new TreeMap<Float, Thing>();
    
    static Logger log = Logger.getLogger(VisualSensor.class.getCanonicalName());

    public VisualSensor() { 
    }

    public VisualSensor(Creature creature, Node scene){
        this.scene =  scene;
        this.owner = creature;
        this.owner.setClosest(null);
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

            //direction from the camera to the thing
            Vector3f directionToCenter = th.shape.getLocalTranslation().subtract(e.getRobotCamera(cameraIndex).getCameraNode().getLocalTranslation()).normalizeLocal();

            Vector3f lowerLeft = new Vector3f((float) (th.getX1() / 10 - e.width / 20), (float) (th.getZ() + th.getDepth()), (float) (th.getY1() / 10 - e.height / 20));
            Vector3f upperRight = new Vector3f((float) (th.getX2() / 10 - e.width / 20), (float) (th.getZ() + th.getDepth()), (float) (th.getY2() / 10 - e.height / 20));

            Vector3f directionToLowerLeft = lowerLeft.subtract(e.getRobotCamera(cameraIndex).getCameraNode().getLocalTranslation()).normalizeLocal();

            Vector3f directionToUpperRight = upperRight.subtract(e.getRobotCamera(cameraIndex).getCameraNode().getLocalTranslation()).normalizeLocal();


            // check if thing is within the camera view:
            Vector3f difference = e.getRobotCamera(cameraIndex).getCameraNode().getLocalRotation().getRotationColumn(2).subtract(directionToCenter);

            //the state must be backup and then set to 0 before using "camera.contais".
            //This is a "must do" procedure documented by JME.
            int stateBKP = e.getRobotCamera(cameraIndex).getCameraNode().getCamera().getPlaneState();
            e.getRobotCamera(cameraIndex).getCameraNode().getCamera().setPlaneState(0);

            Camera.FrustumIntersect aux1 = e.getRobotCamera(cameraIndex).getCameraNode().getCamera().contains(th.shape.getWorldBound());
            if ( aux1 == Camera.FrustumIntersect.Outside){
                return false;
            }
            
            ret = true;
            e.getRobotCamera(cameraIndex).getCameraNode().getCamera().setPlaneState(stateBKP);
            /**
             * Thing is within the camera view, BUT maybe be occluded by another
             * thing.
             */
                // Ray ray = new Ray(e.rcnEven.getCameraNode().getLocalTranslation(), direction);
                Ray rayCenter = new Ray(e.getRobotCamera(cameraIndex).getCameraNode().getLocalTranslation(), directionToCenter);
                Ray rayLowerLeft = new Ray(e.getRobotCamera(cameraIndex).getCameraNode().getLocalTranslation(), directionToLowerLeft);
                Ray rayUpperRight = new Ray(e.getRobotCamera(cameraIndex).getCameraNode().getLocalTranslation(), directionToUpperRight);

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
        PickResults results = new BoundingPickResults();
        results.setCheckDistance(true);
        scene.findPick(ray, results);
        synchronized (e.semaphore3) {
        if (results.getNumber() > 0) {
            for (int it = 0; it < results.getNumber(); it++) {
                Node geom = results.getPickData(it).getTargetMesh().getParent();
                // if (containsNode(geom, e.rcnEven.getCameraNode())) {
                if (containsNode(geom, e.getRobotCamera(cameraIndex).getCameraNode())) {
                    // oops, ignore that
                    continue;
                }
                if (containsNode(geom, (e.getCpool().get(e.getCamera(cameraIndex))).shape)) { //@@@@ attention for camera index
                    // oops, ignore that
                    continue;
                }
                if (containsNode(geom, th.shape)) {
                    /// got our target
                    Float d = results.getPickData(it).getDistance();
                    list.put(d, th);
                    break;
                } else {
                    /**
                     * the ray hit something else than the obstacle being checked.
                     * It is occluded by another Thing.
                     */

                    ret = true; //it is occluded

                    // the ray hit something else than the thing
                    Node aux = results.getPickData(it).getTargetMesh().getParent();


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
