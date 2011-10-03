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
package uberdust.util;

import java.io.Serializable;

/**
 * Holds all information about a coordinate for a Node. Can also be used for GPS positioning.
 *
 * @author Malte Legenhausen
 */
public class Coordinate implements Serializable {

    private static final long serialVersionUID = 4172459795364749105L;
    private Double x;
    private Double y;
    private Double z;
    private Double phi;
    private Double theta;

    public Coordinate() {
    }

    public Coordinate(final Double x, final Double y, final Double z, final Double phi, final Double theta) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.phi = phi;
        this.theta = theta;
    }

    public Coordinate(final Coordinate coordinate) {
        x = coordinate.getX();
        y = coordinate.getY();
        z = coordinate.getZ();
        phi = coordinate.getPhi();
        theta = coordinate.getTheta();
    }

    public Double getX() {
        return x;
    }

    public void setX(final Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(final Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(final Double z) {
        this.z = z;
    }

    public Double getPhi() {
        return phi;
    }

    public void setPhi(final Double phi) {
        this.phi = phi;
    }

    public Double getTheta() {
        return theta;
    }

    public void setTheta(final Double theta) {
        this.theta = theta;
    }

    @Override
    public String toString() {
        String s = "x=" + x
                + ", y=" + y
                + ", z=" + z;
        if (phi != null) s += ", phi=" + phi;
        if (theta != null) s += ", theta=" + theta;

        return s;
    }

    private static final double WGS84_CONST = 298.257222101;

    private static final double WGS84_ALPHA = 1.0 / WGS84_CONST;

    private static final double WGS84_A = 6378137.0;

    private static final double WGS84_B = WGS84_A * (1.0 - WGS84_ALPHA);

    private static final double WGS84_C = WGS84_A * WGS84_A / WGS84_B;

    public static Coordinate rotate(final Coordinate coordinate, final Double phi) {
        final Double rad = Math.toRadians(phi);
        final Double cos = Math.cos(rad);
        final Double sin = Math.sin(rad);
        final Double x = coordinate.getX() * cos - coordinate.getY() * sin;
        final Double y = coordinate.getY() * cos + coordinate.getX() * sin;
        return new Coordinate(x, y, coordinate.getZ(), coordinate.getPhi(), coordinate.getTheta());
    }

    public static Coordinate absolute(final Coordinate origin, final Coordinate coordinate) {
        final Double y = coordinate.getY() + origin.getY();
        final Double x = coordinate.getX() + origin.getX();
        return new Coordinate(x, y, origin.getZ(), origin.getPhi(), origin.getTheta());
    }

    public static Coordinate difference(final Coordinate source, final Coordinate vector) {
        return new Coordinate(vector.getX() - source.getX(), vector.getY() - source.getY(), 0.0, 0.0, 0.0);
    }

    public static double angle(final Coordinate source, final Coordinate vector) {
        return Math.atan2(vector.getY(), vector.getX()) - Math.atan2(source.getY(), source.getX());
    }

    public static double location(final Coordinate point, final Coordinate linePoint1, final Coordinate linePoint2) {
        return (linePoint2.getX() - linePoint1.getX())
                * (point.getY() - linePoint1.getY())
                - (point.getX() - linePoint1.getX())
                * (linePoint2.getY() - linePoint1.getY());
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
