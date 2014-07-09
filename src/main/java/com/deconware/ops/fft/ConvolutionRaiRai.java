package com.deconware.ops.fft;

import net.imagej.ops.Op;  
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;
import org.scijava.Priority;

import com.deconware.algorithms.fft.filters.FrequencyFilter;
import com.deconware.algorithms.fft.filters.Convolution;

@Plugin(type = Op.class, name = "convolution", priority = Priority.NORMAL_PRIORITY)
public class ConvolutionRaiRai<T extends RealType<T>, S extends RealType<S>> 
		extends FrequencyFilterOpRaiRai<T,S>
{
	public FrequencyFilter<T,S> createAlgorithm()
	{
		try
		{
			return new Convolution<T,S>(input,
				kernel, output);
		}
		catch(Exception ex)
		{
			return null;
		}
	}
}