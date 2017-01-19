package paths;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;

/**
 * 
 * A rectangle, analgous to Processing's rect() function.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public class Rect extends Path {
    //definition
    protected Point ab, cd;
    protected int rectMode;
    
    //helper fields
    protected float[] vertices = new float[4]; // one-dimensional coordinates
    protected float perimeter;

    /**
     * 
     * @param a the 1st rect argument, whose meaning is determined by the given
     *            rectMode
     * @param b the 2nd rect argument, whose meaning is determined by the given
     *            rectMode
     * @param c the 3rd rect argument, whose meaning is determined by the given
     *            rectMode
     * @param d the 4th rect argument, whose meaning is determined by the given
     *            rectMode
     * @param rectMode Determines the meaning of a, b, c, and d. The rectMode
     *            can be CENTER, RADIUS, CORNER, or CORNERS.
     */
    public Rect(float a, float b, float c, float d, int rectMode) {
        set(a, b, c, d, rectMode);
    }

    /**
     * 
     * @param ab the 1st and 2nd rect arguments, whose meaning is determined by
     *            the given rectMode
     * @param c the 3rd rect argument, whose meaning is determined by the given
     *            rectMode
     * @param d the 4th rect argument, whose meaning is determined by the given
     *            rectMode
     * @param rectMode Determines the meaning of a, b, c, and d. The rectMode
     *            can be CENTER, RADIUS, CORNER, or CORNERS.
     */
    public Rect(Point ab, float c, float d, int rectMode) {
        set(ab, c, d, rectMode);
    }

    /**
     * 
     * @param ab the 1st and 2nd rect arguments, whose meaning is determined by
     *            the given rectMode
     * @param cd the 3rd and 4th rect argument, whose meaning is determined by
     *            the given rectMode
     * @param rectMode Determines the meaning of a, b, c, and d. The rectMode
     *            can be CENTER, RADIUS, CORNER, or CORNERS.
     */
    public Rect(Point ab, Point cd, int rectMode) {
        set(ab, cd, rectMode);
    }

    /**
     * Copy constructor.
     * 
     * @param rect The rectangle to copy
     */
    public Rect(Rect rect) {
        set(new Point(rect.ab), new Point(rect.cd), rect.rectMode);
        setSampleCount(rect.sampleCount);
    }

    /**
     * Easy constructor.
     * 
     * @param x The x-coordinate of the Path
     * @param y The y-coordinate of the Path
     * @param r The maximum radius of the Path
     */
    public Rect(float x, float y, float r) {
        set(x, y, r, r, RADIUS);
    }

    /**
     * 
     * Sets the position and size of the rectangle.
     * 
     * @param ab the 1st and 2nd rect arguments, whose meaning is determined by
     *            the given rectMode
     * @param cd the 3rd and 4th rect argument, whose meaning is determined by
     *            the given rectMode
     * @param rectMode Determines the meaning of a, b, c, and d. The rectMode
     *            can be CENTER, RADIUS, CORNER, or CORNERS.
     */
    public void set(Point ab, Point cd, int rectMode) {
        switch (rectMode) {
            case CORNERS:
                this.ab = ab;
                this.cd = cd;
                break;
            case CORNER:
            case CENTER:
            case RADIUS:
                this.ab = ab;
                this.cd = new Point(cd);
                break;
            default:
                System.err.println("Invalid rectMode. Use CORNERS, CORNER, CENTER, or RADIUS.");
                set(ab, cd, CORNER);
                break;
        }
        this.rectMode = rectMode;

        setHelperFields();
    }

    /**
     * 
     * Sets the position and size of the rectangle.
     * 
     * @param ab the 1st and 2nd rect arguments, whose meaning is determined by
     *            the given rectMode
     * @param c the 3rd rect argument, whose meaning is determined by the given
     *            rectMode
     * @param d the 4th rect argument, whose meaning is determined by the given
     *            rectMode
     * @param rectMode Determines the meaning of a, b, c, and d. The rectMode
     *            can be CENTER, RADIUS, CORNER, or CORNERS.
     */
    public void set(Point ab, float c, float d, int rectMode) {
        set(ab, new Point(c, d), rectMode);
    }

    /**
     * 
     * Sets the position and size of the rectangle.
     * 
     * @param a the 1st rect argument, whose meaning is determined by the given
     *            rectMode
     * @param b the 2nd rect argument, whose meaning is determined by the given
     *            rectMode
     * @param c the 3rd rect argument, whose meaning is determined by the given
     *            rectMode
     * @param d the 4th rect argument, whose meaning is determined by the given
     *            rectMode
     * @param rectMode Determines the meaning of a, b, c, and d. The rectMode
     *            can be CENTER, RADIUS, CORNER, or CORNERS.
     */
    public void set(float a, float b, float c, float d, int rectMode) {
        set(new Point(a, b), new Point(c, d), rectMode);
    }

    @Override
    public void draw(PGraphics g) {
        style.apply(g);
        g.rectMode(rectMode);
        g.rect(ab.x, ab.y, cd.x, cd.y);
    }

    @Override
    public void trace(Point pt, float u) {
        if (reversed) {
            u = PApplet.map(u, 0, 1, 1, 0);
        }

        if (0 <= u && u < vertices[1]) {
            u = PApplet.map(u, 0, vertices[1], 0, 1);
            pt.x = getX1() + u * getWidth();
            pt.y = getY1();
        } else if (u < vertices[2]) {
            u = PApplet.map(u, vertices[1], vertices[2], 0, 1);
            pt.x = getX2();
            pt.y = getY1() + u * getHeight();
        } else if (u < vertices[3]) {
            u = PApplet.map(u, vertices[2], vertices[3], 0, 1);
            pt.x = getX2() - u * getWidth();
            pt.y = getY2();
        } else if (u < 1) {
            u = PApplet.map(u, vertices[3], 1, 0, 1);
            pt.x = getX1();
            pt.y = getY2() - u * getHeight();
        }
    }

    @Override
    public Path clone() {
        return new Rect(this);
    }

    @Override
    public void translate(float dx, float dy) {
        ab.translate(dx, dy);
        if (rectMode == CORNERS) {
            cd.translate(dx, dy);
        }
    }

    @Override
    public int getGapCount() {
        return 0;
    }

    @Override
    public float getGap(int i) {
        return -1;
    }

    /**
     * Gives the center x-coordinate of the rectangle.
     * 
     * @return The center x-coordinate of the rectangle
     */
    public float getCenx() {
        switch (rectMode) {
            case CENTER:
            case RADIUS:
                return ab.x;
            case CORNER:
                return ab.x + 0.5f * cd.x;
            case CORNERS:
                return ab.x + 0.5f * (cd.x - ab.x);
            default:
                return -1;
        }
    }

    /**
     * Gives the center y-coordinate of the rectangle
     * 
     * @return The center y-coordinate of the rectangle
     */
    public float getCeny() {
        switch (rectMode) {
            case CENTER:
            case RADIUS:
                return ab.y;
            case CORNER:
                return ab.y + 0.5f * cd.y;
            case CORNERS:
                return ab.y + 0.5f * (cd.y - ab.y);
            default:
                return -1;
        }
    }

    /**
     * Gives the minimum x-value of the rectangle.
     * 
     * @return The minimum x-value of the rectangle
     */
    public float getX1() {
        switch (rectMode) {
            case CENTER:
                return ab.x - 0.5f * cd.x;
            case RADIUS:
                return ab.x - cd.x;
            case CORNER:
            case CORNERS:
                return ab.x;
            default:
                return -1;
        }
    }

    /**
     * Gives the minimum y-value of the rectangle
     * 
     * @return The minimum y-value of the rectangle
     */
    public float getY1() {
        switch (rectMode) {
            case CENTER:
                return ab.y - 0.5f * cd.y;
            case RADIUS:
                return ab.y - cd.y;
            case CORNER:
            case CORNERS:
                return ab.y;
            default:
                return -1;
        }
    }

    /**
     * Gives the maximum x-value of the rectangle.
     * 
     * @return The maximum x-value of the rectangle
     */
    public float getX2() {
        switch (rectMode) {
            case CENTER:
                return ab.x + 0.5f * cd.x;
            case RADIUS:
                return ab.x + cd.x;
            case CORNER:
                return ab.x + cd.x;
            case CORNERS:
                return cd.x;
            default:
                return -1;
        }
    }

    /**
     * Gives the maximum y-value of the rectangle.
     * 
     * @return The maximum y-value of the rectangle
     */
    public float getY2() {
        switch (rectMode) {
            case CENTER:
                return ab.y + 0.5f * cd.y;
            case RADIUS:
                return ab.y + cd.y;
            case CORNER:
                return ab.y + cd.y;
            case CORNERS:
                return cd.y;
            default:
                return -1;
        }
    }

    /**
     * Gives the width of the rectangle
     * 
     * @return The width of the rectangle
     */
    public float getWidth() {
        switch (rectMode) {
            case CENTER:
            case CORNER:
                return cd.x;
            case RADIUS:
                return 2f * cd.x;
            case CORNERS:
                return cd.x - ab.x;
            default:
                return -1;
        }
    }

    /**
     * Gives the height of the rectangle.
     * 
     * @return The height of the rectangle
     */
    public float getHeight() {
        switch (rectMode) {
            case CENTER:
            case CORNER:
                return cd.y;
            case RADIUS:
                return 2f * cd.y;
            case CORNERS:
                return cd.y - ab.y;
            default:
                return -1;
        }
    }

    private void setHelperFields() {
        perimeter = 2f * (getX2() - getX1()) + 2f * (getY2() - getY1());
        
        if (vertices == null) {
            vertices = new float[4];
        }

        vertices[0] = 0;
        vertices[1] = (getX2() - getX1()) / perimeter;
        vertices[2] = 0.5f;
        vertices[3] = 0.5f + vertices[1];
    }

    @Override
    public float getTotalDistance() {
        return perimeter;
    }

    @Override
    public String toString() {
        return "Rect [x1=" + getX1() + ", y1=" + getY1() + ", width=" + getWidth() + ", height=" + getHeight() + "]";
    }

}
