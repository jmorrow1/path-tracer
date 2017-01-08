package paths;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;

//TODO Could add an (optional) optimization where, when getting points from the T path and the U path,
//the Blender has the T and U paths return their closest cached points which approximate the desired points
//rather than interpolate between cached points.

/**
 * A path that interpolates between two aggregate paths.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 * @param <T> the type of the first path
 * @param <U> the type of thes second path
 */
public class Blender<T extends Path, U extends Path> extends Path {
    private Point ptA = new Point(0, 0), ptB = new Point(0, 0);
    private T a;
    private U b;
    private float blendAmt;

    /**************************
     ***** Initialization *****
     **************************/

    /**
     * Copy constructor.
     * 
     * @param blender the blender to copy
     */
    public Blender(Blender<T, U> blender) {
        this(blender.a, blender.b, blender.blendAmt, blender.sampleCount);
    }

    /**
     * 
     * @param a the first path
     * @param b the second path
     * @param blendAmt a value between 0 and 1 specifying how much to blend between a and b
     * @param sampleCount the number of sample points
     */
    public Blender(T a, U b, float blendAmt, int sampleCount) {
        super(sampleCount);
        this.a = a;
        this.b = b;
        this.blendAmt = blendAmt;
    }

    // /**
    // * Easy constructor.
    // *
    // * @param x The x-coordinate of the path.
    // * @param y The y-coordinate of the path.
    // * @param r The radius of the path.
    // */
    // public Blender(float x, float y, float r) {
    // //TODO
    // }

    /*************************
     ***** Functionality *****
     *************************/

    @Override
    public void trace(Point pt, float amt) {
        if (reversed) {
            amt = PApplet.map(amt, 0, 1, 1, 0);
        }
        a.trace(ptA, amt);
        b.trace(ptB, amt);
        pt.x = PApplet.lerp(ptA.x, ptB.x, blendAmt);
        pt.y = PApplet.lerp(ptA.y, ptB.y, blendAmt);
    }

    @Override
    public void translate(float dx, float dy) {
        a.translate(dx, dy);
        b.translate(dx, dy);
    }

    /*******************************
     ***** Getters and Setters *****
     *******************************/

    /**
     * 
     * @return the first path
     */
    public T getA() {
        return a;
    }

    /**
     * 
     * @param a the first path
     */
    public void setA(T a) {
        this.a = a;
    }

    /**
     * 
     * @return the second path
     */
    public U getB() {
        return b;
    }

    /**
     * 
     * @param b the second path
     */
    public void setB(U b) {
        this.b = b;
    }

    /**
     * 
     * @return a value between 0 and 1 specifying how much to blend between the
     *         first and second paths
     */
    public float getBlendAmt() {
        return blendAmt;
    }

    /**
     * 
     * @param blendAmt a value between 0 and 1 specifying how much to blend
     *            between the first and second paths
     */
    public void setBlendAmt(float blendAmt) {
        this.blendAmt = blendAmt;
    }

    /**
     * 
     * @param dAmt how much to change the blendAmt
     */
    public void addToBlendAmt(float dAmt) {
        this.blendAmt = Path.remainder(this.blendAmt + dAmt, 1);
    }

    @Override
    public Blender<T, U> clone() {
        return new Blender(this);
    }

    @Override
    public int getGapCount() {
        // TODO I don't know how to do this
        return 0;
    }

    @Override
    public float getGap(int i) {
        // TODO I don't know how to do this
        return -1;
    }
    
    @Override
    public boolean isGap(float u) {
        // TODO I don't know how to do this
        return false;
    }
    
    /**
     * Blends two Paths and makes a Shape.
     * @param a The first Path
     * @param b The second Path
     * @param amt A value from 0 to 1, determining how much to weight Path b over Path a.
     * @param sampleCount The number of sample points to use
     * @return The Shape
     */
    public static Shape blend(Path a, Path b, float amt, int sampleCount) {
        return Shape.blend(a, b, amt, sampleCount);
    }
}