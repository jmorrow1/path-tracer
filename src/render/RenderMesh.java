package render;

import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;
import tracer.Tracer;

/**
 * Draws a list of Tracer objects as a procedural mesh. When two Tracers get
 * within a certain distance of each other, a line is drawn between them.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public class RenderMesh extends Render {
    // the square of the minimum between two Tracers for them to be connected by
    // a line
    protected float sqMinDist;

    // the square of the distance between two Tracers in which the stroke weight
    // is increased
    protected float sqStrokeRampDist;
    
    public RenderMesh(List<Tracer> ts) {
        this(ts, 100, 0);
    }

    public RenderMesh(List<Tracer> ts, float minDist) {
        this(ts, minDist, 0);
    }

    public RenderMesh(List<Tracer> ts, float minDist, float strokeRamp) {
        super(ts);
        this.sqMinDist = minDist * minDist;
        this.sqStrokeRampDist = strokeRamp * strokeRamp;
    }
    
    public RenderMesh(Tracer[] ts) {
        this(ts, 100, 0);
    }

    public RenderMesh(Tracer[] ts, float minDist) {
        this(listify(ts), minDist);
    }

    public RenderMesh(Tracer[] ts, float minDist, float strokeRamp) {
        this(listify(ts), minDist, strokeRamp);
    }

    @Override
    public void draw(PGraphics g) {
        // style
        g.strokeCap(style.strokeCap);
        g.stroke(style.strokeColor);

        // analyze and draw
        for (int i = 0; i < ts.size(); i++) {
            for (int j = i + 1; j < ts.size(); j++) {
                Point a = ts.get(i);
                Point b = ts.get(j);
                float term1 = (a.y - b.y);
                float term2 = (a.x - b.x);
                float sqDist = term1 * term1 + term2 * term2;
                if (sqDist <= sqMinDist) {
                    if (sqDist >= sqMinDist - sqStrokeRampDist) {
                        float sw = PApplet.constrain(
                                PApplet.map(sqDist, sqMinDist, sqMinDist - sqStrokeRampDist, 0, style.strokeWeight),
                                0.00001f,
                                style.strokeWeight);
                        g.strokeWeight(sw);
                    } else {
                        g.strokeWeight(style.strokeWeight);
                    }
                    g.line(a.x, a.y, b.x, b.y);
                }
            }
        }
    }


    public void setMinDist(float minDist) {
        sqMinDist = minDist * minDist;
    }

    public void setStrokeRamp(float strokeRampDist) {
        sqStrokeRampDist = strokeRampDist * strokeRampDist;
    }
}
