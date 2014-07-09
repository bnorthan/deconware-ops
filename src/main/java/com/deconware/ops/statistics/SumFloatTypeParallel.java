package com.deconware.ops.statistics;

import net.imagej.ops.Op;
import net.imagej.ops.statistics.Sum;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.ItemIO;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import net.imagej.ops.Contingent;

import com.deconware.algorithms.parallel.math.ParallelSum;
/**
* Multi-threaded version of dot product 
*
* @author Brian Northan
*/
@Plugin(type = Op.class, name = Sum.NAME, priority = Priority.HIGH_PRIORITY + 10)
public class SumFloatTypeParallel<T extends RealType<T>> implements Op, Contingent
{
	@Parameter(type = ItemIO.INPUT)
	private IterableInterval<T> in;
	
	@Parameter(type = ItemIO.OUTPUT)
	private float out;
	
	@Override
	public void run()
	{
		FloatType output=new FloatType();
		
		output=ParallelSum.RunParallelSum(in, output);
		
		out=output.getRealFloat();
	}
	
	@Override
	public boolean conforms() 
	{
		return true;
	}
}
