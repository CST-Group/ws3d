/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package worldserver3d;

import com.jme3.math.ColorRGBA;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.system.Timer;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.util.BufferUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import model.Creature;
import util.Constants;

/**
 *
 * @author rgudwin
 */
public class CameraRenderer implements SceneProcessor {
    
    public FrameBuffer offBuffer;
    private ImageDisplay display;
    WorldApplication app;
    public Camera offCamera;
    public CameraNode camNode;
    int resX;
    int resY;
    JFrame frame;
    private final ByteBuffer cpuBuf;
    private final BufferedImage image;
    Creature creature;
    
    public CameraRenderer(WorldApplication _app, Node rootNode, Creature c) {
        app = _app;
        creature = c;
        //cpuBuf = BufferUtils.createByteBuffer(resX * resY * 4);
        //image = new BufferedImage(resX, resY,BufferedImage.TYPE_INT_BGR);
        //offBuffer = new FrameBuffer(resX, resY, 1);
        //Camera offCamera = new Camera(resX, resY);
        //camNode = new CameraNode("Robot Camera Node", app.getCamera().clone());
        camNode = new CameraNode("Robot Camera Node", new Camera(250,200));
        //offCamera = app.getCamera().clone();
        offCamera = camNode.getCamera();
        offCamera.setPlaneState(0);
        System.out.println("The original camera has dimensions: "+offCamera.getWidth()+","+offCamera.getHeight());
        //resX = offCamera.getWidth();
        //resY = offCamera.getHeight();
        resX = 250;
        resY = 200;
        cpuBuf = BufferUtils.createByteBuffer(resX * resY * 4);
        image = new BufferedImage(resX, resY,BufferedImage.TYPE_INT_BGR);
        offBuffer = new FrameBuffer(resX, resY, 1);
        ViewPort offView = app.getRenderManager().createPreView("Offscreen View", offCamera);        
        offView.setBackgroundColor(ColorRGBA.DarkGray);
        offView.setClearFlags(true, true, true);
        offView.addProcessor(this);
        offCamera.setFrustumPerspective(80f, 1f, 0.01f, 1000f);
        //locateCamera(offCamera,c);
        offBuffer.setDepthBuffer(Image.Format.Depth);
        offBuffer.setColorBuffer(Image.Format.RGBA8);
        offView.setOutputFrameBuffer(offBuffer);
        offView.attachScene(rootNode);
    }
    
    public void createDisplayFrame(){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                frame = new JFrame(creature.getID().toString());
                display = new ImageDisplay();
                display.setPreferredSize(new Dimension(resX, resY));
                frame.getContentPane().add(display);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter(){
                    @Override
                    public void windowClosed(WindowEvent e){
                        System.out.println("Window closed !");
                    }
                });
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);
            }
        });
    }

    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private class ImageDisplay extends JPanel {

        private long t;
        private long total;
        private int frames;
        private int fps;

        @Override
        public void paintComponent(Graphics gfx) {
            Timer timer = app.getTimer(); 
            super.paintComponent(gfx);
            Graphics2D g2d = (Graphics2D) gfx;
            if (t == 0)
                t = timer.getTime();
            synchronized (image){
                g2d.drawImage(image, null, 0, 0);
            }
            long t2 = timer.getTime();
            long dt = t2 - t;
            total += dt;
            frames ++;
            t = t2;
            if (total > timer.getResolution()) {
                fps = frames;
                total = 0;
                frames = 0;
            }
            g2d.setColor(Color.white);
            g2d.drawString("FPS: "+fps, 0, getHeight() - 100);
        }
    }
    
    public static void convertScreenShot(IntBuffer bgraBuf, BufferedImage out){
        WritableRaster wr = out.getRaster();
        DataBufferInt db = (DataBufferInt) wr.getDataBuffer();
        
        int[] cpuArray = db.getData();
        
        bgraBuf.clear();
        bgraBuf.get(cpuArray);
        
        int width  = wr.getWidth();
        int height = wr.getHeight();

        // flip the components the way AWT likes them
        for (int y = 0; y < height / 2; y++){
            for (int x = 0; x < width; x++){
                int inPtr  = (y * width + x);
                int outPtr = ((height-y-1) * width + x);
                int pixel = cpuArray[inPtr];
                cpuArray[inPtr] = cpuArray[outPtr];
                cpuArray[outPtr] = pixel;
            }
        }
    }

    public void updateImageContents(){
        cpuBuf.clear();
        app.getRenderer().readFrameBuffer(offBuffer, cpuBuf);
        try {
        synchronized (image) {
            convertScreenShot(cpuBuf.asIntBuffer(), image);    
        }
        } catch (Exception e) {
            System.out.println(e);
        }

        if (display != null)
            display.repaint();
    }
    
    @Override
    public void reshape(ViewPort vp, int w, int h) {
    }
    
    @Override
    public void preFrame(float tpf) {
    }

    @Override
    public void postQueue(RenderQueue rq) {
    }

    /**
     * Update the CPU image's contents after the scene has
     * been rendered to the framebuffer.
     */
    @Override
    public void postFrame(FrameBuffer out) {
        updateImageContents();
    }
    
    @Override
    public void setProfiler(AppProfiler profiler) {
        // not implemented
    }
    
    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
    }

    @Override
    public boolean isInitialized() {
        return true;
    }
    
}
