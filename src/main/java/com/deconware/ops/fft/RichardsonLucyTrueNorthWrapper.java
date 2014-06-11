package com.deconware.ops.fft;


import net.imagej.ops.Op;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;
import org.scijava.Priority;

import com.deconware.algorithms.fft.filters.RichardsonLucyFilter;
import com.deconware.algorithms.fft.filters.FrequencyFilter;

@Plugin(type = Op.class, name = "RichardsonLucy", priority = Priority.HIGH_PRIORITY + 1)
public class RichardsonLucyTrueNorthWrapper<T extends RealType<T>, S extends RealType<S>> 
		extends IterativeFilterOp<T,S>
{	
	public FrequencyFilter<T,S> createAlgorithm()
	{
		try
		{
			return new RichardsonLucyFilter<T,S>(input,
				kernel, 
				input.factory(), 
				kernel.factory());
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
}
