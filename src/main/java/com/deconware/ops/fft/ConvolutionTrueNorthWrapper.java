package com.deconware.ops.fft;

import net.imagej.ops.Op;  
import net.imagej.ops.Contingent;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;
import org.scijava.Priority;

import net.imglib2.RandomAccessibleInterval;

import com.deconware.algorithms.fft.filters.FrequencyFilter;
import com.deconware.algorithms.fft.filters.Convolution;

@Plugin(type = Op.class, name = "convolution", priority = Priority.NORMAL_PRIORITY+1)
public class ConvolutionTrueNorthWrapper<T extends RealType<T>> 
		extends FrequencyFilterOp<T,T> implements Contingent
{
	public FrequencyFilter<T,T> createAlgorithm(RandomAccessibleInterval<T> input,
			RandomAccessibleInterval<T> kernel,
			RandomAccessibleInterval<T> output)
	{
		try
		{
			return new Convolution<T,T>(input,
				kernel, output);
		}
		catch(Exception ex)
		{
			return null;
		}
	}
}
