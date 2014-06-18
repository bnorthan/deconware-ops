package com.deconware.ops.math.dot;

import net.imagej.ops.Op;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import net.imagej.ops.Contingent;

import com.deconware.algorithms.parallel.math.ParallelDot;

/**
* Multi-threaded version of dot product 
*
* @author Brian Northan
*/
@Plugin(type = Op.class, name = DotProduct.NAME, priority = Priority.HIGH_PRIORITY + 10)
public class DotProductRealTypeParallel<T extends RealType<T>, V extends RealType<V>> implements DotProduct, Contingent
{
	@Parameter
	private IterableInterval<T> in1;
	
	@Parameter
	private IterableInterval<T> in2;
	
	@Parameter(type = ItemIO.BOTH)
	private T output;
	
	@Override
	public void run()
	{
		ParallelDot.RunParallelDot(in1, in2, output); 
	}
	
	@Override
	public boolean conforms() 
	{
		return true;
	}
}
