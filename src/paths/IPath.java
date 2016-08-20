package paths;

import processing.core.PApplet;

/**
 * 
 * 
 * @author James Morrow
 *
 */
public interface IPath {
	final static Point pt = new Point(0, 0);
	
	/**
	 * 
	 * A continuous function from real values (floats) to Points,
	 * 
	 * Maps a given floating point number from 0 to 1 to a given Point
	 * along the perimeter of the Path.
	 * 
	 * @param pt The Point in which the result is stored.
	 * @param amt A number from 0 to 1.
	 */
	public void trace(Point pt, float amt);
	
	/**
	 * A continuous function from real values (floats) to Points,
	 * 
	 * Maps a given floating point number from 0 to 1 to a Point
	 * along the perimeter of the Path.
	 * 
	 * @param amt A number from 0 to 1.
	 * @return The resulting point.
	 */
	public default Point trace(float amt) {
		Point pt = new Point(0, 0);
		this.trace(pt, amt);
		return pt;
	}

	/**
	 * 
	 * @param pa The PApplet to which the path is drawn.
	 */
	public void display(PApplet pa);
	
	/**
	 * Draws the path by approximating it with a given number of sample points,
	 * and then connecting those points with lines.
	 * 
	 * This is a useful shortcut for classes that implement IPath to use.
	 * It allows an IPath to define its proper display method in terms of this function.
	 * 
	 * @param pa The PApplet to which the path is drawn.
	 * @param granularity The number of sample points.
	 */
	public default void display(PApplet pa, int granularity) {
		float amt = 0;
		float dAmt = 1f/granularity;
		pa.beginShape();
		for (int i=0; i<granularity+1; i++) {
			trace(pt, amt);
			pa.vertex(pt.x, pt.y);
			amt += dAmt;
		}
		pa.endShape();
	}
	/**
	 * Shifts this Path dx units in the x-direction and dy units in the y-direction.
	 * 
	 * @param dx The number of pixels to shift the path right.
	 * @param dy THe number of pixels to shift the path down.
	 */
	public void translate(float dx, float dy);
	
	/**
	 * Returns the slope of the Point on the Path at trace(amt).
	 * 
	 * @param amt The amount.
	 * @return The slope at trace(amt).
	 */
	public default float slope(float amt) {
		if (amt >= 0.001f) {
			Point a = this.trace(amt - 0.001f);
			Point b = this.trace(amt);
			return Point.slope(a, b);
		}
		else {
			Point a = this.trace(amt + 0.001f);
			Point b = this.trace(amt);
			return Point.slope(a, b);
		}
	}
	
	/**
	 * Reverses the path.
	 */
	public void reverse();
	
	/**
	 * Clones the path.
	 * @return
	 */
	public IPath clone();
}