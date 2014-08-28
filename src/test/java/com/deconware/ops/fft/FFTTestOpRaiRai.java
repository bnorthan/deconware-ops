package com.deconware.ops.fft;

import net.imagej.ops.Op;  
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;
import org.scijava.Priority;

import com.deconware.algorithms.fft.filters.FrequencyFilter;
import com.deconware.algorithms.fft.filters.Convolution;

import com.deconware.algorithms.StaticFunctions;

@Plugin(type = Op.class, name = "ffttestop", priority = Priority.NORMAL_PRIORITY)
public class FFTTestOpRaiRai<T extends RealType<T>, S extends RealType<S>> 
		extends FrequencyFilterOpRaiRai<T,S>
{
	public FrequencyFilter<T,S> createAlgorithm()
	{
		try
		{
			return new FFTTestAlgorithm(input, kernel, output);
		}
		catch(Exception ex)
		{
			return null;
		}
	}
}
