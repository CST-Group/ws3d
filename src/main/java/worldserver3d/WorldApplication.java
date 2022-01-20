/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package worldserver3d;

/**
 *
 * @author gudwin
 */

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import model.Environment;
import worldserver3d.view.WorldFrame;

public class WorldApplication extends SimpleApplication {

    public Environment e;
    public WorldAppState wap;
    public WorldFrame wf;
    
    public WorldApplication() {
        e = new Environment(1024,768);
    }
   
    public void simpleInitApp() {
        wap = new WorldAppState(e);
        stateManager.attach(wap);
        getFlyByCamera().setDragToRotate(true);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //System.out.println("Location: "+cam.getLocation()+" Direction: "+cam.getDirection());
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
    
    public void setWorldFrame(WorldFrame nwf) {
        wf = nwf;
    }
    
    
    // Testing
    
    //public Geometry offBox;
    //private float angle = 0;

//    public FrameBuffer offBuffer;
//    private ImageDisplay display;
//
//    //public static final int width = 800, height = 600;
//
//    private final ByteBuffer cpuBuf = BufferUtils.createByteBuffer(Constants.CamResolutionX * Constants.CamResolutionY * 4);
//    private final BufferedImage image = new BufferedImage(Constants.CamResolutionX, Constants.CamResolutionY,BufferedImage.TYPE_INT_BGR);
//    
//    public void createDisplayFrame(){
//        SwingUtilities.invokeLater(new Runnable(){
//            @Override
//            public void run(){
//                JFrame frame = new JFrame("Render Display");
//                display = new ImageDisplay();
//                display.setPreferredSize(new Dimension(Constants.CamResolutionX, Constants.CamResolutionY));
//                frame.getContentPane().add(display);
//                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                frame.addWindowListener(new WindowAdapter(){
//                    @Override
//                    public void windowClosed(WindowEvent e){
//                        System.out.println("Window closed !");
//                    }
//                });
//                frame.pack();
//                frame.setLocationRelativeTo(null);
//                frame.setResizable(false);
//                frame.setVisible(true);
//            }
//        });
//    }
//
//    @Override
//    public void cleanup() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    
//    private class ImageDisplay extends JPanel {
//
//        private long t;
//        private long total;
//        private int frames;
//        private int fps;
//
//        @Override
//        public void paintComponent(Graphics gfx) {
//            super.paintComponent(gfx);
//            Graphics2D g2d = (Graphics2D) gfx;
//            if (t == 0)
//                t = timer.getTime();
//            synchronized (image){
//                g2d.drawImage(image, null, 0, 0);
//            }
//            long t2 = timer.getTime();
//            long dt = t2 - t;
//            total += dt;
//            frames ++;
//            t = t2;
//            if (total > timer.getResolution()) {
//                fps = frames;
//                total = 0;
//                frames = 0;
//            }
//            g2d.setColor(Color.white);
//            g2d.drawString("FPS: "+fps, 0, getHeight() - 100);
//        }
//    }
//
//    public void updateImageContents(){
//        cpuBuf.clear();
//        getRenderer().readFrameBuffer(offBuffer, cpuBuf);
//
//        synchronized (image) {
//            Screenshots.convertScreenShot2(cpuBuf.asIntBuffer(), image);    
//        }
//
//        if (display != null)
//            display.repaint();
//    }
//    
//    @Override
//    public void reshape(ViewPort vp, int w, int h) {
//    }
//    
//    @Override
//    public void preFrame(float tpf) {
//    }
//
//    @Override
//    public void postQueue(RenderQueue rq) {
//    }
//
//    /**
//     * Update the CPU image's contents after the scene has
//     * been rendered to the framebuffer.
//     */
//    @Override
//    public void postFrame(FrameBuffer out) {
//        updateImageContents();
//    }
//    
//    @Override
//    public void setProfiler(AppProfiler profiler) {
//        // not implemented
//    }
//    
//    @Override
//    public void initialize(RenderManager rm, ViewPort vp) {
//    }
//
//    @Override
//    public boolean isInitialized() {
//        return true;
//    }
    
    
}
