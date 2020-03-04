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
 * @author patbgi
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.awt.Color;



public class Material
{
	private double hardness;
	private double taste;
	private double energy;
	private Color  color;


	public Material(Color color)
	{
		hardness = 1.0;
		taste    = 0.0;
		energy   = 0.0;

		this.color = color;
	}

	
	public double getHardness() { return hardness; }
	public double getTaste()    { return taste; }
	public double getEnergy()   { return energy; }
	public Color  getColor()    { return color; }

	public void setHardness(double hardness) { this.hardness = hardness; }
	public void setTaste   (double taste)    { this.taste    = taste; }
	public void setEnergy  (double energy)   { this.energy   = energy; }
	public void setColor   (Color  color)    { this.color    = color; }
        

}
