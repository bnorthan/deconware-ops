package com.deconware.ops.arithmetic.add;

import net.imagej.ops.Ops.Add;
import net.imagej.ops.Op;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.ItemIO;
import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.deconware.algorithms.parallel.math.ParallelAdd;

import org.scijava.plugin.Parameter;

@Plugin(type = Op.class, name = Add.NAME, priority = Priority.HIGH_PRIORITY)
public class AddIntervalWithIntervalP<T extends RealType<T>> implements Add
{
	@Parameter
	private IterableInterval<T> interval1;
	
	@Parameter
	private IterableInterval<T> interval2;
	
	@Parameter(type = ItemIO.BOTH)
	private IterableInterval<T> output;
	
	
	public void run()
	{
		ParallelAdd.Add(interval1, interval2, output);
	}
}
