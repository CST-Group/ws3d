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
 * @author patbgi,gudwin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import javax.swing.*;
import javax.swing.event.*;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;



public class WorldFrame
	extends JFrame implements ActionListener, MenuListener
{
	private ImageIcon icon;
	public  EnvironmentPanel ep;

	//private AddObjectTab        addObject;
	//private DriveCarTab         driveCar;
	//private NewEnvironmentFrame newEnviron;
	//private SocketConfFrame     socketConf;
	//private EditCarTab          editCar;

	public JScrollPane sp;
	
	private JMenuItem saveItem;
	private JMenuItem newItem;
	private JMenuItem exitItem;
        private JMenuItem SimulateItem;

	//demonstra menus com caixas de sele??o e bot?es de r?dio
	private JCheckBoxMenuItem addObjectItem = new JCheckBoxMenuItem ("Add Object");
	private JCheckBoxMenuItem  editCarItem = new JCheckBoxMenuItem("Edit Car");
	private JCheckBoxMenuItem driveCarItem = new JCheckBoxMenuItem("Drive Car");
	//private JCheckBoxMenuItem  controlItem = new JCheckBoxMenuItem("Control");
        static Logger log = Logger.getLogger(WorldFrame.class.getCanonicalName());

	public WorldFrame()
	{
		setTitle("Setup Window - Click to insert Creatures and Obstacles");
		
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage("..images/carro7.GIF");
		setIconImage(img);
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                
		JMenuBar mbar = new JMenuBar();
		//setJMenuBar(mbar);
		
		// demonstra itens ativados e desativados	
		JMenu fileMenu = new JMenu("File");
		fileMenu.addMenuListener(this);
		
		JMenu optionMenu = new JMenu("Options");
		optionMenu.addMenuListener(this);
		
				
		/*final ViewerFrame viewFrame = new ViewerFrame();
		JMenu controlMenu = new JMenu("Controle");
		controlMenu.addMenuListener(this);
		controlMenu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				viewFrame.setLocation(829,0);
				viewFrame.setResizable(false);
				viewFrame.setAlwaysOnTop(true); // a tela fica fixa
				viewFrame.setVisible(true);
			}
		});*/
		
		// demonstra teclas de atalho
		
		JMenuItem openItem = new JMenuItem("Open", new ImageIcon("../images/open.gif")); // Abrir
		newItem = new JMenuItem(" New", new ImageIcon("../images/new.gif"));   // Limpar
		newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		saveItem = new JMenuItem(" Save", new ImageIcon("../images/save.gif"));   // Salvar
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		exitItem = new JMenuItem(" Exit", new ImageIcon("../images/sair.gif"));
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		SimulateItem = new JMenuItem(" Simulate", new ImageIcon("../images/sair.gif"));
		SimulateItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		
		
		mbar.add(makeMenu(fileMenu,
				new Object[] { newItem,	openItem, saveItem, SimulateItem, null, null, exitItem }, this));
		


		ep = new EnvironmentPanel(800, 600);
                //SimulationFrame sf = new SimulationFrame(ep.e);
		

		//addObject  = new AddObjectTab(environment);
		//driveCar   = new  DriveCarTab(this);
		//editCar    = new   EditCarTab(environment);
		//viewFrame  = new ViewerFrame(environment);
		//newEnviron = new NewEnvironmentFrame(this);
		
		
// ***		final ViewerFrame viewFrame = new ViewerFrame(areaDesenho);
		
		
//		addObject.addWindowListener(new WindowAdapter(){
//			public void windowClosing(WindowEvent e){
//				addObjectItem.setSelected(false);
//			}
//		});
//		
//		driveCar. addWindowListener(new WindowAdapter(){
//			public void windowClosing(WindowEvent e){
//				driveCarItem.setSelected(false);
//			}
//		});
//		
//		editCar.  addWindowListener(new WindowAdapter(){
//			public void windowClosing(WindowEvent e){
//				editCarItem.setSelected(false);
//			}
//			});
// ***		viewFrame.addWindowListener(new WindowAdapter(){
// ***			public void windowClosing(WindowEvent e){
// ***				controlItem.setSelected(false);
// ***			}
// ***			public void windowClosed(WindowEvent e){
// ***				controlItem.setSelected(false);
// ***			}
// ***		});
		
//		controlItem.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				if(controlItem.isSelected()){
// ***					viewFrame.setLocation(365,600);
// ***					viewFrame.setResizable(false);
// ***					viewFrame.setAlwaysOnTop(true); // a tela fica fixa
// ***					viewFrame.setVisible(true);
//				}
//				else {
// ***					viewFrame.setVisible(false);
//				}
//			}
//		});
//		addObjectItem.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				if(addObjectItem.isSelected()){
//					addObject.setLocation(829,0);
//					addObject.setResizable(false);
//					addObject.setAlwaysOnTop(true); // a tela fica fixa
//					addObject.setVisible(true);
//				}
//				else {
//					addObject.setVisible(false);
//				}
//			}
//		});
//		
//				
//		
//		editCarItem.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				if(editCarItem.isSelected()){
//					editCar.setLocation(743,0);
//					editCar.setResizable(false);
//					editCar.setAlwaysOnTop(true);
//					editCar.setVisible(true);
//				}
//				else {
//					editCar.setVisible(false);
//				}
//			}
//		});
//		driveCarItem.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent e) {
//				if(driveCarItem.isSelected()){
//					driveCar.setLocation(753,0);
//					driveCar.setResizable(false);
//					driveCar.setAlwaysOnTop(true);
//					driveCar.setVisible(true);
//				}
//				else {
//					driveCar.setVisible(false);
//				}
//			}
//		});
		
		
		// demonstra menus com �cones e aninhados
		mbar.add(makeMenu(optionMenu,
				new Object[]{
				addObjectItem,
				editCarItem,
				driveCarItem,
				//null,
				//null,
				//controlItem,
		},
		this));
		
		// demonstra mnem?nicos
		fileMenu.setMnemonic('F');
		optionMenu.setMnemonic('O');
                sp = new JScrollPane(ep); 
		add(sp);
		setVisible(false);
		setResizable(true);
		pack();

		//socketConf = new SocketConfFrame(this);
		//socketConf.setVisible(true);
	}

	
	public EnvironmentPanel  getEnvironmentPanel()  { return ep; }
//	public AddObjectTab getAddObjectTab() { return addObject; }
//	public DriveCarTab  getDriveCarTab()  { return driveCar; }
//	public EditCarTab   getEditCarTab()   { return editCar; }

	
	public void createNewEnvironmentPanel(int width, int height)
	{
//		getDriveCarTab().dispose();
//		getEditCarTab().dispose();
//		getAddObjectTab().dispose();
				
		getContentPane().removeAll();
		//getEnvironment().stopMoving();
		setEnvironmentPanel(new EnvironmentPanel(width, height));

		JScrollPane jScrollPane = new JScrollPane(ep);
		jScrollPane.getViewport().setBackground(new Color(150, 150, 150));
		add(jScrollPane);

//		setDriveCarTab (new DriveCarTab (this));
//		setEditCarTab  (new EditCarTab  (this.environment));
//		setAddObjectTab(new AddObjectTab(this.environment));
		
		pack();
	}
	
//	public void setAddObjectTab(AddObjectTab addObject)   { this.addObject   = addObject; }
//	public void setDriveCarTab (DriveCarTab  driveCar)    { this.driveCar    = driveCar; }
//	public void setEditCarTab  (EditCarTab   editCar)     { this.editCar     = editCar; }

	public void setEnvironmentPanel(EnvironmentPanel ep) { this.ep = ep; } 


	public void actionPerformed(ActionEvent evt)
	{
		String arg = evt.getActionCommand();
		//System.out.println(arg);
		if(arg.equals(" Exit")){
			System.exit(0);
                        System.runFinalization();
                        System.gc();
                }else
//			codigo para abrir uma janela de arquivos;
			if(arg.equals("Open"))
			{
				try
				{
					String fileName;
					JFileChooser chooser = new JFileChooser();

					if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
					{
						fileName = chooser.getSelectedFile().getCanonicalPath();
						log.info("You chose to open this file: "+fileName);
						ep.e.open(fileName);
					}
				} catch (Exception e) { e.printStackTrace(); }
			}
			else if(arg.equals(" Save"))
			{
		        try
				{
		        	String fileName;
		        	JFileChooser chooser = new JFileChooser();
			    
		        	if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		        	{
		        		fileName = chooser.getSelectedFile().getCanonicalPath();
		        		log.info("You chose to save in this file: "+fileName);
						ep.e.save(fileName);
		        	}
				} catch (Exception e) { e.printStackTrace(); }
			}
			
			else if(arg.equals(" New"))
			{
				//newEnviron.setVisible(true);
				addObjectItem.setSelected(false);
				editCarItem.setSelected(false);
				driveCarItem.setSelected(false);
				//controlItem.setSelected(false);
				
			}
                
                       else if(arg.equals(" Simulate"))
			{
				log.info("Consegui !!!");
                                //SimulationFrame sf = new SimulationFrame(ep.e);
				
			}
			
			
	}

			
public void menuSelected(MenuEvent evt)
{
	//saveItem.setEnabled(!readonlyItem.isSelected());
	//saveAsItem.setEnabled(!readonlyItem.isSelected());
}

public void menuDeselected(MenuEvent evt)
{
	
}

public void menuCanceled(MenuEvent evt)
{
	
}

public static JMenu makeMenu(Object parent, Object[] items, Object target)
{
	JMenu m = null;
	if (parent instanceof JMenu)
		m = (JMenu)parent;
	else if (parent instanceof String)
		m = new JMenu((String)parent);
	else 
		return null;
	for (int i = 0; i < items.length; i++)
	{
		if (items[i] == null)
			m.addSeparator();
		else 
			m.add(makeMenuItem(items[i], target));
	}
	return m;
}

public static JMenuItem makeMenuItem(Object item, Object target)
{
	JMenuItem r = null;
	if (item instanceof String)
		r = new JMenuItem((String)item);
	else if (item instanceof JMenuItem)
		r = (JMenuItem)item;
	else return null;
	
	if (target instanceof ActionListener)
		r.addActionListener((ActionListener)target);
	return r;
}

//public void Save(Component myComponent) {
//    Dimension size = myComponent.getSize();
//    BufferedImage myImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
//    Graphics2D g2 = myImage.createGraphics();
//    g2.setBackground(Color.WHITE);
//    myComponent.paint(g2);
//    JFileChooser chooser = new JFileChooser();
//    int status = chooser.showSaveDialog(this);
//    if (status == JFileChooser.APPROVE_OPTION) {
//        File file = chooser.getSelectedFile();
//        try {
//            OutputStream out = new FileOutputStream(file);
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            encoder.encode(myImage);
//            out.close();
//            
//        } catch (Exception e) {
//            System.out.println(e); 
//        }
//    } 
//} 

/*public static JPopupMenu makePopupMenu(Object[] items, Object target)
{
	JPopupMenu m = new JPopupMenu();
	for (int i =0; i < items.length; i++)
	{
		if(items[i] == null)
			m.addSeparator();
	    else
	    	m.add(makeMenuItem(items[i], target));
	}
	return m;
}*/
	
}
