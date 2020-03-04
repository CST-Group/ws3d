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

import com.jme.math.Vector2f;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;

import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.logging.Logger;
import javax.swing.*;
import model.Creature;
import model.Thing;
import worldserver3d.CreatureNodeFactory;
import worldserver3d.Main;

/**
 * Class that manages the creation of a new WorldFrame: new entities and
 *  infrastructure of the game
 * @author gudwin
 */
public class NewWorldFrame
        extends JFrame {

    Quad q;
    Main m;
    WorldFrame wf = null;
    Node rn = null;
    JPanel mainPanel;
    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JTextField heightText;
    JTextField widthText;
    JButton okButton;
    JButton cancelButton;
    float lastx = 1;
    float lasty = 1;
    static Logger log = Logger.getLogger(NewWorldFrame.class.getCanonicalName());

    Observer environmentObserver = null;
   
    public NewWorldFrame(Main nm, Observer eo) {
        super("Ground surface dimension");
        m = nm;
        environmentObserver = eo;
        this.q = null;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createSwingStuff();
            }
        });

    }

    public void setFloorQuad(Quad quad) {
        this.q = quad;

        //factor 10 due to quad file resize (see SimulationGameState)
        heightText.setText(new Double(10*q.getHeight()).toString());
        widthText.setText(new Double(10*q.getWidth()).toString());
    }

    public void setDimensions(int w, int h) {
        heightText.setText(new Double(h).toString());
        widthText.setText(new Double(w).toString());

        wf.ep.updateDimensions(w, h);
    }

    public void setWorldFrame(WorldFrame wf) {
        this.wf = wf;
    }

    public void setRootNode(Node n) {
        rn = n;
    }

    private void restoreEnvironmentSettings() {
        /**
         * ATTENTION:
         * Environment is rebuilt!!!!
         */

        //Backup section:
        LightState ols = wf.ep.e.ls;
        MaterialState odefaultms = wf.ep.e.defaultms;
        MaterialState flagMS = wf.ep.e.flagMS;
        CreatureNodeFactory ocnf = wf.ep.e.cnf;
       // int ocamera = wf.ep.e.camera;
        MaterialState creatureMS = wf.ep.e.creatureMS;
         //factor 10 due to quad file resize (see SimulationGameState)
        float newwidth = ((float) Double.parseDouble(widthText.getText())/10);
        float newheight = ((float) Double.parseDouble(heightText.getText())/10);
        q.resize(newwidth, newheight);
        q.scaleTextureCoordinates(0, new Vector2f(1 / lastx, 1 / lasty));
        lastx = newwidth / 80;
        lasty = newheight / 60;
        q.scaleTextureCoordinates(0, new Vector2f(lastx, lasty));
        wf.remove(wf.sp);
        
        wf.ep.e.cpoolNotifier.deleteObservers();
        wf.ep = new EnvironmentPanel((int) newwidth * 10, (int) newheight * 10);

        //Restore backup data:
        wf.ep.e.cpoolNotifier.addAnObserver(this.environmentObserver);
        wf.ep.e.ls = ols;
        wf.ep.e.defaultms = odefaultms;
        wf.ep.e.creatureMS = creatureMS;
        wf.ep.e.flagMS = flagMS;
        wf.ep.e.cnf = ocnf;
        wf.ep.e.rcnEven = m.sf.gameState.rcnEven;
        wf.ep.e.rcnOdd = m.sf.gameState.rcnOdd;

        wf.sp = new JScrollPane(wf.ep);
        wf.add(wf.sp);
    }

    public void updateQuadSettings() {

        float newwidth = ((float) Double.parseDouble(widthText.getText())/10);
        float newheight = ((float) Double.parseDouble(heightText.getText())/10);
        log.info("NWF::Environment dimension: width= "+newwidth+" height= "+newheight);
        q.resize(newwidth, newheight);
        q.scaleTextureCoordinates(0, new Vector2f(1 / lastx, 1 / lasty));
        lastx = newwidth / 80;
        lasty = newheight / 60;
        q.scaleTextureCoordinates(0, new Vector2f(lastx, lasty));
        
    }

    private void createSwingStuff() {
        mainPanel = new JPanel();
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();

        heightText = new JTextField("");
        widthText = new JTextField("");

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");


        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    if (q != null) {
                        try {
                            wf.ep.closePanels();
                            //cleanup entities pools:
                            for (Creature c : wf.ep.e.getCpool()) {
                                wf.ep.e.delClist.add(c);
                            }

                            for (Thing t : wf.ep.e.getOpool()) {
                                wf.ep.e.delTlist.add(t);
                            }
                            for (Node n : wf.ep.e.rmiPool) {
                                wf.ep.e.delRMIlist.add(n);
                            }

                            //backup environemnt data:
                            restoreEnvironmentSettings();

                        } catch (Exception ev) {
                            log.severe("Error: NewWorldFrame ");
                            ev.printStackTrace();
                        }
                    }
                    setVisible(false);
                } catch (NumberFormatException nfex) {
                    JOptionPane.showMessageDialog(NewWorldFrame.this, "Entre com valores validos", "ERRO", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        panel1.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Height"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                panel1.getBorder()));
        panel1.setLayout(new GridLayout(1, 1));
        heightText.setEditable(true);
        panel1.add(heightText);

        panel2.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Width"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                panel2.getBorder()));
        panel2.setLayout(new GridLayout(1, 1));
        widthText.setEditable(true);
        panel2.add(widthText);

        panel3.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                panel3.getBorder()));
        panel3.setLayout(new GridLayout(1, 2));
        panel3.add(okButton);
        panel3.add(cancelButton);

        mainPanel.setLayout(new GridLayout(3, 1));
        mainPanel.add(panel1);
        mainPanel.add(panel2);
        mainPanel.add(panel3);
        add(mainPanel);

        pack();
        setVisible(false);
        setResizable(true);
    }
}
