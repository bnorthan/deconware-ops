
package com.deconware.ops.fft;

import net.imagej.ops.Op;
import net.imglib2.type.numeric.RealType;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

import com.deconware.algorithms.fft.filters.Convolution;
import com.deconware.algorithms.fft.filters.FrequencyFilter;
import com.deconware.algorithms.fft.filters.IterativeFilter;

@Plugin(type = Op.class, name = "cdonvolution",
	priority = Priority.NORMAL_PRIORITY)
public abstract class IterativeFilterOpRaiRai<T extends RealType<T>, S extends RealType<S>>
	extends FrequencyFilterOpRaiRai<T, S>
{
	@Parameter
	int iterations;
	
	protected FrequencyFilter<T,S> createAlgorithm()
	{
		IterativeFilter<T,S> iterativeFilter=createIterativeAlgorithm();
		
		iterativeFilter.setMaxIterations(iterations);
		
		return iterativeFilter;
	}
	
	protected abstract IterativeFilter<T,S> createIterativeAlgorithm();
	
}
