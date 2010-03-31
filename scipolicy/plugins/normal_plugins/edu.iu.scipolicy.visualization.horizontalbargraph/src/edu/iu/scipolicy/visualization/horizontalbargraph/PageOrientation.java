package edu.iu.scipolicy.visualization.horizontalbargraph;

import java.util.Collection;

import edu.iu.scipolicy.visualization.horizontalbargraph.bar.Bar;
import edu.iu.scipolicy.visualization.horizontalbargraph.layout.BasicLayout;
import edu.iu.scipolicy.visualization.horizontalbargraph.layout.BoundingBox;

public class PageOrientation {
	public static final double TEXT_WIDTH_FUDGE_FACTOR = 0.75;
	
	private PageOrientationType pageOrientationType;
	private double scale;
	private BasicLayout layout;
	
	public PageOrientation(
			PageOrientationType pageOrientationType, double scale, BasicLayout layout) {
		this.pageOrientationType = pageOrientationType;
		this.scale = scale;
		this.layout = layout;
	}
	
	public PageOrientationType getPageOrientationType() {
		return this.pageOrientationType;
	}
	
	public double getScale() {
		return this.scale;
	}
	
	public double getRotation() {
		return this.pageOrientationType.rotation();
	}
	
	public double getCenteringTranslateX(Collection<Bar> bars) {
		return this.pageOrientationType.centeringTranslateX(this.scale, bars, this.layout);
	}
	
	public double getCenteringTranslateY(Collection<Bar> bars) {
		return this.pageOrientationType.centeringTranslateY(this.scale, bars, this.layout);
	}

	public double getYTranslateForHeader(BoundingBox boundingBox, double distanceFromTop) {
		return (boundingBox.getTop() - (distanceFromTop * BasicLayout.POINTS_PER_INCH));
//		return this.pageOrientationType.yTranslateForHeaderAndFooter(boundingBox, percentage);
//			BasicLayout.PAGE_WIDTH, BasicLayout.PAGE_HEIGHT, percentage);
	}

	public double getYTranslateForFooter(BoundingBox boundingBox, double distanceFromBottom) {
		return (boundingBox.getBottom() + (distanceFromBottom * BasicLayout.POINTS_PER_INCH));
	}

	public enum PageOrientationType {
		PORTRAIT {
			public double rotation() {
				return 0.0;
			}
			
			public double centeringTranslateX(
					double scale, Collection<Bar> bars, BasicLayout layout) {
				double visualizationWidth = layout.calculateTotalWidthWithoutMargins();
				double pageWidthInPoints = BasicLayout.PAGE_WIDTH * BasicLayout.POINTS_PER_INCH;
				double scaledVisualizationWidth = visualizationWidth * scale;
				double fudgeForTextWidth =
					layout.calculateTotalTextWidth(
						layout.getBarLabelFontSize(),
						BasicLayout.MAXIMUM_BAR_LABEL_CHARACTER_COUNT) *
					TEXT_WIDTH_FUDGE_FACTOR *
					scale;
				
				return ((pageWidthInPoints - scaledVisualizationWidth) / 2.0) + fudgeForTextWidth;
			}
			
			public double centeringTranslateY(
					double scale, Collection<Bar> bars, BasicLayout layout) {
				double visualizationHeight = layout.calculateTotalHeightWithoutMargins(bars);
				double pageHeightInPoints = BasicLayout.PAGE_HEIGHT * BasicLayout.POINTS_PER_INCH;
				double scaledVisualizationHeight = visualizationHeight * scale;
				
				return (pageHeightInPoints - scaledVisualizationHeight) / 2.0;
			}

//			public double yTranslateForHeaderAndFooter(
//					BoundingBox boundingBox, double percentage) {
//				return (boundingBox.getTop() * percentage);
//			}
		},
		LANDSCAPE {
			public double rotation() {
				return 90.0;
			}
			
			public double centeringTranslateX(
					double scale, Collection<Bar> bars, BasicLayout layout) {
				double visualizationWidth = layout.calculateTotalWidthWithoutMargins();
				double scaledVisualizationWidth = visualizationWidth * scale;
				double pageHeightInPoints = BasicLayout.PAGE_HEIGHT * BasicLayout.POINTS_PER_INCH;
				double fudgeForTextWidth =
					layout.calculateTotalTextWidth(
						layout.getBarLabelFontSize(),
						BasicLayout.MAXIMUM_BAR_LABEL_CHARACTER_COUNT) *
					TEXT_WIDTH_FUDGE_FACTOR *
					scale;

				return
					((pageHeightInPoints - scaledVisualizationWidth) / 2.0) +
					fudgeForTextWidth;
			}
			
			public double centeringTranslateY(
					double scale, Collection<Bar> bars, BasicLayout layout) {
				double visualizationHeight = layout.calculateTotalHeightWithoutMargins(bars);
				double pageWidthInPoints = BasicLayout.PAGE_WIDTH * BasicLayout.POINTS_PER_INCH;
				double scaledVisualizationHeight = visualizationHeight * scale;
				
				return -((pageWidthInPoints + scaledVisualizationHeight) / 2.0);
			}

//			public double yTranslateForHeaderAndFooter(
//					BoundingBox boundingBox, double percentage) {
//				return ((boundingBox.getRight() * percentage) - boundingBox.getTop());
//			}
		},
		NO_SCALING {
			public double rotation() {
				return 0.0;
			}

			public double centeringTranslateX(
					double scale, Collection<Bar> bars, BasicLayout layout) {
				double visualizationWidth = layout.calculateTotalWidthWithoutMargins();
				BoundingBox boundingBox = layout.calculateBoundingBox(bars);
				double fudgeForTextWidth =
					layout.calculateTotalTextWidth(
						layout.getBarLabelFontSize(),
						BasicLayout.MAXIMUM_BAR_LABEL_CHARACTER_COUNT) *
					TEXT_WIDTH_FUDGE_FACTOR *
					scale;
				
				return ((boundingBox.getRight() - visualizationWidth) / 2.0) + fudgeForTextWidth;
			}

			public double centeringTranslateY(
					double scale, Collection<Bar> bars, BasicLayout layout) {
				double visualizationHeight = layout.calculateTotalHeightWithoutMargins(bars);
				BoundingBox boundingBox = layout.calculateBoundingBox(bars);
				
				return (boundingBox.getTop() - visualizationHeight) / 2.0;
			}

//			public double yTranslateForHeaderAndFooter(
//					BoundingBox boundingBox, double percentage) {
////				return (boundingBox.getTop() * percentage);
//				return percentage;
//			}
		};
		
		public abstract double rotation();
		public abstract double centeringTranslateX(
				double scale, Collection<Bar> bars, BasicLayout layout);
		public abstract double centeringTranslateY(
				double scale, Collection<Bar> bars, BasicLayout layout);
//		public abstract double yTranslateForHeaderAndFooter(
//				BoundingBox boundingBox, double percentage);
	};
}