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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import model.*;
import model.Container;
import util.Constants;

/**
 *
 * @author eccastro
 */
public class ContainerViewer extends JFrame implements Observer {

    JPanel mainPanel;
    JPanel panelDrawers;
    JPanel panelButtons;
    JButton closeButton;
    JButton deleteButton;
    List<JRadioButton> typeOfAction;
    private ButtonGroup group;
    private DefaultButtonModel model;
    BasicArrowButton incButton0;
    BasicArrowButton decButton0;
    BasicArrowButton incButton1;
    BasicArrowButton decButton1;
    BasicArrowButton incButton2;
    BasicArrowButton decButton2;
    BasicArrowButton incButton3;
    BasicArrowButton decButton3;
    BasicArrowButton incButton4;
    BasicArrowButton decButton4;
    BasicArrowButton incButton5;
    BasicArrowButton decButton5;
    BasicArrowButton incButtonPF;
    BasicArrowButton decButtonPF;
    BasicArrowButton incButtonNPF;
    BasicArrowButton decButtonNPF;
    JCheckBoxMenuItem editModeCheckBox;
    private Environment e;
    public Container container;
    JPanel mainmainPanel;
    JPanel panelJewel;
    JPanel panelFood;
    JPanel panelAction;
    JPanel panelFoodAndAction;
    List<JLabel> labelPool;
    List<JTextField> textPool;
    JLabel pFoodLabel;
    JLabel npFoodLabel;
    JTextField pFoodTF;
    JTextField npFoodTF;
    JTextField greenTxt;
    JTextField redTxt;
    JTextField blueTxt;
    JTextField yellowTxt;
    JTextField whiteTxt;
    JTextField magentaTxt;
    static Logger log = Logger.getLogger(ContainerViewer.class.getCanonicalName());

    public ContainerViewer(Environment env) {
        this.container = null;
        this.e = env;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createSwingStuff();
            }
        });

    }

    private void placeInGridBag(int x, int y, Component item, GridBagConstraints grid, JPanel p) {
        grid.gridx = x;
        grid.gridy = y;
        p.add(item, grid);
    }

    private void createSwingStuff() {
        mainmainPanel = new JPanel();
        mainPanel = new JPanel();
        panelJewel = new JPanel();
        panelFood = new JPanel();
        panelButtons = new JPanel();
        panelAction = new JPanel();
        panelFoodAndAction = new JPanel();

        closeButton = new JButton("Close");
        deleteButton = new JButton("Delete me!");
        deleteButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                log.info("*** Cage deleted! ***");
                e.removeThing(container);
                setVisible(false);
            }
        });

        editModeCheckBox = new JCheckBoxMenuItem("Edit Mode");
        editModeCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                try {
                    if (editModeCheckBox.isSelected()) {
                        enableButtons(true);
                        editModeCheckBox.setSelected(true);

                    } else {
                        enableButtons(false);
                        editModeCheckBox.setSelected(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ContainerViewer.this, "Error in obstacle execution.", "ERRO", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        //Panel for jewel items:
        panelJewel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Jewel:"),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)),
                panelJewel.getBorder()));

        panelJewel.setLayout(new GridBagLayout());
        GridBagConstraints gm1 = new GridBagConstraints();
        gm1.fill = GridBagConstraints.VERTICAL;
        gm1.gridx = 0;
        gm1.gridy = 0;
        gm1.weightx = 0.5;

        panelJewel.add(createJewelsKnapsack(Constants.arrayOfColors), gm1);

        panelFoodAndAction.setLayout(new BorderLayout());
        mainmainPanel.setLayout(new BorderLayout());
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(350, 150));
        mainPanel.add(panelJewel, BorderLayout.WEST);

        //Panel for food items:
        panelFood.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Food:"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                panelFood.getBorder()));


        panelFood.setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.fill = GridBagConstraints.VERTICAL;

        grid.weightx = 0.5;
        pFoodLabel = new JLabel("PFood: ");
        placeInGridBag(0, 0, pFoodLabel, grid, panelFood);

        npFoodLabel = new JLabel("NPFood: ");
        placeInGridBag(0, 1, npFoodLabel, grid, panelFood);

        pFoodTF = new JTextField("0", 4);
        pFoodTF.setEditable(false);
        placeInGridBag(1, 0, pFoodTF, grid, panelFood);

        npFoodTF = new JTextField("0", 4);
        npFoodTF.setEditable(false);
        placeInGridBag(1, 1, npFoodTF, grid, panelFood);

        //  inc/dec buttons:
        ContainerViewer.EditButtons ebPF = new ContainerViewer.EditButtons(Constants.categoryPFOOD, Constants.getColorIndex(Constants.colorRED));
        incButtonPF = ebPF.getIncButton();
        decButtonPF = ebPF.getDecButton();
        ContainerViewer.EditButtons ebNPF = new ContainerViewer.EditButtons(Constants.categoryNPFOOD, Constants.getColorIndex(Constants.colorGREEN));
        incButtonNPF = ebNPF.getIncButton();
        decButtonNPF = ebNPF.getDecButton();
        placeInGridBag(2, 0, incButtonPF, grid, panelFood);
        placeInGridBag(3, 0, decButtonPF, grid, panelFood);
        placeInGridBag(2, 1, incButtonNPF, grid, panelFood);
        placeInGridBag(3, 1, decButtonNPF, grid, panelFood);

        panelFoodAndAction.add(panelFood, BorderLayout.NORTH);
        panelAction = createActionRadioButtonsPanel();
        //Panel for actions:
        panelAction.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Action:"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                panelAction.getBorder()));
        panelFoodAndAction.add(panelAction, BorderLayout.SOUTH);
        mainPanel.add(panelFoodAndAction, BorderLayout.EAST);

        //Panel for buttons:
        panelButtons.setLayout(new FlowLayout());
        panelButtons.add(editModeCheckBox);
        panelButtons.add(deleteButton);
        panelButtons.add(closeButton);


        mainmainPanel.add(panelButtons, BorderLayout.SOUTH);
        mainmainPanel.add(mainPanel, BorderLayout.NORTH);
        add(mainmainPanel);
        enableButtons(false);
        pack();
        setVisible(false);
        setResizable(false);
    }

    private JPanel createJewelsKnapsack(String[] array) {

        JPanel typesPanel = new JPanel();
        typesPanel.setLayout(new GridBagLayout());
        labelPool = new ArrayList<JLabel>();
        textPool = new ArrayList<JTextField>();

        JLabel color0Label = new JLabel(array[0]);
        JLabel color1Label = new JLabel(array[1]);
        JLabel color2Label = new JLabel(array[2]);
        JLabel color3Label = new JLabel(array[3]);
        JLabel color4Label = new JLabel(array[4]);
        JLabel color5Label = new JLabel(array[5]);
        labelPool.add(color0Label);
        labelPool.add(color1Label);
        labelPool.add(color2Label);
        labelPool.add(color3Label);
        labelPool.add(color4Label);
        labelPool.add(color5Label);

        greenTxt = new JTextField("0", 4);
        redTxt = new JTextField("0", 4);
        blueTxt = new JTextField("0", 4);
        yellowTxt = new JTextField("0", 4);
        whiteTxt = new JTextField("0", 4);
        magentaTxt = new JTextField("0", 4);

        greenTxt.setEditable(false);
        redTxt.setEditable(false);
        blueTxt.setEditable(false);
        yellowTxt.setEditable(false);
        whiteTxt.setEditable(false);
        magentaTxt.setEditable(false);

        textPool.add(redTxt);
        textPool.add(greenTxt);
        textPool.add(blueTxt);
        textPool.add(yellowTxt);
        textPool.add(magentaTxt);
        textPool.add(whiteTxt);

        GridBagConstraints grid = new GridBagConstraints();
        grid.fill = GridBagConstraints.VERTICAL;
        grid.weightx = 0.5;

        placeInGridBag(0, 0, color0Label, grid, typesPanel);
        placeInGridBag(1, 0, redTxt, grid, typesPanel);
        ContainerViewer.EditButtons ebRed = new ContainerViewer.EditButtons(Constants.categoryJEWEL, Constants.getColorIndex(color0Label.getText()));
        incButton0 = ebRed.getIncButton();
        decButton0 = ebRed.getDecButton();
        placeInGridBag(2, 0, incButton0, grid, typesPanel);
        placeInGridBag(3, 0, decButton0, grid, typesPanel); //end row 0
        placeInGridBag(0, 1, color1Label, grid, typesPanel);
        placeInGridBag(1, 1, greenTxt, grid, typesPanel);
        ContainerViewer.EditButtons ebGreen = new ContainerViewer.EditButtons(Constants.categoryJEWEL, Constants.getColorIndex(color1Label.getText()));
        incButton1 = ebGreen.getIncButton();
        decButton1 = ebGreen.getDecButton();
        placeInGridBag(2, 1, incButton1, grid, typesPanel);
        placeInGridBag(3, 1, decButton1, grid, typesPanel); //end row 1
        placeInGridBag(0, 2, color2Label, grid, typesPanel);
        placeInGridBag(1, 2, blueTxt, grid, typesPanel);
        ContainerViewer.EditButtons ebBlue = new ContainerViewer.EditButtons(Constants.categoryJEWEL, Constants.getColorIndex(color2Label.getText()));
        incButton2 = ebBlue.getIncButton();
        decButton2 = ebBlue.getDecButton();
        placeInGridBag(2, 2, incButton2, grid, typesPanel);
        placeInGridBag(3, 2, decButton2, grid, typesPanel); //end row 2
        placeInGridBag(0, 3, color3Label, grid, typesPanel);
        placeInGridBag(1, 3, yellowTxt, grid, typesPanel);
        ContainerViewer.EditButtons ebYellow = new ContainerViewer.EditButtons(Constants.categoryJEWEL, Constants.getColorIndex(color3Label.getText()));
        incButton3 = ebYellow.getIncButton();
        decButton3 = ebYellow.getDecButton();
        placeInGridBag(2, 3, incButton3, grid, typesPanel);
        placeInGridBag(3, 3, decButton3, grid, typesPanel); //end row 3
        placeInGridBag(0, 4, color4Label, grid, typesPanel);
        placeInGridBag(1, 4, magentaTxt, grid, typesPanel);
        ContainerViewer.EditButtons ebMagenta = new ContainerViewer.EditButtons(Constants.categoryJEWEL, Constants.getColorIndex(color4Label.getText()));
        incButton4 = ebMagenta.getIncButton();
        decButton4 = ebMagenta.getDecButton();
        placeInGridBag(2, 4, incButton4, grid, typesPanel);
        placeInGridBag(3, 4, decButton4, grid, typesPanel); //end row 4
        placeInGridBag(0, 5, color5Label, grid, typesPanel);
        placeInGridBag(1, 5, whiteTxt, grid, typesPanel);
        ContainerViewer.EditButtons ebWhite = new ContainerViewer.EditButtons(Constants.categoryJEWEL, Constants.getColorIndex(color5Label.getText()));
        incButton5 = ebWhite.getIncButton();
        decButton5 = ebWhite.getDecButton();
        placeInGridBag(2, 5, incButton5, grid, typesPanel);
        placeInGridBag(3, 5, decButton5, grid, typesPanel); //end row 5

        return typesPanel;
    }

    public void setContainer(Thing c) {

        if (this.container != null) {
            this.container.deleteObserver(this);
        }
        this.container = (Container) c;
        this.container.addObserver(this);
        if(this.container.getIfOpened()){
            typeOfAction.get(0).setSelected(true);
        } else{
            typeOfAction.get(1).setSelected(true);
        }
        
        refreshData();


    }

    private void enableButtons(boolean edit) {
        //edit is true mainly for test when "edit buttons" are visible

        incButtonPF.setVisible(edit);
        decButtonPF.setVisible(edit);
        incButtonNPF.setVisible(edit);
        decButtonNPF.setVisible(edit);
        incButton0.setVisible(edit);
        decButton0.setVisible(edit);
        incButton1.setVisible(edit);
        decButton1.setVisible(edit);
        incButton2.setVisible(edit);
        decButton2.setVisible(edit);
        incButton3.setVisible(edit);
        decButton3.setVisible(edit);
        incButton4.setVisible(edit);
        decButton4.setVisible(edit);
        incButton5.setVisible(edit);
        decButton5.setVisible(edit);

        deleteButton.setVisible(edit);
        panelAction.setVisible(edit);

    }

    private void refreshData() {

        greenTxt.setText(new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryJEWEL, Constants.getColorIndex(Constants.colorGREEN))).toString());
        redTxt.setText(new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryJEWEL, Constants.getColorIndex(Constants.colorRED))).toString());
        blueTxt.setText(new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryJEWEL, Constants.getColorIndex(Constants.colorBLUE))).toString());
        yellowTxt.setText(new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryJEWEL, Constants.getColorIndex(Constants.colorYELLOW))).toString());
        whiteTxt.setText(new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryJEWEL, Constants.getColorIndex(Constants.colorWHITE))).toString());
        magentaTxt.setText(new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryJEWEL, Constants.getColorIndex(Constants.colorMAGENTA))).toString());

        String ip= new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryPFOOD, Constants.getColorIndex(Constants.colorRED))).toString();
        String inp= new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryNPFOOD, Constants.getColorIndex(Constants.colorGREEN))).toString();
        pFoodTF.setText(new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryPFOOD, Constants.getColorIndex(Constants.colorRED))).toString());
        npFoodTF.setText(new Integer(this.container.getNumberOfElementsOfCategory(Constants.categoryNPFOOD, Constants.getColorIndex(Constants.colorGREEN))).toString());

        log.info("Food: "+ip+" "+inp);
    }

    public void update(Observable o, Object obj) {
        refreshData();
    }

    private JPanel createActionRadioButtonsPanel() {

        JPanel radioPanel = new JPanel();
        typeOfAction = new ArrayList<JRadioButton>();
        JRadioButton type0Radio = new JRadioButton("Open");
        JRadioButton type1Radio = new JRadioButton("Close");


        typeOfAction.add(type0Radio);
        typeOfAction.add(type1Radio);

        type0Radio.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                container.setOpenState(true);
            }
        });

        type1Radio.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                container.setOpenState(false);
            }
        });

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

    private class EditButtons {

        private BasicArrowButton incButton = new BasicArrowButton(BasicArrowButton.NORTH);
        private BasicArrowButton decButton = new BasicArrowButton(BasicArrowButton.SOUTH);
        private int myCategory;
        private int myType = -1;

        public EditButtons(int category, int type) {
            incButton.setPreferredSize(new Dimension(15, 15));
            decButton.setPreferredSize(new Dimension(15, 15));
            myCategory = category;
            myType = type;

            incButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    /**
                     * Simply increase the counter. No Thing is actually
                     * created or added here. It's simply for testing purposes.
                     */

                    container.inc(myCategory, myType);
                    
                    //if (myCategory == Constants.categoryJEWEL) {
                    //    ((Jewel) th).setType(Constants.translateIntoColor(myType));
                    //}
                    //container.putIn(th);

                }
            });

            decButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    /**
                     * Simply decrease the counter. No Thing is actually
                     * removed here. It's simply for testing purposes.
                     */
                    
                    container.dec(myCategory, myType);
                    
                    //container.remove(th);

                }
            });

        }

        public BasicArrowButton getIncButton() {
            return this.incButton;
        }

        public BasicArrowButton getDecButton() {
            return this.decButton;
        }
    }
}