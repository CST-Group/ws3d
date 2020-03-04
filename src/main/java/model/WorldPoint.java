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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * A coordinate point within the System. Currently 2D (x, y, 0).
 *
 * @author eccastro
 */
public class WorldPoint {

    private double x;
    private double y;
    private double z; //currently not in use.

    private Random generator = new Random();

    public WorldPoint(double x, double y, double ang,
            double eval) {
        super();
        this.x = x;
        this.y = y;
    }

    public WorldPoint() {
        super();
        double width = 750.0;
        double heigth = 550.0;

        x = generator.nextDouble() * width;
        y = generator.nextDouble() * heigth;

    }

    public WorldPoint(double x, double y, double ang) {
        this(x, y, ang, 0);
    }

    public WorldPoint(double x, double y) {
        this(x, y, 0);
    }


    /**
     * @return Returns the x.
     */
    public double getX() {
        return x;
    }

    /**
     * @param x The x to set.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return Returns the y.
     */
    public double getY() {
        return y;
    }

    /**
     * @param y The y to set.
     */
    public void setY(double y) {
        this.y = y;
    }

    public String toString() {
        NumberFormat nf = new DecimalFormat("0.00");
        return "(" + nf.format(x) + " , " + nf.format(y) + ")";
    }

    public String simpleToString() {
        NumberFormat nf = new DecimalFormat("0.00");
        return "" + nf.format(x) + " " + nf.format(y);
    }

    private double error = 0.0001;

    public double getXInPrecision() {
        long div = (long) (getX() / error);
        return div * error;
    }

    public double getYInPrecision() {
        long div = (long) (getY() / error);
        return div * error;
    }

    public int hashCode() {

        return (int) (this.getXInPrecision() * 945 + this.getYInPrecision() * 1863);
    }

    public boolean equals(Object o) {
        WorldPoint a = (WorldPoint) o;
        WorldPoint b = (WorldPoint) this;
        return (a.getXInPrecision() == b.getXInPrecision())
                && (a.getYInPrecision() == b.getYInPrecision());
    }


    public boolean isOther(WorldPoint point) {
        if (this.x != point.x || this.y != point.y) {
            return true;
        }
        return false;
    }

    /**
     * Distance between two points in a Cartesian plane.
     */
    public double distanceTo(WorldPoint b) {
        double x1 = b.getX();
        double y1 = b.getY();
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }


}
