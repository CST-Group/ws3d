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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import model.Brick;
import model.Thing;
import com.jme.renderer.ColorRGBA;
import java.util.HashMap;
import model.Material3D;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import model.Environment;
import util.Constants;

/**
 *
 * @author eccastro
 */
public class EditBrickFrame extends JFrame {

    Brick obstacle;
    JPanel mainPanel;
    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JButton closeButton;
    JButton deleteButton;
    private Environment e;
    private JCheckBoxMenuItem hiddenObstacle;
    //JComboBox combo;
    JPanel radioPanel;
    List<JRadioButton> rdPool;
    String[] colorArray;
    JTextArea ta;
    static Logger log = Logger.getLogger(EditBrickFrame.class.getCanonicalName());
    
    public EditBrickFrame(final Environment e) {
        this.obstacle = null;
        this.e = e;

            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    createSwingStuff();
                }
            } );

        
    }
    public void setObstacle(Thing obstac)
	{
        this.obstacle = (Brick)obstac;
        int temp = getColorMaterial3DIdx();
        //System.out.println("******* Obstacle color is "+temp);
        rdPool.get(temp).setSelected(true);
        ta.setText("         "+this.obstacle.getMyName());

    }
    /* Method called by Environment to paint EditBrickTab with
         * correct obstacle visibility status (hidden or not).
         */
        public void update() {

            if (obstacle != null) {

                if (obstacle.returnIfWasHidden()) {
                    hiddenObstacle.setSelected(true);

                } else {
                    hiddenObstacle.setSelected(false);

                }
            }
        }
public void setObjectColor(int materialColorIdx) {

                  log.info(" ******* materialColorIdx = "+materialColorIdx);
                  String txt = colorArray[materialColorIdx];
                  log.info(" ******* materialColorIdx corresponds to = "+ txt);
                  ColorRGBA materialColor = this.e.colorPool.get(txt);
                  Material3D m3D = this.obstacle.getMaterial();
                  m3D.setColor(materialColor);
                  this.obstacle.setMaterial(m3D);

         }
    public void setObjectColor(ColorRGBA materialColor) {

        Material3D m3D = this.obstacle.getMaterial();
        m3D.setColor(materialColor);
        this.obstacle.setMaterial(m3D);
        log.info(" ****** Color changed to "+this.obstacle.getMaterial().getColorName());
    }
public int getColorMaterial3DIdx(){
    int ret = 0; // returns 0 in case of not finding the color
    if (this.obstacle != null) {
         ColorRGBA c = this.obstacle.getMaterial().getColor();
         String s = getIdxFromColor(this.e.colorPool, c);
         log.info(" *********** color is = "+ s + " or "+this.obstacle.getMaterial().getColorName());
         for (int i = 0; i <=5;i++){
             if (colorArray[i].equals(s)){
                 return i;
             }
         }
     }
    return ret;
}

public  String getIdxFromColor(HashMap hm,Object value){
    for(Object o:hm.keySet()){
        if(hm.get(o).equals(value)) {
            return (String)o;
        }
    }
    return null;
}
private JPanel createColorButtons(String[] array){


    JPanel radioPanel = new JPanel();
    rdPool = new ArrayList<JRadioButton>();
    JRadioButton color0Radio = new JRadioButton(array[0]);
    JRadioButton color1Radio = new JRadioButton(array[1]);
    JRadioButton color2Radio = new JRadioButton(array[2]);
    JRadioButton color3Radio = new JRadioButton(array[3]);
    JRadioButton color4Radio = new JRadioButton(array[4]);
    JRadioButton color5Radio = new JRadioButton(array[5]);

    rdPool.add(color0Radio);
    rdPool.add(color1Radio);
    rdPool.add(color2Radio);
    rdPool.add(color3Radio);
    rdPool.add(color4Radio);
    rdPool.add(color5Radio);

    color0Radio.setActionCommand(array[0]);
    color1Radio.setActionCommand(array[1]);
    color2Radio.setActionCommand(array[2]);
    color3Radio.setActionCommand(array[3]);
    color4Radio.setActionCommand(array[4]);
    color5Radio.setActionCommand(array[5]);

    // Register a listener for the radio buttons.
    RadioListener myListener = new RadioListener();
    color0Radio.addActionListener(myListener);
    color1Radio.addActionListener(myListener);
    color2Radio.addActionListener(myListener);
    color3Radio.addActionListener(myListener);
    color4Radio.addActionListener(myListener);
    color5Radio.addActionListener(myListener);

    // Group the radio buttons.
    ButtonGroup group = new ButtonGroup();
    group.add(color0Radio);
    group.add(color1Radio);
    group.add(color2Radio);
    group.add(color3Radio);
    group.add(color4Radio);
    group.add(color5Radio);

    DefaultButtonModel model= new DefaultButtonModel();
    group.setSelected(model, false);

    radioPanel.setLayout(new GridLayout(0, 1));
    radioPanel.add(color0Radio);
    radioPanel.add(color1Radio);
    radioPanel.add(color2Radio);
    radioPanel.add(color3Radio);
    radioPanel.add(color4Radio);
    radioPanel.add(color5Radio);



    return radioPanel;

    }

private void createSwingStuff() {
        mainPanel = new JPanel();
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        ta = new JTextArea("");
        ta.setEditable(false);
        closeButton = new JButton("Close");
        deleteButton = new JButton("Delete me!");
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        deleteButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                log.info("*** Obstacle deleted! ***");
                obstacle.removeRememberMeIcon(e);
                e.removeThing(obstacle);
                setVisible(false);
            }
        });
        panel1.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Edit color"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                panel1.getBorder()));
    panel1.setLayout(new GridLayout(1, 1));

    colorArray = Constants.arrayOfColors;

    panel1.add(createColorButtons(colorArray));

    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(ta, BorderLayout.PAGE_START);
    mainPanel.add(panel1, BorderLayout.LINE_START);

    panel2.setBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Visibility"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                panel2.getBorder()));
        panel2.setLayout(new GridLayout(1, 1));
        hiddenObstacle = new JCheckBoxMenuItem("I'm hidden");
        hiddenObstacle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (hiddenObstacle.isSelected()) {
                       // obstacle.hideMe((Graphics2D) getGraphics());
                        obstacle.hideMe(e);
                       // theMainInstance.i.ep.e.addRememberMeIcon(obstacle, theMainInstance.sf.gameState.flagMS);
                        hiddenObstacle.setSelected(true);

                    } else {
                        //obstacle.undoHideMe((Graphics2D) getGraphics());
                        obstacle.undoHideMe(e);
                       // theMainInstance.i.ep.e.removeRememberMeIcon(obstacle);
                        hiddenObstacle.setSelected(false);
                    }
                    //theMainInstance.sf.gameState.ThingsRN.updateRenderState();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(EditBrickFrame.this, "Error in obstacle execution.", "ERRO", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel2.add(hiddenObstacle);
        mainPanel.add(panel2, BorderLayout.LINE_END);

		panel3.setLayout(new GridLayout(1,2));
        panel3.add(deleteButton);
        panel3.add(closeButton);
        mainPanel.add(panel3, BorderLayout.PAGE_END);

        add(mainPanel);
        pack();
        setVisible(false);
        setResizable(false);
}
class RadioListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {

            ColorRGBA materialColor = e.colorPool.get(ev.getActionCommand().toString());
            setObjectColor(materialColor);
        }
}
}
