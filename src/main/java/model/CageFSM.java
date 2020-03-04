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

import java.util.logging.Logger;
import util.Constants;

/**
 *
 * @author ecalhau
 */
public class CageFSM {

    private Cage cage;
    static Logger log = Logger.getLogger(CageFSM.class.getCanonicalName());
    
    
    private enum State {

        EMPTY_OPENED {
            @Override
        void processADDPFAction(CageFSM cfsm) {
            cfsm.state = FULL_OPENED_APPLE;
            sendMessage(cfsm);
        }

            @Override
        void processADDNPFAction(CageFSM cfsm) {
            cfsm.state = FULL_OPENED_NUT;
            sendMessage(cfsm);
        }

            @Override
        void processADDJewelAction(CageFSM cfsm) {
            cfsm.state = FULL_OPENED_JEWEL;
            sendMessage(cfsm);
        }

            @Override
        void processCloseAction(CageFSM cfsm){
            cfsm.state = EMPTY_CLOSED;
            sendMessage(cfsm);
        }

          },
        EMPTY_CLOSED {

            @Override
            void processADDPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_APPLE;
                sendMessage(cfsm);
            }

            @Override
            void processADDNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_NUT;
                sendMessage(cfsm);
            }

            @Override
            void processADDJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_JEWEL;
                sendMessage(cfsm);
            }

            @Override
            void processOpenAction(CageFSM cfsm) {
                cfsm.state = EMPTY_OPENED;
                sendMessage(cfsm);
            }
        },
        FULL_CLOSED_APPLE {

            @Override
            void processDELPFAction(CageFSM cfsm) {
                cfsm.state = EMPTY_CLOSED;
                sendMessage(cfsm);
            }

            @Override
            void processADDNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MN;
                sendMessage(cfsm);
            }

            @Override
            void processADDJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MJ;
                sendMessage(cfsm);
            }

            @Override
            void processOpenAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_APPLE;
                sendMessage(cfsm);
            }
        },
        FULL_OPENED_APPLE {

            @Override
            void processDELPFAction(CageFSM cfsm) {
                cfsm.state = EMPTY_OPENED;
                sendMessage(cfsm);
            }

            @Override
            void processADDNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MN;
                sendMessage(cfsm);
            }

            @Override
            void processADDJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MJ;
                sendMessage(cfsm);
            }

            @Override
            void processCloseAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_APPLE;
                sendMessage(cfsm);
            }
        },
        FULL_CLOSED_NUT {

            @Override
            void processDELNPFAction(CageFSM cfsm) {
                cfsm.state = EMPTY_CLOSED;
                sendMessage(cfsm);
            }

            @Override
            void processADDPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MN;
                sendMessage(cfsm);
            }

            @Override
            void processADDJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_NJ;
                sendMessage(cfsm);
            }

            @Override
            void processOpenAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_NUT;
                sendMessage(cfsm);
            }
        },
        FULL_OPENED_NUT {
            @Override
            void processDELNPFAction(CageFSM cfsm) {
                cfsm.state = EMPTY_OPENED;
                sendMessage(cfsm);
            }

            @Override
            void processADDPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MN;
                sendMessage(cfsm);
            }

            @Override
            void processADDJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_NJ;
                sendMessage(cfsm);
            }

            @Override
            void processCloseAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_NUT;
                sendMessage(cfsm);
            }
        },
        FULL_OPENED_JEWEL {
            @Override
            void processDELJewelAction(CageFSM cfsm) {
                cfsm.state = EMPTY_OPENED;
                sendMessage(cfsm);
            }

            @Override
            void processADDNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_NJ;
                sendMessage(cfsm);
            }

            @Override
            void processADDPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MJ;
                sendMessage(cfsm);
            }

            @Override
            void processCloseAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_JEWEL;
                sendMessage(cfsm);
            }
        },
        FULL_CLOSED_JEWEL {
            @Override
            void processDELJewelAction(CageFSM cfsm) {
                cfsm.state = EMPTY_CLOSED;
                sendMessage(cfsm);
            }

            @Override
            void processADDPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MJ;
                sendMessage(cfsm);
            }

            @Override
            void processADDNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_NJ;
                sendMessage(cfsm);
            }

            @Override
            void processOpenAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_JEWEL;
                sendMessage(cfsm);
            }
        },
        FULL_OPENED_MN {

            @Override
            void processDELPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_NUT;
                sendMessage(cfsm);
            }

            @Override
            void processDELNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_APPLE;
                sendMessage(cfsm);
            }

            @Override
            void processADDJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MNJ;
                sendMessage(cfsm);
            }

            @Override
            void processCloseAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MN;
                sendMessage(cfsm);
            }
        },
        FULL_CLOSED_MN {
            @Override
            void processDELPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_NUT;
                sendMessage(cfsm);
            }

            @Override
            void processDELNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_APPLE;
                sendMessage(cfsm);
            }

            @Override
            void processADDJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MNJ;
                sendMessage(cfsm);
            }

            @Override
            void processOpenAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MN;
                sendMessage(cfsm);
            }
        },
        FULL_OPENED_MJ {
            @Override
            void processDELPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_JEWEL;
                sendMessage(cfsm);
            }

            @Override
            void processDELJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_APPLE;
                sendMessage(cfsm);
            }

            @Override
            void processADDNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MNJ;
                sendMessage(cfsm);
            }

            @Override
            void processCloseAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MJ;
                sendMessage(cfsm);
            }
        },
        FULL_CLOSED_MJ {
            @Override
            void processDELPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_JEWEL;
                sendMessage(cfsm);
            }

            @Override
            void processDELJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_APPLE;
                sendMessage(cfsm);
            }

            @Override
            void processADDNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MNJ;
                sendMessage(cfsm);
            }

            @Override
            void processOpenAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MJ;
                sendMessage(cfsm);
            }
        },
        FULL_OPENED_NJ {
            @Override
            void processDELNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_JEWEL;
                sendMessage(cfsm);
            }

            @Override
            void processDELJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_NUT;
                sendMessage(cfsm);
            }

            @Override
            void processADDPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MNJ;
                sendMessage(cfsm);
            }

            @Override
            void processCloseAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_NJ;
                sendMessage(cfsm);
            }
        },
        FULL_CLOSED_NJ {
            @Override
            void processDELNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_JEWEL;
                sendMessage(cfsm);
            }

            @Override
            void processDELJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_NUT;
                sendMessage(cfsm);
            }

            @Override
            void processADDPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MNJ;
                sendMessage(cfsm);
            }

            @Override
            void processOpenAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_NJ;
                sendMessage(cfsm);
            }
      

        },
        FULL_OPENED_MNJ {

            @Override
            void processDELPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_NJ;
                sendMessage(cfsm);
            }

            @Override
            void processDELNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MJ;
                sendMessage(cfsm);
            }

            @Override
            void processDELJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MN;
                sendMessage(cfsm);
            }

            @Override
            void processCloseAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MNJ;
                sendMessage(cfsm);
            }
        },
        FULL_CLOSED_MNJ {

            @Override
            void processDELPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_NJ;
                sendMessage(cfsm);
            }

            @Override
            void processDELNPFAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MJ;
                sendMessage(cfsm);
            }

            @Override
            void processDELJewelAction(CageFSM cfsm) {
                cfsm.state = FULL_CLOSED_MN;
                sendMessage(cfsm);
            }

            @Override
            void processOpenAction(CageFSM cfsm) {
                cfsm.state = FULL_OPENED_MNJ;
                sendMessage(cfsm);
            }
        };

        public void exec(FSMEvent.CageFSMEvent ev, CageFSM cfsm) {
            log.info("State is: "+cfsm.getState());
            log.info("Received event: "+ev+" and changed state.");
            
            switch (ev) {

                case Add_PF_Action:
                    processADDPFAction(cfsm);
                    break;
                case Add_NPF_Action:
                    processADDNPFAction(cfsm);
                    break;
                case Add_J_Action:
                    processADDJewelAction(cfsm);
                    break;
                case Del_PF_Action:
                    processDELPFAction(cfsm);
                    break;
                case Del_NPF_Action:
                    processDELNPFAction(cfsm);
                    break;
                case Del_J_Action:
                    processDELJewelAction(cfsm);
                    break;
                case Open:
                    processOpenAction(cfsm);
                    break;
                case Close:
                    processCloseAction(cfsm);
                    break;
            }
            cfsm.getCage().setStatus(cfsm.getStateToInt());
        }
        
        /**
         * Override the next methods according to each state
         *
         * @param cfsm
         */
        void processADDPFAction(CageFSM cfsm) {
            log.info("Do nothing");
            sendMessage(cfsm);
        }

        void processADDNPFAction(CageFSM cfsm) {
            log.info("Do nothing");
            sendMessage(cfsm);
        }

        void processADDJewelAction(CageFSM cfsm) {
            log.info("Do nothing");
            sendMessage(cfsm);
        }

        void processDELPFAction(CageFSM cfsm) {
            log.info("Do nothing");
            sendMessage(cfsm);
        }

        void processDELNPFAction(CageFSM cfsm) {
            log.info("Do nothing");
            sendMessage(cfsm);
        }
        void processDELJewelAction(CageFSM cfsm){
            log.info("Do nothing");
            sendMessage(cfsm);
        }
        void processOpenAction(CageFSM cfsm){
            log.info("Do nothing");
            sendMessage(cfsm);
        }
        void processCloseAction(CageFSM cfsm){
            log.info("Do nothing");
            sendMessage(cfsm);
        }
        
    }
    
    private CageFSM.State state = CageFSM.State.EMPTY_OPENED; //initial
  
    public CageFSM(Cage c){
        this.cage = c;
    }
    public Cage getCage() {
        return cage;
    }
    
    public CageFSM.State getState() {
        return state;
    }
    //Stage instance "mapped" to an int. This is understood by other classes:
    public int getStateToInt() {

        switch (state) {
            case EMPTY_OPENED:
                return Constants.EMPTY_OPENED;
            case EMPTY_CLOSED:
                return Constants.EMPTY_CLOSED;
            case FULL_CLOSED_APPLE:
                return Constants.FULL_CLOSED_APPLE;
            case FULL_OPENED_APPLE:
                return Constants.FULL_OPENED_APPLE;
            case FULL_CLOSED_NUT:
                return Constants.FULL_CLOSED_NUT;
            case FULL_OPENED_NUT:
                return Constants.FULL_OPENED_NUT;
            case FULL_OPENED_JEWEL:
                return Constants.FULL_OPENED_JEWEL;
            case FULL_CLOSED_JEWEL:
                return Constants.FULL_CLOSED_JEWEL;
            case FULL_OPENED_MNJ:
                return Constants.FULL_OPENED_MNJ;
            case FULL_CLOSED_MNJ:
                return Constants.FULL_CLOSED_MNJ;
            case FULL_OPENED_MN:
                return Constants.FULL_OPENED_MN;
            case FULL_CLOSED_MN:
                return Constants.FULL_CLOSED_MN;
            case FULL_OPENED_MJ:
                return Constants.FULL_OPENED_MJ;
            case FULL_CLOSED_MJ:
                return Constants.FULL_CLOSED_MJ;
            case FULL_OPENED_NJ:
                return Constants.FULL_OPENED_NJ;
            case FULL_CLOSED_NJ:
                return Constants.FULL_CLOSED_NJ;
            default:
                log.severe("Error in CageFSM::getStateToInt: state does not exist!!!");
                return -1;
        }

    }

    public void setState(CageFSM.State state) {
        this.state = state;
    }
    
    public void processEvent(FSMEvent.CageFSMEvent ev){
        log.info("Event received: " + ev);
        this.state.exec(ev, this);
        
    }

    private static void sendMessage(CageFSM cfsm) {
        log.info("Current state is: " + cfsm.getState());
    }


}

