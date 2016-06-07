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
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import javax.swing.*;
import model.Environment;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observer;
import java.util.TreeSet;
import model.Creature;
import model.Leaflet;
import util.Constants;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eccastro
 */
public class KnapsackAndScoreFrame extends JFrame implements Observer {

    JPanel mainmainPanel;
    JPanel mainPanel;
    JPanel panel1;
    JPanel panel1_1;
    JPanel panel1_2;
    
    JPanel panel2;
    JPanel panel3;
    JPanel panel_2_3;
    JPanel panelButtons;
    JPanel panelEnergy;
    JPanel panelMood;
    JButton closeButton;
    JButton refillButton;
    JButton cheerUPButton;
    JButton stopButton;
    List<JLabel> labelPool;
    List<JTextField> textPool;
    List<JTextField> textPoolLeaflet;
    JLabel scoreLabel;
    JLabel serotoninLabel;
    JLabel endorphineLabel;
    JTextField scoreTF;
    JLabel pFoodLabel;
    JLabel npFoodLabel;
    JTextField pFoodTF;
    JTextField npFoodTF;
    private Environment e;
    public Creature creature;
    JTextField greenTxt;
    JTextField redTxt;
    JTextField blueTxt;
    JTextField yellowTxt;
    JTextField whiteTxt;
    JTextField magentaTxt;
    JPanel leafletTypesPanel = new JPanel();
    HashMap<Long, List<JTextField>> leafletsTextFields = new HashMap<Long, List<JTextField>>();
    JTextArea ta;
    JProgressBar energyBar = new JProgressBar(0, (int) Constants.CREATURE_MAX_FUEL);
    JProgressBar serotoninBar = new JProgressBar(0, (int) Constants.CREATURE_MAX_SEROTONIN);
    JProgressBar endorphineBar = new JProgressBar(0, (int) Constants.CREATURE_MAX_ENDORPHINE);
    static Logger log = Logger.getLogger(KnapsackAndScoreFrame.class.getCanonicalName());
    

    public KnapsackAndScoreFrame() throws InvocationTargetException {
        this.creature = null;
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    createSwingStuff();
                }
            });
        } catch (InterruptedException ie) {
            return;
        } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite);
        }
//        SwingUtilities.invokeLater(new Runnable() {
//
//            public void run() {
//                createSwingStuff();
//            }
//        });
//
    }

    public void update() {
        if (this.creature != null) {
            refreshKnapsackScoreData();
            if(this.creature.ifHasAnyLeaflet()) refreshLeafletSituation();
            reBuildLeafletPanel();
        }
    }

    private JPanel createJewelsKnapsack(String[] array) {

        JPanel typesPanel = new JPanel();
        typesPanel.setLayout(new GridLayout(8, 2)); //era 6
        labelPool = new ArrayList<JLabel>();
        textPool = new ArrayList<JTextField>();

        JLabel color0Label = new JLabel(array[0]);
        JLabel color1Label = new JLabel(array[1]);
        JLabel color2Label = new JLabel(array[2]);
        JLabel color3Label = new JLabel(array[3]);
        JLabel color4Label = new JLabel(array[4]);
        JLabel color5Label = new JLabel(array[5]);
        
        JLabel completedLabel = new JLabel("Completed:");
        JLabel valueLabel = new JLabel("Leaflet value:");
        
        
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

        typesPanel.add(color0Label);
        typesPanel.add(redTxt);
        typesPanel.add(color1Label);
        typesPanel.add(greenTxt);
        typesPanel.add(color2Label);
        typesPanel.add(blueTxt);
        typesPanel.add(color3Label);
        typesPanel.add(yellowTxt);
        typesPanel.add(color4Label);
        typesPanel.add(magentaTxt);
        typesPanel.add(color5Label);
        typesPanel.add(whiteTxt);
        typesPanel.add(new JLabel(""));
        typesPanel.add(completedLabel);
        typesPanel.add(new JLabel(""));
        typesPanel.add(valueLabel);
        return typesPanel;

    }
              
    private void updateLeafletFields() {

        TreeSet tree = new TreeSet();
        for (Leaflet l : e.getLeafletsOfOwner(creature.getID())) {
            tree.add(l.getID());
        }
        resetLeaflet();
        for (int i = 0; i <= Constants.MAX_NUMBER_OF_COLORS - 1; i++) {
           for (int j = 0; j <= Constants.MAX_NUMBER_OF_LEAFLETS - 1; j++) {
                Object[] array = tree.toArray();
                Leaflet l = (Leaflet) e.getLeafletPool().get((Long) array[j]);

                String str = l.getNumberOfJewels(Constants.getColorItem(i)).toString();
                ((JTextField) leafletTypesPanel.getComponent(i * 3 + j)).setText(str);
                ((JTextField) leafletTypesPanel.getComponent(7 * 3 + j )).setText(""+l.getPayment());//last line: payment
            }
        }
        repaint();
        
    }

    private void addLeafletTextFields(Long leafletID) {
        List<JTextField> leafletTextFieldList = new ArrayList();
        JTextField greenTxtLeaflet = new JTextField("0", 3);
        JTextField redTxtLeaflet = new JTextField("0", 3);
        JTextField blueTxtLeaflet = new JTextField("0", 3);
        JTextField yellowTxtLeaflet = new JTextField("0", 3);
        JTextField whiteTxtLeaflet = new JTextField("0", 3);
        JTextField magentaTxtLeaflet = new JTextField("0", 3);
        
        JTextField situationTxtLeaflet = new JTextField("NO", 3);
        JTextField leafletValue = new JTextField("0", 3);

        greenTxtLeaflet.setEditable(false);
        redTxtLeaflet.setEditable(false);
        blueTxtLeaflet.setEditable(false);
        yellowTxtLeaflet.setEditable(false);
        whiteTxtLeaflet.setEditable(false);
        magentaTxtLeaflet.setEditable(false);
        situationTxtLeaflet.setEditable(false);
        leafletValue.setEditable(false);

        leafletTextFieldList.add(redTxtLeaflet);
        leafletTextFieldList.add(greenTxtLeaflet);
        leafletTextFieldList.add(blueTxtLeaflet);
        leafletTextFieldList.add(yellowTxtLeaflet);
        leafletTextFieldList.add(whiteTxtLeaflet);
        leafletTextFieldList.add(magentaTxtLeaflet);
        leafletTextFieldList.add(situationTxtLeaflet);
        leafletTextFieldList.add(leafletValue);

        leafletsTextFields.put(leafletID, leafletTextFieldList);
    }
    
     
    
    

    private void initBuildLeafletsTextFields() {

        
        for (int i = 0; i <= Constants.MAX_NUMBER_OF_LEAFLETS-1; i++) {

            addLeafletTextFields(new Long(i));

        }

    }
    
     

    private void loadLeafletsTextFields(JPanel typesPanel) {
        typesPanel.removeAll();
        //aqui for (int i = 0; i <= Constants.MAX_NUMBER_OF_COLORS - 1; i++) {
        for (int i = 0; i <= Constants.MAX_NUMBER_OF_COLORS +1; i++) {//2 more lines   
            for (Iterator<Long> iter = leafletsTextFields.keySet().iterator(); iter.hasNext();) {
                Long ID = iter.next();
                typesPanel.add(leafletsTextFields.get(ID).get(i));
            }
        }

    }
        
    

    private JPanel createLeafletJewels() {

        leafletTypesPanel.setLayout(new GridLayout(8, Constants.MAX_NUMBER_OF_LEAFLETS));//era 7
        
        initBuildLeafletsTextFields();
        loadLeafletsTextFields(leafletTypesPanel);

        return leafletTypesPanel;

    }
    
    

    public void setCreature(Creature c) {
        if (this.creature != null) {
            this.creature.sack.deleteObserver(this);
        }
        this.creature = c;
        ta.setText("                       " + this.creature.getMyName());
        this.creature.sack.addObserver(this);
        this.creature.fuelNotifier.addAnObserver(this);
        this.creature.serotoninNotifier.addAnObserver(this);
        this.creature.endorphineNotifier.addAnObserver(this);
        this.creature.leafletNotifier.addAnObserver(this);
        refreshEnergyData();
        refreshSerotoninData();
        refreshEndorphineData();
        

    }

    public void setEnvironment(Environment env) {
        e = env;
        e.leafletNotifier.addAnObserver(this);
    }

    public void refreshKnapsackScoreData() {

        scoreTF.setText(new Integer(this.creature.sack.score).toString());
        greenTxt.setText((this.creature.sack.getNumberOfJewels(Constants.colorGREEN)).toString());
        redTxt.setText((this.creature.sack.getNumberOfJewels(Constants.colorRED)).toString());
        blueTxt.setText((this.creature.sack.getNumberOfJewels(Constants.colorBLUE)).toString());
        yellowTxt.setText((this.creature.sack.getNumberOfJewels(Constants.colorYELLOW)).toString());
        magentaTxt.setText((this.creature.sack.getNumberOfJewels(Constants.colorMAGENTA)).toString());
        whiteTxt.setText((this.creature.sack.getNumberOfJewels(Constants.colorWHITE)).toString());

        pFoodTF.setText((this.creature.sack.getNumberOfFood(Constants.PFOOD)).toString());
        npFoodTF.setText((this.creature.sack.getNumberOfFood(Constants.NPFOOD)).toString());

        log.info("__________refreshKnapsackScoreData___end_____");
    }
    
    
    private void refreshLeafletSituation() {
        
        log.info("---------------refreshLeafletSituation---------------");
        
        TreeSet tree = new TreeSet();
        for (Leaflet l : e.getLeafletsOfOwner(creature.getID())) {
            tree.add(l.getID());
        }
       
            String str = "";
            for (int j = 0; j <= Constants.MAX_NUMBER_OF_LEAFLETS - 1; j++) {
                Object[] array = tree.toArray();
                Leaflet l = (Leaflet) e.getLeafletPool().get((Long) array[j]);
                
                if (l.isIfCompleted()) {
                    str = " YES ";

                } else {
                     str = "  NO ";
                }
                                            
               ((JTextField) leafletTypesPanel.getComponent(6 * 3 + j)).setText(str);

            }
       
        repaint();
    }
    
     public void refreshScore() {

        scoreTF.setText(new Integer(this.creature.sack.score).toString());
        log.info("__________refreshScore_____");
    }
    
    

    public void refreshEnergyData() {
        log.info("---------------refreshEnergyData---------------");

        if (this.creature.getFuel() >= (0.5 * Constants.CREATURE_MAX_FUEL)) {
            energyBar.setForeground(Color.GREEN);
        } else if ((this.creature.getFuel() < (0.5 * Constants.CREATURE_MAX_FUEL)) && (this.creature.getFuel() >= (0.25 * Constants.CREATURE_MAX_FUEL))) {
            energyBar.setForeground(Color.YELLOW);
        } else {
            energyBar.setForeground(Color.RED);
        }
        energyBar.setValue(this.creature.getFuel());
    }

    public void refreshSerotoninData() {
        log.info("---------------refreshSerotoninData---------------");

        if (this.creature.getSerotonin() >= (0.5 * Constants.CREATURE_MAX_SEROTONIN)) {
            serotoninBar.setForeground(Color.GREEN);
        } else if ((this.creature.getSerotonin() < (0.5 * Constants.CREATURE_MAX_SEROTONIN)) && (this.creature.getSerotonin() >= (0.25 * Constants.CREATURE_MAX_SEROTONIN))) {
            serotoninBar.setForeground(Color.YELLOW);
        } else {
            serotoninBar.setForeground(Color.RED);
        }
        serotoninBar.setValue(this.creature.getSerotonin());
    }

        public void refreshEndorphineData() {
        log.info("---------------refreshEndorphineData---------------");

        if (this.creature.getEndorphine() >= (0.5 * Constants.CREATURE_MAX_ENDORPHINE)) {
            endorphineBar.setForeground(Color.GREEN);
        } else if ((this.creature.getEndorphine() < (0.5 * Constants.CREATURE_MAX_ENDORPHINE)) && (this.creature.getEndorphine() >= (0.25 * Constants.CREATURE_MAX_ENDORPHINE))) {
            endorphineBar.setForeground(Color.YELLOW);
        } else {
            endorphineBar.setForeground(Color.RED);
        }
        endorphineBar.setValue(this.creature.getEndorphine());
    }
    
    private void reBuildLeafletPanel() {

        if ((e != null) && (!e.getLeafletPool().isEmpty())) {
            updateLeafletFields();

        }
    }

    private void updateLeafletActivity(Long leafletID) {

        TreeSet tree = new TreeSet();
        for (Leaflet l : e.getLeafletsOfOwner(creature.getID())) {
            tree.add(l.getID());
        }
       // resetLeaflet(); não esta funcionando. Está atualizando os campos errados  
        for (int i = 0; i <= Constants.MAX_NUMBER_OF_COLORS - 1; i++) {
       //aqui for (int i = 0; i <= Constants.MAX_NUMBER_OF_COLORS; i++) {
            for (int j = 0; j <= Constants.MAX_NUMBER_OF_LEAFLETS - 1; j++) {
                Object[] array = tree.toArray();
                Leaflet l = (Leaflet) e.getLeafletPool().get((Long) array[j]);
                if (l.getActivity() == 1) {
                    ((JTextField) leafletTypesPanel.getComponent(i * 3 + j)).setEnabled(true);
                } else {
                    ((JTextField) leafletTypesPanel.getComponent(i * 3 + j)).setEnabled(false);
                }
            }
        }
        repaint();

    }

    private void resetLeaflet() {

        for (int i = 0; i <= Constants.MAX_NUMBER_OF_LEAFLETS * Constants.LEAFLET_ITEMS_NUMBER; i++) {
            ((JTextField) leafletTypesPanel.getComponent(i)).setText((new Integer(0)).toString());
        }
        log.info("Leaflet was reset.");

    }

    private void createSwingStuff() {
        mainmainPanel = new JPanel();
        mainPanel = new JPanel();
        panel1 = new JPanel();
        panel1_1 = new JPanel();
        panel1_2 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        panel_2_3 = new JPanel();
        panelButtons = new JPanel();
        panelEnergy = new JPanel();
        panelMood = new JPanel();
        ta = new JTextArea("");
        ta.setEditable(false);
        
        closeButton = new JButton("Close");
        stopButton = new JButton("Start/Stop");
        refillButton = new JButton("Refill");
        cheerUPButton = new JButton("Cheer Up");     
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        stopButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if (creature.hasStarted) {
                    creature.hasStarted = false;
                    creature.setFuel(0);
                    stopButton.setText("Start");

                } else {
                    creature.hasStarted = true;
                    creature.setFuel(Constants.CREATURE_MAX_FUEL / 2);
                    stopButton.setText("Stop");
                }

            }
        });
        refillButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                log.info("*** Refill ***");
                refill();
                repaint();

            }
        });

        cheerUPButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ev) {
                log.info("*** Cheer Up ***");
                forceIncreaseSerotonin();
                forceIncreaseEndorphine(); //here???@@@
                repaint();

            }
        });

        panel1_1.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Knapsack:"),
                                BorderFactory.createEmptyBorder(3, 3, 3, 3)),
                        panel1.getBorder()));
        panel1_2.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Leaflets:"),
                                    BorderFactory.createEmptyBorder(3, 3, 3, 6)),
                        panel1.getBorder()));
        
       

        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gm1 = new GridBagConstraints();
        gm1.fill = GridBagConstraints.VERTICAL;
        gm1.gridx = 0;
        gm1.gridy = 0;

        panel1_1.add(createJewelsKnapsack(Constants.arrayOfColors));
        panel1_2.add(createLeafletJewels());
        

        panel1.add(panel1_1);

        gm1.gridx = 0;
        gm1.gridy = 1;
        panel1.add(panel1_2);
        
     

        mainmainPanel.setLayout(new BorderLayout());

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gm = new GridBagConstraints();
        gm.anchor = GridBagConstraints.FIRST_LINE_START;
        mainPanel.add(panel1, gm);

        panel_2_3.setLayout(new GridLayout(2, 1));

        panel2.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Score:"),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                        panel2.getBorder()));
        panel2.setLayout(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.gridx = 0;
        grid.gridy = 0;
        grid.weightx = 0.5;
        scoreLabel = new JLabel("Score: ");
        serotoninLabel = new JLabel("Serotonin: ");
        endorphineLabel = new JLabel("Endorphine: ");
        panel2.add(scoreLabel, grid);
        scoreTF = new JTextField("0", 4);
        scoreTF.setEditable(false);
        grid.gridx = 1;
        panel2.add(scoreTF, grid);

        panel_2_3.add(panel2);

        panel3.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Food:"),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)),
                        panel3.getBorder()));
        panel3.setLayout(new GridBagLayout());
        grid.fill = GridBagConstraints.VERTICAL;
        grid.gridx = 0;
        grid.gridy = 0;
        grid.weightx = 0.5;
        pFoodLabel = new JLabel("PFood: ");
        panel3.add(pFoodLabel, grid);
        grid.gridy = 1;
        npFoodLabel = new JLabel("NPFood: ");
        panel3.add(npFoodLabel, grid);
        grid.gridx = 1;
        grid.gridy = 0;
        pFoodTF = new JTextField("0", 4);
        pFoodTF.setEditable(false);
        panel3.add(pFoodTF, grid);

        grid.gridx = 1;
        grid.gridy = 1;
        npFoodTF = new JTextField("0", 4);
        npFoodTF.setEditable(false);
        panel3.add(npFoodTF, grid);

        panel_2_3.add(panel3);

        gm.anchor = GridBagConstraints.FIRST_LINE_END;
        mainPanel.add(panel_2_3, gm);

//
        GridLayout gd = new GridLayout(1, 3);
        gd.setHgap(20);
        panelButtons.setLayout(gd);

        panelButtons.add(refillButton);
        refillButton.setVisible(true);
        panelButtons.add(cheerUPButton);
        cheerUPButton.setVisible(true);
        panelButtons.add(stopButton);
        stopButton.setVisible(true);
        panelButtons.add(closeButton);

        panelEnergy.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Energy:"),
                                BorderFactory.createEmptyBorder(2, 2, 2, 2)),
                        panelEnergy.getBorder()));
        panelEnergy.setLayout(new GridLayout(1, 1));
        energyBar.setStringPainted(true);
        panelEnergy.add(energyBar);

        gm.fill = GridBagConstraints.BOTH;

        gm.gridy = 1;
        gm.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(panelEnergy, gm);

        panelMood.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Hormones:"),
                                BorderFactory.createEmptyBorder(2, 2, 2, 2)),
                        panelMood.getBorder()));
        panelMood.setLayout(new GridLayout(2, 2));
        serotoninBar.setStringPainted(true);
        panelMood.add(serotoninLabel);
        panelMood.add(serotoninBar);
        
        endorphineBar.setStringPainted(true);
        panelMood.add(endorphineLabel);
        panelMood.add(endorphineBar);

        gm.fill = GridBagConstraints.BOTH;

        gm.gridy = 2;
        gm.gridwidth = GridBagConstraints.REMAINDER;
        mainPanel.add(panelMood, gm);

        gm.gridy = 3;
        mainPanel.add(panelButtons, gm);

        mainmainPanel.add(ta, BorderLayout.PAGE_START);
        mainmainPanel.add(mainPanel, BorderLayout.PAGE_END);
        add(mainmainPanel);
        pack();
        setVisible(false);
        setResizable(false);
    }

    private void refill() {
        if (this.creature != null) {
            this.creature.refill();
        }
    }

    private void forceIncreaseSerotonin() {
        if (this.creature != null) {
            this.creature.increaseSerotonin();
        }
    }

    private void forceIncreaseEndorphine() {
        if (this.creature != null) {
            this.creature.increaseEndorphine();
        }
    }

    public void update(Observable o, Object obj) {

        if (obj != null) {
            if ((Long) obj == 0) {
                refreshKnapsackScoreData();
                refreshLeafletSituation();
                
            } else if ((Long) obj == 1) {
                reBuildLeafletPanel();
            } else {
                updateLeafletActivity((Long) obj);
                refreshKnapsackScoreData();
            }

        } else {
            refreshEnergyData();
            refreshSerotoninData();
            refreshEndorphineData();
            refreshLeafletSituation();
           
                     
        }

    }
}
