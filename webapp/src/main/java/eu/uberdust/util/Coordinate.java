/**
 * Copyright (C) 2011 Universität zu Lübeck, Institut für Telematik (ITM),
 *                             Research Academic Computer Technology Institute (RACTI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.uberdust.util;

import java.io.Serializable;

/**
 * Holds all information about a coordinate for a Node. Can also be used for GPS positioning.
 *
 * @author Malte Legenhausen
 */
public final class Coordinate implements Serializable {
    /**
     * Serial Version Unique ID.
     */
    private static final long serialVersionUID = 4172459795364749105L;

    /**
     * X cartesian axis.
     */
    private Double x;

    /**
     * Y cartesian axis.
     */
    private Double y;

    /**
     * Z cartesian axis.
     */
    private Double z;

    /**
     * Phi angle.
     */
    private Double phi;

    /**
     * Theta angle.
     */
    private Double theta;

    /**
     * Constructor.
     */
    public Coordinate() {
        // empty constructor
    }

    /**
     * Constructor.
     */
    public Coordinate(final Double x, final Double y, final Double z, final Double phi, final Double theta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.phi = phi;
        this.theta = theta;
    }

    /**
     * Constructor.
     */
    public Coordinate(final Double x, final Double y, final Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.phi = 0.0;
        this.theta = 0.0;
    }

    /**
     * Returns x.
     *
     * @return x
     */
    public Double getX() {
        return x;
    }

    /**
     * Sets x.
     *
     * @param x x
     */
    public void setX(final Double x) {
        this.x = x;
    }

    /**
     * Returns y.
     *
     * @return y.
     */
    public Double getY() {
        return y;
    }

    /**
     * Sets y.
     *
     * @param y y.
     */
    public void setY(final Double y) {
        this.y = y;
    }

    /**
     * Returns z.
     *
     * @return z.
     */
    public Double getZ() {
        return z;
    }

    /**
     * Sets z.
     *
     * @param z z.
     */
    public void setZ(final Double z) {
        this.z = z;
    }

    /**
     * Returns phi.
     *
     * @return phi.
     */
    public Double getPhi() {
        return phi;
    }

    /**
     * Sets phi.
     *
     * @param phi phi.
     */
    public void setPhi(final Double phi) {
        this.phi = phi;
    }

    /**
     * Returns theta.
     *
     * @return theta.
     */
    public Double getTheta() {
        return theta;
    }

    /**
     * Sets theta.
     *
     * @param theta theta.
     */
    public void setTheta(final Double theta) {
        this.theta = theta;
    }

    /**
     * Returns string representation of coordinate.
     *
     * @return string representation of coordinate.
     */
    public String toString() {
        return "x=" + x + ", y=" + y + ", z=" + z + ((phi == null) ? ("") : (", phi=" + phi))
                + ((theta == null) ? ("") : (", theta=" + theta));
    }

    /**
     * WGS84 constant.
     */
    private static final double WGS84_CONST = 298.257222101;

    /**
     * WGS84_ALPHA constant.
     */
    private static final double WGS84_ALPHA = 1.0 / WGS84_CONST;

    /**
     * WGS84_A constant.
     */
    private static final double WGS84_A = 6378137.0;

    /**
     * WGS84_B constant.
     */
    private static final double WGS84_B = WGS84_A * (1.0 - WGS84_ALPHA);

    /**
     * WGS84_C constant.
     */
    private static final double WGS84_C = WGS84_A * WGS84_A / WGS84_B;

    /**
     * Rotate coordinates using phi angle.
     *
     * @param coordinate , coordinate instance.
     * @param phi        , phi angle.
     * @return rotated coordinates
     */
    public static Coordinate rotate(final Coordinate coordinate, final Double phi) {
        final Double rad = Math.toRadians(phi);
        final Double cos = Math.cos(rad);
        final Double sin = Math.sin(rad);
        final Double x = coordinate.getX() * cos - coordinate.getY() * sin;
        final Double y = coordinate.getY() * cos + coordinate.getX() * sin;
        return new Coordinate(x, y, coordinate.getZ(), coordinate.getPhi(), coordinate.getTheta());
    }

    /**
     * Absolute coordinates based on an origin coordinate.
     *
     * @param origin     , origin coordinate.
     * @param coordinate , relative coordinate.
     * @return absolute cooordinates.
     */
    public static Coordinate absolute(final Coordinate origin, final Coordinate coordinate) {
        final Double y = coordinate.getY() + origin.getY();
        final Double x = coordinate.getX() + origin.getX();
        return new Coordinate(x, y, origin.getZ(), origin.getPhi(), origin.getTheta());
    }

    /**
     * Transforms a coordinate from a xyz coordinate system in an equivalent geographical coordinate
     * with latitude, longitude and height information.
     * The units that is used for the xyz system are meters.
     *
     * @param coordinate The xyz-coordinate that has to be converted.
     * @return The geographic coordinate.
     */
    public static Coordinate xyz2blh(final Coordinate coordinate) {
        final double x = coordinate.getX();
        final double y = coordinate.getY();
        final double z = coordinate.getZ();

        final double roh = 180.0 / Math.PI;

        final double e0 = (WGS84_A * WGS84_A) - (WGS84_B * WGS84_B);
        final double e1 = Math.sqrt(e0 / (WGS84_A * WGS84_A));
        final double e2 = Math.sqrt(e0 / (WGS84_B * WGS84_B));

        final double p = Math.sqrt((x * x) + (y * y));

        final double theta = Math.atan((z * WGS84_A) / (p * WGS84_B));

        final double l = Math.atan(y / x) * roh;
        final double b = Math.atan((z + (e2 * e2 * WGS84_B * Math.pow(Math.sin(theta), 3))) / (p - (e1 * e1 * WGS84_A * Math.pow(Math.cos(theta), 3))));

        final double eta2 = e2 * e2 * Math.pow(Math.cos(b), 2);
        final double v = Math.sqrt(1.0 + eta2);
        final double n = WGS84_C / v;

        final double h = (p / Math.cos(b)) - n;
        return new Coordinate(b * roh, l, h, coordinate.getPhi(), coordinate.getTheta());
    }

    /**
     * Transforms a coordinate from the geographic coordinate system to an equivalent xyz coordinate system.
     * The units that is used for the xyz system are meters.
     *
     * @param coordinate The geographic coordinate that has to be converted.
     * @return The converted xyz coordinate.
     */
    public static Coordinate blh2xyz(final Coordinate coordinate) {
        final double roh = Math.PI / 180.0;

        final double e = Math.sqrt(((WGS84_A * WGS84_A) - (WGS84_B * WGS84_B)) / (WGS84_B * WGS84_B));

        final double b = coordinate.getX() * roh;
        final double l = coordinate.getY() * roh;

        final double eta2 = e * e * Math.pow(Math.cos(b), 2);
        final double v = Math.sqrt(1.0 + eta2);
        final double n = WGS84_C / v;

        final double h = coordinate.getZ();
        final double x = (n + h) * Math.cos(b) * Math.cos(l);
        final double y = (n + h) * Math.cos(b) * Math.sin(l);
        final double z = (Math.pow(WGS84_B / WGS84_A, 2) * n + h) * Math.sin(b);
        return new Coordinate(x, y, z, coordinate.getPhi(), coordinate.getTheta());
    }
}
