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
package model;

/**
 *
 * @author eccastro
 */

import java.awt.Color;
import util.Constants;

public class Material3D {


    private double hardness;

    private double energy;

    private double shininess; //only jewel shines!

    private Color color = Color.blue;

    //public MaterialState materialState;
    //this variables stores the original properties (hardness and color) of
    // a hidden obstacle, since whenever it is hidden it's hardness is set to 0.0
    private double previousHardness;
    private Color previousColor;

    public Material3D(){ //Savable matters only!

    }

    public Material3D(double hardness, double energy){
        this.hardness = hardness;
        this.energy = energy;
        this.shininess = 0;
        //this.materialState = materialState;
        this.color = Color.yellow;
    }

    public Material3D(Color color, double hardness, double energy) {
        this.hardness = hardness;
        this.shininess = 0;
        this.energy = energy;

        this.color = color;
//        this.materialState = materialState;
//        this.materialState.setAmbient(ColorRGBA.black);
//        this.materialState.setDiffuse(color);
//        this.materialState.setSpecular(ColorRGBA.black);
//        this.materialState.setShininess(0.0f);
//        this.materialState.setEmissive(ColorRGBA.black);
//        this.materialState.setEnabled(true);

    }

    public Material3D(Color color, double hardness, double energy, double shininess) {
        this(color, hardness, energy);
        this.shininess = shininess;
    }
    
    public Material3D(Color color) {
        this.hardness = 1.0;
        this.energy = 0.0;
        this.shininess = 0;

        this.color = color;
        
//        this.materialState = materialState;
//        if (this.materialState != null) {
//        this.materialState.setAmbient(ColorRGBA.black);
//        this.materialState.setDiffuse(color);
//        this.materialState.setSpecular(ColorRGBA.black);
//        this.materialState.setShininess(0.0f);
//        this.materialState.setEmissive(ColorRGBA.black);
//        this.materialState.setEnabled(true);
//        }
    }
    public Material3D(double shininess, Color color) {
        this(color);
        this.shininess = shininess;
    }
    //for creature only
//    public Material3D() {
//        this.hardness = 1.0;
//        this.energy = 0.0;
//
//       
////        this.materialState = materialState;
////         if (this.materialState != null) {
////        this.materialState.setShininess(0.0f);
////        this.materialState.setEnabled(true);
////        }
//    }

    public boolean setEnergy(double value){
        boolean ret = true;
        this.energy = value;
        if (energy <= 0.0) ret = false;
        
        return ret;
    }

    public void makeItNotHard(){
        this.hardness = 0.0;
    }

    public double getEnergy()   { return energy; }
    public double getHardness() { return hardness; }
    public Color getColor(){
        return this.color;
    }

    public void setShininess(double value){
        this.shininess = value;
    }
    public double getShininess()    { return shininess; }

    public void setHiddenObstacleMaterial() {
        previousHardness = hardness;
        previousColor = color;
        hardness = 0.0;
        color = Color.gray;
    }

    public void undoHiddenObstacleMaterial() {
        hardness = previousHardness;
        color = previousColor;
    }

    public void setColor(Color c) {
        this.color = c;
//        if (this.materialState != null) {
//            this.materialState.setAmbient(ColorRGBA.black);
//            this.materialState.setDiffuse(color);
//            this.materialState.setSpecular(ColorRGBA.black);
//            this.materialState.setShininess(0.0f);
//        //this.materialState.setShininess(NO_SHININESS);
//            this.materialState.setEmissive(ColorRGBA.black);
//            this.materialState.setEnabled(true);
//        }
    }
     public String getColorName() {
        String st = new String("None");
        if(this.color.equals(Color.red) )st = Constants.colorRED;
        else if(this.color.equals(Color.green) )st = Constants.colorGREEN;
        else if(this.color.equals(Color.blue) )st = Constants.colorBLUE;
        else if(this.color.equals(Color.yellow) )st = Constants.colorYELLOW;
        else if(this.color.equals(Color.magenta) )st = Constants.colorMAGENTA;
        else if(this.color.equals(Color.white) )st = Constants.colorWHITE;
        else if(this.color.equals(Color.darkGray) )st = Constants.colorDARKGRAY_SPOILED;
        else if(this.color.equals(Color.orange) )st = Constants.colorORANGE;

        return st;
    }

    public int getColorTypeIndex() {

        if(this.color.equals(Color.red) )return Constants.getColorIndex(Constants.colorRED);
        else if(this.color.equals(Color.green) )return Constants.getColorIndex(Constants.colorGREEN);
        else if(this.color.equals(Color.blue) )return Constants.getColorIndex(Constants.colorBLUE);
        else if(this.color.equals(Color.yellow) )return Constants.getColorIndex(Constants.colorYELLOW);
        else if(this.color.equals(Color.magenta) )return Constants.getColorIndex(Constants.colorMAGENTA);
        else if(this.color.equals(Color.white) )return Constants.getColorIndex(Constants.colorWHITE);

        return -1; //invalid
    }
    
}
