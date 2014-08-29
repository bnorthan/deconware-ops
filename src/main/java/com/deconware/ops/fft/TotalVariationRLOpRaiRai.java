
package com.deconware.ops.fft;

import net.imagej.ops.Op;
import net.imglib2.type.numeric.RealType;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

import com.deconware.algorithms.fft.filters.TotalVariationRL;
import com.deconware.algorithms.fft.filters.IterativeFilter;

@Plugin(type = Op.class, name = "totalvariationrl",
	priority = Priority.NORMAL_PRIORITY)
public class TotalVariationRLOpRaiRai<T extends RealType<T>, S extends RealType<S>>
	extends IterativeFilterOpRaiRai<T, S>
{

	@Parameter
	float regularizationFactor;

	public IterativeFilter<T, S> createIterativeAlgorithm() {
		try {
			// create a RichardsonLucy filter
			TotalVariationRL<T, S> totalVariationRL =
				new TotalVariationRL<T, S>(input, kernel, output);

			totalVariationRL.setRegularizationFactor(regularizationFactor);

			return totalVariationRL;

		}
		catch (Exception ex) {
			return null;
		}
	}
}
