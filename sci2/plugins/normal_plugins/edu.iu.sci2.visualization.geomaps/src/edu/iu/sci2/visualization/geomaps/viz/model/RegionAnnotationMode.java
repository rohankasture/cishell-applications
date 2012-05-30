package edu.iu.sci2.visualization.geomaps.viz.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;

import org.cishell.utilities.StringUtilities;

import prefuse.data.Table;
import prefuse.data.Tuple;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import edu.iu.sci2.visualization.geomaps.data.GeoDataset;
import edu.iu.sci2.visualization.geomaps.data.GeoDataset.Stage;
import edu.iu.sci2.visualization.geomaps.data.GeoDatum;
import edu.iu.sci2.visualization.geomaps.viz.AnnotationMode;
import edu.iu.sci2.visualization.geomaps.viz.Circle;
import edu.iu.sci2.visualization.geomaps.viz.FeatureDimension;
import edu.iu.sci2.visualization.geomaps.viz.FeatureView;
import edu.iu.sci2.visualization.geomaps.viz.VizDimension.Binding;
import edu.iu.sci2.visualization.geomaps.viz.coding.Coding;
import edu.iu.sci2.visualization.geomaps.viz.strategy.Strategy;

public class RegionAnnotationMode extends AnnotationMode<String, FeatureDimension> {
	public static final String FEATURE_NAME_ID = "featureName";
	
	private final String featureNameColumnName;

	public RegionAnnotationMode(String featureNameColumnName) {
		this.featureNameColumnName = featureNameColumnName;
	}
	
	
	@Override
	protected EnumSet<FeatureDimension> dimensions() {
		return EnumSet.allOf(FeatureDimension.class);
	}
	
	@Override
	protected GeoDataset<String, FeatureDimension> readTable(Table table,
			Collection<Binding<FeatureDimension>> bindings) {
		return GeoDataset.fromTable(table, bindings, FeatureDimension.class,
				new Function<Tuple, String>() {
					@Override
					public String apply(Tuple row) {
						return normalizeFeatureName(
								StringUtilities.interpretObjectAsString(
										row.get(featureNameColumnName)));
					}					
				});
	}
	
	public static Collection<FeatureView> asFeatureViews(Collection<? extends GeoDatum<String, FeatureDimension>> valuedFeatures, final Collection<? extends Coding<FeatureDimension>> codings) {
		return Collections2.transform(
				valuedFeatures,
				new Function<GeoDatum<String, FeatureDimension>, FeatureView>() {
					@Override
					public FeatureView apply(GeoDatum<String, FeatureDimension> valuedFeature) {
						String featureName = valuedFeature.getGeo();
						
						EnumMap<FeatureDimension, Strategy> strategies = Maps.newEnumMap(FeatureDimension.class);
						for (Coding<FeatureDimension> coding : codings) {
							strategies.put(coding.dimension(), coding.strategyForValue(valuedFeature.valueInDimension(coding.dimension())));
						}
						
						return new FeatureView(featureName, strategies);
					}			
				});
	}



	@Override
	protected Collection<Circle> makeCircles(GeoDataset<String, FeatureDimension> scaledData,
			Collection<? extends Coding<FeatureDimension>> codings) {
		return ImmutableSet.<Circle>of(); // No circles in the regions mode
	}


	@Override
	protected Collection<FeatureView> makeFeatureViews(
			GeoDataset<String, FeatureDimension> scaledData,
			Collection<? extends Coding<FeatureDimension>> codings) {
		return asFeatureViews(scaledData.geoData(Stage.SCALED), codings);
	}


	public static String normalizeFeatureName(String featureName) {
		return featureName.toLowerCase();
	}
}
