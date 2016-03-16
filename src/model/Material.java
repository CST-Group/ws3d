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
