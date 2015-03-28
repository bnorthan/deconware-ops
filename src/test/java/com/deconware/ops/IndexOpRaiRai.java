package com.deconware.ops;

import net.imagej.ops.AbstractStrictFunction;
import net.imglib2.type.numeric.RealType;

import net.imglib2.RandomAccessibleInterval;

public class IndexOpRaiRai<T extends RealType<T>> 
	extends AbstractStrictFunction<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>>
{
	public RandomAccessibleInterval<T> compute(RandomAccessibleInterval<T> in, RandomAccessibleInterval<T> out)
	{
		long min[]=new long[in.numDimensions()];
		long max[]=new long[in.numDimensions()];
		
		in.min(min);
		in.max(max);
		
		long outmin[]=new long[out.numDimensions()];
		long outmax[]=new long[out.numDimensions()];
		
		out.min(outmin);
		out.max(outmax);
		
		System.out.println("in: "+in.numDimensions()+" min/max: "+min[0]+":"+max[0]);
		System.out.println("outs: "+in.numDimensions()+" min/max: "+outmin[0]+":"+outmax[0]);
		
		return null;
	}
}
