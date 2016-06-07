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
import model.Thing;
import model.Food;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import model.Environment;
import util.Constants;
import worldserver3d.ThingCreator;

/**
 *
 * @author eccastro
 */
public class EditFoodFrame extends JFrame {

    Thing food;
    Thing jewel;
    Thing DS;
    JPanel mainPanel;
    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JButton closeButton;
    JButton deleteButton;
    JButton newCreatureButton;
    JButton newJewelButton;
    private Environment e;
    private JCheckBoxMenuItem hiddenObstacle;
//JComboBox combo;
    JPanel radioPanel;
    List<JRadioButton> typeOfFood;
    public double x, y;
    private DefaultButtonModel model;
    private ButtonGroup group;
    JTextArea ta;
    Logger log;

    public EditFoodFrame(final Environment e) {
        log = Logger.getLogger(EditFoodFrame.class.getCanonicalName());
        this.food = null;
        this.e = e;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createSwingStuff();
            }
        });
    }

    public void setFood(Thing fd) {
        this.food = fd;
        ta.setText("                                     "+this.food.getMyName());
        if (((Food) this.food).perishable) {
            typeOfFood.get(1).setSelected(true);
        } else {
            typeOfFood.get(0).setSelected(true);
        }

        hiddenObstacle.setEnabled(true);
        if (food.returnIfWasHidden()) {
            hiddenObstacle.setSelected(true);

        } else {
            hiddenObstacle.setSelected(false);

        }
        setVisible(false);
    }
    /* Method called by Environment to paint EditBrickTab with
     * correct obstacle visibility status (hidden or not).
     */

    public void showForCreation() {

        hiddenObstacle.setSelected(false);
        hiddenObstacle.setEnabled(false);
        for (JRadioButton tof : typeOfFood) {
            tof.setEnabled(true);
        }
        newCreatureButton.setEnabled(true);
        newJewelButton.setEnabled(true);
        group.setSelected(model, true);
    }

    private JPanel createFoodTypeButtons(String[] array) {


        JPanel radioPanel = new JPanel();
        typeOfFood = new ArrayList<JRadioButton>();
        JRadioButton type0Radio = new JRadioButton(this.e.nonPerishableFood);
        JRadioButton type1Radio = new JRadioButton(this.e.perishableFood);


        typeOfFood.add(type0Radio);
        typeOfFood.add(type1Radio);

        type0Radio.setActionCommand(this.e.nonPerishableFood);
        type1Radio.setActionCommand(this.e.perishableFood);

// Register a listener for the radio buttons.
        RadioListener myListener = new RadioListener(type0Radio, type1Radio, newCreatureButton, newJewelButton);
        type0Radio.addActionListener(myListener);
        type1Radio.addActionListener(myListener);

// Group the radio buttons.
        group = new ButtonGroup();
        group.add(type0Radio);
        group.add(type1Radio);

        model = new DefaultButtonModel();
        group.setSelected(model, false);

        radioPanel.setLayout(new GridLayout(0, 1));
        radioPanel.add(type0Radio);
        radioPanel.add(type1Radio);

        return radioPanel;

    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void placeCreatureHere(double mouseXini, double mouseYini) {

        /**
         * Note that through the edit window the motor system of the robot is
         * always a differential steering approach.
         */
          ThingCreator tc = new ThingCreator(e);
          tc.createCreature(true, mouseXini, mouseYini, 0);
    }

    public void placeJewelHere(double mouseXini, double mouseYini) {

        ThingCreator tc = new ThingCreator(e);
        jewel = tc.createThing(Constants.categoryJEWEL, x, y);
    }

    public void update() {

        newCreatureButton.setEnabled(false);
        newJewelButton.setEnabled(false);

        if (food != null) {

            for (JRadioButton tof : typeOfFood) {
                tof.setEnabled(false);
            }
            group.setSelected(model, false);


            hiddenObstacle.setEnabled(true);
            if (food.returnIfWasHidden()) {
                hiddenObstacle.setSelected(true);

            } else {
                hiddenObstacle.setSelected(false);

            }

        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    class RadioListener implements ActionListener {

        private JRadioButton NPFoodRadio;
        private JRadioButton PFoodRadio;
        private JButton newCreatureButton;
        private JButton newJewelButton;

        public RadioListener(JRadioButton NPFoodRadio, JRadioButton PFoodRadio, JButton newCreatureButton,  JButton newJewelButton) {
            this.NPFoodRadio = NPFoodRadio;
            this.PFoodRadio = PFoodRadio;
            this.newCreatureButton = newCreatureButton;
            this.newJewelButton = newJewelButton;
        }

        public void actionPerformed(ActionEvent ev) {

            ThingCreator tc = new ThingCreator(e);
            if (e.nonPerishableFood.equals(ev.getActionCommand())) {
                food = tc.createThing(Constants.categoryNPFOOD, x, y);
                PFoodRadio.setEnabled(false);
            } else { //perishable food
                food = tc.createThing(Constants.categoryPFOOD, x, y);
                NPFoodRadio.setEnabled(false);
            }
            newCreatureButton.setEnabled(false);
            newJewelButton.setEnabled(false);
            setFood(food);
        }
    }

    private void createSwingStuff() {
        mainPanel = new JPanel();
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        closeButton = new JButton("Close");
        deleteButton = new JButton("Delete me!");
        ta = new JTextArea("");
        ta.setEditable(false);
        newCreatureButton = new JButton("New Creature");

        newCreatureButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                log.info("*** Create creature! ***");
                placeCreatureHere(x, y);
                setVisible(false);
            }
        });
        newJewelButton = new JButton("New Jewel");

        newJewelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                log.info("*** Create Jewel! ***");
                placeJewelHere(x, y);
                setVisible(false);
            }
        });
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        deleteButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                log.info("*** Food deleted! ***");
                food.removeRememberMeIcon(e);
                e.removeThing(food);
                setVisible(false);
            }
        });
        panel1.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Food type"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                panel1.getBorder()));
        panel1.setLayout(new GridLayout(1, 1));

        String[] foodTypes = {this.e.nonPerishableFood, this.e.perishableFood};

        panel1.add(createFoodTypeButtons(foodTypes));

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
                    //System.out.println("======= Event command name : "+e.getActionCommand()+" and params: "+e.paramString() );
                    if (hiddenObstacle.isSelected()) {
                        food.hideMe(e);
                        hiddenObstacle.setSelected(true);

                    } else {
                        food.undoHideMe(e);
                        hiddenObstacle.setSelected(false);
                    }
                    //theMainInstance.sf.gameState.ThingsRN.updateRenderState();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(EditFoodFrame.this, "Error in hidden obstacle execution.", "ERRO", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel2.add(hiddenObstacle);
        mainPanel.add(panel2, BorderLayout.LINE_END);

        panel3.setLayout(new GridLayout(1, 4));
        panel3.add(newCreatureButton);
        panel3.add(newJewelButton);
        panel3.add(deleteButton);
        panel3.add(closeButton);
        mainPanel.add(panel3, BorderLayout.PAGE_END);

        add(mainPanel);
        pack();
        setVisible(false);
        setResizable(false);

    }
}
