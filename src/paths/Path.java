package paths;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;

/**
 * 
 * A continuous sequence of points in 2D space.
 * 
 * <br><br>
 * 
 * Usage:<br>
 * To get a point on an Path p, use p.trace(u) or p.trace(pt, u) where u
 * is a floating point value between 0 and 1 and pt is a Point (a coordinate in 2D space).
 * 
 * <br><br>
 * 
 * p.trace(0) returns a Point at the beginning of the path.
 * p.trace(0.5) returns a Point in the middle of the path.
 * p.trace(1) returns the point at the end of the path.
 * And so on.
 * 
 * <br><br>
 * 
 * Alternatively, you can use p.trace(pt, u) which, instead of returning a Point, stores
 * the result of the computation in the given pt. This method is more efficient because it
 * doesn't require a new Point to be allocated.
 * 
 * <br><br>
 * 
 * To display an Path p, use p.display(g) where g is a PGraphics object.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public abstract class Path {
	private final static Point pt = new Point(0, 0);
	protected boolean reversed;
	protected int granularity = 100;
	
	public Path() {}
	
	/**
	 * 
	 * @param granularity the number of sample points
	 */
	public Path(int granularity) {
		this.granularity = granularity;
	}
	
	public void draw(PGraphics g) {
		if (granularity != -1) {
			draw(g, granularity);
		}
	}

	public void draw(PGraphics g, float u1, float u2) {
		boolean inRange = (0 <= u1 && u1 <= 1 && 0 <= u2 && u2 <= 1);
		if (!inRange) {
			throw new IllegalArgumentException("draw(g, " + u1 + ", " + u2 + ") called with values outside the range 0 to 1.");
		}
	
		if (u1 > u2) {
			draw(g, u1, 1);
			draw(g, 0, u2);
		}
		else {
			float length = PApplet.abs(u1 - u2);
			int n = (int)(granularity * length);
			float du = length / n;
					
			g.beginShape();
			float u = u1;
			for (int i=0; i<n; i++) {
				trace(pt, u);
				g.vertex(pt.x, pt.y);
				u = (u+du) % 1f;
			}
			g.endShape();
		}
	}
	
	public abstract void trace(Point pt, float u);

	/**
	 * 
	 * @return true, if the path is set to reversed and false otherwise
	 */
	public boolean isReversed() {
		return reversed;
	}
	
	public void reverse() {
		reversed = !reversed;
	}
	
	public abstract Path clone();
	
	/**
	 * A continuous function from real values (floats) to Points.
	 * 
	 * <br><br>
	 * 
	 * Maps a given floating point number from 0 to 1 to a Point
	 * along the perimeter of the Path.
	 * 
	 * @param u A number from 0 to 1.
	 * @return The resulting point.
	 */
	public Point trace(float u) {
		Point pt = new Point(0, 0);
		this.trace(pt, u);
		return pt;
	}
	
	/**
	 * Draws the path by approximating it with a given number of sample points,
	 * and then connecting those points with lines.
	 * 
	 * <br><br>
	 * 
	 * This is a useful shortcut for classes that implement IPath to use.
	 * It allows an IPath to define its proper display method in terms of this function.
	 * 
	 * @param pa The PApplet to which the path is drawn.
	 * @param granularity The number of sample points.
	 */
	public void draw(PGraphics g, int granularity) {
		float amt = 0;
		float dAmt = 1f/granularity;
		g.beginShape();
		for (int i=0; i<granularity+1; i++) {
			trace(pt, amt);
			g.vertex(pt.x, pt.y);
			amt += dAmt;
		}
		g.endShape();
	}
	
	/**
	 * Shifts this Path dx units in the x-direction and dy units in the y-direction.
	 * 
	 * @param dx The number of pixels to shift the path right.
	 * @param dy THe number of pixels to shift the path down.
	 */
	public abstract void translate(float dx, float dy);
	
	/**
	 * Returns the slope of the Point on the Path at trace(amt).
	 * 
	 * @param amt The amount.
	 * @return The slope at trace(amt).
	 */
	public float slope(float amt) {
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
}
