package paths;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;

/**
 * 
 * A line in 2-D space.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public class Line extends Path {
    private Point a, b;
    public final static int DEFAULT_INTERSECT_RADIUS = 8;

    /**************************
     ***** Initialization *****
     **************************/

    /**
     * 
     * @param ax the x-coordinate of the first point
     * @param ay the y-coordinate of the first point
     * @param bx the x-ccoordinate of the second point
     * @param by the y-coordinate of the second point
     */
    public Line(float ax, float ay, float bx, float by) {
        this(new Point(ax, ay), new Point(bx, by));
    }
    
    /**
     * 
     * @param a the start point
     * @param b the end point
     */
    public Line(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Copy constructor.
     * 
     * @param line the line to copy
     */
    public Line(Line line) {
        this(line.getAx(), line.getAy(), line.getBx(), line.getBy());
        setSampleCount(line.sampleCount);
    }

    /**
     * Easy constructor.
     * 
     * @param x The x-coordinate of the path.
     * @param y The y-coordinate of the path.
     * @param r The radius of the path.
     */
    public Line(float x, float y, float r) {
        this(x - r * PApplet.cos(0.75f * PApplet.PI), y - r * PApplet.sin(0.75f * PApplet.PI),
                x + r * PApplet.cos(1.75f * PApplet.PI), y + r * PApplet.sin(1.75f * PApplet.PI));
    }
    
    /**************************
     ***** Functionality *****
     **************************/

    @Override
    public void draw(PGraphics g) {
        if (style.stroke) {
            g.strokeCap(style.strokeCap);
            g.stroke(style.strokeColor);
            g.strokeWeight(style.strokeWeight);
        }
        g.line(a.x, a.y, b.x, b.y);
    }

    @Override
    public void trace(Point pt, float amt) {
        pt.x = PApplet.lerp(a.x, b.x, amt);
        pt.y = PApplet.lerp(a.y, b.y, amt);
    }

    /**
     * Interpolates between the line defined by (a.x, a.y) and (b.x, b.y) by the
     * given amount (a value between 0 and 1) and stores the result in the given
     * point.
     * 
     * @param pt The point in which to store the result
     * @param a The first point
     * @param b The second point
     * @param amt The amount by which to interpolate (a value from 0 to 1)
     */
    public static void trace(Point pt, Point a, Point b, float amt) {
        pt.x = PApplet.lerp(a.x, b.x, amt);
        pt.y = PApplet.lerp(a.y, b.y, amt);
    }

    /**
     * Interpolates between the line defined by (ax, ay) and (bx, by) by the
     * given amount (a value between 0 and 1) and stores the result in the given
     * point.
     * 
     * @param pt the point
     * @param ax the x-coordinate of the first point
     * @param ay the y-coordinate of the first point
     * @param bx the x-coordinate of the second point
     * @param by the y-coordinate of the second point
     * @param amt the amount by which to interpolate (a value from 0 to 1)
     */
    public static void trace(Point pt, float ax, float ay, float bx, float by, float amt) {
        pt.x = PApplet.lerp(ax, bx, amt);
        pt.y = PApplet.lerp(ay, by, amt);
    }

    /**
     * Returns true if the given point comes within the given radius to any
     * point in the line.
     * 
     * @param x the x of the point
     * @param y the y of the point
     * @param intersectRadius the maximum distance that can be considered
     *            touching
     * @return true if the point touches the line and false otherwise
     */
    public boolean touches(float x, float y, float intersectRadius) {
        return touches(a.x, a.y, b.x, b.y, x, y, intersectRadius);
    }

    /**
     * Returns true if the given point comes in close enough contact with the
     * line.
     * 
     * @param x the x of the point
     * @param y the y of the point
     * @return true if the point touches the line and false otherwise
     */
    public boolean touches(float x, float y) {
        return touches(a.x, a.y, b.x, b.y, x, y, DEFAULT_INTERSECT_RADIUS);
    }

    /**
     * Returns true if the given point comes in close enough contact with the
     * line.
     * 
     * @param ax the x-coordinate of the first point
     * @param ay the y-coordinate of the first point
     * @param bx the x-coordinate of the second point
     * @param by the y-coordinate of the second point
     * @param x the x of the point
     * @param y the y of the point
     * @return true if the point touches the line and false otherwise
     */
    public static boolean touches(float ax, float ay, float bx, float by, float x, float y) {
        return touches(ax, ay, bx, by, x, y, DEFAULT_INTERSECT_RADIUS);
    }

    /**
     * Returns true if the line defined by (ax, ay) and (bx, by) comes within
     * the given radius to any point in the line.
     * 
     * @param ax the x-coordinate of the first point
     * @param ay the y-coordinate of the second point
     * @param bx the x-coordinate of the first point
     * @param by the y-coordinate of the second point
     * @param x the x of the point
     * @param y the y of the point
     * @param intersectRadius the maximum distance that can be considered
     *            touching
     * @return true if the point touches the line and false otherwise
     */
    public static boolean touches(float ax, float ay, float bx, float by, float x, float y, float intersectRadius) {
        float x1 = PApplet.min(ax, bx);
        float y1 = PApplet.min(ay, by);
        float x2 = PApplet.max(ax, bx);
        float y2 = PApplet.max(ay, by);
        if (!((x1 - intersectRadius <= x && x <= x2 + intersectRadius && y1 - intersectRadius <= y
                && y <= y2 + intersectRadius)
                || (x1 - intersectRadius <= x && x <= x2 + intersectRadius && y2 - intersectRadius <= y
                        && y <= y1 + intersectRadius)
                || (x2 - intersectRadius <= x && x <= x1 + intersectRadius && y1 - intersectRadius <= y
                        && y <= y2 + intersectRadius)
                || (x2 - intersectRadius <= x && x <= x1 + intersectRadius && y2 - intersectRadius <= y
                        && y <= y1 + intersectRadius))) {
            return false;
        }

        // compute the euclidean distance between (x1, y1) and (x2, y2)
        float lab = PApplet.dist(x1, y1, x2, y2);
        // compute the direction vector D from (x1, y1) to (x2, y2)
        float dx = (x2 - x1) / lab;
        float dy = (y2 - y1) / lab;

        float t = dx * (x - x1) + dy * (y - y1);

        float ex = t * dx + x1;
        float ey = t * dy + y1;

        // compute the eculidean distance from e to (x, y)
        float lec = PApplet.dist(ex, ey, x, y);

        // test if line touches circle centered about (x, y)
        if (lec < DEFAULT_INTERSECT_RADIUS) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void translate(float dx, float dy) {
        a.translate(dx, dy);
        b.translate(dx, dy);
    }

    @Override
    public Line clone() {
        return new Line(this);
    }

    public void reverse() {
        super.reverse();
        Point temp = b;
        b = a;
        a = temp;
    }

    /*******************************
     ***** Getters and Setters *****
     *******************************/

    @Override
    public float getTotalDistance() {
        return PApplet.dist(a.x, a.y, b.x, b.y);
    }

    public float getCenx() {
        return PApplet.lerp(a.x, b.x, 0.5f);
    }

    public float getCeny() {
        return PApplet.lerp(a.y, b.y, 0.5f);
    }

    /**
     * 
     * @return the length of the line
     */
    public float getLength() {
        return getTotalDistance();
    }

    public float getWidth() {
        return PApplet.max(a.x, b.x) - PApplet.min(a.x, b.x);
    }

    public float getHeight() {
        return PApplet.max(a.y, b.y) - PApplet.min(a.y, b.y);
    }
    
    /**
     * 
     * @return the line's start point
     */
    public Point getA() {
        return a;
    }
    
    /**
     * 
     * @param a the line's start point
     */
    public void setA(Point a) {
        this.a = a;
    }

    /**
     * 
     * @return the x-coordinate of the line's start point
     */
    public float getAx() {
        return a.x;
    }

    /**
     * 
     * @return the y-coordinate of the line's start point
     */
    public float getAy() {
        return a.y;
    }
    
    /**
     * 
     * @return the line's end point
     */
    public Point getB() {
        return b;
    }
    
    /**
     * 
     * @param b the line's end point
     */
    public void setB(Point b) {
        this.b = b;
    }

    /**
     * 
     * @return the x-coordinate of the line's end point
     */
    public float getBx() {
        return b.x;
    }

    /**
     * 
     * @return the y-coordinate of the line's end point
     */
    public float getBy() {
        return b.y;
    }

    @Override
    public int getGapCount() {
        return 1;
    }

    @Override
    public float getGap(int i) {
        return 1;
    }
    
    /**
     * Computes the distance between a and b.
     * @param a Point a
     * @param b Point b
     * @return The distance
     */
    public static float dist(Point a, Point b) {
        return PApplet.dist(a.x, a.y, b.x, b.y);
    }
}
