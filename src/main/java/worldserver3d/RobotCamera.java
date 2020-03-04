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

import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
//import com.jmex.physics.DynamicPhysicsNode;
import model.Environment;

public class RobotCamera {
    /**
     * Displaysystem.
     */
    private DisplaySystem disp = DisplaySystem.getDisplaySystem();
    
    /**
     * the quad where the camera is displayed on.
     */
    private Quad monitorQuad;
    
    /**
     * The node with the monitor quad attached.
     */
    private Node monitorNode;
    
    /**
     * the CameraNode which gets attached to the missile.
     */
    private CameraNode robotCamNode;
    
    /**
     * the Texturerenderer which renders the scene to a texture.
     */
    private TextureRenderer tRenderer;
    
    /**
     *  Texture to render the scene to.
     */
    private Texture2D fakeTex;
    
    /**
     * TextureState with the rendered scene.
     */
    private TextureState screenTextureState;
    
    private float lastRend = 1;
    /**
     * render speed of the missile camera.
     */
    private float throttle = 1/30f;
    
    /**
     * updatespeed of the noise.
     */
    private float noiseThrottle = 1/10f;
    
    /**
     * alternative position of the noise texture.
     */
    private Vector3f texturePos = new Vector3f(0.25f, 0.25f, 0.25f);
    boolean textSwitch = false;
    private Texture noiseTex;
    private TextureState noiseTextureState;
    
    /**
     * the scene to render;
     */
    private Spatial scene;
    
    public int robot_view = -1;
    
    /**
     * creates the missile cam.
     * @param x position of the total area of the display in ortho values
     * @param y position of the total area of the display in ortho values
     * @param width width of the environment display in ortho values
     * @param height height of the environment display in ortho values
     */
     public RobotCamera(final float x, final float y, final float width, final float height) {
    //public RobotCamera(final float x, final float y) {
        
        createNoiseTextureState();
        
        tRenderer = disp.createTextureRenderer(
                240, 180, TextureRenderer.Target.Texture2D);
        //          60, 50, TextureRenderer.Target.Texture2D);
        
        robotCamNode = new CameraNode("Robot Camera Node", tRenderer.getCamera());
        //tRenderer.getCamera().setFrustumFar(100000);
       // tRenderer.getCamera().setFrustum(1.0f, 100000.0f, -0.55f, 1f, 0.4125f, -0.4125f);
        //tRenderer.getCamera().resize(240, 180);
        tRenderer.getCamera().setFrustumPerspective(45.0f,(float) width / (float) height, 1, 1000);
        tRenderer.getCamera().update();
        robotCamNode.setLocalTranslation(new Vector3f(0, 2.5f, -10));
        robotCamNode.updateGeometricState(0, true);
        robotCamNode.updateWorldData(0);

        monitorNode = new Node("Monitor Node");
        monitorQuad = new Quad("Monitor");
        monitorQuad.initialize(240, 180);
        monitorQuad.setLocalTranslation(x, y, 0);
        monitorQuad.setIsCollidable(true);
        
        Quad quad2 = new Quad("Monitor");
        quad2.initialize(250, 190);
        quad2.setLocalTranslation(x, y, 0);
        monitorNode.attachChild(quad2);
        monitorNode.attachChild(monitorQuad);

        // Ok, now lets create the Texture object that our scene will be rendered to.
        tRenderer.setBackgroundColor(new ColorRGBA(0f, 0f, 0f, 1f));
        fakeTex = new Texture2D();
        fakeTex.setRenderToTextureType(Texture.RenderToTextureType.RGBA);
        tRenderer.setupTexture(fakeTex);
        screenTextureState = disp.getRenderer().createTextureState();
        screenTextureState.setTexture(fakeTex);
        screenTextureState.setEnabled(true);
        monitorQuad.setRenderState(screenTextureState);
        
        monitorNode.updateGeometricState(0.0f, true);
        monitorNode.updateRenderState();
        monitorNode.setLightCombineMode(LightCombineMode.Off);
    }
    /**
     * creates the missile cam.
     * @param x position of the display in ortho values
     * @param y position of the display in ortho values
     * @param scene the root of the scene to render
     */
//     public RobotCamera(final float x, final float y, final Spatial scene) {
//         this(x,y);
//         this.scene = scene;
//     }
    /**
     * creates a default TextureState to display a noise picture
     * while the cam is offline
     */
    public void createNoiseTextureState() {
        
        noiseTex = TextureManager.loadTexture(
                    RobotCamera.class.getClassLoader().getResource("images/noise.jpg"),
                    Texture.MinificationFilter.BilinearNoMipMaps, 
                    Texture.MagnificationFilter.Bilinear);
        noiseTex.setWrap(Texture.WrapMode.Repeat);
        noiseTex.setTranslation(new Vector3f());
        noiseTextureState = disp.getRenderer().createTextureState();
        noiseTextureState.setTexture(noiseTex);
    }
    
    public Node getMonitorNode() {
        return monitorNode;
    }
     public Quad getMonitorQuad() {
        return monitorQuad;
    }
    public CameraNode getCameraNode() {
        return robotCamNode;
    }
    
    /**
     * renders the texture.
     * once the CameraNode gets detached from its client,
     * the camera disables itself and displays the noise.   
     * @param tpf time since last frame.
     */
    public void render(final float tpf) {       
        if (robot_view > -1) {
            // render the robot cam to a texture
            lastRend += tpf;
            if (lastRend > throttle ) {
              tRenderer.render(scene, fakeTex);
              lastRend = 0;
            }
        } else {
            // animate the noise texture
            lastRend += tpf;
            if (lastRend > noiseThrottle ) {
                textSwitch = !textSwitch;
                if (textSwitch) {
                    noiseTex.setTranslation(texturePos);
                } else { 
                    noiseTex.setTranslation(Vector3f.ZERO.clone());
                }
                lastRend = 0;
            }
        }
        
    }
    public void render(final float tpf, Spatial scene) {
        this.scene = scene;
        this.render(tpf);
    }
    public void setPosition(Environment e, boolean ifEven) {
        Quaternion qx = new Quaternion();
        float angle_cam;
        float turn;
        float delta_x;
        float delta_y;
        float x_cam;
        float y_cam;
        if (ifEven){
            setRobot(e.getCamera(0));
            if (e.getCamera(0) < 0) return;
        }
        else {
            setRobot(e.getCamera(1));
            if (e.getCamera(1) < 0) return;
        }
        //if (e.camera < 0) return;
        turn = (float) e.getCpool().get(robot_view).getPitch();
        angle_cam = -(float)(turn-90) * 3.14159265f/180; 
        qx.fromAngles(0f,angle_cam, 0f);    
        getCameraNode().setLocalRotation(qx);
        delta_x = 0.03f * (float)Math.cos(angle_cam-Math.PI/2);
        delta_y = - 0.03f * (float)Math.sin(angle_cam-Math.PI/2);
        x_cam = (float) e.getCpool().get(robot_view).getX()/10-e.width/20+delta_x;
        y_cam = (float) e.getCpool().get(robot_view).getY()/10-e.height/20+delta_y;
        getCameraNode().setLocalTranslation(x_cam, 2.5f, y_cam);
        getCameraNode().updateGeometricState(0, true);
        //render(tpf);
    }
    
    public void setRobot(int robot) {
        robot_view = robot;
        if (robot_view > -1) {
            monitorQuad.setRenderState(screenTextureState);
        } else {
            monitorQuad.setRenderState(noiseTextureState);
        }
        monitorNode.updateRenderState();
    }
   public void setRCNodeName(String name){
       monitorNode.setName(name);
   }  
}
