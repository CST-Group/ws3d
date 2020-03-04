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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import model.Creature;
import model.Thing;
import org.w3c.dom.Attr;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author ecalhau
 */
public class WriteToXMLFile {

    private String pathToFile;

    public WriteToXMLFile(String fileName) {
        pathToFile = fileName;
    }

    public void writeToFile(int width, int height, List<Thing> ordinarythings, List<Creature> creatures) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            //root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("environment");
            doc.appendChild(rootElement);

            Element widthElem = doc.createElement("width");
            Attr widthAttr = doc.createAttribute("width");
            widthAttr.setValue((new Integer(width).toString()));
            widthElem.setAttributeNode(widthAttr);
            rootElement.appendChild(widthElem);

            Element heightElem = doc.createElement("height");
            Attr heightAttr = doc.createAttribute("height");
            heightAttr.setValue((new Integer(height).toString()));
            heightElem.setAttributeNode(heightAttr);
            rootElement.appendChild(heightElem);

            for(Thing th: ordinarythings){
                thingToXML(th, doc, rootElement);
            }
            for(Thing c: creatures){
                thingToXML(c, doc, rootElement);
            }

          //write the content into xml file
	  TransformerFactory transformerFactory = TransformerFactory.newInstance();
	  Transformer transformer = transformerFactory.newTransformer();
	  DOMSource source = new DOMSource(doc);
	  StreamResult result =  new StreamResult(new File(this.pathToFile));
	  transformer.transform(source, result);

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            Logger.getLogger(WriteToXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        }catch(TransformerException tfe){
	 tfe.printStackTrace();
         Logger.getLogger(WriteToXMLFile.class.getName()).log(Level.SEVERE, null, tfe);
     }
    }

    private void thingToXML(Thing th, Document doc, Element rootElement) {

        Element thing = doc.createElement("thing");
        rootElement.appendChild(thing);

        Element category = doc.createElement("category");
        Attr categoryAttr = doc.createAttribute("category");
        categoryAttr.setValue((new Integer(th.category).toString()));  
        category.setAttributeNode(categoryAttr);
        thing.appendChild(category);

        Element subcategory = doc.createElement("subcategory");
        Attr subcategoryAttr = doc.createAttribute("subcategory");
        subcategoryAttr.setValue((new Integer(th.subCategory).toString()));
        subcategory.setAttributeNode(subcategoryAttr);
        thing.appendChild(subcategory);

        Element color = doc.createElement("color");
        Attr colorAttr = doc.createAttribute("color");
        colorAttr.setValue(th.getMaterial().getColorName());
        color.setAttributeNode(colorAttr);
        thing.appendChild(color);

        Element x1 = doc.createElement("x1");
        Attr x1Attr = doc.createAttribute("x1");
        x1Attr.setValue((new Double(th.getX1()).toString()));
        x1.setAttributeNode(x1Attr);
        thing.appendChild(x1);

        Element y1 = doc.createElement("y1");
        Attr y1Attr = doc.createAttribute("y1");
        y1Attr.setValue((new Double(th.getY1()).toString()));
        y1.setAttributeNode(y1Attr);
        thing.appendChild(y1);

        Element x2 = doc.createElement("x2");
        Attr x2Attr = doc.createAttribute("x2");
        x2Attr.setValue((new Double(th.getX2()).toString()));
        x2.setAttributeNode(x2Attr);
        thing.appendChild(x2);

        Element y2 = doc.createElement("y2");
        Attr y2Attr = doc.createAttribute("y2");
        y2Attr.setValue((new Double(th.getY2()).toString()));
        y2.setAttributeNode(y2Attr);
        thing.appendChild(y2);

        Element wasHidden = doc.createElement("wasHidden");
        Attr ifHiddenAttr = doc.createAttribute("wasHidden");
        ifHiddenAttr.setValue(""+th.wasHidden);
        wasHidden.setAttributeNode(ifHiddenAttr);
        thing.appendChild(wasHidden);

        Element pitch = doc.createElement("pitch");
        Attr pitchAttr = doc.createAttribute("pitch");
        pitchAttr.setValue((new Double(th.getPitch()).toString()));
        pitch.setAttributeNode(pitchAttr);
        thing.appendChild(pitch);

        Element motorSystem = doc.createElement("motorSystem");
        Attr motorSystemAttr = doc.createAttribute("motorSystem");
        motorSystemAttr.setValue((new Integer(th.getMotorSystem()).toString()));
        motorSystem.setAttributeNode(motorSystemAttr);
        thing.appendChild(motorSystem);
    }
}
