/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package worldserver3d;

import worldserver3d.gamestate.SimulationGameState;
import com.jme.input.MouseInput;
import com.jme.system.GameSettings;
import com.jme.util.GameTaskQueueManager;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;
import java.util.concurrent.Callable;

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
    
    SimulationFrame(Main m) {
    root = m;    

    game = new StandardGame("Elisa's EpisodicMemoryGame"); // Create our game
    //try {
    GameSettings gs = game.getSettings();    
    gs.setWidth(1024);
    gs.setHeight(768);

    try {
    game.start();	// Start the game thread
    } catch (Exception ev) {System.out.println("Game start exception !!!");}

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
