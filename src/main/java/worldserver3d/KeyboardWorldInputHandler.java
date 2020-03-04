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

import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyBackwardAction;
import com.jme.input.action.KeyForwardAction;
import com.jme.input.action.KeyLookDownAction;
import com.jme.input.action.KeyLookUpAction;
import com.jme.input.action.KeyRotateLeftAction;
import com.jme.input.action.KeyRotateRightAction;
import com.jme.input.action.KeyStrafeDownAction;
import com.jme.input.action.KeyStrafeLeftAction;
import com.jme.input.action.KeyStrafeRightAction;
import com.jme.input.action.KeyStrafeUpAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import worldserver3d.action.KeyAscendAction;
import worldserver3d.action.KeyDescendAction;
import worldserver3d.action.KeyMoveBackAction;
import worldserver3d.action.KeyMoveFrontAction;
import worldserver3d.action.KeySpinLeftAction;
import worldserver3d.action.KeySpinRightAction;
import worldserver3d.action.KeyTurnLeftAction;
import worldserver3d.action.KeyTurnRightAction;

/**
 * <code>KeyboardLookHandler</code> defines an InputHandler that sets
 * input to be controlled similar to First Person Shooting games. By default the
 * commands are, WSAD moves the camera forward, backward and strafes. The
 * arrow keys rotate and tilt the camera.
 */
public class KeyboardWorldInputHandler extends InputHandler {
    private KeyForwardAction forward;
    private KeyBackwardAction backward;
    private KeyStrafeLeftAction sLeft;
    private KeyStrafeRightAction sRight;
    private KeyRotateRightAction right;
    private KeyRotateLeftAction left;
    private KeyStrafeDownAction down;
    private KeyStrafeUpAction up;

    private float moveSpeed;
    
    public KeyboardWorldInputHandler( Camera cam, float moveSpeed, float rotateSpeed ) {
        this.moveSpeed = moveSpeed;
        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();

        keyboard.set( "forward", KeyInput.KEY_W );
        keyboard.set( "backward", KeyInput.KEY_S );
        keyboard.set( "strafeLeft", KeyInput.KEY_A );
        keyboard.set( "strafeRight", KeyInput.KEY_D );
        keyboard.set( "lookUp", KeyInput.KEY_M );
        keyboard.set( "lookDown", KeyInput.KEY_N );
        keyboard.set( "turnRight", KeyInput.KEY_O );
        keyboard.set( "turnLeft", KeyInput.KEY_P );
        keyboard.set( "elevateUp", KeyInput.KEY_Q);
        keyboard.set( "elevateDown", KeyInput.KEY_Z);
        keyboard.set( "tleft", KeyInput.KEY_RIGHT);
        keyboard.set( "tright", KeyInput.KEY_LEFT);
        keyboard.set( "ascend", KeyInput.KEY_Y);
        keyboard.set( "descend", KeyInput.KEY_H);
        keyboard.set( "moveforward", KeyInput.KEY_UP);
        keyboard.set( "movebackward", KeyInput.KEY_DOWN);
        keyboard.set( "spinleft", KeyInput.KEY_C);
        keyboard.set( "spinright", KeyInput.KEY_V);
        KeyTurnLeftAction tl = new KeyTurnLeftAction( cam, 0.5f );
        addAction(tl, "tleft", true );
        KeyTurnRightAction tr = new KeyTurnRightAction( cam, 0.5f );
        addAction(tr, "tright", true );
        KeyAscendAction aa = new KeyAscendAction(cam, 0.5f);
        addAction(aa,"ascend",true);
        KeyDescendAction da = new KeyDescendAction(cam, 0.5f);
        addAction(da,"descend",true);
        KeyMoveFrontAction mf = new KeyMoveFrontAction(cam, 0.5f);
        addAction(mf,"moveforward",true);
        KeyMoveBackAction mb = new KeyMoveBackAction(cam, 0.5f);
        addAction(mb,"movebackward",true);
        KeySpinLeftAction sl = new KeySpinLeftAction(cam, 0.5f);
        addAction(sl,"spinleft",true);
        KeySpinRightAction sr = new KeySpinRightAction(cam, 0.5f);
        addAction(sr,"spinright",true);
        forward = new KeyForwardAction( cam, moveSpeed );
        addAction( forward, "forward", true );
        backward = new KeyBackwardAction( cam, moveSpeed );
        addAction( backward, "backward", true );
        sLeft = new KeyStrafeLeftAction( cam, moveSpeed );
        addAction( sLeft, "strafeLeft", true );
        sRight = new KeyStrafeRightAction( cam, moveSpeed );
        addAction( sRight, "strafeRight", true );
        addAction( new KeyLookUpAction( cam, rotateSpeed ), "lookUp", true );
        addAction( new KeyLookDownAction( cam, rotateSpeed ), "lookDown", true );
        down = new KeyStrafeDownAction(cam, moveSpeed);
        Vector3f upVec = new Vector3f(cam.getUp());
        down.setUpVector(upVec);
        addAction(down, "elevateDown", true);
        up = new KeyStrafeUpAction( cam, moveSpeed );
        up.setUpVector(upVec);
        addAction( up, "elevateUp", true);
        right = new KeyRotateRightAction( cam, rotateSpeed );
        right.setLockAxis(new Vector3f(cam.getUp()));
        addAction(right, "turnRight", true );
        left = new KeyRotateLeftAction( cam, rotateSpeed );
        left.setLockAxis(new Vector3f(cam.getUp()));
        addAction( left, "turnLeft", true );
    }
    
    public void setLockAxis(Vector3f lock) {
        right.setLockAxis(new Vector3f(lock));
        left.setLockAxis(new Vector3f(lock));
    }
    
    public void setUpAxis(Vector3f upAxis) {
        up.setUpVector(upAxis);
        down.setUpVector(upAxis);
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        if(moveSpeed < 0) {
            moveSpeed = 0;
        }
        this.moveSpeed = moveSpeed;
        
        forward.setSpeed(moveSpeed);
        backward.setSpeed(moveSpeed);
        sLeft.setSpeed(moveSpeed);
        sRight.setSpeed(moveSpeed);
        down.setSpeed(moveSpeed);
        up.setSpeed(moveSpeed);
    }
}
