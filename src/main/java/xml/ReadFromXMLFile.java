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
package xml;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import model.Creature;
import model.Environment;
import model.Thing;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import util.Constants;
import worldserver3d.ThingCreator;

/**
 *
 * @author ecalhau
 */
public class ReadFromXMLFile {

    private String pathToFile;

    public ReadFromXMLFile(String fileName) {
        pathToFile = fileName;
    }


    public int[] readDimFromFile() {

        int width = 0, height = 0;
        try {
            File xmlFile = new File(pathToFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList listAttrEnv = doc.getElementsByTagName("environment");
            NodeList listAttr = null;
            for (int thIdx = 0; thIdx < listAttrEnv.getLength(); thIdx++) { //each environment
                Element e = (Element) listAttrEnv.item(thIdx);
                listAttr = e.getChildNodes(); //width and height

            }
            //each attribute of environment: only width and height currently
            for (int attrIdx = 0; attrIdx < listAttr.getLength(); attrIdx++) {
                Element ea = (Element) listAttr.item(attrIdx);

                if ((ea.getTagName()).equals("width")) {
                    width = new Integer(ea.getAttributeNode("width").getValue()).intValue();
                } else if ((ea.getTagName()).equals("height")) {
                    height = new Integer(ea.getAttributeNode("height").getValue()).intValue();
                }
            }


        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadFromXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //returnEnvironment dimension settings:
        return new int[]{width, height};
    }

    public void readFromFile(Environment ev) {
        try {
            File xmlFile = new File(pathToFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList listThings = doc.getElementsByTagName("thing");

            for (int thIdx = 0; thIdx < listThings.getLength(); thIdx++) { //each thing

                Element e = (Element) listThings.item(thIdx);

                xmlToThing(e, ev);
            }


        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ReadFromXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Thing xmlToThing(Element xmlThing, Environment ev) {

        int category = -1;
        int subcategory = -1;
        String color = null;
        double x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        boolean wasHidden = false;
        double pitch = 0;
        int motorSystem = 2;

        NodeList listAttr = xmlThing.getChildNodes();


        for (int attrIdx = 0; attrIdx < listAttr.getLength(); attrIdx++) { //each attribute of thing
            Element ea = (Element) listAttr.item(attrIdx);

            //String attrValue = ea.getAttributeNode(ea.getTagName()).getValue().toString();

            if ((ea.getTagName()).equals("category")) {
                category = new Integer(ea.getAttributeNode("category").getValue()).intValue();
            } else if ((ea.getTagName()).equals("subcategory")) {
                subcategory = new Integer(ea.getAttributeNode("subcategory").getValue()).intValue();
            } else if ((ea.getTagName()).equals("color")) {
                color = ea.getAttributeNode("color").getValue();
            } else if ((ea.getTagName()).equals("x1")) {
                x1 = new Double(ea.getAttributeNode("x1").getValue()).doubleValue();
            } else if ((ea.getTagName()).equals("y1")) {
                y1 = new Double(ea.getAttributeNode("y1").getValue()).doubleValue();
            } else if ((ea.getTagName()).equals("x2")) {
                x2 = new Double(ea.getAttributeNode("x2").getValue()).doubleValue();
            } else if ((ea.getTagName()).equals("y2")) {
                y2 = new Double(ea.getAttributeNode("y2").getValue()).doubleValue();
            } else if ((ea.getTagName()).equals("wasHidden")) {
                if (ea.getAttributeNode("wasHidden").getValue().equals("true")) {
                    wasHidden = true;
                }

            } else if ((ea.getTagName()).equals("pitch")) {
                pitch = new Double(ea.getAttributeNode("pitch").getValue()).doubleValue();
            } else if ((ea.getTagName()).equals("motorSystem")) {
                motorSystem = new Integer(ea.getAttributeNode("motorSystem").getValue()).intValue();
            }

        }


        Thing th = null;
        ThingCreator tc = new ThingCreator(ev);

        if (category == Constants.categoryCREATURE) {
            if (color.equals(Constants.colorRED)) {
                th = tc.createCreature(true, x1, y1, pitch);
            } else {
                th = tc.createCreature(false, x1, y1, pitch);
            }

        } else {
            th = tc.reCreateThing(category, subcategory, color, x1, y1, x2, y2, wasHidden);
        }

        return th;

    }
}
