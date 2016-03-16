/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
