package render;

import java.util.List;

import processing.core.PGraphics;
import tracer.Point;
import tracer.Tracer;

/**
 * Draws a list of Tracer objects as a shape using Processing's beginShape() and endShape() method.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public class RenderShape extends Render {
	//style
	protected boolean closeShape;
	protected boolean showFill, showStroke;
	protected int fillColor, strokeColor;
	
	public RenderShape(List<Tracer> ts, boolean closeShape, boolean showFill, boolean showStroke, int fillColor,
			int strokeColor) {
		super(ts);
		this.closeShape = closeShape;
		this.showFill = showFill;
		this.showStroke = showStroke;
		this.fillColor = fillColor;
		this.strokeColor = strokeColor;
	}

	public RenderShape(List<Tracer> ts) {
		this(ts, true, false, true, -1, 0x000000ff);
	}
	
	@Override
	public void draw(PGraphics g) {
		//stroke
		if (showStroke) {
			g.stroke(strokeColor);
		}
		else {
			g.noStroke();
		}
		
		//fill
		if (showFill) {
			g.fill(fillColor);
		}
		else {
			g.noFill();
		}
		
		//shape
		g.beginShape();
		for (int i=0; i<ts.size(); i++) {
			Point pt = ts.get(i).location();
			g.vertex(pt.x, pt.y);
		}
		if (closeShape) {
			g.endShape(g.CLOSE);
		}
		else {
			g.endShape();
		}
	}

	public void setCloseShape(boolean closeShape) {
		this.closeShape = closeShape;
	}

	public void showFill(boolean showFill) {
		this.showFill = showFill;
	}

	public void showStroke(boolean showStroke) {
		this.showStroke = showStroke;
	}

	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}
}