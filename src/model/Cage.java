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

import com.jme.scene.Node;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import util.Constants;

/**
 *
 * @author ecalhau
 */
public class Cage extends Container {

    /**
     * State of Cage.
     */
    protected int state = Constants.EMPTY_OPENED;
    /**
     * Finite State Machine of the Cage
     */
    protected CageFSM cageFSM;
    static Logger log = Logger.getLogger(Cage.class.getCanonicalName());

    //Default constructor: initially it contain only Food.
    public Cage(double x, double y, Environment ev, MaterialState ms, TextureState ts) {
        super(x, y, ev, ms);
        try {
            this.category = Constants.categoryCAGE;
            this.comX = x;
            this.comY = y;
            /**
             * Please check the center of mass. It is currently not well
             * positioned.
             * 
             * TODO: Evaluate the size of the cage.
             */
            x1 = x;
            y1 = y;
            x2 = x;
            y2 = y;
            this.ts = ts;
            sf = new Thing.ThingShapeFactory("images/empty_cage_p.3DS", this);
            shape = sf.getNode(0.03f);
            shape.setRenderState(ms);
            setTexture("images/texture9.jpeg");
            affordances = new ArrayList<Integer>();
            affordances.add(Constants.Affordance__INSERTABLE);
            affordances.add(Constants.Affordance__REMOVEFROMABLE);
            affordances.add(Constants.Affordance__OPENABLE);
            affordances.add(Constants.Affordance__CLOSEABLE);

            cageFSM = new CageFSM(this);

        } catch (IOException ex) {
            log.info("!!!!!Cage: Error ! ");
            ex.printStackTrace();
        }

    }

    public Cage(double x, double y, Environment ev, MaterialState ms, TextureState ts, int category) {
        super(x, y, ev, ms, category);
        try {
            this.category = Constants.categoryCAGE;
            this.comX = x;
            this.comY = y;
            x1 = x;
            y1 = y;
            x2 = x;
            y2 = y;
            this.ts = ts;
            sf = new Thing.ThingShapeFactory("images/empty_cage_p.3DS", this);
            shape = sf.getNode(0.03f);
            shape.setRenderState(ms);
            setTexture("images/texture9.jpeg");

            affordances = new ArrayList<Integer>();
            affordances.add(Constants.Affordance__INSERTABLE);
            affordances.add(Constants.Affordance__REMOVEFROMABLE);
            affordances.add(Constants.Affordance__OPENABLE);
            affordances.add(Constants.Affordance__CLOSEABLE);

            cageFSM = new CageFSM(this);

        } catch (IOException ex) {
            log.severe("!!!!!Cage: Error ! ");
            ex.printStackTrace();
        }
    }

    public int getStatus() {
        return state;
    }

    public void setStatus(int cageStatus) {
        this.state = cageStatus;
        log.info(" cageStatus:  "+cageStatus);
        switch (cageStatus) {
            case Constants.FULL_OPENED_APPLE:
                updateShape("images/apple_cage_open_p.3DS", 0.03f, this.e);
                break;

            case Constants.FULL_CLOSED_APPLE:
                updateShape("images/apple_cage_p.3DS", 0.03f, this.e);
                break;

            case Constants.FULL_OPENED_NUT:
                updateShape("images/nut_cage_open_p.3DS", 0.03f, this.e);
                break;
            case Constants.FULL_CLOSED_NUT:
                updateShape("images/nut_cage_p.3DS", 0.03f, this.e);
                break;

            case Constants.EMPTY_OPENED:
                updateShape("images/empty_cage_p.3DS", 0.03f, this.e);
                break;
            case Constants.EMPTY_CLOSED:
                updateShape("images/empty_cage_closed_p.3DS", 0.03f, this.e);
                break;

            case Constants.FULL_OPENED_JEWEL:
                updateShape("images/jewel_cage_open_p.3DS", 0.03f, this.e);
                break;
            case Constants.FULL_CLOSED_JEWEL:
                updateShape("images/jewel_cage_p.3DS", 0.03f, this.e);
                break;

            case Constants.FULL_OPENED_MNJ:
                updateShape("images/mnj_cage_open_p.3DS", 0.03f, this.e);
                break;
            case Constants.FULL_CLOSED_MNJ:
                updateShape("images/mnj_cage_p.3DS", 0.03f, this.e);
                break;

            case Constants.FULL_OPENED_MN:
                updateShape("images/mn_cage_open_p.3DS", 0.03f, this.e);
                break;
            case Constants.FULL_CLOSED_MN:
                updateShape("images/mn_cage_p.3DS", 0.03f, this.e);
                break;

            case Constants.FULL_OPENED_MJ:
                updateShape("images/mj_cage_open_p.3DS", 0.03f, this.e);
                break;
            case Constants.FULL_CLOSED_MJ:
                updateShape("images/mj_cage_p.3DS", 0.03f, this.e);
                break;

            case Constants.FULL_OPENED_NJ:
                updateShape("images/nj_cage_open_p.3DS", 0.03f, this.e);
                break;
            case Constants.FULL_CLOSED_NJ:
                updateShape("images/nj_cage_p.3DS", 0.03f, this.e);
                break;
            default:
                log.severe("Error in Cage::setStatus !!!");
        }
    }

    @Override
    public void moveTo(double dx, double dy) {
        synchronized (e.semaphore) {
            x1 += dx;
            y1 += dy;
            x2 += dx;
            y2 += dy;
            comX += dx;
            comY += dy;
            if (!e.getOpoolModified().contains(this)) {
                e.addToOpoolModified(this);
            }
        }
    }

    @Override
    public void initPlace() {
    }

    @Override
    public Node myLocalTransformations(Node modelw) {
        modelw.getLocalRotation().fromAngles(270 * 3.141592f / 180, 360 * 3.141592f / 180, 0f);
        switch (getStatus()) {
            case Constants.FULL_OPENED_APPLE:
                modelw.setLocalTranslation(5f, 0, -16f);
                break;

            case Constants.FULL_CLOSED_APPLE:
                modelw.setLocalTranslation(9f, 0, 0);
                break;

            case Constants.FULL_OPENED_NUT:
                modelw.setLocalTranslation(5f, 0, -16f);
                break;
            case Constants.FULL_CLOSED_NUT:
                modelw.setLocalTranslation(-6f, 0, 0);
                break;

            case Constants.EMPTY_OPENED:
                modelw.setLocalTranslation(-24f, 0, 0);
                break;
            case Constants.EMPTY_CLOSED:
                modelw.setLocalTranslation(13f, 0, -16f);
                break;

            case Constants.FULL_OPENED_JEWEL:
                modelw.setLocalTranslation(3.5f, 0, 0);
                break;
            case Constants.FULL_CLOSED_JEWEL:
                modelw.setLocalTranslation(10f, 0, 0);
                break;

            case Constants.FULL_OPENED_MNJ:
                modelw.setLocalTranslation(-6.5f, 0, 5f);
                break;
            case Constants.FULL_CLOSED_MNJ:
                modelw.setLocalTranslation(0, 0, 5f);
                break;

            case Constants.FULL_OPENED_MN:
                modelw.setLocalTranslation(-6.5f, 0, -3f);
                break;
            case Constants.FULL_CLOSED_MN:
                modelw.setLocalTranslation(0, 0, -3f);
                break;

            case Constants.FULL_OPENED_MJ:
                modelw.setLocalTranslation(-16.5f, 0, -3f);
                break;
            case Constants.FULL_CLOSED_MJ:
                modelw.setLocalTranslation(-10f, 0, -3f);
                break;

            case Constants.FULL_OPENED_NJ:
                modelw.setLocalTranslation(-16.5f, 0, 4.5f);
                break;
            case Constants.FULL_CLOSED_NJ:
                modelw.setLocalTranslation(-10f, 0, 4.5f);
                break;
        }
        return modelw;

    }

    @Override
    public Knapsack putMeInKnapsack(Knapsack sack) {
        return sack;
    }

    @Override
    public void setID(Long id, Environment e) {
        this.ID = id;
        String name = Constants.CAGE_PREFIX;
        this.shape.setName(name.concat(id.toString()));
        myName = name.concat(id.toString());
        e.thingMap.put(myName, this);
    }

    @Override
    protected void sendAddDelEvent(int category, boolean isAdd) {
        switch (category) {


            case Constants.categoryJEWEL:
                if (isAdd) {
                    cageFSM.processEvent(FSMEvent.CageFSMEvent.Add_J_Action);
                } else {
                    cageFSM.processEvent(FSMEvent.CageFSMEvent.Del_J_Action);
                }
                break;

            case Constants.categoryNPFOOD:
                if (isAdd) {
                    cageFSM.processEvent(FSMEvent.CageFSMEvent.Add_NPF_Action);
                } else {
                    cageFSM.processEvent(FSMEvent.CageFSMEvent.Del_NPF_Action);
                }

                break;

            case Constants.categoryPFOOD:
                if (isAdd) {
                    cageFSM.processEvent(FSMEvent.CageFSMEvent.Add_PF_Action);
                } else {
                    cageFSM.processEvent(FSMEvent.CageFSMEvent.Del_PF_Action);
                }

                break;

            default:
                log.severe("Error in Cage::sendAddDelEvent: no recognizable Thing category!!!");
        }
    }

    @Override
    protected void sendCloseOpenEvent(boolean isOpen) {
        if (isOpen) {
            cageFSM.processEvent(FSMEvent.CageFSMEvent.Open);
        } else {
            cageFSM.processEvent(FSMEvent.CageFSMEvent.Close);
        }
    }
}
