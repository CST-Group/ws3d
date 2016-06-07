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
 * @author eccastro
 */
public class OldShapeFactory {
    
    static Logger log = Logger.getLogger(OldShapeFactory.class.getCanonicalName());

    public ByteArrayOutputStream BO;
    public TextureState ts;
    public OldShapeFactory(String pathToModel) {
         MaxToJme C1 = new MaxToJme();
         BO = new ByteArrayOutputStream();
         URL maxFile = OldShapeFactory.class.getClassLoader().getResource(pathToModel);
        try {
            C1.convert(new BufferedInputStream(maxFile.openStream()),BO);
         } catch (IOException exc) {
            log.severe("Erro em OldShapeFactory constructor !");
         }
    }

    public void setTexture(String pathToImageTexture){
         this.ts.setTexture(TextureManager.loadTexture(OldShapeFactory.class.getClassLoader().getResource(
                pathToImageTexture),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear));
         this.ts.setEnabled(true);

    }

    private Node getNode(float scale, float verticalTranslation) {
       Node modelw=null;
        try {ByteArrayInputStream ModelInputStream = new ByteArrayInputStream(BO.toByteArray());
             modelw = (Node)BinaryImporter.getInstance().load(ModelInputStream);
               } catch (IOException exc) {
                     log.severe("Erro em OldShapeFactory::getNode() !");
               }
        //modelw.setLocalScale(.05f);
        if (scale > 0 ) modelw.setLocalScale(scale);
        Quaternion quat90 = new Quaternion();
        quat90.fromAngles(270 * 3.141592f/180, 3.141592f, 0f);
        modelw.setLocalRotation(quat90);
        modelw.setLocalTranslation(0,verticalTranslation,0);
        modelw.setRenderState(ts);
        Node model = null;
        model = new Node("Modelx");
        model.attachChild(modelw);
        model.setModelBound(new BoundingBox());
        model.updateModelBound();
        return(model);
    }

    private Node getNode(String pathToImageTexture, TextureState txState) {
        Node modelw=null;
        try {ByteArrayInputStream ModelInputStream = new ByteArrayInputStream(BO.toByteArray());
             modelw = (Node)BinaryImporter.getInstance().load(ModelInputStream);
               } catch (IOException exc) {
                     log.severe("Erro em OldShapeFactory::getNode() !");
               }
        //modelw.setLocalScale(.02f);
        Quaternion quat90 = new Quaternion();
        quat90.fromAngles(270 * 3.141592f/180, 3.141592f, 0f);
        modelw.setLocalRotation(quat90);
        //modelw.setLocalTranslation(-7f,2.0f,0);
        this.ts = txState;
        setTexture(pathToImageTexture);
        modelw.setRenderState(ts);
        Node model = null;
        model = new Node("Model0");
        model.attachChild(modelw);
        model.setModelBound(new BoundingBox());
        model.updateModelBound();
        return(model);

    }
//    public Node getNode(Thing th) {
//         Node modelw = new Node("New shape");
//         Geometry geo= new TriMesh("TriMesh of Thing");
//        try {ByteArrayInputStream ModelInputStream = new ByteArrayInputStream(BO.toByteArray());
//             modelw = (Node)BinaryImporter.getInstance().load(ModelInputStream);
//               } catch (IOException exc) {
//                     System.out.println("Erro !");
//               }
//        modelw = th.myLocalTransformations();
////        if (scale > 0 ) modelw.setLocalScale(scale);
////        Quaternion quat90 = new Quaternion();
////        quat90.fromAngles(270 * 3.141592f/180, 3.141592f, 0f);
////        modelw.setLocalRotation(quat90);
////        modelw.setLocalTranslation(0,verticalTranslation,0);
//
//        modelw.setRenderState(ts);
//        modelw.attachChild(geo);
//        Node model = null;
//        model = new Node("Modelx");
//        model.attachChild(modelw);
//        model.setModelBound(new BoundingBox());
//        model.updateModelBound();
//        return(model);
//     }
}

