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

import worldserver3d.gamestate.SimulationGameState;
import com.jme.input.MouseInput;
import com.jme.system.GameSettings;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 *
 * @author gudwin
 */
public class SimulationFrame {
    
    //final Environment e;
    //WorldFrame wf;
    Main root;
    public SimulationGameState gameState;
    StandardGame game;
    static Logger log = Logger.getLogger(SimulationFrame.class.getCanonicalName());
    
    SimulationFrame(Main m) {
    root = m;    

    game = new StandardGame("WorldServer3D Game"); // Create our game
    //try {
    GameSettings gs = game.getSettings();    
    gs.setWidth(1024);
    gs.setHeight(768);

    try {
    game.start();	// Start the game thread
    } catch (Exception ev) {log.severe("Game start exception !!!");}

    MouseInput.get().setCursorVisible(true);
    GameTaskQueueManager.getManager().update(new Callable<Void>(){

			public Void call() throws Exception {
				gameState = new SimulationGameState("simulation",root, game);	// Create our game state
				GameStateManager.getInstance().attachChild(gameState);	// Attach it to the GameStateManager
				gameState.setActive(true);	// Activate it
                return null;
			}
	    });
    }    

}
