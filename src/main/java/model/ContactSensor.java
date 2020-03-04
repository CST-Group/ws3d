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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import util.Constants;

/**
 *
 */
public class ContactSensor extends Sensor {

    private String action = "NONE";

    public ContactSensor(Creature c) {

        this.owner = c;
    }

    public void setAction(String ac) {
        this.action = ac;
    }

    public String getAction() {
        return action;
    }

    public String getActionExecutedAndTarget() {

        String actionAttribs = "NONE";
        if (!action.equals("NONE")) {
            try {

                JSONObject jsonAll = new JSONObject();
                JSONObject jsonThingAttribs = new JSONObject();

                jsonAll.put(Constants.TOKEN_ACTION, action);
                JSONObject jsonAttribs = new JSONObject(getThingAttributes(thing));
                jsonThingAttribs.put(thing.getMyName(), jsonAttribs);
                jsonAll.put(Constants.TOKEN_THING_DATA, jsonThingAttribs);

                actionAttribs = jsonAll.toString();
            } catch (JSONException ex) {
                Logger.getLogger(ContactSensor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //System.out.println("------ret::: "+actionAttribs);
        return actionAttribs;
    }

    private String getThingAttributes(Thing o) {

        String ret = "";
        double coords;
        if (o != null) {
            try {
      
                switch (action) {
                    case Constants.ACTION_NAME_EAT:
                        coords = Constants.AFTER_EAT_COORDS;
                        break;
                    case Constants.ACTION_NAME_PUTINTOBAG:
                        coords = Constants.AFTER_PUT_IN_BAG_COORDS;
                        break;
                    case Constants.ACTION_NAME_HIDE:
                        coords = Constants.AFTER_HIDE_COORDS;
                        break;
                    case Constants.ACTION_NAME_DELIVER:
                        coords = Constants.AFTER_DELIVERY_COORDS;
                        break;
                    default:
                        coords = -1;
                        break;
                }
                
                JSONObject jsonThingAtt = new JSONObject();
                jsonThingAtt.put(Constants.TOKEN_NAME_ID, o.getMyName());
                jsonThingAtt.put(Constants.TOKEN_COLOR, o.getMaterial().getColorName());
                jsonThingAtt.put(Constants.TOKEN_CATEGORY, o.category);
                jsonThingAtt.put(Constants.TOKEN_X1, coords);
                jsonThingAtt.put(Constants.TOKEN_X2, coords);
                jsonThingAtt.put(Constants.TOKEN_Y1, coords);
                jsonThingAtt.put(Constants.TOKEN_Y2, coords);
                jsonThingAtt.put(Constants.TOKEN_THING_PITCH, o.getPitch());
                jsonThingAtt.put(Constants.TOKEN_THING_ENERGY, o.getMaterial().getEnergy());
                jsonThingAtt.put(Constants.TOKEN_CENTER_OF_MASS_X, coords);
                jsonThingAtt.put(Constants.TOKEN_CENTER_OF_MASS_Y, coords);
                jsonThingAtt.put(Constants.TOKEN_HARDNESS, o.getMaterial().getHardness());
                jsonThingAtt.put(Constants.TOKEN_THING_ENERGY, o.getMaterial().getEnergy());
                jsonThingAtt.put(Constants.TOKEN_SHININESS, o.getMaterial().getShininess());
                jsonThingAtt.put(Constants.TOKEN_OCCLUDED, o.isOccluded);
                
                ret = jsonThingAtt.toString();
            } catch (JSONException ex) {
                Logger.getLogger(ContactSensor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return ret;
    }
}
