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

/**
 *
 * @author ecalhau
 */
public class FSMEvent {

    private static FSMEvent instance = null;
    
    //Events of the Finite State Machine that defines the state transitions according
    // to the action performed upon the Cage. Ultimately, it defines the Cage
    // shape which represents the content of the Cage.
    public enum CageFSMEvent {

        Add_PF_Action, //at least one item of Perishable category is inserted
        Add_NPF_Action, //at least one item of Non-Perishable category is inserted
        Add_J_Action, //at least one item of Jewel category is inserted
        Del_PF_Action, //all items of Perishable category were removed
        Del_NPF_Action, //all items of Non-Perishable category were removed
        Del_J_Action, //all items of Jewel category were removed
        Open, //container must be opened
        Close //container must be closed   
    }
    /**
     * For other types of container, create the corresponding FSM events here.
     */
   
    
    /**
     * Singleton
     */
    private FSMEvent() {
        
    }
    public static FSMEvent getInstance() {
        if (instance == null) {
            instance = new FSMEvent();
        }
        return instance;
    }
}
