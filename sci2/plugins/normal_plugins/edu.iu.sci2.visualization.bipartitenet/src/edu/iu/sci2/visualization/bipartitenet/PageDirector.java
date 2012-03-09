package edu.iu.sci2.visualization.bipartitenet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import math.geom2d.Point2D;
import math.geom2d.line.LineSegment2D;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.iu.sci2.visualization.bipartitenet.component.CircleRadiusLegend;
import edu.iu.sci2.visualization.bipartitenet.component.ComplexLabelPainter;
import edu.iu.sci2.visualization.bipartitenet.component.EdgeWeightLegend;
import edu.iu.sci2.visualization.bipartitenet.component.Paintable;
import edu.iu.sci2.visualization.bipartitenet.component.PaintableContainer;
import edu.iu.sci2.visualization.bipartitenet.component.SimpleLabelPainter;
import edu.iu.sci2.visualization.bipartitenet.component.SimpleLabelPainter.XAlignment;
import edu.iu.sci2.visualization.bipartitenet.component.SimpleLabelPainter.YAlignment;
import edu.iu.sci2.visualization.bipartitenet.model.BipartiteGraphDataModel;
import edu.iu.sci2.visualization.bipartitenet.model.Edge;
import edu.iu.sci2.visualization.bipartitenet.model.Node;
import edu.iu.sci2.visualization.bipartitenet.scale.ConstantValue;
import edu.iu.sci2.visualization.bipartitenet.scale.Scale;
import edu.iu.sci2.visualization.bipartitenet.scale.ZeroAnchoredCircleRadiusScale;

public class PageDirector implements Paintable {
	public static enum Layout {
		PRINT(792, 612,
				new Point2D(18, 18), // title top-left corner
				new LineSegment2D(296, 144, 296, 412),
				new LineSegment2D(792 - 296, 144, 792 - 296, 412),
				12, 3),
		WEB(1280, 960,
				null, // No title!
				new LineSegment2D(480, 100, 480, 780),
				new LineSegment2D(800, 100, 800, 780),
				20, 5);
		
		private final int width;
		private final int height;
		private final LineSegment2D leftLine;
		private final LineSegment2D rightLine;
		private final int maxNodeRadius;
		private final Point2D headerPosition;
		private final int maxEdgeThickness;

		private Layout(int width, int height, Point2D headerPosition, 
				LineSegment2D leftLine, LineSegment2D rightLine,
				int maxNodeRadius, int maxEdgeThickness) {
			this.width = width;
			this.height = height;
			this.headerPosition = headerPosition;
			this.leftLine = leftLine;
			this.rightLine = rightLine;
			this.maxNodeRadius = maxNodeRadius;
			this.maxEdgeThickness = maxEdgeThickness;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		LineSegment2D getLeftLine() {
			return leftLine;
		}

		LineSegment2D getRightLine() {
			return rightLine;
		}
		
		Point2D getLeftTitlePosition() {
			return leftLine.getFirstPoint().translate(maxNodeRadius, -50);
		}
		
		Point2D getRightTitlePosition() {
			return rightLine.getFirstPoint().translate(- maxNodeRadius, -50);
		}
		
		Point2D getHeaderPosition() {
			return headerPosition;
		}

		Point2D getCircleLegendPosition() {
			return leftLine.getLastPoint().translate(-50, 50);
		}
		
		Point2D getEdgeLegendPosition() {
			return rightLine.getLastPoint().translate(50, 50);
		}
		
		Point2D getFooterPosition() {
			Point2D thePoint = new Point2D(width / 2.0, height - 20);
			return thePoint;
		}

		public int getMaxNodeRadius() {
			return maxNodeRadius;
		}

		public int getMaxEdgeThickness() {
			return maxEdgeThickness;
		}
	}
	
	private PaintableContainer painter = new PaintableContainer();
	private BipartiteGraphDataModel dataModel;

	public static final Font BASIC_FONT = findBasicFont(10);
	private static final Font TITLE_FONT = BASIC_FONT.deriveFont(Font.BOLD, 14);
	private static final Font SUB_TITLE_FONT = BASIC_FONT.deriveFont(Font.BOLD);
	private static final String TITLE = "Bipartite Network Graph";

	private final Layout layout;
	
	private final String footer = "NIH�s Reporter Web site (projectreporter.nih.gov), NETE & CNS (cns.iu.edu)";

	public PageDirector(final Layout layout, 
			final BipartiteGraphDataModel dataModel, 
			final String leftSideType, final String leftSideTitle, 
			final String rightSideType, final String rightSideTitle) {
		this.layout = layout;
		this.dataModel = dataModel;

		// Make codings for the nodes and edges (size and color)
		// If the nodes/edges are not weighted, it makes a "constant" coding.
		// Only put in a legend if the nodes/edges are weighted.
		Scale<Double,Double> nodeCoding = makeNodeCoding();
		if (dataModel.hasWeightedNodes()) {
			painter.add(makeNodeLegend(nodeCoding));
		}

		Scale<Double,Double> edgeCoding = makeEdgeCoding();
		if (dataModel.hasWeightedEdges()) {
			painter.add(makeEdgeLegend(edgeCoding));
		}
		
		BipartiteGraphRenderer renderer = new BipartiteGraphRenderer(dataModel,
				layout.getLeftLine(), layout.getRightLine(), layout.getMaxNodeRadius(), nodeCoding, edgeCoding);
		painter.add(renderer);
		
		// The main title, and headers
		Point2D headerPosition = layout.getHeaderPosition();
		// Position's null if there should be no title 
		if (headerPosition != null) {
			painter.add(
					new ComplexLabelPainter.Builder(headerPosition, BASIC_FONT, Color.BLACK)
					.addLine(TITLE, TITLE_FONT)
					.addLine("Generated from Cornell NSF Data")
					.addLine(getTimeStamp())
					.build());
		}
		
		// The titles of the two columns
		painter.add(new SimpleLabelPainter(layout.getLeftTitlePosition(), 
				XAlignment.RIGHT, YAlignment.BASELINE, leftSideTitle, SUB_TITLE_FONT, null));
		painter.add(new SimpleLabelPainter(layout.getRightTitlePosition(), 
				XAlignment.LEFT, YAlignment.BASELINE, rightSideTitle, SUB_TITLE_FONT, null));
		
		// The footer
		painter.add(new SimpleLabelPainter(layout.getFooterPosition(), 
				XAlignment.CENTER, YAlignment.BASELINE, footer, BASIC_FONT, Color.gray));
	}

	private String getTimeStamp() {
		// MMMMMdyyyy, hmmaazzz!  And you?
		return new SimpleDateFormat("MMMMMMMMMMMMMMMMM d, yyyy | h:mm aa zzz").format(new Date());
	}

	/**
	 * Looks for a font that will work on this system.  It tries several that are likely to
	 * be present on a Windows or Linux system, and falls back to Java's default font.
	 * @return
	 */
	private static Font findBasicFont(int size) {
		final String JAVA_FALLBACK_FONT = "Dialog";
		ImmutableList<String> fontFamiliesToTry =
				ImmutableList.of("Arial", "Helvetica", "FreeSans", "Nimbus Sans");
		
		Font thisFont = new Font(JAVA_FALLBACK_FONT, Font.PLAIN, 12);
		for (String family : fontFamiliesToTry) {
			System.out.println("Trying " + family);
			thisFont = new Font(family, Font.PLAIN, size);
			if (! thisFont.getFamily().equals(JAVA_FALLBACK_FONT)) {
				// found one that the system has!
				System.out.println(String.format("Yes, deciding on %s (started with %s)", thisFont.getFamily(),
						family));
				break;
			}
		}
		
		return thisFont;
	}


	private double calculateMaxNodeRadius() {
		int maxNodesOnOneSide = 
				Math.max(
					dataModel.getLeftNodes().size(), 
					dataModel.getRightNodes().size());
		return Math.min(layout.getMaxNodeRadius(), layout.getLeftLine().getLength() / maxNodesOnOneSide);
	}

	private Scale<Double, Double> makeNodeCoding() {
		if (dataModel.hasWeightedNodes()) {
			Scale<Double, Double> nodeScale = new ZeroAnchoredCircleRadiusScale(calculateMaxNodeRadius());
			nodeScale.train(Iterables.transform(dataModel.getLeftNodes(), Node.WEIGHT_GETTER));
			nodeScale.train(Iterables.transform(dataModel.getRightNodes(), Node.WEIGHT_GETTER));
			nodeScale.doneTraining();
			return nodeScale;
		} else {
			return new ConstantValue<Double, Double>(calculateMaxNodeRadius());
		}
	}

	private Scale<Double,Double> makeEdgeCoding() {
		if (dataModel.hasWeightedEdges()) {
			Scale<Double,Double> thicknessScale = new ZeroAnchoredCircleRadiusScale(layout.getMaxEdgeThickness());
			thicknessScale.train(Iterables.transform(dataModel.getEdges(), Edge.WEIGHT_GETTER));
			thicknessScale.doneTraining();
			return thicknessScale;
		} else {
			return new ConstantValue<Double,Double>(Double.valueOf(1));
		}
	}

	private Paintable makeNodeLegend(Scale<Double,Double> coding) {
		ArrayList<Double> labels = Lists.newArrayList(coding.getExtrema());
		double halfway = (labels.get(0) + labels.get(1)) / 2.0;
		labels.add(1, halfway);
		CircleRadiusLegend legend = new CircleRadiusLegend(
				layout.getCircleLegendPosition(), "Circle Area: "
						+ dataModel.getNodeValueAttribute(), coding, ImmutableList.copyOf(labels));
		return legend;
	}

	private Paintable makeEdgeLegend(Scale<Double,Double> edgeCoding) {
		ArrayList<Double> labels = Lists.newArrayList(edgeCoding.getExtrema());
		double halfway = (labels.get(0) + labels.get(1)) / 2.0;
		labels.add(1, halfway);
		EdgeWeightLegend legend = new EdgeWeightLegend(layout.getEdgeLegendPosition(), 
				"Edge Weight: " + dataModel.getEdgeValueAttribute(),
				edgeCoding, ImmutableList.copyOf(labels));
		return legend;
	}

	@Override
	public void paint(Graphics2D g) {
		painter.paint(g);
	}
}
