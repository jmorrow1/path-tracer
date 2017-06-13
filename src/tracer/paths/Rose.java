package tracer.paths;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;

/**
 * 
 * A flower-like pattern made from two sinusoidal motions of different
 * frequencies.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public class Rose extends Path {
    private Point cen;
    private float xRadius, yRadius, freq1, freq2;

    /**************************
     ***** Initialization *****
     **************************/

    /**
     * Copy constructor.
     * 
     * @param rose the rose to copy
     */
    public Rose(Rose rose) {
        this(rose.cen.clone(), rose.xRadius, rose.yRadius, rose.freq1, rose.freq2, rose.sampleCount);
        setSampleCount(rose.sampleCount);
    }
    
    //TODO Add constructor that estimates the sample count based on total distance

    /**
     * 
     * @param cenx the center x-coordinate
     * @param ceny the center y-coordinate
     * @param radius the radius
     * @param freq1 the first frequency
     * @param freq2 the second frequency
     * @param sampleCount the number of sample points
     */
    public Rose(float cenx, float ceny, float radius, float freq1, float freq2, int sampleCount) {
        this(new Point(cenx, ceny), radius, radius, freq1, freq2, sampleCount);
    }
    
    /**
     * 
     * @param cen the center of the path
     * @param radius the radius
     * @param freq1 the first frequency
     * @param freq2 the second frequency
     * @param sampleCount the number of sample points
     */
    public Rose(Point cen, float radius, float freq1, float freq2, int sampleCount) {
        this(cen, radius, radius, freq1, freq2, sampleCount);
    }

    /**
     * 
     * @param cenx the center x-coordinate
     * @param ceny the center y-coordinate
     * @param xRadius half of the width
     * @param yRadius half of the height
     * @param freq1 the first frequency
     * @param freq2 the second frequency
     * @param sampleCount the number of sample points
     */
    public Rose(Point cen, float xRadius, float yRadius, float freq1, float freq2, int sampleCount) {
        super(sampleCount);
        this.cen = cen;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
        this.freq1 = freq1;
        this.freq2 = freq2;
    } 

    /**
     * Easy constructor.
     * 
     * @param x The x-coordinate of the path.
     * @param y The y-coordinate of the path.
     * @param r The radius of the path.
     */
    public Rose(float x, float y, float r) {
        this(new Point(x, y), r);
    }
    
    /**
     * Easy constructor.
     * 
     * @param center the center of the path
     * @param r the ratius of the path
     */
    public Rose(Point center, float r) {
        this(center, r, r, 3, 5, 100);
    }

    /*************************
     ***** Functionality *****
     *************************/

    @Override
    public void trace(Point target, float u) {
        u = Path.remainder(u, 1.0f);
        
        if (reversed) {
            u *= -1;
        }
        float alpha = u * PApplet.TWO_PI * freq1;
        float beta = u * PApplet.TWO_PI * freq2;
        float x = cen.x + xRadius * PApplet.cos(alpha);
        float y = cen.y + yRadius * PApplet.sin(alpha);
        float lerpAmt = PApplet.map(PApplet.sin(beta), -1, 1, 0, 1);
        target.x = PApplet.lerp(cen.x, x, lerpAmt);
        target.y = PApplet.lerp(cen.y, y, lerpAmt);
    }

    @Override
    public void translate(float dx, float dy) {
        cen.translate(dx, dy);
    }

    /*******************************
     ***** Getters and Setters *****
     *******************************/
    
    /**
     * 
     * @return the center of the path
     */
    public Point getCenter() {
        return cen;
    }
    
    /**
     * 
     * @param cen the center of the path
     */
    public void setCenter(Point cen) {
        this.cen = cen;
    }

    /**
     * 
     * @return the center x-coordinate
     */
    public float getCenx() {
        return cen.x;
    }

    /**
     * 
     * @param cenx the center x-coordinate
     */
    public void setCenx(float cenx) {
        this.cen.x = cenx;
    }

    /**
     * 
     * @return the center y-coordinate
     */
    public float getCeny() {
        return cen.y;
    }

    /**
     * 
     * @param ceny the center y-coordinate
     */
    public void setCeny(float ceny) {
        this.cen.y = ceny;
    }

    /**
     * 
     * @return half of the width
     */
    public float getXRadius() {
        return xRadius;
    }

    /**
     * 
     * @param xRadius half of the width
     */
    public void setXRadius(float xRadius) {
        this.xRadius = xRadius;
    }

    /**
     * 
     * @return half of the height
     */
    public float getYRadius() {
        return yRadius;
    }

    /**
     * 
     * @param yRadius half of the height
     */
    public void setYRadius(float yRadius) {
        this.yRadius = yRadius;
    }

    /**
     * 
     * @return the first frequency
     */
    public float getFreq1() {
        return freq1;
    }

    /**
     * 
     * @param freq1 the first frequency
     */
    public void setFreq1(float freq1) {
        this.freq1 = freq1;
    }

    /**
     * 
     * @return the second frequency
     */
    public float getFreq2() {
        return freq2;
    }

    /**
     * 
     * @param freq2 the second frequency
     */
    public void setFreq2(float freq2) {
        this.freq2 = freq2;
    }

    @Override
    public Rose clone() {
        return new Rose(this);
    }

    @Override
    public int getGapCount() {
        return 0;
    }

    @Override
    public float getGap(int i) {
        return -1;
    }
    
    @Override
    public String toString() {
        return "Flower [cen=" + cen + ", xRadius=" + xRadius + ", yRadius= " + yRadius + ", freq1=" + freq1 + ", freq2=" + freq2 + "]";
    }
}