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

package worldserver3d;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.scene.Node;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.MaxToJme;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
/**
 *
 * @author gudwin
 */
public class CreatureNodeFactory {
    
    static Logger log = Logger.getLogger(CreatureNodeFactory.class.getCanonicalName());
    
    ByteArrayOutputStream BO;
    TextureState ts;
    public CreatureNodeFactory() {
         MaxToJme C1 = new MaxToJme();
        BO = new ByteArrayOutputStream();
         URL maxFile = CreatureNodeFactory.class.getClassLoader().getResource("images/robo.3ds");
        try {
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
         } catch (IOException exc) {
            log.severe("Erro em CreatureNodeFactory constructor");
         }
    }

    public void setTexture(TextureState txState){
         this.ts = txState;
         this.ts.setTexture(TextureManager.loadTexture(CreatureNodeFactory.class.getClassLoader().getResource(
                "images/red.jpg"),
                 //"images/reddish_copper.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear));
         this.ts.setEnabled(true);

    }
    private Node getCreatureNode() {
        Node modelw=null;
        try {ByteArrayInputStream CreatureModelInputStream = new ByteArrayInputStream(BO.toByteArray());             
             modelw = (Node)BinaryImporter.getInstance().load(CreatureModelInputStream);
               } catch (IOException exc) {
                     log.severe("Erro em CreatureNodeFactory::getCreatureNode()");
               }  
        modelw.setLocalScale(.02f);
        Quaternion quat90 = new Quaternion();
        quat90.fromAngles(270 * 3.141592f/180, 3.141592f, 0f);
        modelw.setLocalRotation(quat90);
        modelw.setLocalTranslation(-7f,2.0f,0);
        modelw.setRenderState(ts);
        Node model = null;
        model = new Node("Model0");
        model.attachChild(modelw);
        model.setModelBound(new BoundingBox());
        model.updateModelBound();
        return(model);
    }

    //Change the default texture:
    private Node getCreatureNode(String pathToImageTexture, TextureState txState) {
        Node modelw=null;
        try {ByteArrayInputStream CreatureModelInputStream = new ByteArrayInputStream(BO.toByteArray());
             modelw = (Node)BinaryImporter.getInstance().load(CreatureModelInputStream);
               } catch (IOException exc) {
                     log.severe("Erro em CreatureNodeFactory::getCreatureNode()!");
               }
        modelw.setLocalScale(.02f);
        Quaternion quat90 = new Quaternion();
        quat90.fromAngles(270 * 3.141592f/180, 3.141592f, 0f);
        modelw.setLocalRotation(quat90);
        modelw.setLocalTranslation(-7f,2.0f,0);
        setTexture(txState);
        ts.setTexture(TextureManager.loadTexture(CreatureNodeFactory.class.getClassLoader().getResource(
                pathToImageTexture),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear));
         ts.setEnabled(true);
        modelw.setRenderState(ts);
        Node model = null;
        model = new Node("Model0");
        model.attachChild(modelw);
        model.setModelBound(new BoundingBox());
        model.updateModelBound();
        return(model);

    }
}
