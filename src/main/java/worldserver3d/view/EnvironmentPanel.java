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

package worldserver3d.view;


/**
 * @author patbgi
 * @author eccastro
 * 
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import model.*;
import model.Creature;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import com.jme.system.DisplaySystem;
import java.util.logging.Logger;

public class EnvironmentPanel
	extends JPanel implements MouseListener, MouseMotionListener
    {
	
	OutputStream values;
	public Environment e;
        
	private final int NONE        = -1;
	private final int CAR         = -2;
	private final int CAR_POINT   = -3;
	private final int TARGET      = -4;
	private final int CORNER_X1Y1 = -6;
	private final int CORNER_X1Y2 = -7;
	private final int CORNER_X2Y1 = -8;
	private final int CORNER_X2Y2 = -9;
        
        private final int crotating    = 0;
        private final int cmoving      = 1;
        private final int omoving      = 2;
        private final int free         = 3;
        private final int oscaling     = 4;
	
	private final double dTime = 15;
        private final String newCreaturePlaceErrorTitle = "Place a creature!";
        private final String newCreaturePlaceErrorMsg = "Not on an obstacle!!!";


    public EditBrickFrame obstacleTab;
    public ContainerViewer containerViewer;
    public HashMap<Long, KnapsackAndScoreFrame> scoreTabList;
    public EditFoodFrame foodTab;
    public EditJewelFrame jewelTab;
	
    public DisplaySystem display;
	private int mouseXini, mouseYini;
	private int mouseXfin, mouseYfin;

	private boolean isMoving;
	private int     objectID = -1;
        private int     creatureID = -1;
        private int obstacleIdx=-1;

	

	private Socket displaySocket = null;
	private boolean endConnection = false;

    private KnapsackAndScoreFrame scorePanel;
	private boolean canMove = true;
	private boolean changed;

	private Timer timer = new Timer();
	private TimerTask runningCar;

	private ICEvent icEvent = new ICEvent();

	private boolean hasChanged = true;

	
	
	private boolean showPath = false;	
	List path = new ArrayList();

	private boolean showPlan = false;	
	List plan = new ArrayList();	
	
	private boolean showTree = false;	
	Map tree = new HashMap();
        static Logger log = Logger.getLogger(EnvironmentPanel.class.getCanonicalName());

    public void mouseClicked(MouseEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseDragged(MouseEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseMoved(MouseEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	private class ICEvent extends Observable {  // Identify colors 
		
		public void setValue(Color c)
		{
			setChanged();
			notifyObservers(c);
		}
	}
	
	private class FEvent extends Observable { // Fuel
		
		public void setValue(int value)
		{
			setChanged();
			//System.out.println("Fuel: " + value);
			notifyObservers(new Integer(value));
		}
	}
	
	public EnvironmentPanel(int width, int height)
	{
        display = DisplaySystem.getDisplaySystem();
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);
        e = new Environment(width,height);

        obstacleTab = new EditBrickFrame(e);
        containerViewer = new ContainerViewer(e);
        scoreTabList = new HashMap();
        foodTab = new EditFoodFrame(e);
        jewelTab = new EditJewelFrame(e);
		
		isMoving = false;
		objectID = NONE;
        log.info("Environment dimension: width= "+width+" height= "+height);
	}


        public void updateDimensions(int width, int height){
            display = DisplaySystem.getDisplaySystem();
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.white);
		addMouseListener(this);
		addMouseMotionListener(this);
            this.e.updateDimensions(width, height);

        }
	public boolean getMoving() { return isMoving; }
	

	private boolean shouldSimplify (Point2D a, Point2D b, boolean simplify) {
		
		double margem = 10;
		int width  = getPreferredSize().width;
		int height = getPreferredSize().height;

		
		if (a.getX() < margem && b.getX() > (width-margem))
			return true;
		
		if (b.getX() < margem && a.getX() > (width-margem))
			return true;
		
		if (a.getY() < margem && b.getY() > (height-margem))
			return true;
		
		if (b.getY() < margem && a.getY() > (height-margem))
			return true;		
			
		return false;
	}
	
	private void paintPath (List path, Graphics2D g2D, boolean simplify) {
		Point2D lastPoint = null;
		for (int i=0; i<path.size(); i++) {
			Point2D point = (Point2D)path.get(i);
			//g2D.fillRect((int)point.getX(), (int)point.getY(), 5, 5);
			if (lastPoint != null)  {
				if (!shouldSimplify(point, lastPoint, simplify)) {
					g2D.drawLine((int)point.getX(), (int)point.getY(), (int)lastPoint.getX(), (int)lastPoint.getY());
				}
			}
			lastPoint = point;
		}		
	}

	private void paintTree (Map tree, Graphics2D g2D, boolean simplify) {
		Set entrySet = tree.entrySet();
		synchronized (entrySet) {
			Iterator entryIterator = entrySet.iterator();
			while (entryIterator.hasNext()) {
				Map.Entry entry = (Map.Entry) entryIterator.next();
				Point2D a = (Point2D)entry.getKey();
				Point2D b = (Point2D)entry.getValue();
				
				if (!shouldSimplify(a, b, simplify)) {
					g2D.drawLine((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());
				}				
			}
		}
	}	
	
public int GetCreatureIndex(int x, int y) {
    for (Creature c : e.getCpool())
        if (c.contains(x, y) || c.pointContains(x, y)) return e.getCpool().indexOf(c);
 return -1;   
}        
        
public int GetObjectIndex(int x, int y) {
    for (Thing o : e.getOpool())
        if (o.containsX1Y1(x, y) || o.containsX1Y2(x, y) || o.containsX2Y1(x, y) || o.containsX2Y2(x, y) || o.contains(x, y)) return e.getOpool().indexOf(o);
 return -1;   
}
        
        
	public void startMoving()
	{
		if (!isMoving)
		{
			removeMouseListener(this);
			removeMouseMotionListener(this);
			
			final EnvironmentPanel env = this;
			if (runningCar != null) runningCar.cancel();
			runningCar = new TimerTask() {
                           public void run() {
                                synchronized (env) {
                                    for (Creature c : e.getCpool())
                                        c.move(dTime, env.e);
//                                        if (c.contains(target.getX(), target.getY())) {
//                                            target.setRandomPosition(0, getPreferredSize().width, 0, getPreferredSize().height, env);
//                                        }
                                        repaint();
                                }
                            }
			};
			timer.scheduleAtFixedRate(runningCar, 0, (int)dTime);
			isMoving = true;
		}
	}

        public void step() {
            removeMouseListener(this);
	    removeMouseMotionListener(this);
            final EnvironmentPanel env = this;
            synchronized (env) {
                for (Creature c : env.e.getCpool())
                   if ((c.hasStarted) && (c.getFuel() > 0)) {
                        c.move(dTime, env.e);
//                        if (car.contains(target.getX(), target.getY())) {
//                            target.setRandomPosition(0, getPreferredSize().width, 0, getPreferredSize().height, env);
                   }
                repaint();
            }
        }
        
        
	public void stopMoving()
	{
		if (isMoving)
		{
			addMouseListener(this);
			addMouseMotionListener(this);
                        if (runningCar != null) runningCar.cancel();
			runningCar = new TimerTask() {
                           public void run() {
                                synchronized (this) {
                                    //try {
                                    //    env.wait();
                                    //} catch (InterruptedException e) {
                                    //}

                                    //if (car.getFuel() > 0) {
                                        //car.move(dTime, obstacles, nObstacles, env);
                                        //if (car.contains(target.getX(), target.getY())) {
                                        //    target.setRandomPosition(0, getPreferredSize().width, 0, getPreferredSize().height, env);
                                        //}
                                    for (Creature c : e.getCpool())
                                    {   c.updateVisualSensorPosition();
                                        c.updateContactSensorPosition();
                                        c.updateContactSensor(e);
                                        c.updateVisualSensor(e);
                                    }   
                                        repaint();
                                    //}
                                }
                            }
			};
			timer.scheduleAtFixedRate(runningCar, 0, (int)dTime);
			isMoving = false;
			//System.out.println("stopped...");
		}
	}



	public void mouseExited (MouseEvent event) {}
	public void mouseEntered(MouseEvent event) {}


	
	public void addICEventObserver(Observer o){
		icEvent.addObserver(o);
	}
	
	
	

	
	public boolean colideWithObstacle (double x, double y, double dist) {
		for (Thing o : e.getOpool())
                {    
			if (x >= o.getX1()-dist && 
				x <= o.getX2()+dist &&
				y >= o.getY1()-dist &&
				y <= o.getY2()+dist)
				return true;
		}
		return false;
	}



	
	//Retorna se o cen?rio foi alterado manualmente
	public boolean hasChanged() {
		return this.hasChanged;
	}


	//ajusta se o cen?rio foi alterado manualmente
	public void setChanged(boolean changed) {
		this.hasChanged = changed;
	}


	/**
	 * @param b
	 */
	public void setShowPath(boolean show) {
		this.showPath = show;		
	}
	public void setShowPlan(boolean show) {
		this.showPlan = show;		
	}	
	public void setShowTree(boolean show) {
		this.showTree = show;		
	}		
	
	
	public void addPathStep (Point2D newPoint) {
		
		//Simplifica os pontos do trajeto
		for (int i=Math.max(path.size()-10, 1); i<path.size()-3; i++) {
			//Para cada ponto intermedi?rio do trajeto
			Point2D p  = (Point2D)path.get(i  );
			
			//E seus vizinhos da frente e de tr?s
			Point2D p1 = (Point2D)path.get(i-1);
			Point2D p2 = (Point2D)path.get(i+1);
			
			//Se est? razoavelmente perto dos vizinhos
			if ( (p1.distance(p) > 50) || (p2.distance(p) > 50) )
				continue;			
			
			//E est? alinhado com eles (Calculado por um produto vetorial -> Valores altos indicam pontos desalinhados
			Point2D v1 = new Point2D.Double (p1.getX() - p.getX(), p1.getY() - p.getY() );
			Point2D v2 = new Point2D.Double (p2.getX() - p.getX(), p2.getY() - p.getY() );					
			double produtoInterno = v1.getX()*v2.getY() - v1.getY()*v2.getX();

			//Ent?o pode ser removido do trajeto, 
			//sendo substituido por uma linha reta sem grandes prejuizos. 
			if ( Math.abs(produtoInterno) < 2) {
				path.remove(i);
				i--;
			}
			
		}
		
		
		this.path.add(newPoint);
	}

	public void clearPath() {
		path.clear();		
	}
	public synchronized Environment getEnvironment() {
		return e;		
	}

    public void closePanels() {

        obstacleTab.dispose();
        //scoreTab.dispose();
        foodTab.dispose();
        for(KnapsackAndScoreFrame ksf: scoreTabList.values()){
            ksf.dispose();
        }
    }
//public void setTabs(EditBrickFrame bt, KnapsackAndScoreFrame st, EditFoodFrame ft, EditJewelFrame pt){
//        obstacleTab = bt;
//        scoreTab = st;
//        foodTab = ft;
//        jewelTab = pt;
//}
//public void setBrickTab(EditBrickFrame bt){
//    obstacleTab = bt;
//}
//public void setFoodTab(EditFoodFrame bt){
//    foodTab = bt;
//}
//public void setScoreTab(KnapsackAndScoreFrame bt){
//    scoreTab = bt;
//}
//public void setJewelTab(EditJewelFrame bt){
//    jewelTab = bt;
//}
}
