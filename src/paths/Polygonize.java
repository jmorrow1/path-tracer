package paths;

import processing.core.PApplet;

/**
 * 
 * @author James Morrow
 *
 */
public class Polygonize {
	/**
	 * Makes a regular polygon, a polygon circumscribed in an ellipse,
	 * with its angles equally spaced apart.
	 * 
	 * @param c
	 * @param numPoints
	 * @param startAngle
	 * @return
	 */
	public static Path makeRegularPolygon(Circle c, int numPoints, float startAngle) {
		return makePolygon(c.getCenx(), c.getCeny(), c.getRadius(), c.getRadius(), numPoints, startAngle);
	}

	/**
	 * Makes a regular polygon, a polygon circumscribed in an ellipse,
	 * with its angles equally spaced apart.
	 * 
	 * @param cenx
	 * @param ceny
	 * @param radius
	 * @param numPoints
	 * @param startAngle
	 */
	public static Path makeRegularPolygon(float cenx, float ceny, float radius, int numPoints, float startAngle) {
		return makePolygon(cenx, ceny, radius, radius, numPoints, startAngle);
	}
	
	/**
	 * Makes a polygon circumscribed in an ellipse, with its angles equally
	 * spaced from each other (analogous to a regular polygon).
	 * 
	 * @param e
	 * @param numPoints
	 * @param startAngle
	 * @return
	 */
	public static Path makePolygon(Ellipse e, int numPoints, float startAngle) {
		return makePolygon(e.getCenx(), e.getCeny(), 0.5f*e.getWidth(), 0.5f*e.getHeight(), numPoints, startAngle);
	}
	
	/**
	 * Makes a polygon circumscribed in an ellipse, with its angles equally
	 * spaced from each other (analogous to a regular polygon).
	 * 
	 * @param cenx
	 * @param ceny
	 * @param half_width
	 * @param half_height
	 * @param numVertices
	 * @param startAngle
	 * @return
	 */
	public static Path makePolygon(float cenx, float ceny,
			float half_width, float half_height, int numVertices, float startAngle) {
		if (numVertices > 0) {
			float theta = startAngle;
			float dTheta = PApplet.TWO_PI / numVertices;
			Point[] vertices = new Point[numVertices + 1];
			for (int i=0; i<numVertices; i++) {
				vertices[i] = new Point(cenx + half_width*PApplet.cos(theta),
								        ceny + half_height*PApplet.sin(theta));
				theta += dTheta;
			}
			vertices[numVertices] = vertices[0].clone();
			return new GranularPath(vertices);
		}
		else {
			return new GranularPath(new Point[] {});
		}
	}
	
	/**
	 * Makes a polygon circumscribed in a circle, with its vertices positioned
	 * along the circle at the given circles.
	 * 
	 * @param c
	 * @param angles
	 * @return
	 */
	public static Path makePolygon(Circle c, float[] angles) {
		return makePolygon(c.getCenx(), c.getCeny(), c.getRadius(), c.getRadius(), angles);
	}
	
	/**
	 * Makes a polygon circumscribed in a circle, with its vertices positioned
	 * along the circle at the given circles.
	 * 
	 * @param cenx
	 * @param ceny
	 * @param radius
	 * @param angles
	 * @return
	 */
	public static Path makePolygon(float cenx, float ceny, float radius, float[] angles) {
		return makePolygon(cenx, ceny, radius, radius, angles);
	}

	/**
	 * Makes a polygon circumscribed in an ellipse, with its vertices positioned
	 * along the ellipse at the given angles.
	 * 
	 * @param e
	 * @param angles
	 * @return
	 */
	public static Path makePolygon(Ellipse e, float[] angles) {
		return makePolygon(e.getCenx(), e.getCeny(), 0.5f*e.getWidth(), 0.5f*e.getHeight(), angles);
	}
	
	/**
	 * Makes a polygon circumscribed in an ellipse, with its vertices positioned
	 * along the ellipse at the given angles.
	 * 
	 * @param cenx
	 * @param ceny
	 * @param half_width
	 * @param half_height
	 * @param angles
	 * @return
	 */
	public static Path makePolygon(float cenx, float ceny, float half_width, float half_height, float[] angles) {
		if (angles.length != 0) {
			angles = PApplet.sort(angles);
			int n = angles.length;
			Point[] vertices = new Point[n + 1];
			
			int i=0;
			while (i < n) {
				vertices[i] = new Point(cenx + half_width*PApplet.cos(angles[i]),
								        ceny + half_height*PApplet.sin(angles[i]));
			}
			vertices[i] = vertices[0].clone();
			return new GranularPath(vertices);
		}
		else {
			return new GranularPath(new Point[] {});
		}
	}
}