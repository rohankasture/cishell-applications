package edu.iu.sci2.visualization.geomaps.scaling;

import org.cishell.framework.algorithm.AlgorithmExecutionException;

import edu.iu.sci2.visualization.geomaps.utility.Range;

public class LogScaler implements Scaler {
	@Override
	public boolean canScale(double value) {
		return (value > 0.0);
	}

	@Override
	public double scale(double rawValue) throws AlgorithmExecutionException {
		if (canScale(rawValue)) {
			return Math.log10(rawValue);
		}
		else {
			throw new AlgorithmExecutionException("Cannot scale " + rawValue + ".");
		}
	}
	@Override
	public double invert(double value) {
		return Math.pow(10.0, value);
	}

	@Override
	public String getUnscalableMessage() {
		return "Logarithmic scale is defined only for positive numbers.";
	}
	
	@Override
	public Range<Double> scale(Range<Double> range) throws AlgorithmExecutionException {
		double scaledMin = scale(range.getMin());
		double scaledMax = scale(range.getMax());
		
		return new Range<Double>(scaledMin, scaledMax);
	}
}
