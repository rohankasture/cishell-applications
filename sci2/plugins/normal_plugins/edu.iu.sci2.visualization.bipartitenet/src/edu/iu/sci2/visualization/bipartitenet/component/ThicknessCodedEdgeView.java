package edu.iu.sci2.visualization.bipartitenet.component;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.line.AbstractLine2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.line.StraightLine2D;
import math.geom2d.polygon.SimplePolygon2D;

import com.google.common.collect.ImmutableList;

import edu.iu.sci2.visualization.bipartitenet.model.Edge;
import edu.iu.sci2.visualization.bipartitenet.scale.Scale;

public class ThicknessCodedEdgeView implements Paintable {
	
	private static final double NODE_EDGE_SPACE = 4;
	private final NodeView dest;
	private final NodeView src;
	private final Scale<Double,Double> edgeCoding;
	private final Edge e;

	public ThicknessCodedEdgeView(Edge e, NodeView src, NodeView dest, Scale<Double,Double> edgeCoding) {
		this.e = e;
		this.src = src;
		this.dest = dest;
		this.edgeCoding = edgeCoding;
	}

	@Override
	public void paint(Graphics2D g) {
		Double edgeThickness = edgeCoding.apply(e.getWeight());
		LineSegment2D baseLine = new LineSegment2D(src.getNodeCenter(), dest.getNodeCenter());
		double tStart = (src.getRadius() + NODE_EDGE_SPACE) / baseLine.getLength(),
				tEnd = (baseLine.getLength() - dest.getRadius() - NODE_EDGE_SPACE)
							/ baseLine.getLength();
		AbstractLine2D grossLine = baseLine.getSubCurve(tStart, tEnd);
		drawArrow(grossLine, edgeThickness.floatValue(), g);
		
	}

	public static void drawArrow(AbstractLine2D grossLine, float edgeThickness, Graphics2D g) {
		// Make the line actually end on the given point:
		double endOffset = getArrowHeadLength(edgeThickness);
		
		// tStart=0: 
		// this resolves an inconsistency between the Java2D and PS rendering models by setting the
		// right line length for the PS rendering model.  There might be a way to use the Stroke 
		// to have a particular line end cap that will work in both rendering models.
		double tStart = 0,
				tEnd = 1 - (endOffset / grossLine.getLength());
		AbstractLine2D fineLine = grossLine.getSubCurve(tStart, tEnd);
		
		g.setStroke(new BasicStroke(edgeThickness));
		
		fineLine.draw(g);

		drawArrowHead(fineLine, edgeThickness, g);
	}

	public static void drawArrowHead(AbstractLine2D line, double edgeThickness, Graphics2D g) {
		Point2D end = line.getLastPoint();
		Point2D tip = line.getSupportingLine().getPoint(1 + (getArrowHeadLength(edgeThickness) / line.getLength()));
		Point2D arrowHookProjection = line.getPoint(1 - edgeThickness / line.getLength());
		
		StraightLine2D perp = line.getPerpendicular(arrowHookProjection);
		Circle2D arrowWidth = new Circle2D(arrowHookProjection, 1.5 * edgeThickness);
		ImmutableList<Point2D> backPoints = ImmutableList.copyOf(arrowWidth.getIntersections(perp));
		
		SimplePolygon2D arrowHead = new SimplePolygon2D(backPoints.get(0), tip, backPoints.get(1), end);
		arrowHead.fill(g);
	}


	private static double getArrowHeadLength(double edgeThickness) {
		return 2 * edgeThickness;
	}
}
